package DynamicReduce;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;

import java.util.ArrayList;
import java.util.List;

public class InlineExecutor {
    InlineAnalyst.MethodCallPoint point;
    public InlineExecutor(InlineAnalyst.MethodCallPoint point){
        this.point=point;
    }
    MethodDeclaration getMethod(String methodKey){
        final MethodDeclaration[] res = new MethodDeclaration[1];
        MainReducer.cu.accept(new ASTVisitor() {
            @Override
            public boolean visit(MethodDeclaration node) {
                IMethodBinding binding = node.resolveBinding();
                if (binding != null) {
                    if(binding.getKey().equals(methodKey)){
                        res[0] =node;
                        return false;
                    }
                }
                return super.visit(node);
            }
        });
        return res[0];
    }
    List<InlineAnalyst.LocalVariable> getLocalVariables(ASTNode method){
        List<InlineAnalyst.LocalVariable> vars=new ArrayList<>();
        method.accept(new ASTVisitor() {
            @Override
            public boolean visit(VariableDeclarationFragment node) {
                ASTNode parent = node.getParent();
                System.out.println("局部变量: " + node.getName());
                IVariableBinding binding = node.resolveBinding();
                if (binding != null) {
                    String uniqueKey = binding.getKey(); // 获取唯一标识
                    System.out.println("变量: " + node.getName().getIdentifier() + "，唯一标识: " + uniqueKey);
                    vars.add(new InlineAnalyst.LocalVariable(node.getName().getIdentifier(),uniqueKey));
                } else {
                    System.out.println("无法解析变量绑定: " + node.getName().getIdentifier());
                }
                return super.visit(node);
            }
        });
        return vars;
    }

