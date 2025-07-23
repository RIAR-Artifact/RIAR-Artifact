package dynamicReduce.reducer;

import dynamicReduce.parser.FileParser;
import dynamicReduce.parser.RewriterType;
import dynamicReduce.utils.Logger;
import dynamicReduce.utils.Record;
import dynamicReduce.utils.RecordType;

public class ReducerRunner {
    Reducer reducer;
    ReducerType reducerType;
    RecordType recordType;
    public ReducerRunner(ReducerType reducerType,RecordType recordType){
        this.reducerType=reducerType;
        this.recordType=recordType;
        switch (reducerType){
            case COVERAGE_REDUCER -> {
                FileParser parser=new FileParser(RewriterType.REVERSED_DEPENDENCY_REWRITER);
                reducer=new CoverageReducer(parser);
            }
            case GLOBAL_REDUCER -> {
                FileParser parser=new FileParser(RewriterType.DEPENDENCY_REWRITER);
                reducer=new GlobalReducer(parser);
            }
            case H_GLOBAL_REDUCER -> {
                FileParser parser=new FileParser(RewriterType.DEPENDENCY_REWRITER);
                reducer=new HGlobalReducer(parser);
            }
            case CALL_TRACE_REDUCER -> {
                FileParser parser=new FileParser(RewriterType.DEPENDENCY_REWRITER);
                reducer=new CallTraceReducer(parser);
            }
            case ORIGIN_ORDER_REDUCER -> {
                FileParser parser=new FileParser(RewriterType.DEPENDENCY_REWRITER);
                reducer=new OriginOrderReducer(parser);
            }
            case RANDOM_TRACE_REDUCER -> {
                FileParser parser=new FileParser(RewriterType.DEPENDENCY_REWRITER);
                reducer=new RandomTraceReducer(parser);
            }
            case EXPRESSION_REDUCER -> {
                FileParser parser=new FileParser(RewriterType.EXPRESSION_REWRITER);
                reducer=new ExpressionReducer(parser);
            }
            case INLINE_REDUCER -> {
                FileParser parser=new FileParser(RewriterType.DEPENDENCY_REWRITER);
                reducer=new InlineReducer(parser);
            }
            default -> {
                Logger.log("Not implemented reducer: "+reducerType);
                return;
            }
        }
        run();
    }

    void run(){
        Record.v().startRecord(recordType);
        Logger.log("--- "+reducerType.name()+" start---");
        reducer.run();
        Logger.log("--- "+reducerType.name()+" end---");
        Record.v().endRecord(recordType);
    }
}
