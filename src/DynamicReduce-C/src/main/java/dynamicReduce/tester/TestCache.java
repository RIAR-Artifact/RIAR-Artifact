package dynamicReduce.tester;

import dynamicReduce.utils.Logger;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.CRC32;

public class TestCache {
    private final Map<Long,Set<Integer>> failedTest;

    private long lastHash;
    public TestCache(){
        failedTest=new HashMap<>();
    }

    public void reset(String text){
        failedTest.clear();
    }

    public boolean check(String text){
        text=text.replaceAll("\\s+"," ");
        lastHash=textHash(text);
        boolean flag=true;
        if(failedTest.containsKey(lastHash)){
            Set<Integer> files=failedTest.get(lastHash);
            for(Integer fileIndex:files){
                String recordFile=TestRecord.v().getFile(fileIndex).replaceAll("\\s+"," ");
                if(text.equals(recordFile)){
                    Logger.logFile(TestRecord.v().getCurrentIndex()+" equals with "+fileIndex);
                    flag=false;
                    break;
                }
            }
        }
        if(flag){
            if(!failedTest.containsKey(lastHash)){
                failedTest.put(lastHash,new HashSet<>());
            }
            failedTest.get(lastHash).add(TestRecord.v().getCurrentIndex());
        }
        return flag;
    }
    public void success(){
        if(failedTest.containsKey(lastHash)){
            failedTest.get(lastHash).remove(TestRecord.v().getCurrentIndex());
        }
    }
    public long textHash(String text){
        CRC32 crc = new CRC32();
        crc.update(text.getBytes(StandardCharsets.UTF_8));
        return crc.getValue();
    }
}
