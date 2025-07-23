package dynamicReduce.reducer;

import org.eclipse.cdt.core.dom.ast.*;
import dynamicReduce.parser.ExpReduceUnit;
import dynamicReduce.parser.FileParser;
import dynamicReduce.tester.OutputTester;
import dynamicReduce.tester.TestContext;
import dynamicReduce.utils.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionReducer extends Reducer{

    public static boolean suppressTimeout=false;
    public static boolean extendedReduce=true;
    List<ExpReduceUnit> toDelete;
    List<ExpReduceUnit> delayedDelete;
    Set<String> timeoutRecord;

    public ExpressionReducer(FileParser parser) {
        super(parser);
        toDelete=new ArrayList<>();
        delayedDelete=new ArrayList<>();
        timeoutRecord=new HashSet<>();
    }

    private void handleList(List<IASTNode[]> nodesList){
        int length=nodesList.get(0).length;
        if(length==0){
            return;
        }
        if(length==1){
            List<int[]> deleteSpace=new ArrayList<>();
            for (IASTNode[] nodes:nodesList){
                if(nodes.length<1){continue;}
                deleteSpace.add(new int[]{nodes[0].getFileLocation().getNodeOffset(),
                        nodes[0].getFileLocation().getNodeOffset()+nodes[0].getFileLocation().getNodeLength()});
            }
            toDelete.add(new ExpReduceUnit(deleteSpace,nodesList.get(0)[0].getRawSignature()));
            return;
        }
        List<int[]> deleteSpace=new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (IASTNode[] nodes:nodesList) {
            IASTFileLocation last = nodes[nodes.length - 1].getFileLocation();
            for (int i = 0; i < nodes.length&&nodes==nodesList.get(0); i++) {
                if (i != 0) {
                    sb.append(",");
                }
                sb.append(nodes[i].getRawSignature());
            }
            deleteSpace.add(new int[]{nodes[0].getFileLocation().getNodeOffset(),last.getNodeOffset()+last.getNodeLength()});

        }
        toDelete.add(new ExpReduceUnit(deleteSpace,sb.toString()));
        for (int i = 0; i < length; i++) {
            deleteSpace=new ArrayList<>();
            for (IASTNode[] nodes:nodesList) {
                if(nodes.length<i){continue;}
                IASTFileLocation location = nodes[i].getFileLocation();
                if (i == 0) {
                    if(nodes.length==1){
                        deleteSpace.add(new int[]{location.getNodeOffset(), location.getNodeOffset()+location.getNodeLength()});
                    }else {
                        IASTFileLocation next = nodes[i + 1].getFileLocation();
                        deleteSpace.add(new int[]{location.getNodeOffset(), next.getNodeOffset()});
                    }
                } else {
                    IASTFileLocation prev = nodes[i - 1].getFileLocation();
                    deleteSpace.add(new int[]{prev.getNodeOffset() + prev.getNodeLength(),
                            location.getNodeOffset() + location.getNodeLength()});
                }
            }
            toDelete.add(new ExpReduceUnit(deleteSpace, nodesList.get(0)[i].getRawSignature()));
        }
    }

    private void analyseExpression(IASTExpression expression){
        if(expression instanceof IASTBinaryExpression binaryExpression){
            IASTExpression operand1=binaryExpression.getOperand1();
            IASTExpression operand2=binaryExpression.getOperand2();
            toDelete.add(new ExpReduceUnit(expression,operand1));
            toDelete.add(new ExpReduceUnit(expression,operand2));
        }else if (expression instanceof IASTUnaryExpression unaryExpression){
            IASTExpression operand=unaryExpression.getOperand();
            toDelete.add(new ExpReduceUnit(expression,operand));
        }else if(expression instanceof IASTArraySubscriptExpression arraySubscriptExpression){
            IASTExpression arrayExpression =arraySubscriptExpression.getArrayExpression();
            IASTNode argument=arraySubscriptExpression.getArgument();
            toDelete.add(new ExpReduceUnit(expression,arrayExpression));
            toDelete.add(new ExpReduceUnit(expression,argument));
        }else if(expression instanceof IASTFunctionCallExpression functionCallExpression){
            IASTNode[] arguments=functionCallExpression.getArguments();
            List<IASTNode[]> argumentList=new ArrayList<>();
            argumentList.add(arguments);
            handleList(argumentList);
            for (IASTNode argument:arguments){
                toDelete.add(new ExpReduceUnit(expression,argument));
            }
        }else if(expression instanceof IASTConditionalExpression conditionalExpression){
            IASTExpression logicalConditionExpression=conditionalExpression.getLogicalConditionExpression();
            IASTExpression positiveResultExpression=conditionalExpression.getPositiveResultExpression();
            IASTExpression negativeResultExpression=conditionalExpression.getNegativeResultExpression();
            toDelete.add(new ExpReduceUnit(expression,logicalConditionExpression));
            toDelete.add(new ExpReduceUnit(expression,positiveResultExpression));
            toDelete.add(new ExpReduceUnit(expression,negativeResultExpression));
        }else if (expression instanceof IASTCastExpression castExpression){
            IASTExpression operand=castExpression.getOperand();
            toDelete.add(new ExpReduceUnit(expression,operand));
        }else if(expression instanceof IASTFieldReference fieldReference){
            fieldReference.getFieldName();
        }else if(expression instanceof IASTExpressionList expressionList){
            for (IASTExpression expression1:expressionList.getExpressions()){
                toDelete.add(new ExpReduceUnit(expression,expression1));
            }
        }else if(expression instanceof IASTIdExpression idExpression){
            IBinding binding=idExpression.getName().resolveBinding();
            if(binding instanceof IVariable iVariable){
                IType type=iVariable.getType();
                while (type instanceof ITypedef typedef){
                    type=typedef.getType();
                }
                if(type instanceof IBasicType){
                    toDelete.add(new ExpReduceUnit(expression,"0"));
                }
            }
        }
        if(expression instanceof IASTLiteralExpression){
            return;
        }
        IASTNode parent = expression.getParent();
        if (parent instanceof IASTForStatement forStatement) {
            if(expression==forStatement.getIterationExpression()){
                toDelete.add(new ExpReduceUnit(expression, (IASTNode) null));
            }
            if(expression==forStatement.getConditionExpression()){
                if(extendedReduce){
                    toDelete.add(new ExpReduceUnit(expression,"0"));
                }
                toDelete.add(new ExpReduceUnit(expression, (IASTNode) null));
            }
        }else if(parent instanceof IASTIfStatement ifStatement){
            if(expression==ifStatement.getConditionExpression()){
                toDelete.add(new ExpReduceUnit(expression,"0"));
                if(extendedReduce){
                    toDelete.add(new ExpReduceUnit(expression,"1"));
                }
            }
        }else if(parent instanceof IASTWhileStatement whileStatement){
            if(expression==whileStatement.getCondition()){
                toDelete.add(new ExpReduceUnit(expression,"0"));
                if(extendedReduce){
                    toDelete.add(new ExpReduceUnit(expression,"1"));
                }
            }
        }
    }

    private void getDeclSpecifier(IASTDeclSpecifier declSpecifier){
        String text=declSpecifier.getRawSignature();
        IASTFileLocation fileLocation=declSpecifier.getFileLocation();
        Pattern p = Pattern.compile("\\S+");
        Matcher m = p.matcher(text);
        List<ExpReduceUnit> units=new ArrayList<>();
        while (m.find()) {
            units.add(new ExpReduceUnit(fileLocation.getNodeOffset()+m.start(), fileLocation.getNodeOffset()+m.end(), m.group() ));
        }
        for (int i = 0; i < units.size()-1; i++) {
            toDelete.add(units.get(i));
        }
    }
    protected IASTNode getRoot(){
        return parser.getAST();
    }
    private List<IASTNode[]> findCalls(IBinding binding){
        List<IASTNode[]> res=new ArrayList<>();
        parser.getAST().accept(new ASTVisitor(true) {
            @Override
            public int visit(IASTExpression expression) {
                if(expression instanceof IASTFunctionCallExpression functionCallExpression){
                    IASTExpression functionNameExpression = functionCallExpression.getFunctionNameExpression();
                    if (functionNameExpression instanceof IASTIdExpression idExpression) {
                        if(idExpression.getName().resolveBinding().equals(binding)) {
                            IASTNode[] arguments = functionCallExpression.getArguments();
                            res.add(arguments);
                        }
                    }
                }
                return super.visit(expression);
            }
        });
        return res;
    }
    private void findExpReduceUnits(){
        toDelete.clear();
        IASTNode root=getRoot();
        root.accept(new ASTVisitor(true) {
            @Override
            public int visit(IASTExpression expression) {
                analyseExpression(expression);
                return super.visit(expression);
            }

            @Override
            public int visit(IASTDeclaration declaration) {
                if(!extendedReduce){return super.visit(declaration);}
                if(declaration instanceof IASTSimpleDeclaration simpleDeclaration){
                    getDeclSpecifier(simpleDeclaration.getDeclSpecifier());
                    if(simpleDeclaration.getDeclarators().length>1){
                        List<IASTNode[]> declList=new ArrayList<>();
                        declList.add(simpleDeclaration.getDeclarators());
                        handleList(declList);
                    }
                }else if(declaration instanceof IASTFunctionDefinition functionDefinition){
                    if(root instanceof IASTTranslationUnit){
                        return PROCESS_SKIP;
                    }
                    getDeclSpecifier(functionDefinition.getDeclSpecifier());
                    if(functionDefinition.getDeclarator() instanceof IASTStandardFunctionDeclarator standardFunctionDeclarator) {
                        List<IASTNode[]> argumentList=findCalls(standardFunctionDeclarator.getName().resolveBinding());
                        argumentList.add(0,standardFunctionDeclarator.getParameters());
                        handleList(argumentList);
                        for (IASTParameterDeclaration parameterDeclaration : standardFunctionDeclarator.getParameters()) {
                            getDeclSpecifier(parameterDeclaration.getDeclSpecifier());
                        }
                    }
                }
                return super.visit(declaration);
            }

            @Override
            public int visit(IASTDeclarator declarator) {
                IASTInitializer initializer= declarator.getInitializer();
                if(initializer!=null){
                    toDelete.add(new ExpReduceUnit(initializer,(IASTNode)null));
                }
                return super.visit(declarator);
            }

            @Override
            public int visit(IASTInitializer initializer) {
                if(!extendedReduce){return super.visit(initializer);}
                if(initializer instanceof IASTInitializerList initializerList){
                    if(initializerList.getClauses().length==0){return super.visit(initializer);}
                    toDelete.add(new ExpReduceUnit(initializer,"{}"));
                    toDelete.add(new ExpReduceUnit(initializer,initializerList.getClauses()[0].getRawSignature()));
                    toDelete.add(new ExpReduceUnit(initializer,"{"+initializerList.getClauses()[0].getRawSignature()+"}"));
                }
                return super.visit(initializer);
            }
            @Override
            public int visit(IASTStatement statement){
                if(!extendedReduce){return super.visit(statement);}
                if (statement instanceof IASTIfStatement ifStatement){
                    IASTNode elseClause=ifStatement.getElseClause();
                    if(elseClause!=null){
                        IASTFileLocation thenLocation=ifStatement.getThenClause().getFileLocation();
                        IASTFileLocation elseLocation=elseClause.getFileLocation();
                        int start=thenLocation.getNodeOffset()+thenLocation.getNodeLength();
                        int end=elseLocation.getNodeOffset()+elseLocation.getNodeLength();
                        toDelete.add(new ExpReduceUnit(start,end,"else "+elseClause.getRawSignature()));
                        toDelete.add(new ExpReduceUnit(start,elseLocation.getNodeOffset(),"else"));
                    }
                }
                if(statement instanceof IASTCompoundStatement compoundStatement){
                    /*
                    if(statement.getParent() instanceof IASTIfStatement ifStatement){
                        if(statement==ifStatement.getThenClause()&&ifStatement.getElseClause()!=null){
                            return super.visit(statement);
                        }
                    }*/
                    if(compoundStatement.getStatements().length==0){
                        toDelete.add(new ExpReduceUnit(compoundStatement,"0;"));
                    }
                }
                return super.visit(statement);
            }
        });
        if(suppressTimeout){
            delayedDelete.clear();
            for (int i = 0; i < toDelete.size();) {
                if(timeoutRecord.contains(toDelete.get(i).getIdentifier())){
                    delayedDelete.add(toDelete.get(i));
                    toDelete.remove(i);
                }else {
                    i++;
                }
            }
            for (String timeout:timeoutRecord){
                Logger.log("Timeout record: "+timeout);
            }
        }
    }


    @Override
    public void run() {
        if(suppressTimeout){
            runSuppressTimeout();
            return;
        }
        findExpReduceUnits();
        DeltaSelector<ExpReduceUnit> unitSelector=new DeltaSelector<>(toDelete,1);
        while (!unitSelector.ended){
            List<ExpReduceUnit> units=unitSelector.get();
            boolean succeed=parser.getFileRewriter().reduceNodes(units);
            if (!succeed){
                unitSelector.fail();
                continue;
            }
            TestContext testContext=new OutputTester().run();
            if(testContext.getTestStatus().succeed()){
                success();
                findExpReduceUnits();
                unitSelector.success(toDelete);
            }else {
                Logger.log("Reduce failed.");
                unitSelector.fail();
            }
        }
    }

    private void runSuppressTimeout(){
        findExpReduceUnits();
        DeltaSelector<ExpReduceUnit> unitSelector=new DeltaSelector<>(toDelete,1);
        boolean delayedRun=false;
        while (!unitSelector.ended||!delayedRun){
            if(unitSelector.ended){
                if(delayedDelete.size()==0){
                    delayedRun=true;break;
                }
                unitSelector=new DeltaSelector<>(delayedDelete,1);
                delayedRun=true;
            }
            List<ExpReduceUnit> units=unitSelector.get();
            boolean succeed=parser.getFileRewriter().reduceNodes(units);
            if (!succeed){
                unitSelector.fail();
                continue;
            }
            TestContext testContext=new OutputTester().run();
            if(testContext.getTestStatus().succeed()){
                success();
                findExpReduceUnits();
                if(!delayedRun){
                    unitSelector.success(toDelete);
                }else {
                    unitSelector.success(delayedDelete);
                }
            }else {
                if(testContext.getTestStatus().timeout()) {
                    for (ExpReduceUnit unit:units) {
                        timeoutRecord.add(unit.getIdentifier());
                    }
                }
                Logger.log("Reduce failed.");
                unitSelector.fail();
            }
        }

    }
}
