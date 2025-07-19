package DynamicReduce;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.JavaCore;
import java.util.HashMap;
import java.util.Map;

public class vt {
    public static void main(String[] args) {
        String sourceCode = "public class Example {\n int x;\n void method() {\n int x = 10; System.out.println(x); } }";

        // 创建 AST 解析器
        ASTParser parser = ASTParser.newParser(AST.JLS13);
        parser.setResolveBindings(true);
        parser.setSource(sourceCode.toCharArray());
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setEnvironment(null, null, null, true);
        parser.setUnitName("example.java");
        CompilationUnit cu = (CompilationUnit) (parser.createAST(null));

        // 存储变量定义和使用的节点
        Map<IBinding, ASTNode> variableDefinitions = new HashMap<>();
        Map<IBinding, ASTNode> variableUsages = new HashMap<>();

        // 访问者模式
        cu.accept(new ASTVisitor() {
            @Override
            public boolean visit(VariableDeclarationFragment node) {
                System.out.println("declare:"+node.getName());
                IBinding binding = node.resolveBinding();
                if (binding != null) {
                    // 成功解析
                    System.out.println("declare Binding Key: " + binding.getKey()+" "+ node.getName());
                } else {
                    // 解析失败
                    System.out.println("Binding is null for node: " + node.getName());
                }
                return super.visit(node);
            }

            @Override
            public boolean visit(SimpleName node) {
                System.out.println(node.getIdentifier());
                IBinding binding = node.resolveBinding();
                if (binding != null) {
                    // 成功解析
                    System.out.println("use Binding Key: " + binding.getKey()+" "+node.getIdentifier());
                } else {
                    // 解析失败
                    System.out.println("Binding is null for node: " + node.getIdentifier());
                }
                return super.visit(node);
            }
        });

        // 输出变量的定义和使用情况
        System.out.println("Variable Definitions:");
        variableDefinitions.forEach((binding, node) -> {
            int lineNumber = cu.getLineNumber(node.getStartPosition());
            System.out.println("Variable defined at line " + lineNumber + ", Node: " + node);
        });

        System.out.println("\nVariable Usages:");
        variableUsages.forEach((binding, node) -> {
            int lineNumber = cu.getLineNumber(node.getStartPosition());
            System.out.println("Variable used at line " + lineNumber + ", Node: " + node);
        });
    }
}