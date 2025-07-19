package DynamicReduce;


import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import name.fraser.neil.plaintext.diff_match_patch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.System.exit;

public class OutputTester {
    static class FileEdit{
        public FileEdit(Patch<Character> patch,int size){
            StringBuilder strBuilder = new StringBuilder(editString);
            for (AbstractDelta<Character> delta:patch.getDeltas()){
                if(delta.getType()== DeltaType.DELETE){
                    strBuilder.append("D");
                }else if(delta.getType()== DeltaType.INSERT){
                    strBuilder.append("I");
                }else if(delta.getType()== DeltaType.CHANGE){
                    strBuilder.append("C");
                }
                strBuilder.append(delta.getSource().getPosition()).append(",").append(delta.getSource().size());
            }
            editString=strBuilder.toString();
            System.out.println(editString);
            this.size=size;
        }

        public FileEdit(List<diff_match_patch.Diff> diffs,int size){
// 输出每个diff的开始位置和长度
            int startPos=0;
            StringBuilder strBuilder = new StringBuilder(editString);
            for (diff_match_patch.Diff diff : diffs) {
                // 获取差异的操作类型
                String operation = diff.operation.toString();  // INSERT, DELETE, EQUAL
                String text = diff.text;  // 差异内容
                int length = text.length();  // 长度
                if(diff.operation== diff_match_patch.Operation.DELETE){
                    strBuilder.append("D").append(startPos).append(",").append(length);
                }else if(diff.operation== diff_match_patch.Operation.INSERT){
                    strBuilder.append("I").append(startPos).append(",").append(length);
                }
                if(diff.operation!= diff_match_patch.Operation.INSERT){
                    startPos+=length;
                }

            }
            editString=strBuilder.toString();
            System.out.println(editString);
            this.size=size;
        }
        public int size;
        String editString="";
        public static boolean equal(FileEdit edit1,FileEdit edit2){
            return Objects.equals(edit1.editString, edit2.editString);
        }
    }
    static Map<Integer,List<FileEdit>> failCache=new HashMap<>();

    public static String JdkPath="/media/tilt/内容/Ubuntu/jdk/jdk-11.0.24";
    public static String classPath="/home/tilt/tilt233/cases/FuzzerUtils/.:.";
    public static String lastSuccess="";
    public static boolean editPrepared=false;

