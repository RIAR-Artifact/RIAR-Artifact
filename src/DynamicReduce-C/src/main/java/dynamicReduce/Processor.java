package dynamicReduce;

import dynamicReduce.reducer.CallTraceReducer;
import dynamicReduce.reducer.ExpressionReducer;
import dynamicReduce.reducer.ReducerRunner;
import dynamicReduce.reducer.ReducerType;
import dynamicReduce.tester.InitialChecker;
import dynamicReduce.tester.TestRecord;
import dynamicReduce.utils.Logger;
import dynamicReduce.utils.Record;
import dynamicReduce.utils.RecordType;

import java.util.Objects;

public class Processor {
    boolean enableCoverage=true;
    boolean callTraceRandom=false;
    boolean enableCallTrace=true;
    public Processor(String baseDir,String targetFileName,String testBashName){
        TestRecord.initialize(baseDir,targetFileName,testBashName);
    }
    public void run(){
        boolean flag=false;
        for (int i = 0; i < 5; i++) {
            if(InitialChecker.check()){
                Logger.log("Initial check passed");
                TestRecord.v().success();
                flag=true;
                break;
            }
        }
        if(!flag){
            Logger.log("Initial check failed");
            return;
        }
        ReducerRunner reducerRunner;
        Record.v().startRecord(RecordType.TOTAL);
        /*reducerRunner = new ReducerRunner(ReducerType.COVERAGE_REDUCER, RecordType.COVERAGE_REDUCER);
        */
        String lastSuccess="";
        while (!Objects.equals(lastSuccess, TestRecord.v().getLastSuccess())){
            lastSuccess=TestRecord.v().getLastSuccess();
            if (enableCoverage) {
                reducerRunner = new ReducerRunner(ReducerType.COVERAGE_REDUCER, RecordType.COVERAGE_REDUCER);
            }
            if(enableCallTrace){
                if (callTraceRandom) {
                    reducerRunner = new ReducerRunner(ReducerType.RANDOM_TRACE_REDUCER, RecordType.RANDOM_TRACE_REDUCER);
                } else {
                    reducerRunner = new ReducerRunner(ReducerType.CALL_TRACE_REDUCER, RecordType.CALL_TRACE_REDUCER);
                }
            }else {
                reducerRunner = new ReducerRunner(ReducerType.ORIGIN_ORDER_REDUCER, RecordType.ORIGIN_ORDER_REDUCER);
            }
            reducerRunner = new ReducerRunner(ReducerType.H_GLOBAL_REDUCER, RecordType.H_GLOBAL_REDUCER);
            reducerRunner = new ReducerRunner(ReducerType.EXPRESSION_REDUCER,RecordType.EXPRESSION_REDUCER);
            reducerRunner = new ReducerRunner(ReducerType.INLINE_REDUCER, RecordType.INLINE_REDUCER);
        }
        Record.v().endRecord(RecordType.TOTAL);
        TestRecord.v().finalOutput();
        Record.v().recordOutput();
    }
/*
    public void run(){
        if(InitialChecker.check()){
            Logger.log("Initial check passed");
        }else {
            Logger.log("Initial check failed");
            //return;
        }
        ReducerRunner reducerRunner;
        Record.v().startRecord(RecordType.TOTAL);
        if(enableCoverage){
            reducerRunner = new ReducerRunner(ReducerType.COVERAGE_REDUCER,RecordType.COVERAGE_REDUCER);
        }
        if(callTraceRandom){
            reducerRunner = new ReducerRunner(ReducerType.RANDOM_TRACE_REDUCER,RecordType.CALL_TRACE_REDUCER);
        }else {
            reducerRunner = new ReducerRunner(ReducerType.CALL_TRACE_REDUCER,RecordType.CALL_TRACE_REDUCER);
        }
        reducerRunner = new ReducerRunner(ReducerType.INLINE_REDUCER,RecordType.INLINE_REDUCER);
        reducerRunner = new ReducerRunner(ReducerType.H_GLOBAL_REDUCER,RecordType.H_GLOBAL_REDUCER);
        reducerRunner = new ReducerRunner(ReducerType.EXPRESSION_REDUCER,RecordType.EXPRESSION_REDUCER);
        reducerRunner = new ReducerRunner(ReducerType.INLINE_REDUCER,RecordType.INLINE_REDUCER_2);
        reducerRunner = new ReducerRunner(ReducerType.GLOBAL_REDUCER,RecordType.GLOBAL_REDUCER);
        Record.v().endRecord(RecordType.TOTAL);
        TestRecord.v().finalOutput();
        Record.v().recordOutput();
    }
*/

    public void setEnableCoverage(boolean enableCoverage) {
        this.enableCoverage = enableCoverage;
    }

    public void setCallTraceRandom(boolean callTraceRandom) {
        this.callTraceRandom = callTraceRandom;
    }

    public void setLinearOrderOpt(boolean enableLinearOrderOpt) {
        CallTraceReducer.enableReduce=enableLinearOrderOpt;
    }
    public void setExtendedReduce(boolean enableExtendedReduceOpt) {
        ExpressionReducer.extendedReduce =enableExtendedReduceOpt;
    }
    public void setEnableCallTrace(boolean enableCallTraceOpt) {
        enableCallTrace =enableCallTraceOpt;
    }
}
