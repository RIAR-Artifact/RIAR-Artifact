import dynamicReduce.reducer.ReducerRunner;
import dynamicReduce.reducer.ReducerType;
import dynamicReduce.tester.TestRecord;
import dynamicReduce.utils.RecordType;

import java.io.File;

public class ReducerTest {
    public static void main(String[] args) throws Exception {
        TestRecord.initialize("testcases"+ File.separator+"test1","Test.c","test.sh");
        /*if(InitialChecker.check()){
            Logger.log("Initial check passed");
        }*/
        ReducerRunner reducerRunner=new ReducerRunner(ReducerType.INLINE_REDUCER, RecordType.INLINE_REDUCER);
        //reducerRunner=new ReducerRunner(ReducerType.GLOBAL_REDUCER);
        TestRecord.v().finalOutput();
    }
}
