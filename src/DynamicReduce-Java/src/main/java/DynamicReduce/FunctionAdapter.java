package DynamicReduce;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import java.util.HashSet;
import java.util.Set;

public class FunctionAdapter {
    public static Set<MethodDeclaration> methodsToRemove = new HashSet<>();
    public static void insertPrint(){
        MainReducer.cu.accept(new MethodPrintVisitor());
    }
    private static Set<String> foundMethod=new HashSet<>();

    public static void undoInsertPrint(){
        MainReducer.cu.accept(new MethodCleaner());
    }
    public static class MethodPrintVisitor extends ASTVisitor{
        @Override
        public boolean visit(MethodDeclaration node){

            AST ast=MainReducer.cu.getAST();
            MethodInvocation printMethod = ast.newMethodInvocation();
            QualifiedName receiver = ast.newQualifiedName(ast.newSimpleName("System"), ast.newSimpleName("out"));
            printMethod.setExpression(receiver);
            printMethod.setName(ast.newSimpleName("println")); // 设置方法名为 println

            StringLiteral stringLiteral = ast.newStringLiteral();
            stringLiteral.setLiteralValue("Entering method: " + node.getName());
            printMethod.arguments().add(stringLiteral); // 添加参数

            ASTNode printStatement = ast.newExpressionStatement(printMethod);
            ListRewrite listRewrite = MainReducer.rewriter.getListRewrite(node.getBody(), Block.STATEMENTS_PROPERTY);
            listRewrite.insertFirst(printStatement, null);

            MainReducer.cu.rewrite(MainReducer.document, null);

            return true;
        }
    }
    private static class MethodCleaner extends ASTVisitor{
        @Override
        public boolean visit(MethodDeclaration method) {
            Block body = method.getBody();
            if (body != null) {
                body.statements().remove(0); // 添加打印语句
            }
            return true;
        }
    }
}
