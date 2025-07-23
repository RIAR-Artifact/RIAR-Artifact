import java.util.HashMap;
import java.util.Map;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.*;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.internal.core.dom.rewrite.ASTModification;
import org.eclipse.cdt.internal.core.dom.rewrite.ASTModificationStore;
import org.eclipse.cdt.internal.core.dom.rewrite.astwriter.ASTWriter;
import org.eclipse.cdt.internal.core.dom.rewrite.commenthandler.NodeCommentMap;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.internal.core.dom.rewrite.ASTRewriteAnalyzer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

public class FunctionalTest {
    public static void main(String[] args) throws Exception {
        String code =
                "int main() {\n" +
                        "    int a = 1;\n" +
                        "    int b = 2;\n" +
                        "    return 0;\n" +
                        "}";

        // 创建源代码内容
        FileContent fileContent = FileContent.create("test.c", code.toCharArray());

        // 初始化 CDT 的解析器工具
        Map<String, String> definedSymbols = new HashMap<>();
        IScannerInfo scannerInfo = new ScannerInfo(definedSymbols);
        IncludeFileContentProvider emptyIncludes = IncludeFileContentProvider.getEmptyFilesProvider();
        IIndex index = null;  // 如果不使用索引，可以设置为 null
        int opts = ILanguage.OPTION_IS_SOURCE_UNIT;
        IParserLogService log = new DefaultLogService();

        // 解析 C 代码为 IASTTranslationUnit
        IASTTranslationUnit tu = GCCLanguage.getDefault().getASTTranslationUnit(
                fileContent, scannerInfo, emptyIncludes, index, opts, log);

        // 获取函数体
        IASTFunctionDefinition func = (IASTFunctionDefinition) tu.getDeclarations()[0];
        IASTCompoundStatement body = (IASTCompoundStatement) func.getBody();

        // 获取并删除第一个语句
        IASTStatement stmtToRemove = body.getStatements()[0];
        ASTRewrite rewriter = ASTRewrite.create(tu);
        rewriter.remove(stmtToRemove, null);
        Document document = new Document(code);  // 用原始代码创建一个文档对象

        // 获取修改后的 AST
        String modifiedCode = new ASTWriter().write(tu);

        // 输出修改后的代码
        System.out.println("------ 修改后的代码 ------\n" + modifiedCode);
    }

}
