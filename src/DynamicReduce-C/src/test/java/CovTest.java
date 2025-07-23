import dynamicReduce.parser.FileParser;
import dynamicReduce.parser.RewriterType;
import dynamicReduce.reducer.CoverageHelper;
import dynamicReduce.tester.TestRecord;
import dynamicReduce.utils.Logger;

import java.io.File;
import java.util.List;

public class CovTest {
    public static void main(String[] args) throws Exception {
        TestRecord.initialize("testcases"+ File.separator+"test1","Test.c","test.sh");
        //TestRecord.v().newFileOutput(TestRecord.v().getLastSuccess());
        FileParser parser=new FileParser(RewriterType.DEPENDENCY_REWRITER);
        //new CoverageHelper(parser.getAST()).genCoverageFile();
        List<Integer> uncoveredLines=new CoverageHelper(parser.getAST()).getCoverageResolver().getUnexecutedLines();

        Logger.log("未被执行的行号：");
        for (int line : uncoveredLines) {
            Logger.log("Line " + line);
        }
    }
}
