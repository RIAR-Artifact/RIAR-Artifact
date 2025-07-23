package dynamicReduce.tester;

import dynamicReduce.utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import static java.lang.System.exit;

public class CoverageTester extends OutputTester{
    private boolean compileTarget() throws IOException {
        int exitCode=1;
        BufferedReader reader = null;
        try {
            ProcessBuilder processBuilder;

            if(System.getProperty("os.name").toLowerCase().contains("nux")){
                processBuilder=new ProcessBuilder("gcc","-fprofile-arcs","-ftest-coverage","-O0","-w",
                        "-g",TestRecord.v().getTargetFileName(),"-o","test");
            }else {
                processBuilder=new ProcessBuilder("gcc","-fprofile-arcs","-ftest-coverage","-O0","-w",
                        "-g",TestRecord.v().getTargetFileName(),"-o","Test.exe");
            }
            processBuilder.directory(new File(TestRecord.v().getFullPath()));
            Process process=processBuilder.start();
            reader= new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream())
            );
            String line;
            while ((line = errReader.readLine()) != null) {
                System.out.println(line);
            }
            exitCode=process.waitFor();
        }catch (IOException | InterruptedException e){
            Logger.log("Failed to compile source file");
            e.printStackTrace();
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("输出: " + line);
            }
            exit(7);
        }
        if(exitCode!=0){
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("输出: " + line);
            }
            Logger.log("Failed to compile source file "+exitCode);
            return false;
        }
        return true;
    }
    private int runTarget(){
        int exitCode=1;
        try {
            ProcessBuilder processBuilder;

            if(System.getProperty("os.name").toLowerCase().contains("nux")){
                processBuilder=new ProcessBuilder("."+File.separator+"test");
            }else {
                processBuilder=new ProcessBuilder(TestRecord.v().getFullPath()+File.separator+"Test.exe");
            }
            processBuilder.directory(new File(TestRecord.v().getFullPath()));
            Process process=processBuilder.start();
            if(process.waitFor(5000, TimeUnit.MILLISECONDS)){
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
    private boolean runGcov(){
        int exitCode=1;
        try {
            ProcessBuilder processBuilder=new ProcessBuilder("gcov",TestRecord.v().getTargetFileName());
            processBuilder.directory(new File(TestRecord.v().getFullPath()));
            Process process=processBuilder.start();
            exitCode=process.waitFor();
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
            exit(7);
        }
        if(exitCode!=0){
            Logger.log("Failed to run gcov");
            return false;
        }
        return true;
    }
    @Override
    public TestContext run() {
        try{
            if(!compileTarget()){
                return new TestContext(new TestStatus(false,false));
            }
        } catch (IOException e) {
            return new TestContext(new TestStatus(false,false));
        }
        int exitCode=runTarget();
        File gcdaFile=new File(TestRecord.v().getFullPath()+File.separator+TestRecord.v().getTargetFileNameWithoutPrefix()+".gcda");
        if(!gcdaFile.exists()){
            return new TestContext(new TestStatus(false,false));
        }
        if(!runGcov()){
            return new TestContext(new TestStatus(false,false));
        }
        return new TestContext(new TestStatus(exitCode==0,exitCode==7777),
                new CoverageResolver(new File(TestRecord.v().getFullPath()+File.separator+TestRecord.v().getTargetFileName()+".gcov")));
    }
}
