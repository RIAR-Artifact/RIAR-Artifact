package dynamicReduce.reducer;

import org.eclipse.cdt.core.dom.ast.*;
import dynamicReduce.parser.rewriter.Rewriter;
import dynamicReduce.tester.TestRecord;
import dynamicReduce.utils.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class CallUnit {
    private final IASTFunctionCallExpression callPoint;
    private final IASTFunctionDefinition functionDefinition;
    IASTParameterDeclaration[] params;
    String postfix;
    int functionBegin=0;
    int functionEnd=0;
    public CallUnit(IASTFunctionCallExpression callPoint, IASTFunctionDefinition functionDefinition) {
        this.callPoint = callPoint;
        this.functionDefinition = functionDefinition;
        postfix="__"+functionDefinition.getDeclarator().getName().getBinding().getName();
        IASTFunctionDeclarator declarator = functionDefinition.getDeclarator();
        if (declarator instanceof IASTStandardFunctionDeclarator standardFunctionDeclarator) {
            params = standardFunctionDeclarator.getParameters();
        }
    }

    public IASTFunctionDefinition getFunctionDefinition() {
        return functionDefinition;
    }

    public void inline(){
        Rewriter rewriter=new Rewriter(TestRecord.v().getLastSuccess());
        if(functionDefinition.getBody() instanceof IASTCompoundStatement compoundStatement){
            if(compoundStatement.getStatements().length==0){
                rewriter.removeNode(functionDefinition);
                rewriter.removeNode(callPoint);
                rewriter.finish();
                return;
            }
            IASTStatement[] statements=compoundStatement.getStatements();
            functionBegin=statements[0].getFileLocation().getNodeOffset();
            IASTFileLocation endLineLocation=statements[statements.length-1].getFileLocation();
            functionEnd=endLineLocation.getNodeOffset()+endLineLocation.getNodeLength();
        }else {
            IASTFileLocation bodyLocation=functionDefinition.getBody().getFileLocation();
            functionBegin=bodyLocation.getNodeOffset();
            functionEnd=bodyLocation.getNodeOffset()+bodyLocation.getNodeLength();
        }
        Logger.log("inlining "+callPoint.getRawSignature());
        renameParametersAndVariables(rewriter);
        clearReturn(rewriter);
        moveFunctionBody(rewriter);
        rewriter.finish();
    }

    private void renameParametersAndVariables(Rewriter rewriter){
        Set<IBinding> ids=new HashSet<>();
        List<IBinding> paramIDs=new ArrayList<>();
        IASTInitializerClause[] initializerClauses=callPoint.getArguments();
        for (IASTParameterDeclaration param : params) {
            paramIDs.add(param.getDeclarator().getName().getBinding());
        }
        functionDefinition.getBody().accept(new ASTVisitor(true) {
            @Override
            public int visit(IASTDeclarator declarator) {
                IBinding binding=declarator.getName().getBinding();
                if(binding!=null) {
                    ids.add(binding);
                }
                return super.visit(declarator);
            }
        });
        functionDefinition.getBody().accept(new ASTVisitor(true) {
            @Override
            public int visit(IASTName name) {
                if(ids.contains(name.getBinding())){
                    IASTFileLocation location= name.getFileLocation();
                    rewriter.insertAt(postfix,location.getNodeOffset()+location.getNodeLength());
                }else {
                    for (int i=0;i<paramIDs.size();i++){
                        if(name.getBinding()==paramIDs.get(i)){
                            rewriter.replaceNode(initializerClauses[i],name);
                        }
                    }
                }
                return super.visit(name);
            }
        });
    }

    private void clearReturn(Rewriter rewriter){
        IFunction funcBinding= (IFunction) functionDefinition.getDeclarator().getName().getBinding();
        if(funcBinding.getType().getReturnType().toString().equals("void")){
            functionDefinition.getBody().accept(new ASTVisitor(true) {
                @Override
                public int visit(IASTStatement statement) {
                    if(statement instanceof IASTReturnStatement){
                        rewriter.removeNode(statement);
                    }
                    return super.visit(statement);
                }
            });
            IASTNode callStmt=callPoint.getParent();
            if (!(callStmt instanceof IASTStatement)){
                callStmt=callPoint;
            }
            rewriter.removeNode(callStmt);
            return;
        }
        final int[] returnCount = {0};
        functionDefinition.getBody().accept(new ASTVisitor(true) {
            @Override
            public int visit(IASTStatement statement) {
                if(statement instanceof IASTReturnStatement){
                    returnCount[0]++;
                }
                return super.visit(statement);
            }
        });
        if(returnCount[0]==1){
            functionDefinition.getBody().accept(new ASTVisitor(true) {
                @Override
                public int visit(IASTStatement statement) {
                    if(statement instanceof IASTReturnStatement returnStatement){
                        rewriter.replaceNode(returnStatement.getReturnArgument(),callPoint);
                        rewriter.removeNode(returnStatement);
                    }
                    return super.visit(statement);
                }
            });
        }else {
            String returnVar="return"+postfix;
            String declaration = funcBinding.getType().getReturnType().toString() + " " + returnVar + ";\n";
            rewriter.insertAt(declaration,functionBegin);
            functionDefinition.getBody().accept(new ASTVisitor(true) {
                @Override
                public int visit(IASTStatement statement) {
                    if(statement instanceof IASTReturnStatement returnStatement){

                        String assign=returnVar+"="+rewriter.getNodeText(returnStatement.getReturnArgument())+";";
                        rewriter.replaceNode(assign,returnStatement);
                    }
                    return super.visit(statement);
                }
            });
            rewriter.replaceNode(returnVar,callPoint);
        }

    }

    private void moveFunctionBody(Rewriter rewriter){
        String body=rewriter.getText(functionBegin,functionEnd);
        IASTNode upperStmt=callPoint.getParent();
        while (!(upperStmt instanceof IASTStatement)){
            upperStmt=upperStmt.getParent();
            if(upperStmt instanceof IASTTranslationUnit){
                Logger.log("can't find Upper Statement");
                upperStmt=callPoint;
                break;
            }
        }
        IASTFileLocation fileLocation= upperStmt.getFileLocation();
        boolean flag=false;
        if(upperStmt.getParent() instanceof IASTForStatement statement){
            if(upperStmt==statement.getBody()){flag=true;}
        }
        if(upperStmt.getParent() instanceof IASTWhileStatement statement){
            if(upperStmt==statement.getBody()){flag=true;}
        }
        if(upperStmt.getParent() instanceof IASTIfStatement statement){
            if(upperStmt==statement.getThenClause()||upperStmt==statement.getElseClause()){flag=true;}
        }
        if(flag){
            body="{"+body;
            rewriter.insertAt(body,fileLocation.getNodeOffset());
            rewriter.insertAt("}",fileLocation.getNodeOffset()+fileLocation.getNodeLength());
        }else {
            rewriter.insertAt(body,fileLocation.getNodeOffset());
        }
        rewriter.removeNode(functionDefinition);
    }

    @Override
    public int hashCode() {
        return callPoint.hashCode()+functionDefinition.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CallUnit callUnit){
            return callUnit.callPoint==this.callPoint&&callUnit.functionDefinition==this.functionDefinition;
        }
        return false;
    }
}