    void renameVariable(InlineAnalyst.LocalVariable var, String method){
        MainReducer.cu.accept(new ASTVisitor() {
            @Override
            public boolean visit(SimpleName node) {
                IBinding binding = node.resolveBinding();
                if (binding instanceof IVariableBinding varBinding) {
                    if (var.variableKey.equals(varBinding.getKey())) { // 确保是目标变量
                        SimpleName newName = MainReducer.cu.getAST().newSimpleName(var.variable+"_"+method);
                        MainReducer.rewriter.replace(node, newName, null);
                    }
                }
                return super.visit(node);
            }
        });
    }
    void renameParameter(String varKey,ASTNode newNode){
        MainReducer.cu.accept(new ASTVisitor() {
            @Override
            public boolean visit(SimpleName node) {
                IBinding binding = node.resolveBinding();
                if (binding instanceof IVariableBinding varBinding) {
                    if (varKey.equals(varBinding.getKey())) { // 确保是目标变量
                        System.out.println("replace "+node+" with "+newNode);
                        MainReducer.rewriter.replace(node, newNode, null);
                    }
                }
                return super.visit(node);
            }
        });
    }
    void inlineBody(){
        MethodDeclaration targetMethod=getMethod(point.targetMethodKey);
        Block targetBlock=targetMethod.getBody();
        if (targetBlock == null) return;
        if(!(point.callStatement.getParent() instanceof Block)){
            Block newBlock = MainReducer.cu.getAST().newBlock();

            // 将原来的语句添加到 Block 中
            newBlock.statements().add(ASTNode.copySubtree(MainReducer.cu.getAST(), point.callStatement));

            // 用新的 Block 替换原来的语句
            MainReducer.rewriter.replace(point.callStatement, newBlock, null);
            point.callStatement=(ASTNode) newBlock.statements().get(0);
        }
        ListRewrite listRewrite = MainReducer.rewriter.getListRewrite(point.callStatement.getParent(), Block.STATEMENTS_PROPERTY);
        //listRewrite.insertBefore(targetBlock,point.callStatement,null);
        List<ASTNode> statements=targetBlock.statements();
        for (ASTNode statement:statements
        ) {
            listRewrite.insertBefore(statement,point.callStatement,null);
        }
        MainReducer.rewriter.remove(targetMethod,null);
        Type returnType = targetMethod.getReturnType2();
        if (returnType.isPrimitiveType()) {
            PrimitiveType primitiveType = (PrimitiveType) returnType;
            if (primitiveType.getPrimitiveTypeCode() == PrimitiveType.VOID) {
                MainReducer.rewriter.remove(point.callStatement,null);
            }
        }
    }
    void renameVariablesAndParameters(){
        MethodDeclaration sourceMethod=getMethod(point.sourceMethodKey);
        List<InlineAnalyst.LocalVariable> sourceVars=getLocalVariables(sourceMethod);
        MethodDeclaration targetMethod=getMethod(point.targetMethodKey);
        List<InlineAnalyst.LocalVariable> targetVars=getLocalVariables(targetMethod);
        for (InlineAnalyst.LocalVariable targetVar:targetVars
        ) {
            for (InlineAnalyst.LocalVariable sourceVar:sourceVars
            ) {
                if(targetVar.variable.equals(sourceVar.variable)){
                    renameVariable(targetVar,point.targetMethod);
                }
            }
        }
        //处理参数
        List<String> parameters=new ArrayList<>();
        for (Object obj : targetMethod.parameters()) {
            SingleVariableDeclaration param = (SingleVariableDeclaration) obj;
            IVariableBinding binding = param.resolveBinding();
            if (binding != null) {
                parameters.add(binding.getKey());
            }else {
                System.out.println("无法解析参数: "+param.getName().getIdentifier());
            }
        }
        List<Expression> arguments = point.methodInvocation.arguments();
        int i=0;
        for (Expression arg : arguments) {
            renameParameter(parameters.get(i),arg);
            i++;
        }
    }
    boolean inLoop(ASTNode node){

        ASTNode parent = node.getParent();
        while (parent != null && !(parent instanceof MethodDeclaration)) {
            if (parent instanceof ForStatement||parent instanceof WhileStatement||parent instanceof EnhancedForStatement
                    ||parent instanceof DoStatement||parent instanceof SwitchStatement) {  // 判断是否在循环内
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }
    void clearReturn(){
        MethodDeclaration targetMethod=getMethod(point.targetMethodKey);
        Type returnType = targetMethod.getReturnType2();
        boolean voidReturn=false;
        if (returnType.isPrimitiveType()) {
            PrimitiveType primitiveType = (PrimitiveType) returnType;
            if (primitiveType.getPrimitiveTypeCode() == PrimitiveType.VOID) {
                System.out.println("voidReturn");
                voidReturn=true;
            }
        }
        final int[] returnNum = {0};
        targetMethod.accept(new ASTVisitor() {
            @Override
            public boolean visit(ReturnStatement node) {
                returnNum[0]++;
                return super.visit(node);
            }
        });
        final ASTNode[] returnNode = new ASTNode[1];
        if(returnNum[0]<=1){
            boolean finalVoidReturn = voidReturn;
            targetMethod.accept(new ASTVisitor() {
                @Override
                public boolean visit(ReturnStatement node) {
                    if(!finalVoidReturn){
                        returnNode[0] =node.getExpression();
                    }
                    if(inLoop(node)){
                        BreakStatement breakStatement = MainReducer.cu.getAST().newBreakStatement();
                        MainReducer.rewriter.replace(node, breakStatement, null);
                    }else {
                        MainReducer.rewriter.remove(node, null);
                        if(node.getParent() instanceof Block block){
                            block.statements().remove(node);
                        }
                    }
                    return super.visit(node);
                }
            });
        }else {
            if (voidReturn){
                targetMethod.accept(new ASTVisitor() {
                    @Override
                    public boolean visit(ReturnStatement node) {
                        if(inLoop(node)){
                            BreakStatement breakStatement = MainReducer.cu.getAST().newBreakStatement();
                            MainReducer.rewriter.replace(node, breakStatement, null);
                        }else {
                            MainReducer.rewriter.remove(node, null);
                            if(node.getParent() instanceof Block block){
                                block.statements().remove(node);
                            }
                        }
                        return super.visit(node);
                    }
                });
            }else {
                VariableDeclarationFragment fragment = MainReducer.cu.getAST().newVariableDeclarationFragment();
                SimpleName resVar=MainReducer.cu.getAST().newSimpleName("res_"+point.targetMethod);
                fragment.setName(resVar); // 变量名
                fragment.setInitializer(null); // 初始化为空
                VariableDeclarationStatement varDecl = MainReducer.cu.getAST().newVariableDeclarationStatement(fragment);
                varDecl.setType((Type) ASTNode.copySubtree(MainReducer.cu.getAST(), returnType));
                Block methodBody = targetMethod.getBody();
                if (methodBody != null) {
                    ListRewrite listRewrite = MainReducer.rewriter.getListRewrite(methodBody, Block.STATEMENTS_PROPERTY);
                    listRewrite.insertFirst(varDecl, null); // 插入到方法体最前面
                }

                returnNode[0] =resVar;
                targetMethod.accept(new ASTVisitor() {
                    @Override
                    public boolean visit(ReturnStatement node) {
                        if(inLoop(node)){
                            BreakStatement breakStatement = MainReducer.cu.getAST().newBreakStatement();
                            if(!(node.getParent() instanceof Block)){
                                Block newBlock = MainReducer.cu.getAST().newBlock();

                                // 将原来的语句添加到 Block 中
                                newBlock.statements().add(ASTNode.copySubtree(MainReducer.cu.getAST(), node));

                                // 用新的 Block 替换原来的语句
                                MainReducer.rewriter.replace(node, newBlock, null);
                                node= (ReturnStatement) newBlock.statements().get(0);
                            }
                            ListRewrite listRewrite = MainReducer.rewriter.getListRewrite(node.getParent(), Block.STATEMENTS_PROPERTY);
                            listRewrite.insertBefore(breakStatement,node,null);
                        }
                        Assignment assignment = MainReducer.cu.getAST().newAssignment();
                        assignment.setLeftHandSide(resVar);
                        assignment.setRightHandSide(node.getExpression());
                        MainReducer.rewriter.replace(node,assignment,null);
                        if(node.getParent() instanceof Block block){
                            for (int i = 0; i < block.statements().size(); i++) {
                                if (block.statements().get(i).equals(node)){
                                    block.statements().set(i,assignment);
                                    break;
                                }
                            }
                        }
                        return super.visit(node);
                    }
                });
            }
        }
        if(!voidReturn){
            MainReducer.rewriter.replace(point.methodInvocation,returnNode[0],null);
        }
    }
    UndoEdit undoEdit;
    public void inline(){
        renameVariablesAndParameters();
        clearReturn();
        inlineBody();
        try {
            TextEdit textEdit= MainReducer.rewriter.rewriteAST(MainReducer.document, null);
            undoEdit=textEdit.apply(MainReducer.document);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }
    public void undoInline(){
        if (undoEdit == null) {
            return;
        }
        try {
            undoEdit.apply(MainReducer.document);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
    }
}
