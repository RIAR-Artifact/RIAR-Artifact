package DynamicReduce;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.Document;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class MainReducer {
    public static String targetClass="Test";
    public static String fullClassName="Test";
    public static String regex="10";
    public static CompilationUnit cu;
    public static String workingDir="TestCase/";
    public static String baseDir="TestCase/";
    public static String bashName="test.sh";
    //public static List<String> className;
    public static int timeoutSeconds=20;
    public static String currentJvmPath="";
    public static String sourceCode;
    public static boolean enableCoverage=true;
    public static ASTParser parser;
    public static ASTRewrite rewriter;
    public static Document document;
    private static int workingNum=0;
    public static boolean timeout=false;
    static TestCounter testCounter=new TestCounter();

    public static void init(){

        workingDir=baseDir+"temp0/";
        Path targetDir= Paths.get(workingDir);
        try {
            deleteDirectory(new File(workingDir));
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            Path sourceFile;
            sourceFile= Paths.get(baseDir+targetClass+".java");
            Path targetFile = Paths.get(workingDir+targetClass+".java");
            Files.copy(Paths.get(baseDir+bashName),Paths.get(workingDir+bashName));
            Files.copy(sourceFile,targetFile);
        }catch (IOException e) {
            e.printStackTrace();
        }

        boolean flag=false;
        for (int i = 0; i < 15; i++) {
            if(testOutput(false)){
                flag=true;
                break;
            }
        }
        if(!flag){
            System.out.println("初始化错误");
            System.exit(1);
        }
    }
    public static String getCurrentFile() throws IOException {
        Path targetFile = Paths.get(workingDir+targetClass+".java");
        return Files.readString(targetFile);
    }

    public static void analyseCurrent(){
        try {
            sourceCode = Files.readString(Paths.get(workingDir+targetClass+".java"));
            document=new Document(sourceCode);
            parser = ASTParser.newParser(AST.JLS13);
            parser.setResolveBindings(true);
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
            System.out.println("output: "+workingDir+targetClass+".java");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void tempOutput(String output){
        advanceWorkingDir();
        try {
            Files.write(Paths.get(workingDir+targetClass+".java"), output.getBytes());
            System.out.println("output: "+workingDir+targetClass+".java");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void fileOutput(){
        try {
            //FunctionAdapter.undoInsertPrint();
            String output=document.get();
            Files.write(Paths.get(baseDir+targetClass+"_reduced.java"), output.getBytes());
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*
     */
    public static void fileOutput(String fileSuffix){
        try {
            //FunctionAdapter.undoInsertPrint();
            String output=document.get();
            Files.write(Paths.get(baseDir+targetClass+"_"+fileSuffix+".java"), output.getBytes());
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean testOutput(boolean testCoverage){
        if(testCoverage){
            OutputTester.runJacoco();
            return true;
        }else {
            return OutputTester.runBash();
        }


    }
    public static void advanceWorkingDir(){
        workingNum++;
        workingDir=baseDir+"temp"+workingNum+"/";
        deleteDirectory(new File(workingDir));
        try {
            Path targetDir=Paths.get(workingDir);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            Files.copy(Paths.get(baseDir+bashName),Paths.get(workingDir+bashName));
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void decentWorkingDir(){
        System.out.println("decentWorkingDir");
        if (workingNum < 0) {
            return;
        }
        workingNum--;
        String newDir=baseDir+"temp"+workingNum+"/";
        File dir=new File(workingDir);
        if(deleteDirectory(dir)){
            workingDir=newDir;
        }else {
            System.out.println("decentWorkingDir Failed");
            workingNum++;
        }
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
                    }else if (!file.delete()) {
                        System.out.println("无法删除文件: " + file.getAbsolutePath());
                    }
                }
            }
        }
        // 删除文件夹本身
        return directory.delete();
    }

    private static boolean compileJavaFileOld(String filePath) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.err.println("找不到 Java 编译器。");
            return false;
        }
        int result = compiler.run(null, null, null,filePath);
        if (result == 0) {
            System.out.println("编译成功！");
            return true;
        } else {
            System.err.println("编译失败。");
            return false;
        }
    }


    public static void run(){
        init();
        DeclarationFiller.start();
        OutputTester.init();
        /**/
        if (enableCoverage) {
            testCounter.start("coverage");
            CoverageDelta.run();
            testCounter.finish("coverage");
        }
        OutputTester.init();
        testCounter.start("callTrace");
        CallTraceDelta.run();
        testCounter.finish("callTrace");
        testCounter.start("inline");
        CommentAnalyst.run();
        InlineAnalyst.run();
        testCounter.finish("inline");
        testCounter.start("globalDD1");
        analyseCurrent();
        GlobalDelta.statementLevel = CallTraceDelta.reverse || InlineAnalyst.inlineWorked;
        GlobalDelta.globalDD();
        testCounter.finish("globalDD1");

        OutputTester.init();
        testCounter.start("expressionDelta");
        ExpressionAnalyst.expressionDelta();
        testCounter.finish("expressionDelta");
        OutputTester.init();
        testCounter.start("globalDD2");
        GlobalDelta.statementLevel = true;
        GlobalDelta.globalDD();
        testCounter.finish("globalDD2");
        GlobalDelta.statementLevel = true;
        GlobalDelta.blockUndone = true;
        GlobalDelta.globalDD();
        GlobalDelta.blockUndone = false;
        String nowSuccess = "";
        try {
            nowSuccess = getCurrentFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        testCounter.endTest();
        fileOutput();
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
