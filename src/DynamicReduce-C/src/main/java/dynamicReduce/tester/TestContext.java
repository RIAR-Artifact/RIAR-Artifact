package dynamicReduce.tester;

public class TestContext {

    TestStatus testStatus;
    CoverageResolver coverageResolver;
    CallTraceResolver callTraceResolver;
    public TestContext(TestStatus testStatus) {
        this.testStatus = testStatus;
        this.coverageResolver=null;
    }

    public TestContext(TestStatus testStatus,CoverageResolver coverageResolver){
        this.testStatus=testStatus;
        this.coverageResolver=coverageResolver;
    }

    public TestContext(TestStatus testStatus,CallTraceResolver callTraceResolver){
        this.testStatus=testStatus;
        this.callTraceResolver=callTraceResolver;
    }
    public TestStatus getTestStatus() {
        return testStatus;
    }

    public CoverageResolver getCoverageResolver() {
        return coverageResolver;
    }

    public CallTraceResolver getCallTraceResolver() {
        return callTraceResolver;
    }
}
