package DynamicReduce;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static DynamicReduce.MainReducer.baseDir;

public class TestCounter {
    class Counter{
        long startTime=0;
        long timeSum=0;
        public int startTestCount=0;
        public int testCount;
        public void start(){
            startTime=System.currentTimeMillis();
            startTestCount=executionTime;
        }
        public void finish(){
            timeSum+=System.currentTimeMillis()-startTime;
            testCount=executionTime-startTestCount;
        }
    }
    long startTime;
    int executionTime=0;
    int cacheWorkTime=0;
    long prevCacheStartTime=0;
    long cacheConsume=0;
    long prevPin1=0;
    long sum1=0;
    int timeoutCount=0;
    boolean expressionDelta=false;
    int expressionTimeout=0;

    Map<String,Counter> counters=new HashMap<>();
    Counter[] effectCounter=new Counter[3];
    public TestCounter(){
        startTime=System.currentTimeMillis();
        for (int i = 0; i < 3; i++) {
            effectCounter[i]=new Counter();
        }
    }
    public void execute(){
        executionTime++;
    }
    public void cacheWork(){
        cacheWorkTime++;
    }
    public void timeout(){
        timeoutCount++;
        if(expressionDelta){
            expressionTimeout++;
        }
    }
    public void cacheStart(){
        prevCacheStartTime=System.currentTimeMillis();
    }
    public void cacheFinish(){
        cacheConsume+=System.currentTimeMillis()-prevCacheStartTime;
    }
    public void start1(){
        prevPin1=System.currentTimeMillis();
    }
    public void finish1(){
        sum1+=System.currentTimeMillis()-prevPin1;
    }
    public void start(String name){
        getCounter(name).start();
        if (Objects.equals(name, "expressionDelta")) {
            expressionDelta=true;
        }
    }
    public void finish(String name){
        getCounter(name).finish();
        //MainReducer.fileOutput(name);
        if (Objects.equals(name, "expressionDelta")) {
            expressionDelta=false;
        }
    }
    public void startEffectCounter(int index){
        effectCounter[index].start();
    }
    public void endEffectCounter(int index){
        effectCounter[index].finish();
    }
    Counter getCounter(String name){
        if(!counters.containsKey(name)){
            counters.put(name,new Counter());
        }
        return counters.get(name);
    }
    public void endTest(){
        List<String> lines = new ArrayList<>();
        lines.add("time: "+(System.currentTimeMillis()-startTime)/1000.0+" seconds");
        lines.add("Test script execution count: "+executionTime);
        lines.add("cache take time: "+cacheConsume/1000.0+" seconds");
        lines.add("cache compute take time: "+sum1/1000.0+" seconds");
        lines.add("cache work count: "+cacheWorkTime);
        lines.add("timeout count: "+timeoutCount);
        lines.add("timeout in expression delta: "+expressionTimeout);
        lines.add("coverage reduce take time: "+getCounter("coverage").timeSum/1000.0+" seconds");
        lines.add("coverage reduce execution count: "+getCounter("coverage").testCount+" ");
        lines.add("callTrace reduce take time: "+getCounter("callTrace").timeSum/1000.0+" seconds");
        lines.add("callTrace reduce execution count: "+getCounter("callTrace").testCount);
        lines.add("Inline reduce take time: "+getCounter("inline").timeSum/1000.0+" seconds");
        lines.add("Inline reduce execution count: "+getCounter("inline").testCount+" ");
        lines.add("globalDD1 reduce take time: "+getCounter("globalDD1").timeSum/1000.0+" seconds");
        lines.add("globalDD1 reduce execution count: "+getCounter("globalDD1").testCount);
        lines.add("expressionDelta reduce take time: "+getCounter("expressionDelta").timeSum/1000.0+" seconds");
        lines.add("expressionDelta reduce execution count: "+getCounter("expressionDelta").testCount);
        lines.add("globalDD2 reduce take time: "+getCounter("globalDD2").timeSum/1000.0+" seconds");
        lines.add("globalDD2 reduce execution count: "+getCounter("globalDD2").testCount);
        lines.add("effectCounter1 take time: "+effectCounter[0].timeSum/1000.0+" seconds");
        lines.add("effectCounter2 take time: "+effectCounter[1].timeSum/1000.0+" seconds");
        lines.add("effectCounter3 take time: "+effectCounter[2].timeSum/1000.0+" seconds");
        try {
            Files.write(Paths.get(baseDir+"record.txt"), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
