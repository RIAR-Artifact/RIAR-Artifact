package DynamicReduce;

import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import java.util.ArrayList;
import java.util.List;

public class InlineAnalyst {
    public static class LocalVariable{
        public String variable;
        public String variableKey;
        public LocalVariable(String variable,String variableKey){
            this.variable=variable;
            this.variableKey=variableKey;
        }
    }
    public static class MethodCallPoint{
        public String targetMethod;
        public String targetMethodKey;
        public String sourceMethodKey;
        public ASTNode callStatement;
        public MethodInvocation methodInvocation;
        public MethodCallPoint(String targetMethod,String targetMethodKey,String sourceMethodKey,ASTNode callStatement,MethodInvocation methodInvocation){
            this.targetMethod=targetMethod;
            this.targetMethodKey=targetMethodKey;
            this.sourceMethodKey=sourceMethodKey;
            this.callStatement=callStatement;
            this.methodInvocation=methodInvocation;
        }
    }
    public static boolean inlineWorked=false;
    static List<String> methodProcessed=new ArrayList<>();
    static List<String> methods=new ArrayList<>();
    static List<MethodCallPoint> methodCalls=new ArrayList<>();
    public static String formatCode(String sourceCode) {
        CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(null);
        TextEdit edit = codeFormatter.format(CodeFormatter.K_COMPILATION_UNIT, sourceCode, 0, sourceCode.length(), 0, null);

        if (edit != null) {
            Document document = new Document(sourceCode);
            try {
                edit.apply(document);
                return document.get();
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        return sourceCode;
    }
    static void findCalls(){
        methodCalls.clear();
        methods.clear();
        MainReducer.cu.accept(new ASTVisitor() {
            @Override
            public boolean visit(MethodInvocation node) {
                ASTNode parent=node;
                while (!(parent instanceof Statement)){
                    parent=parent.getParent();
                }
                IMethodBinding methodBinding = node.resolveMethodBinding();
                if (methodBinding != null) {
                    methodCalls.add(new MethodCallPoint(node.getName().getIdentifier(),methodBinding.getKey(), currentMethod, parent, node));
                }else {
                    System.out.println("无法解析方法绑定: " + node.getName().getIdentifier());
                }
                return super.visit(node);
            }
            String currentMethod="";
            @Override
            public boolean visit(MethodDeclaration node) {

                IMethodBinding binding = node.resolveBinding();
                if (binding != null) {
                    methods.add(binding.getKey());
                    currentMethod=binding.getKey();
                }
                return super.visit(node);
            }
        });
        List<MethodCallPoint> toDelete=new ArrayList<>();
        for (MethodCallPoint point:methodCalls
        ) {
            //System.out.println("Method call: "+point.sourceMethodKey+" calls "+point.targetMethod+"\n"+point.callStatement+"\n"+point.methodInvocation);
            if(!methods.contains(point.targetMethodKey)){
                toDelete.add(point);
            }
        }
        for (MethodCallPoint point:toDelete
             ) {
            methodCalls.remove(point);
        }
    }

    public static void run(){
        boolean methodFlag=true;
        while (methodFlag){
            MainReducer.analyseCurrent();
            findCalls();
            methodFlag=false;
            for (String methodKey:methods
                 ) {
                if(methodProcessed.contains(methodKey)){continue;}
                int count=0;MethodCallPoint deletePoint=null;
                for (MethodCallPoint point:methodCalls
                ) {
                    if(point.targetMethodKey.equals(methodKey)){
                        count++;
                        deletePoint=point;
                    }
                }
                if(count == 1){
                    System.out.println("Start inline method "+methodKey);
                    methodFlag=true;
                    methodProcessed.add(methodKey);
                    InlineExecutor inline=new InlineExecutor(deletePoint);
                    inline.inline();
                    MainReducer.tempOutput();
                    boolean res=MainReducer.testOutput(false);
                    inline.undoInline();
                    if(res){
                        inlineWorked=true;
                        System.out.println("inline method success "+methodKey);
                    }else {
                        System.out.println("inline method failed "+methodKey);
                        MainReducer.decentWorkingDir();
                    }
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        MainReducer.init();

        run();
        //System.out.println("修改后的代码:\n" + formatCode(MainReducer.document.get()));
    }
}
