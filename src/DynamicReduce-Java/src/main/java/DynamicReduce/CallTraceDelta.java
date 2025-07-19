package DynamicReduce;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static DynamicReduce.MainReducer.*;

public class CallTraceDelta {

    public static boolean reverse=false;
    public static boolean randomOrder=false;
    public static boolean enabled=true;
    public static boolean linearOrder=false;
    static List<String> methods;
    static List<String> allNames;
    static Map<String,ASTNode> methodNodes;
    static List<ASTNode> methodTracedNodes;
    static String prefix = "Entering method: ";
    static List<ASTNode> methodStmt;
    static List<ASTNode> fixedDelete;

    public static void getFunctionTrace(){
        allNames=new ArrayList<>();
        methodNodes=new HashMap<>();
        methodTracedNodes=new ArrayList<>();
        methodStmt=new ArrayList<>();
        fixedDelete=new ArrayList<>();
        MainReducer.cu.accept(new MethodPrintVisitor());
        TextEdit edits = MainReducer.rewriter.rewriteAST(document, null);
        UndoEdit undoEdit=null;
        try {
            undoEdit=edits.apply(document);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        MainReducer.tempOutput();
        MainReducer.testOutput(false);
        getOutput();
        try {
            if(undoEdit!=null){undoEdit.apply(document);
            }else {
                System.out.println("failed to undo edit");
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        MainReducer.decentWorkingDir();
        try {
            String sourceCode = Files.readString(Paths.get(workingDir+targetClass+".java"));
            document=new Document(sourceCode);
            rewriter= ASTRewrite.create(cu.getAST());
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void resolveTracedNodes(List<String> methodOrder){
        for (String method:methodOrder
        ) {
            if(!methodNodes.containsKey(method)){continue;}
            methodTracedNodes.add(methodNodes.get(method));
        }
    }

    private static void getOutput(){
        String outFile = workingDir+"out1.txt";
        try {
            List<String> lines = Files.readAllLines(Paths.get(outFile));
            methods = lines.stream()
                    .filter(line -> line.startsWith(prefix))
                    .map(line -> line.substring(prefix.length())).distinct().toList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static class MethodPrintVisitor extends ASTVisitor {
        @Override
        public boolean visit(MethodDeclaration node){
            String name=node.resolveBinding().getKey();
            allNames.add(name);
            methodNodes.put(name,node);
            AST ast=MainReducer.cu.getAST();
            MethodInvocation printMethod = ast.newMethodInvocation();
            QualifiedName receiver = ast.newQualifiedName(ast.newSimpleName("System"), ast.newSimpleName("out"));
            printMethod.setExpression(receiver);
            printMethod.setName(ast.newSimpleName("println")); // 设置方法名为 println

            StringLiteral stringLiteral = ast.newStringLiteral();
            stringLiteral.setLiteralValue(prefix + name+"\n");
            printMethod.arguments().add(stringLiteral); // 添加参数

            ASTNode printStatement = ast.newExpressionStatement(printMethod);
            ListRewrite listRewrite = MainReducer.rewriter.getListRewrite(node.getBody(), Block.STATEMENTS_PROPERTY);
            listRewrite.insertFirst(printStatement, null);

            return true;
        }
        @Override
        public boolean visit(Initializer node) {

            String name=node.getClass().getName()+"<Initializer>";
            allNames.add(name);
            methodNodes.put(name,node);
            AST ast=MainReducer.cu.getAST();
            MethodInvocation printMethod = ast.newMethodInvocation();
            QualifiedName receiver = ast.newQualifiedName(ast.newSimpleName("System"), ast.newSimpleName("out"));
            printMethod.setExpression(receiver);
            printMethod.setName(ast.newSimpleName("println")); // 设置方法名为 println

            StringLiteral stringLiteral = ast.newStringLiteral();
            stringLiteral.setLiteralValue(prefix +name+"\n");
            printMethod.arguments().add(stringLiteral); // 添加参数

            ASTNode printStatement = ast.newExpressionStatement(printMethod);
            ListRewrite listRewrite = MainReducer.rewriter.getListRewrite(node.getBody(), Block.STATEMENTS_PROPERTY);
            listRewrite.insertFirst(printStatement, null);

            return true;
        }
    }

    public static class MethodFinder extends ASTVisitor {
        @Override
        public boolean visit(MethodDeclaration node){
            String name=node.resolveBinding().getKey();
            allNames.add(name);
            methodNodes.put(name,node);
            return true;
        }
        @Override
        public boolean visit(Initializer node) {
            String name=node.getClass().getName()+"<Initializer>";
            allNames.add(name);
            methodNodes.put(name,node);
            return true;
        }
    }
    public static void methodDelta(){
        fixedDelete.clear();
        for (int i=0;i<methodTracedNodes.size();i++
             ) {
            if(i==methodTracedNodes.size()-1){
                GlobalDelta.outputFile=true;
            }
            methodStmt.clear();
            //methodStmt.add(methodTracedNodes.get(i));
            methodTracedNodes.get(i).accept(new FindEle());
            GlobalDelta.additionalNodes.clear();
            GlobalDelta.additionalNodes.addAll(fixedDelete);
            fixedDelete.addAll(GlobalDelta.DeltaDebugging(methodStmt));
        }
    }
    public static void run(){
        MainReducer.analyseCurrent();
        if(enabled){testCounter.startEffectCounter(0);}
        getFunctionTrace();
        //List<String> initialMethodOrder = new ArrayList<>(methods);
        List<String> initialMethodOrder = new ArrayList<>();
        if(enabled) {
            for (String method : methods) {
                if (reverse) {
                    initialMethodOrder.add(0, method);
                } else {
                    initialMethodOrder.add(method);
                }
            }
        }
        for (String name:allNames){
            if(!initialMethodOrder.contains(name)){
                initialMethodOrder.add(name);
            }
        }
        if(randomOrder){
            Collections.shuffle(initialMethodOrder);
        }
        if(enabled){testCounter.endEffectCounter(0);}

        for (String s : initialMethodOrder) {
            System.out.println("working in method "+s);
            MainReducer.analyseCurrent();
            methodNodes.clear();
            MainReducer.cu.accept(new MethodFinder());
            if (!methodNodes.containsKey(s)) {
                System.out.println("Method not found" + s);
                continue;
            }
            ASTNode methodNode = methodNodes.get(s);
            if (!GlobalDelta.DeltaDebugging(new ArrayList<ASTNode>(Collections.singleton(methodNode))).isEmpty()) {
                System.out.println("deleted whole method at once: " + s);
                continue;
            }
            methodStmt.clear();
            methodNode.accept(new FindEle());
            int step = methodStmt.size() / 2;
            GlobalDelta.startPin=0;


            if(linearOrder){testCounter.startEffectCounter(1);}
            ExecutionSorter executionCounter=new ExecutionSorter(methodStmt);
            if(linearOrder){testCounter.endEffectCounter(1);}
            while (step > 0) {
                if(linearOrder){
                    methodStmt=executionCounter.rearrange(methodStmt);
                }
                step = GlobalDelta.DeltaDebugging1StepWithPin(methodStmt, step);
                if (step > 0) {
                    MainReducer.analyseCurrent();
                    methodNodes.clear();
                    MainReducer.cu.accept(new MethodFinder());
                    methodNode = methodNodes.get(s);
                    methodStmt.clear();
                    methodNode.accept(new FindEle());
                }
            }
        }
    }

    private static class FindEle extends ASTVisitor {
        @Override
        public void preVisit(ASTNode node) {
            if(node instanceof Statement statement){
                if(node.getParent() instanceof MethodDeclaration){return;}
                if(node instanceof Block&& ((Block) node).statements().isEmpty()){
                    return;
                }
                if(!GlobalDelta.statementFilter(statement)
                ){
                    return;
                }
                System.out.println("stmt "+ methodStmt.size()+": "+node);
                methodStmt.add(node);
            }
        }
    }

    public static void main(String[] args) {
        MainReducer.init();
        DeclarationFiller.start();
        MainReducer.analyseCurrent();
        getFunctionTrace();resolveTracedNodes(methods);
        methodDelta();
    }
}
