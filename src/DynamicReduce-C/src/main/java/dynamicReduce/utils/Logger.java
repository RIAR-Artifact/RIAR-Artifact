package dynamicReduce.utils;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import dynamicReduce.tester.TestRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Logger {
    static class LogContext{
        String content;
        LocalDateTime time;
        String className;

        public LogContext(String content, LocalDateTime time, String className) {
            this.content = content;
            this.time = time;
            this.className = className;
        }
        public void log(){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String prefix="["+time.format(formatter)+"] ["+className+"] ";
            String[] lines = content.split("\n");
            for (int i = 0; i < lines.length; i++) {
                if (i == 0) {
                    System.out.println(prefix + lines[i]);
                } else {
                    System.out.println(" ".repeat(prefix.length()) + lines[i]);
                }
            }
        }
    }
    static int partLogs=0;
    static String logFileName = "log.txt";
    static List<LogContext> waitingLogs=new ArrayList<>();
    public static String lessText(IASTNode node){
        String text= node.getRawSignature();
        int lineStart=node.getFileLocation().getStartingLineNumber();
        int lineEnd=node.getFileLocation().getEndingLineNumber();
        String[] lines = text.split("\\R");
        if(lines.length==1){return lineStart+": "+text;}
        return lineStart+": "+lines[0]+"\n"+lineEnd+": "+lines[lines.length-1];
    }
    public static void log(String content){
        LocalDateTime now = LocalDateTime.now();
        String className="";
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 2) {
            StackTraceElement caller = stackTrace[2];
            className= caller.getClassName();
        }
        LogContext context=new LogContext(content,now,className);
        if(partLogs>0){
            waitingLogs.add(context);
        }else {
            context.log();
        }
    }
    public static void logFile(String line){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String prefix="["+now.format(formatter)+"] ";

        try (FileWriter fw = new FileWriter(TestRecord.v().getBaseDir()+ File.separator+ logFileName, true)) { // true表示追加模式
            fw.write(prefix+line + System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    boolean working=false;
    public void logStart(String content){
        working=true;
        partLogs++;
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String className="";
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 2) {
            StackTraceElement caller = stackTrace[2];
            className= caller.getClassName();
        }
        String prefix="["+now.format(formatter)+"] ["+className+"] ";
        System.out.print(prefix+content);
    }
    public void logPart(String content){
        System.out.print(content);
    }
    public void endLog(){
        System.out.print("\n");
        partLogs--;
        working=false;
        if(partLogs<=0){
            for (LogContext context:waitingLogs
                 ) {
                context.log();
            }
        }
    }
}
