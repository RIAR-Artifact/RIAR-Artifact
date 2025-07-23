package dynamicReduce.tester;

public class TestStatus {
    private final boolean timeout;
    private final boolean succeed;
    public TestStatus(boolean succeed,boolean timeout){
        this.succeed=succeed;
        this.timeout=timeout;
    }
    public boolean timeout(){
        return timeout;
    }
    public boolean succeed(){
        return succeed;
    }
}
