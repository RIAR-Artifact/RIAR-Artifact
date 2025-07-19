package DynamicReduce;

public class TimeoutController {
    int lastSuccess;
    int timeoutMilliSeconds;
    boolean timeout;
    boolean autoAdapt;
    public TimeoutController(boolean autoAdapt){
        lastSuccess=15000;
        timeoutMilliSeconds=30000;
        timeout=false;
        this.autoAdapt=autoAdapt;
    }
    public void success(int time){
        timeout=false;
        lastSuccess=time;
        if(autoAdapt) {
            timeoutMilliSeconds = lastSuccess * 2;
        }
    }
    public void failed(int time){
        timeout=false;
    }
    public void timeout(){
        timeout=true;
        if(autoAdapt) {
            timeoutMilliSeconds+=1000;
        }
    }
    public int getTimeoutMilliSeconds(){
        return timeoutMilliSeconds;
    }
    public boolean getStatus(){
        return timeout;
    }
}
