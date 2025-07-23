package dynamicReduce.tester;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import dynamicReduce.utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoverageResolver {
    boolean canResolve;
    List<Integer> unexecutedLines;
    List<Integer> nonExecutableLines ;
    List<Integer> executedLines ;
    Map<Integer,Integer> executionMap;
    public CoverageResolver(File gcovFile){
        canResolve=true;
        unexecutedLines = new ArrayList<>();
        nonExecutableLines = new ArrayList<>();
        executedLines = new ArrayList<>();
        executionMap=new HashMap<>();

        Pattern unExecutedPattern = Pattern.compile("^\\s*#+\\s*:+\\s*(\\d+):.*$");
        Pattern nonExecutablePattern = Pattern.compile("^\\s*-\\s*:+\\s*(\\d+):.*$");
        Pattern executedPattern = Pattern.compile("^\\s*(\\d+)\\*?\\s*:\\s*(\\d+):.*$");
        try (BufferedReader br = new BufferedReader(new FileReader(gcovFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher m1 = unExecutedPattern.matcher(line);
                Matcher m2 = nonExecutablePattern.matcher(line);
                Matcher m3 = executedPattern.matcher(line);

                if (m1.find()) {
                    unexecutedLines.add(Integer.parseInt(m1.group(1)));
                } else if (m2.find()) {
                    nonExecutableLines.add(Integer.parseInt(m2.group(1)));
                } else if (m3.find()) {
                    executedLines.add(Integer.parseInt(m3.group(2)));
                    executionMap.put(Integer.parseInt(m3.group(2)),Integer.parseInt(m3.group(1)));
                }
            }
        }catch (java.io.IOException e){
            Logger.log("Failed to resolve gcov file");
            canResolve=false;
        }
    }
    public boolean canResolve(){
        return canResolve;
    }

    public int getExecutionCount(IASTNode node){
        IASTFileLocation fileLocation =node.getFileLocation();
        int res=0;
        for (int i = fileLocation.getStartingLineNumber(); i < fileLocation.getEndingLineNumber(); i++) {
            if(executionMap.containsKey(i)){
                res=Math.max(res,executionMap.get(i));
            }
        }
        return res;
    }
    public void setShift(int shift){
        unexecutedLines.replaceAll(x -> x + shift);
        executedLines.replaceAll(x -> x + shift);
        nonExecutableLines.replaceAll(x -> x + shift);
    }

    public boolean nodeExecuted(IASTNode node){
        int startLine=node.getFileLocation().getStartingLineNumber();
        int endLine=node.getFileLocation().getEndingLineNumber();
        if(startLine==endLine&&nonExecutableLines.contains(startLine)){return false;}
        if(unexecutedLines.contains(startLine)){return true;}
        if(executedLines.contains(startLine)){return false;}
        boolean flag=false;
        for (int i = startLine; i <= endLine; i++) {
            if(unexecutedLines.contains(i)){flag=true;}
            if(executedLines.contains(startLine)){return false;}
        }
        return flag;
    }

    public List<Integer> getUnexecutedLines() {
        return unexecutedLines;
    }

    public List<Integer> getNonExecutableLines() {
        return nonExecutableLines;
    }

    public List<Integer> getExecutedLines() {
        return executedLines;
    }
}
