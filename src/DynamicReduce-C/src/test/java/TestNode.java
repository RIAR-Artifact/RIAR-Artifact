import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import dynamicReduce.parser.FileParser;
import dynamicReduce.parser.RewriterType;
import dynamicReduce.tester.InitialChecker;
import dynamicReduce.tester.TestRecord;
import dynamicReduce.utils.Logger;
import java.io.File;

public class TestNode {
    public static void main(String[] args){
        TestRecord.initialize("testcases"+ File.separator+"test1","Test.c","test.sh");
        if(InitialChecker.check()){
            Logger.log("Initial check passed");
        }
        FileParser fileParser=new FileParser(RewriterType.DEPENDENCY_REWRITER);
        final IASTNode[] node = new ASTNode[2];

        fileParser.getAST().accept(new ASTVisitor(true) {
            {
                shouldVisitNames = true;
            }
            @Override
            public int visit(IASTDeclaration name) {
                Logger.log("IASTDeclaration "+name.getRawSignature());
                return PROCESS_CONTINUE;
            }
        });
    }
}
