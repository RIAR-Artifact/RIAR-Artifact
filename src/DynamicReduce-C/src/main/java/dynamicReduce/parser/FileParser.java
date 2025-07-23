package dynamicReduce.parser;

import dynamicReduce.parser.rewriter.Rewriter;
import dynamicReduce.tester.TestRecord;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.gnu.c.GCCLanguage;
import org.eclipse.cdt.core.model.ILanguage;
import org.eclipse.cdt.core.parser.*;
import org.eclipse.core.runtime.CoreException;


import java.util.HashMap;

public class FileParser {

    IASTTranslationUnit ast;
    FileRewriter fileRewriter;
    RewriterType rewriterType;

    public FileParser(RewriterType rewriterType){
        this.rewriterType=rewriterType;
        refresh();
    }


    public void refresh(){
        FileContent fileContent = FileContent.create("", TestRecord.v().getLastSuccess().toCharArray());
        ScannerInfo scannerInfo = new ScannerInfo(new HashMap<>(), new String[0]);
        IncludeFileContentProvider includeProvider = IncludeFileContentProvider.getEmptyFilesProvider();
        IParserLogService log = new DefaultLogService();
        ILanguage language = GCCLanguage.getDefault();

        try {
             ast = language.getASTTranslationUnit(fileContent,scannerInfo,includeProvider, null,ILanguage.OPTION_IS_SOURCE_UNIT,log);
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
        Rewriter rewriter=new Rewriter(TestRecord.v().getLastSuccess());
        switch (rewriterType){
            case DEPENDENCY_REWRITER -> {
                fileRewriter=new DependencyRewriter(rewriter, ast);}
            case EXPRESSION_REWRITER -> {
                fileRewriter=new ExpressionRewriter(rewriter);new DependencyGraph(ast);
            }
            case REVERSED_DEPENDENCY_REWRITER -> {
                fileRewriter=new ReversedDependencyRewriter(rewriter,ast);
            }
            default -> {
                fileRewriter=new DependencyRewriter(rewriter, ast);}
        }
    }
    public IASTTranslationUnit getAST(){
        return ast;
    }

    public FileRewriter getFileRewriter() {
        return fileRewriter;
    }
}