    public static String subDirectory="";
    public static String originalFile;
    public static TimeoutController timeoutController=new TimeoutController(false);
    public static void init(){
        try {
            originalFile=Files.readString(Path.of(MainReducer.workingDir+MainReducer.targetClass+".java"));
            editPrepared=true;
            failCache.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static FileEdit getDiff(){
        if(!editPrepared){return null;}
        String currentFile;
        try {
            currentFile=Files.readString(Path.of(MainReducer.workingDir+MainReducer.targetClass+".java"));
            /*
            List<Character> originalList=originalFile.chars().mapToObj(c->(char)c).toList();
            List<Character> currentList=currentFile.chars().mapToObj(c->(char)c).toList();
            System.out.println(originalList.size()+" "+currentList.size());
            Patch<Character> patch= DiffUtils.diff(originalList,currentList);
            return new FileEdit(patch,currentList.size());

             */
            diff_match_patch dmp = new diff_match_patch();
            List<diff_match_patch.Diff> diffs = dmp.diff_main(originalFile, currentFile);

            return new FileEdit(diffs,currentFile.length());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static int cacheWorkTime=0;
    public static boolean runBash(){
        MainReducer.testCounter.cacheStart();
        MainReducer.testCounter.start1();
        FileEdit fileEdit=getDiff();
        MainReducer.testCounter.finish1();
        if(fileEdit!=null) {
            if (failCache.containsKey(fileEdit.size)){
                for (FileEdit prevFileEdit:failCache.get(fileEdit.size)){
                    if(FileEdit.equal(prevFileEdit,fileEdit)){
                        System.out.println("Cache worked for the "+cacheWorkTime++ + " time");
                        MainReducer.testCounter.cacheWork();
                        MainReducer.testCounter.cacheFinish();
                        return false;
                    }
                }
            }
        }
        MainReducer.testCounter.cacheFinish();
        if(checkChanges()){
            System.out.println("Unchanged output produced");
            return false;}
        MainReducer.testCounter.execute();
        try {
            String cmd="./" +MainReducer.bashName;
            ProcessBuilder processBuilder=new ProcessBuilder();
            processBuilder.directory(new File(System.getProperty("user.dir")+"/"+MainReducer.workingDir));
            Process process=processBuilder.command(cmd).start();
            int exitCode=1;
            long startTIme=System.currentTimeMillis();
            if(process.waitFor(timeoutController.getTimeoutMilliSeconds(), TimeUnit.MILLISECONDS)){
                exitCode= process.exitValue();
            }else {
                MainReducer.testCounter.timeout();
                System.out.println("进程超时，强制停止"+process.pid());
                process.destroy();
                if(!process.waitFor(100,TimeUnit.MILLISECONDS)){
                    process.destroyForcibly();
                }
                exitCode=7777;
            }
            System.out.println(System.getProperty("user.dir")+"/"+MainReducer.workingDir+" "+exitCode);
            if(exitCode==0){
                timeoutController.success((int)(System.currentTimeMillis()-startTIme));
            }else if(exitCode==7777){
                timeoutController.timeout();
            }else  {
                timeoutController.failed((int)(System.currentTimeMillis()-startTIme));
            }
            if(exitCode==0){
                lastSuccess=MainReducer.workingDir;
                if(fileEdit!=null){
                    Set<Integer> removeKey=new HashSet<>();
                    for(Integer key:failCache.keySet()){
                        if(key>fileEdit.size){
                            removeKey.add(key);
                        }
                    }
                    for (Integer key:removeKey){
                        failCache.remove(key);
                    }
                }
            }else if(fileEdit!=null){
                if (!failCache.containsKey(fileEdit.size)){
                    failCache.put(fileEdit.size,new ArrayList<FileEdit>());
                }
                failCache.get(fileEdit.size).add(fileEdit);
            }

            MainReducer.testCounter.endTest();
            return exitCode==0;
        }catch (IOException|InterruptedException e){
            e.printStackTrace();
            exit(-1);
        }
        return false;
    }
    public static boolean runBashForce(){
        MainReducer.testCounter.execute();
        try {
            String cmd="./" +MainReducer.bashName;
            ProcessBuilder processBuilder=new ProcessBuilder();
            processBuilder.directory(new File(System.getProperty("user.dir")+"/"+MainReducer.workingDir));
            Process process=processBuilder.command(cmd).start();
            int exitCode=1;
            long startTIme=System.currentTimeMillis();
            if(process.waitFor(timeoutController.getTimeoutMilliSeconds(), TimeUnit.MILLISECONDS)){
                exitCode= process.exitValue();
            }else {
                MainReducer.testCounter.timeout();
                System.out.println("进程超时，强制停止"+process.pid());
                process.destroy();
                if(!process.waitFor(100,TimeUnit.MILLISECONDS)){
                    process.destroyForcibly();
                }
                exitCode=7777;
            }
            System.out.println(System.getProperty("user.dir")+"/"+MainReducer.workingDir+" "+exitCode);
            if(exitCode==0){
                timeoutController.success((int)(System.currentTimeMillis()-startTIme));
            }else if(exitCode==7777){
                timeoutController.timeout();
            }else  {
                timeoutController.failed((int)(System.currentTimeMillis()-startTIme));
            }
            if(exitCode==0){
                lastSuccess=MainReducer.workingDir;
            }

            MainReducer.testCounter.endTest();
            return exitCode==0;
        }catch (IOException|InterruptedException e){
            e.printStackTrace();
            exit(-1);
        }
        return false;
    }
    static boolean checkChanges() {
        if(Objects.equals(lastSuccess, "")){return false;}
        try {
            byte[] lastFile= Files.readAllBytes(Paths.get(lastSuccess+MainReducer.targetClass+".java"));
            byte[] currentFile= Files.readAllBytes(Paths.get(MainReducer.workingDir+MainReducer.targetClass+".java"));
            return Arrays.equals(lastFile,currentFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    static boolean compileTarget(){
        if(!Objects.equals(subDirectory, "")){
            String currentDir=System.getProperty("user.dir")+"/"+MainReducer.workingDir;
            Path subDir=Paths.get(currentDir+subDirectory);
            Path source=Paths.get(currentDir + MainReducer.targetClass+".java");
            Path target=Paths.get(currentDir +subDirectory+ MainReducer.targetClass+".java");
            try {
                Files.createDirectories(subDir);
                Files.copy(source,target, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            String javacPath=JdkPath+"/bin/javac";
            ProcessBuilder processBuilder = new ProcessBuilder(javacPath, "-cp",classPath, subDirectory+ MainReducer.targetClass+".java");
            processBuilder.directory(new File(System.getProperty("user.dir")+"/"+MainReducer.workingDir));
            Process process = processBuilder.start();
            int exitCode=process.waitFor();

            if(exitCode==0){System.out.println("编译成功");return true;}
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("编译失败");return false;
    }
    public static void runJacoco(){
        if(!compileTarget()){return;}
        try {
            System.out.println("开始运行程序"+JdkPath+"java");
            String jacocoAgentPath = System.getProperty("user.dir")+"/"+"jacoco-0.8.12/lib/jacocoagent.jar";
            String coverageOutput = "jacoco.exec";  // 覆盖率输出文件
            String javaAgentOption = String.format("-javaagent:%s=destfile=%s", jacocoAgentPath, coverageOutput);
            String javaPath=JdkPath+"/bin/java";
            ProcessBuilder processBuilder = new ProcessBuilder(javaPath, "-cp",classPath,javaAgentOption, MainReducer.fullClassName);
            processBuilder.directory(new File(System.getProperty("user.dir")+"/"+MainReducer.workingDir));
            File outFile=new File(MainReducer.workingDir+"out1.txt");
            processBuilder.redirectOutput(outFile);
            processBuilder.redirectError(outFile);
            int exitCode=777777;
            Process process = processBuilder.start();
            boolean finished = process.waitFor(timeoutController.getTimeoutMilliSeconds(), TimeUnit.MILLISECONDS);
            if (finished) {
                // 获取进程的退出代码
                exitCode = process.exitValue();
                System.out.println("Process completed with exit code: " + exitCode);
            } else {
                // 超时，杀死进程
                System.out.println("Process timed out. Terminating the process...");
                process.destroy();
                if(!process.waitFor(100,TimeUnit.MILLISECONDS)){
                    process.destroyForcibly();
                }
            }
            System.out.println("覆盖率数据已保存到 " + coverageOutput);
            System.out.println("程序退出 退出码：" + exitCode);
            CoverageResolver.JacocoCoverageReader();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
