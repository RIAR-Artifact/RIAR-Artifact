package dynamicReduce.parser;

import dynamicReduce.parser.rewriter.Rewriter;
import dynamicReduce.utils.Logger;

import java.util.List;

public class ExpressionRewriter implements FileRewriter<ExpReduceUnit>{
    Rewriter rewriter;
    public ExpressionRewriter(Rewriter rewriter){
        this.rewriter=rewriter;
    }
    @Override
    public boolean reduceNodes(List<ExpReduceUnit> units) {
        if(units.isEmpty()){return false;}
        for (ExpReduceUnit unit:units){
            //Logger.log(unit.toString());
            //Logger.logFile(unit.toString());
            unit.apply(rewriter);
        }
        rewriter.finish();
        return true;
    }


}
