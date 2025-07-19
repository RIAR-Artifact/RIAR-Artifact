package DynamicReduce;

import org.eclipse.jdt.core.dom.*;

import java.util.*;

import static DynamicReduce.MainReducer.document;

public class ConstraintAnalyst {
    public static boolean findReturnConstraint=false;
    public static int getLineNumber(ASTNode node) {
        try {
            return document.getLineOfOffset(node.getStartPosition()) + 1;
        } catch (org.eclipse.jface.text.BadLocationException e) {
            e.printStackTrace();
        }
        return -1; // 默认返回-1表示错误
    }
    static ASTNode findParentStmtNode(ASTNode node){
        if (node==null){return null;}
        if (node instanceof Statement){
            return node;
        }
        if (node instanceof FieldDeclaration){
            return node;
        }
        if (node instanceof CompilationUnit){
            return null;
        }
        if (node.getParent() instanceof TypeDeclaration ){
            return node;
        }
        return findParentStmtNode(node.getParent());
    }

    static ASTNode findParentMethodNode(ASTNode node){
        if (node==null){return null;}
        if (node instanceof MethodDeclaration){
            return node;
        }
        if (node instanceof CompilationUnit){
            return null;
        }
        if (node.getParent() instanceof TypeDeclaration ){
            return node;
        }
        return findParentMethodNode(node.getParent());
    }
    public static Map<IBinding, ASTNode> variableDefinitions = new HashMap<>();
    public static Map<IMethodBinding,ASTNode> methodDefinitions =new HashMap<>();

    public static void resolveConstrains(){
        variableDefinitions.clear();
        methodDefinitions.clear();
        Constraint.v.clear();
        MainReducer.cu.accept(new DefinitionFinder());
        MainReducer.cu.accept(new ConstrainFinder());
    }
    private static class DefinitionFinder extends ASTVisitor{
        Stack<ASTNode> stmt=new Stack<>();
        @Override
        public void preVisit(ASTNode node){
            if(!(node instanceof Statement||node instanceof MethodDeclaration||node instanceof Initializer)){
                return;
            }
            if(!stmt.empty()){
                for (ASTNode upper:stmt
                     ) {

                    //System.out.println("stmt " + upper+ " at Line "+getLineNumber(upper)+
                    //        " contains " + node+ " at Line "+getLineNumber(node));
                    Constraint.v.addContainConstraint(upper,node);
                }
            }
            stmt.add(node);
        }
        @Override
        public void postVisit(ASTNode node){
            if(!(node instanceof Statement||node instanceof MethodDeclaration||node instanceof Initializer)||stmt.empty()){
                return;
            }
            if(stmt.peek()==node){
                stmt.pop();
            }
        }

        @Override
        public boolean visit(VariableDeclarationFragment node) {
            IBinding binding = node.resolveBinding();
            if (binding != null) {
                // 成功解析
                //System.out.println("declare Binding Key: " + binding.getKey()+" "+ node.getName());
                variableDefinitions.put(binding,findParentStmtNode(node));
            } else {
                // 解析失败
                System.out.println("Binding is null for node: " + node.getName());
            }
            return super.visit(node);
        }

        @Override
        public boolean visit(MethodDeclaration node) {
            IMethodBinding methodBinding = node.resolveBinding();

            if (methodBinding != null) {
                methodDefinitions.put(methodBinding,node);
            }
            return super.visit(node);
        }
    }
    private static class ConstrainFinder extends ASTVisitor {
        @Override
        public boolean visit(ReturnStatement node) {
            ASTNode methodNode=findParentMethodNode(node);
            if(methodNode!=null){
                if(findReturnConstraint){
                    Constraint.v.addConstraint(node,methodNode);
                }
            }
            return super.visit(node);
        }
        @Override
        public boolean visit(SimpleName node) {
            IBinding binding=node.resolveBinding();
            if(variableDefinitions.containsKey(binding)){
                ASTNode def=variableDefinitions.get(binding);
                ASTNode use=findParentStmtNode(node);
                if(def!=use){
                    Constraint.v.addConstraint(def,use);
                }
            }
            return super.visit(node);
        }
        @Override
        public boolean visit(MethodDeclaration node) {
            IMethodBinding methodBinding = node.resolveBinding();

            if (methodBinding != null) {
                Set<IMethodBinding> bindings=methodDefinitions.keySet();
                for (IMethodBinding overriddenMethod:bindings
                     ) {
                    if (!methodBinding.isConstructor() && methodBinding.overrides(overriddenMethod)) {
                        //System.out.println("Method " + methodBinding.getName()+ " at Line "+getLineNumber(node)+
                        //        " overrides " + overriddenMethod.getName()+ " at Line "+getLineNumber(methodDefinitions.get(overriddenMethod)));
                        Constraint.v.addConstraint(methodDefinitions.get(overriddenMethod),node);
                    }
                }
            }
            return super.visit(node);
        }
        @Override
        public boolean visit(MethodInvocation node) {
            IMethodBinding methodBinding = node.resolveMethodBinding();

            if (methodBinding != null) {
                Set<IMethodBinding> bindings=methodDefinitions.keySet();
                for (IMethodBinding defMethod:bindings
                ) {
                    if(Objects.equals(methodBinding.getKey(), defMethod.getKey())){
                        //System.out.println("Method " + methodBinding.getName()+ " at Line "+getLineNumber(methodDefinitions.get(defMethod))+
                        //        " is called by "+findParentStmtNode(node).toString()+" at Line "+getLineNumber(node));
                        Constraint.v.addConstraint(methodDefinitions.get(defMethod),findParentStmtNode(node));
                    }
                }
            }
            return super.visit(node);
        }
    }
}
