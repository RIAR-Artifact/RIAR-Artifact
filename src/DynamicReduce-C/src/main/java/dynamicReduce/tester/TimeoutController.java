package dynamicReduce.tester;

public class TimeoutController {
    public TimeoutController(){

    }

    long timeoutMills=30000;
    long lastSuccess=timeoutMills;
    public void success(long timeMills){
        lastSuccess=timeMills;
        timeoutMills=timeMills+timeoutMills/2;
    }
    public void fail(long timeMills){
        timeoutMills=Math.max(lastSuccess,timeMills)+timeoutMills/2;
    }
    public long getTimeoutMills(){
        return timeoutMills;
    }
}
