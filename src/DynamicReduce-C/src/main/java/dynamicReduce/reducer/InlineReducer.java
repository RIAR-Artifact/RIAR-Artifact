package dynamicReduce.reducer;


import org.eclipse.cdt.core.dom.ast.*;
import dynamicReduce.parser.FileParser;
import dynamicReduce.parser.UniqueID;
import dynamicReduce.tester.OutputTester;
import dynamicReduce.tester.TestContext;
import dynamicReduce.utils.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InlineReducer extends Reducer{

    private final List<CallUnit> callUnits;
    private final List<MacroUnit> macroUnits;
    private final List<DeclPair> declPairs;
    private final Set<String> proceedUnits;
    public InlineReducer(FileParser parser) {
        super(parser);
        callUnits=new ArrayList<>();
        macroUnits=new ArrayList<>();
        proceedUnits=new HashSet<>();
        declPairs=new ArrayList<>();
    }

    private void findCallUnits(){
        callUnits.clear();
        List<IASTFunctionDefinition> functionDefinitions=new ArrayList<>();
        List<IASTFunctionCallExpression> callExpressions=new ArrayList<>();
        parser.getAST().accept(new ASTVisitor(true) {
            @Override
            public int visit(IASTDeclaration declaration) {
                if(declaration instanceof IASTFunctionDefinition functionDefinition){
                    functionDefinitions.add(functionDefinition);
                }
                return super.visit(declaration);
            }
            @Override
            public int visit(IASTExpression expression) {
                if(expression instanceof IASTFunctionCallExpression functionCallExpression){
                    callExpressions.add(functionCallExpression);
                }
                return super.visit(expression);
            }
        });
        for (IASTFunctionDefinition functionDefinition:functionDefinitions){
            IBinding functionBinding=functionDefinition.getDeclarator().getName().getBinding();
            if(functionBinding==null){
                continue;
            }
            UniqueID functionID=new UniqueID(functionBinding);
            if(proceedUnits.contains(functionID.toString())){
                continue;
            }
            IASTFunctionCallExpression call=null;
            boolean flag=false;
            int callCount=0;
            for (IASTFunctionCallExpression callExpression:callExpressions){
                IASTExpression functionNameExpression=callExpression.getFunctionNameExpression();
                if(functionNameExpression instanceof IASTIdExpression idExpression){
                    IBinding callBinding=idExpression.getName().getBinding();
                    UniqueID callID=new UniqueID(callBinding);
                    if(functionID.toString().equals(callID.toString())){
                        callCount++;
                        if(call!=null){flag=false;break;}
                        call=callExpression;
                        flag=true;
                    }
                }
            }
            if(callCount>0){

            }
            if(!flag) {continue;}
            callUnits.add(new CallUnit(call,functionDefinition));
        }
    }

    private void findMacroUnits(){
        macroUnits.clear();
        parser.getAST().accept(new ASTVisitor(true) {
            @Override
            public int visit(IASTDeclaration declaration) {
                if (declaration instanceof IASTSimpleDeclaration simpleDecl) {
                    for (IASTDeclarator declarator : simpleDecl.getDeclarators()) {
                        IASTName typedefName = declarator.getName();
                        IBinding binding = typedefName.resolveBinding();
                        if (binding instanceof ITypedef) {
                            MacroUnit macroUnit=new MacroUnit(declaration,typedefName);
                            if(!proceedUnits.contains(macroUnit.getName())){
                                macroUnits.add(macroUnit);
                            }
                        }
                    }
                }
                return PROCESS_CONTINUE;
            }
        });
    }
    private void findDeclPairs(){
        declPairs.clear();
        parser.getAST().accept(new ASTVisitor(true) {
            @Override
            public int visit(IASTDeclaration declaration) {
                if (declaration instanceof IASTSimpleDeclaration simpleDeclaration1) {
                    IASTNode parent=declaration.getParent();
                    String decl1=simpleDeclaration1.getDeclSpecifier().getRawSignature();
                    boolean flag=false;
                    for (IASTNode child:parent.getChildren()){
                        if(flag){
                            if(child instanceof IASTSimpleDeclaration simpleDeclaration2){
                                String decl2=simpleDeclaration2.getDeclSpecifier().getRawSignature();
                                if(decl1.equals(decl2)){
                                    DeclPair declPair=new DeclPair(simpleDeclaration1, simpleDeclaration2);
                                    if(!proceedUnits.contains(declPair.toString())){
                                        Logger.log(declPair.toString());
                                        declPairs.add(declPair);
                                    }
                                    return PROCESS_CONTINUE;
                                }
                            }
                        }
                        if(child.getFileLocation().getNodeOffset()==declaration.getFileLocation().getNodeOffset()){flag=true;}
                    }
                }
                return PROCESS_CONTINUE;
            }

            @Override
            public int visit(IASTStatement statement) {
                if(statement instanceof IASTDeclarationStatement declarationStatement1){
                    if(declarationStatement1.getDeclaration() instanceof IASTSimpleDeclaration simpleDeclaration1) {
                        IASTNode parent=statement.getParent();
                        String decl1 =simpleDeclaration1.getDeclSpecifier().getRawSignature();
                        boolean flag = false;
                        for (IASTNode child : parent.getChildren()) {
                            if (flag) {
                                if (child instanceof IASTDeclarationStatement declarationStatement2) {
                                    if(declarationStatement2.getDeclaration() instanceof IASTSimpleDeclaration simpleDeclaration2){
                                        String decl2 = simpleDeclaration2.getDeclSpecifier().getRawSignature();
                                        if (decl1.equals(decl2)) {
                                            DeclPair declPair=new DeclPair(simpleDeclaration1, simpleDeclaration2);
                                            if(!proceedUnits.contains(declPair.toString())){
                                                declPairs.add(declPair);
                                            }
                                            return super.visit(statement);
                                        }
                                    }
                                }
                            }
                            if (child.getFileLocation().getNodeOffset() == statement.getFileLocation().getNodeOffset()) {
                                flag = true;
                            }
                        }
                    }
                }
                return super.visit(statement);
            }
        });
    }
    @Override
    public void run() {
        while (true) {
            findCallUnits();
            if(callUnits.isEmpty()){break;}
            CallUnit unit=callUnits.get(0);
            IBinding binding=unit.getFunctionDefinition().getDeclarator().getName().getBinding();
            UniqueID id=new UniqueID(binding);
            proceedUnits.add(id.toString());
            unit.inline();
            TestContext context=new OutputTester().run();
            if(context.getTestStatus().succeed()){
                success();
            }
        }
        proceedUnits.clear();
        while (true) {
            findMacroUnits();
            if(macroUnits.isEmpty()){break;}
            MacroUnit unit=macroUnits.get(0);
            proceedUnits.add(unit.getName());
            unit.inline();
            TestContext context=new OutputTester().run();
            if(context.getTestStatus().succeed()){
                success();
            }
        }
        proceedUnits.clear();
        while (true) {
            findDeclPairs();
            if(declPairs.isEmpty()){break;}
            DeclPair declPair=declPairs.get(0);
            proceedUnits.add(declPair.toString());
            declPair.inline();
            TestContext context=new OutputTester().run();
            if(context.getTestStatus().succeed()){
                success();
            }
        }
    }
}
