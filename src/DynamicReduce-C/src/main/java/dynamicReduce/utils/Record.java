package dynamicReduce.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dynamicReduce.tester.TestContext;
import dynamicReduce.tester.TestRecord;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Record {
    enum RecordCountItem{
        RUN_BASH,TIMEOUT,SUCCESS,
    }
    enum RecordTimeItem{
        TOTAL,BASH,TIME
    }
    static class SingleRecord{
        static class TimeCounter{
            long sumTime=0;
            long lastStart=0;
            boolean counting=false;
            public void start(){
                counting=true;
                lastStart=System.currentTimeMillis();
            }
            public void end(){
                counting=false;
                sumTime+=System.currentTimeMillis()-lastStart;
            }
            public long getSumTime(){
                if(counting){
                    return sumTime+System.currentTimeMillis()-lastStart;
                }
                return sumTime;
            }
        }
        private boolean on;
        private final Map<RecordCountItem,Integer> counts;
        private final Map<RecordTimeItem,TimeCounter> times;
        public SingleRecord(){
            on=false;
            counts=new HashMap<>();
            times=new HashMap<>();
        }
        public void start(){
            on=true;
            startTime(RecordTimeItem.TOTAL);
        }
        public void end(){
            endTime(RecordTimeItem.TOTAL);
            on=false;
        }
        public void count(RecordCountItem countItem){
            if(!on){return;}
            if(!counts.containsKey(countItem)){
                counts.put(countItem,0);
            }
            counts.put(countItem,counts.get(countItem)+1);
        }
        public int getCount(RecordCountItem countItem){
            if(!counts.containsKey(countItem)){
                return 0;
            }
            return counts.get(countItem);
        }
        public void startTime(RecordTimeItem timeItem){
            if(!on){return;}
            if(!times.containsKey(timeItem)){
                times.put(timeItem, new TimeCounter());
            }
            times.get(timeItem).start();
        }
        public void endTime(RecordTimeItem timeItem){
            if(!on){return;}
            if(!times.containsKey(timeItem)){
                times.put(timeItem, new TimeCounter());
            }
            times.get(timeItem).end();
        }
        public float getTime(RecordTimeItem timeItem){
            if(!times.containsKey(timeItem)){
                return 0;
            }
            return times.get(timeItem).getSumTime()/1000.0f;
        }
    }
    private static Record instance=null;
    public static Record v(){
        if (instance==null){
            instance=new Record();
        }
        return instance;
    }
    private final Map<RecordType,SingleRecord> records;
    private final SingleRecord[] effectRecord=new SingleRecord[3];
    private SingleRecord cacheRecord;
    private Record(){
        records=new HashMap<>();
        cacheRecord=new SingleRecord();
        cacheRecord.start();
        for (int i = 0; i < 3; i++) {
            effectRecord[i]=new SingleRecord();
            effectRecord[i].start();
        }
    }

    public void startCache(){
        cacheRecord.startTime(RecordTimeItem.TIME);
        cacheRecord.count(RecordCountItem.RUN_BASH);
    }
    public void endCache(boolean succeed){
        cacheRecord.endTime(RecordTimeItem.TIME);
        if(succeed){
            cacheRecord.count(RecordCountItem.SUCCESS);
        }
    }
    public void startRecord(RecordType recordType){
        if(!records.containsKey(recordType)){
            records.put(recordType,new SingleRecord());
        }
        records.get(recordType).start();
        Logger.logFile("---"+recordType.name()+" start---");
    }

    public void endRecord(RecordType recordType){
        records.get(recordType).end();
    }
    public void startEffectRecord(int index){
        effectRecord[index].startTime(RecordTimeItem.TIME);
    }

    public void endEffectRecord(int index){
        effectRecord[index].endTime(RecordTimeItem.TIME);
    }
    public void startRun(){
        for (RecordType recordType:records.keySet()){
            SingleRecord record=records.get(recordType);
            record.count(RecordCountItem.RUN_BASH);
            record.startTime(RecordTimeItem.BASH);
        }
    }
    public void endRun(TestContext context){
        for (RecordType recordType:records.keySet()){
            SingleRecord record=records.get(recordType);
            if(context.getTestStatus().succeed()){
                record.count(RecordCountItem.SUCCESS);
            }
            if(context.getTestStatus().timeout()){
                record.count(RecordCountItem.TIMEOUT);
            }
            record.endTime(RecordTimeItem.BASH);
        }
        recordOutput();
        if(context.getTestStatus().succeed()){
            Logger.logFile(TestRecord.v().getFullPath()+" succeed");
        }else {
            Logger.logFile(TestRecord.v().getFullPath()+" failed");
        }
        if(context.getTestStatus().timeout()) {
            Logger.logFile(TestRecord.v().getFullPath()+" timeout");
        }
    }
    public void recordOutput(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        Map<String,Map<String,Object>> data=new HashMap<>();
        for (RecordType recordType:records.keySet()){
            SingleRecord record=records.get(recordType);
            Map<String,Object> records=new HashMap<>();
            records.put("TotalTime",record.getTime(RecordTimeItem.TOTAL));
            records.put("BashTime",record.getTime(RecordTimeItem.BASH));
            records.put("BashRunCount",record.getCount(RecordCountItem.RUN_BASH));
            records.put("SuccessCount",record.getCount(RecordCountItem.SUCCESS));
            records.put("TimeoutCount",record.getCount(RecordCountItem.TIMEOUT));
            data.put(recordType.name(),records);
        }
        {
            Map<String,Object> records=new HashMap<>();
            records.put("TotalTime",cacheRecord.getTime(RecordTimeItem.TIME));
            records.put("QueryCount",cacheRecord.getCount(RecordCountItem.RUN_BASH));
            records.put("WorkCount",cacheRecord.getCount(RecordCountItem.SUCCESS));
            data.put("CACHE",records);
        }
        for (int i = 0; i < 3; i++) {
            Map<String,Object> records=new HashMap<>();
            records.put("Time",effectRecord[i].getTime(RecordTimeItem.TIME));
            data.put("effectRecord"+i,records);
        }
        try {
            mapper.writeValue(new File(TestRecord.v().getBaseDir()+File.separator+"record.json"), data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
