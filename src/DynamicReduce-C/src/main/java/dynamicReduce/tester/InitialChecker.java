package dynamicReduce.tester;

public class InitialChecker {

    public static boolean check(){
        TestRecord.v().newFileOutput(TestRecord.v().getLastSuccess());
        TestContext testContext=new OutputTester().runUnchecked();
        return testContext.getTestStatus().succeed();
    }
}
