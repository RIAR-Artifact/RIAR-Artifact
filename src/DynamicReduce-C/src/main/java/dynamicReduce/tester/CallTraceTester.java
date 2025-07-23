package dynamicReduce.tester;

import dynamicReduce.utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import static java.lang.System.exit;

public class CallTraceTester extends OutputTester{
    private boolean compileTarget() {
        int exitCode=1;
        try {
            ProcessBuilder processBuilder;
            if(System.getProperty("os.name").toLowerCase().contains("nux")){
                processBuilder=new ProcessBuilder("gcc",TestRecord.v().getTargetFileName(),"-o","test");
            }else {
                processBuilder=new ProcessBuilder("gcc",TestRecord.v().getTargetFileName(),"-o","Test.exe");
            }
            processBuilder.directory(new File(TestRecord.v().getFullPath()));
            Process process=processBuilder.start();
            BufferedReader errReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream())
            );
            String line;
            StringBuilder output=new StringBuilder();
            while ((line = errReader.readLine()) != null) {
                output.append(line);
            }
            if(process.waitFor(15000, TimeUnit.MILLISECONDS)){
                exitCode= process.exitValue();
            }else {
                process.destroy();
                if(!process.waitFor(100, TimeUnit.MILLISECONDS)){
                    process.destroyForcibly();
                }
                exitCode=7777;
            }
            if(exitCode!=0){
                Logger.log(output.toString());
            }
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
            Logger.log("Failed to compile source file");
            e.printStackTrace();
            exit(7);
        }
        if(exitCode!=0){
            Logger.log("Failed to compile source file "+exitCode);
            return false;
        }
        return true;
    }
    private int runTarget(File outputFile){
        int exitCode=1;
        try {
            ProcessBuilder processBuilder;
            if(System.getProperty("os.name").toLowerCase().contains("nux")){
                processBuilder=new ProcessBuilder("."+File.separator+"test");
            }else {
                processBuilder=new ProcessBuilder(TestRecord.v().getFullPath()+File.separator+"Test.exe");
            }
            processBuilder.directory(new File(TestRecord.v().getFullPath()));
            processBuilder.redirectOutput(outputFile);
            Process process=processBuilder.start();
            if(process.waitFor(15000, TimeUnit.MILLISECONDS)){
                exitCode= process.exitValue();
            }else {
                process.destroy();
                if(!process.waitFor(100, TimeUnit.MILLISECONDS)){
                    process.destroyForcibly();
                }
                exitCode=7777;
            }
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
            exit(7);
        }
        Logger.log("exit code is "+exitCode);
        return exitCode;
    }
    @Override
    public TestContext run() {
        if(!compileTarget()){
            return new TestContext(new TestStatus(false,false));
        }
        File outputFile=new File(TestRecord.v().getFullPath()+File.separator+"stdout.txt");
        int exitCode=runTarget(outputFile);
        return new TestContext(new TestStatus(exitCode==0,exitCode==7777),new CallTraceResolver(outputFile));
    }
}
