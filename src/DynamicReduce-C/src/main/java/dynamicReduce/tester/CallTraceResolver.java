package dynamicReduce.tester;

import dynamicReduce.utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CallTraceResolver {
    File stdoutFile;
    public CallTraceResolver(File file){
        stdoutFile=file;
    }

    public List<String> resolveTrace(String prefix){
        List<String> calledFunction = new ArrayList<>();
        Pattern pattern = Pattern.compile("^\\s*#+\\s*:\\s*(\\d+):.*$");

        try (BufferedReader br = new BufferedReader(new FileReader(stdoutFile))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.stripLeading(); // 去除前导空白

                if (line.startsWith(prefix)) {
                    String extracted = line.substring(prefix.length());
                    if(!calledFunction.contains(extracted)) {
                        calledFunction.add(extracted);
                    }
                }
            }
        }catch (java.io.IOException e){
            Logger.log("Failed to resolve gcov file");
        }
        return calledFunction;
    }
}
