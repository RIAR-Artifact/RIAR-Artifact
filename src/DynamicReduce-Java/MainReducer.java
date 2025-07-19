package org.example;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.internal.core.ClassFile;
import org.eclipse.jface.text.Document;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.ConstraintAnalyst.resolveConstrains;

public class MainReducer {
    public static String targetClass="NestedInterface";
    public static String regex="Exception in thread";
    public static CompilationUnit cu;
    public static String workingDir="RealBug/";
    public static String baseDir="RealBug/";
    public static ASTParser parser;
    public static ASTRewrite rewriter;
    public static Document document;
    private static int workingNum=0;

    public static void init(){
        workingDir=baseDir+"temp0/";
        Path targetDir= Paths.get(workingDir);
        try {
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            Path sourceFile;
            sourceFile= Paths.get(baseDir+targetClass+".java");
            Path targetFile = Paths.get(workingDir+targetClass+".java");
            Files.copy(sourceFile,targetFile);
            parser = ASTParser.newParser(AST.JLS13);
            parser.setResolveBindings(true);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void analyseCurrent(){
        try {
            String sourceCode = Files.readString(Paths.get(workingDir+targetClass+".java"));
            document=new Document(sourceCode);
            parser.setSource(sourceCode.toCharArray());
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            parser.setEnvironment(null, null, null, true);
            parser.setUnitName(targetClass+".java");
            System.out.println("Analyse started");
            cu = (CompilationUnit) (parser.createAST(null));
            rewriter=ASTRewrite.create(cu.getAST());
            ConstraintAnalyst.resolveConstrains();
            System.out.println("Analyse complete");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void tempOutput(){
        advanceWorkingDir();
        try {
            //String output = cu.toString();
            String output=document.get();
            Files.write(Paths.get(workingDir+targetClass+".java"), output.getBytes());
        }catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void fileOutput(){
        try {
            FunctionAdapter.undoInsertPrint();
            String output = cu.toString();
            Files.write(Paths.get(baseDir+targetClass+"_reduced.java"), output.getBytes());
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static boolean compileJavaFileWithCmd(String filePath) {
        int output=1;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/home/tilt/JVM/jdk1.7.0_25/bin/javac", filePath);
            Process process = processBuilder.start();
            File outputFile = new File(workingDir+"compileOut.txt");
            processBuilder.redirectOutput(outputFile); // 重定向输出到文件
            processBuilder.redirectError(outputFile);
            output=process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        if(output==0){
            System.out.println("编译成功！");
        }else {
            System.out.println("编译失败！");
        }
        return output==0;
    }
    public static boolean testOutput(){
        String filePath = workingDir+targetClass+".java"; // 替换为要编译和运行的 Java 文件路径
        // 编译 Java 文件
        if (compileJavaFileWithCmd(filePath)) {
            // 运行编译后的类
            runJavaClassWithCmd(workingDir+"out.txt");
        }else {
            return false;
        }
        String outFile = workingDir+"out.txt"; // 替换为你的文件路径
        String output=null;
        try {
            // 读取文件为字符串
            output=new String(Files.readAllBytes(Paths.get(outFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(output==null){
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(output);
        return matcher.find();
    }


    public static boolean testOutput2(){
        String filePath = workingDir+targetClass+".java"; // 替换为要编译和运行的 Java 文件路径
        // 编译 Java 文件
        if (compileJavaFile(filePath)) {
            // 运行编译后的类
            runJavaClass(workingDir+"out.txt");
        }else {
            return false;
        }
        String outFile = workingDir+"out.txt"; // 替换为你的文件路径
        String output=null;
        try {
            // 读取文件为字符串
            output=new String(Files.readAllBytes(Paths.get(outFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(output==null){
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(output);
        return matcher.find();
    }
    public static void advanceWorkingDir(){
        workingNum++;
        workingDir=baseDir+"temp"+workingNum+"/";
        try {
            Path targetDir=Paths.get(workingDir);
            System.out.println("In Dir "+workingDir);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void decentWorkingDir(){
        if (workingNum < 0) {
            return;
        }
        workingNum--;
        String newDir="TestCase/temp"+workingNum+"/";
        File dir=new File(workingDir);
        deleteDirectory(dir);
        workingDir=newDir;
    }
    public static boolean deleteDirectory(File directory) {
        // 检查目录是否存在
        if (!directory.exists()) {
            return false;
        }

        // 检查是否是文件夹
        if (directory.isDirectory()) {
            // 获取文件夹中的所有文件和子文件夹
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    // 递归删除每个文件和子文件夹
                    if (file.isDirectory()) {
                        deleteDirectory(file); // 递归删除子文件夹
                    }
                    // 删除文件或空文件夹
                    if (!file.delete()) {
                        System.out.println("无法删除文件: " + file.getAbsolutePath());
                    }
                }
            }
        }
        // 删除文件夹本身
        return directory.delete();
    }
    private static boolean checkDifference(){
        if(workingNum==0){return false;}
        try {
            Path path1 = Paths.get(workingDir+targetClass+".java");
            Path path2 = Paths.get(baseDir+"temp"+(workingNum-1)+"/"+targetClass+".java");

            long mismatchIndex = Files.mismatch(path1, path2);
            return mismatchIndex==-1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean compileJavaFile(String filePath) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.err.println("找不到 Java 编译器。");
            return false;
        }

        int result = compiler.run(null, null, null, filePath);
        if (result == 0) {
            System.out.println("编译成功！");
            return true;
        } else {
            System.err.println("编译失败。");
            return false;
        }
    }

    private static void runJavaClassWithCmd(String outputFilePath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/home/tilt/JVM/jdk1.7.0_25/bin/java", "-cp", workingDir+".", targetClass);
            File outputFile = new File(outputFilePath);
            processBuilder.redirectOutput(outputFile); // 重定向输出到文件
            processBuilder.redirectError(outputFile);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            System.out.println("程序退出，退出码：" + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void runJavaClass(String outputFilePath) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", workingDir+".", targetClass);
            File outputFile = new File(outputFilePath);
            processBuilder.redirectOutput(outputFile); // 重定向输出到文件
            processBuilder.redirectError(outputFile);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            System.out.println("程序退出，退出码：" + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void run(){
        init();
        analyseCurrent();
        //ConstraintAnalyst.solveConstrains();
        GlobalDelta.globalDD();
/*
        if(!testOutput()){return;}
        FunctionAdapter.insertPrint();
        tempOutput();
        if(!testOutput()){return;}
        boolean complete=false;
        while (!complete){
            analyseCurrent();
            //adaption
            tempOutput();
            if(!testOutput()){continue;}
            complete=checkDifference();
        }
        fileOutput();
*/
    }
    public static void main(String[] args) {
        run();
    }
}
