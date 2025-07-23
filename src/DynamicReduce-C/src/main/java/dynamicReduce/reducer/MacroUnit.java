package dynamicReduce.reducer;

import org.eclipse.cdt.core.dom.ast.*;
import dynamicReduce.parser.rewriter.Rewriter;
import dynamicReduce.tester.TestRecord;
import dynamicReduce.utils.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacroUnit {

    private String targetText;
    private String replaceText;
    private IASTNode toDelete;
    public MacroUnit(IASTDeclaration declaration,IASTName typedefName){
        toDelete=declaration;
        IBinding binding=typedefName.resolveBinding();
        ITypedef typedefBinding=(ITypedef) binding;
        targetText =typedefName.toString();

        IType actualType = typedefBinding.getType();

        // 5. 打印实际类型信息
        if (actualType instanceof IBasicType basicType) {
            replaceText= basicType.toString();
        } else if (actualType instanceof ICompositeType compositeType) {
            replaceText="struct "+compositeType.toString();
        } else {
            replaceText= actualType.toString();
        }
    }
    public void inline(){
        Logger.log("inlining "+targetText);
        String originalText=TestRecord.v().getLastSuccess();
        int startPos=toDelete.getFileLocation().getNodeOffset()+toDelete.getFileLocation().getNodeLength();
        Rewriter rewriter=new Rewriter(originalText);

        Pattern usagePattern = Pattern.compile("\\b" + Pattern.quote(targetText) + "\\b");
        Matcher usageMatcher = usagePattern.matcher(originalText);

        while (usageMatcher.find()) {
            int start = usageMatcher.start();
            int end = usageMatcher.end();
            if(start<startPos){continue;}
            rewriter.replacePhase(start,end,replaceText);
        }
        rewriter.removeNode(toDelete);
        rewriter.finish();
    }
    public String getName(){
        return targetText;
    }
}
