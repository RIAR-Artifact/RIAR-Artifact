package dynamicReduce.tester;
import dynamicReduce.utils.Logger;
import dynamicReduce.utils.Record;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class TestRecord {
    private static TestRecord instance;
    public static TestRecord v(){
        return instance;
    }


    String baseDir;
    String targetFileName;
    String testBashName;
    String testDir;
    String lastSuccessFile;
    String newFile;
    int currentIndex;

    TestCache cache;

    public static void initialize(String baseDir,String targetFileName,String testBashName){
        instance=new TestRecord(baseDir,targetFileName,testBashName);
    }
    TestRecord(String baseDir,String targetFileName,String testBashName){
        this.baseDir=baseDir;
        this.targetFileName=targetFileName;
        this.testBashName=testBashName;
        cache=new TestCache();
        currentIndex=0;
        Random random=new Random();
        testDir="DR_test_"+Math.abs(random.nextInt()%100000);
        Logger.log("Temp output in directory "+testDir);
        try {
            lastSuccessFile=Files.readString(Paths.get(baseDir + File.separator + targetFileName));
            lastSuccessFile=lastSuccessFile.replaceAll("(?m)^\\s*\\n", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void success(){
        try {
            lastSuccessFile=Files.readString(getTargetFile());
            cache.success();
            Logger.log("last success updated");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTestBashName() {
        return testBashName;
    }

    public String getBaseDir() {
        return baseDir;
    }

    private Path getTargetFile(){
        return Paths.get(getFullPath()+File.separator+targetFileName);
    }
    public Path getTargetBash(){
        return Paths.get(getFullPath()+File.separator+testBashName);
    }
    public String getTargetFileName() {
        return targetFileName;
    }
    public String getTargetFileNameWithoutPrefix() {
        int lastDot = targetFileName.lastIndexOf('.');
        return (lastDot != -1) ? targetFileName.substring(0, lastDot) : targetFileName;
    }
    public String getIndex(){
        return String.format("%05d", currentIndex);
    }
    public int getCurrentIndex(){
        return currentIndex;
    }
    public String getFullPath(){
        try {
            Files.createDirectories(Paths.get(baseDir + File.separator + testDir + File.separator + "temp" + getIndex()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return baseDir+ File.separator+testDir+File.separator+"temp"+getIndex();
    }

    public String getLastSuccess() {
        return lastSuccessFile;
    }

    public void finalOutput(){
        try (FileWriter writer = new FileWriter(getBaseDir()+File.separator+getTargetFileNameWithoutPrefix()+"_reduced.c")){
            writer.write(lastSuccessFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFile(int index){
        try {
            String folder=String.format("%05d", index);
            Path filePath=Paths.get(baseDir+ File.separator+testDir+File.separator+"temp"+folder+File.separator+targetFileName);
            return Files.readString(getTargetFile());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void resetCache(){
        cache.reset(lastSuccessFile);
    }

    public boolean checkOutput(){
        if(lastSuccessFile.equals(newFile)){
            return false;
        }/**/
        Record.v().startCache();
        boolean flag=cache.check(newFile);
        Record.v().endCache(!flag);
        if(!flag){
            Logger.log("cache worked");
        }
        return flag;
    }
    public void newFileOutput(String file){
        newFile=file.replaceAll("(?m)^\\s*\\n", "");
        currentIndex++;
        String fullPath=getFullPath();
        Path originBash= Paths.get(TestRecord.v().getBaseDir()+File.separator+TestRecord.v().getTestBashName());
        Path targetBash= TestRecord.v().getTargetBash();
        try (FileWriter writer = new FileWriter(fullPath+File.separator+targetFileName)){
            writer.write(newFile);
            Files.copy(originBash,targetBash);
            Logger.log("Working in directory: "+fullPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
