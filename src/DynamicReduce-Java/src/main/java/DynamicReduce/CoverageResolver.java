package DynamicReduce;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.ICounter;
import org.jacoco.core.analysis.ILine;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.tools.ExecFileLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class CoverageResolver {
    public static String[] classes={OutputTester.subDirectory+ MainReducer.targetClass+".class"};
    public static HashMap<Integer,Boolean> covered=new HashMap<>();

    public static void JacocoCoverageReader() throws IOException {
        // jacoco.exec文件路径
        String execFile = MainReducer.workingDir+"jacoco.exec";

        // Load execution data from jacoco.exec
        ExecFileLoader loader = new ExecFileLoader();
        loader.load(new File(execFile));
        ExecutionDataStore executionDataStore = loader.getExecutionDataStore();

        // Analyze class files
        CoverageBuilder coverageBuilder = new CoverageBuilder();
        Analyzer analyzer = new Analyzer(executionDataStore, coverageBuilder);
        classes= new String[]{OutputTester.subDirectory + MainReducer.targetClass + ".class"};
        for (String classesDir:classes){
            analyzer.analyzeAll(new File(MainReducer.workingDir+classesDir));
        }
        // Access each class's method line coverage information
        covered.clear();
        coverageBuilder.getClasses().forEach(coverage -> {
            //System.out.println("Class: " + coverage.getName());
            coverage.getMethods().forEach(method -> {
                System.out.println(" Method: " + method.getName()+" "+method.getDesc()+" "+method.getFirstLine());
                boolean flag=true;
                // Access line-by-line coverage for each method
                for (int i = method.getFirstLine(); i <= method.getLastLine(); i++) {
                    if(covered.containsKey(i)){
                        //System.out.printf("  Line %d: %b%n", i, covered.get(i));
                        if(covered.get(i)){flag=false;continue;}
                    }
                    ILine lineCounter = method.getLine(i);
                    String status = lineCounter.getStatus() == ICounter.FULLY_COVERED ? "Fully Covered"
                            : lineCounter.getStatus() == ICounter.PARTLY_COVERED ? "Partially Covered"
                            : "Not Covered";
                    covered.put(i,lineCounter.getStatus()==ICounter.PARTLY_COVERED||lineCounter.getStatus()==ICounter.FULLY_COVERED);
                    if(covered.get(i)){flag=false;}
                    //System.out.printf("  Line %d: %s%n", i, status);
                }
            });
        });
        Set<Integer> keys=covered.keySet();
        for (Integer key:keys
             ) {
            if(covered.get(key)){
                System.out.println("  Line "+key+ " covered");
            }else {
                System.out.println("  Line "+key+ " not covered");
            }
        }
    }
}
