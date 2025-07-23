package dynamicReduce.reducer;

import org.eclipse.cdt.core.dom.ast.*;
import dynamicReduce.parser.rewriter.Rewriter;
import dynamicReduce.tester.CoverageResolver;
import dynamicReduce.tester.CoverageTester;
import dynamicReduce.tester.TestContext;
import dynamicReduce.tester.TestRecord;

public class CoverageHelper {
    IASTTranslationUnit ast;
    static final String defGcov="void __gcov_reset(void);void __gcov_dump(void);\n";
    static final String useGcov="__gcov_dump();__gcov_reset();";

    public CoverageHelper(IASTTranslationUnit ast) {
        this.ast = ast;
    }

    public CoverageResolver getCoverageResolver(){
        TestRecord.v().newFileOutput(TestRecord.v().getLastSuccess());
        TestContext testContext=new CoverageTester().run();
        CoverageResolver coverageResolver=testContext.getCoverageResolver();
        if(coverageResolver!=null&&coverageResolver.canResolve()){
            return coverageResolver;
        }
        genCoverageFile();
        testContext=new CoverageTester().run();
        coverageResolver=testContext.getCoverageResolver();
        if(coverageResolver!=null&&coverageResolver.canResolve()){
            coverageResolver.setShift(-1);
            return coverageResolver;
        }
        return null;
    }
    private void genCoverageFile(){
        Rewriter rewriter=new Rewriter(TestRecord.v().getLastSuccess());
        rewriter.insertAt(defGcov,0);
        ast.accept(new ASTVisitor(true) {

            @Override
            public int visit(IASTStatement statement) {
                if(statement.getParent() instanceof IASTCompoundStatement) {
                    rewriter.insertBefore(useGcov, statement);
                }
                return super.visit(statement);
            }
        });
        rewriter.finish();
    }
}
