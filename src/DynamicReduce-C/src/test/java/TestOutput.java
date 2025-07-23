import dynamicReduce.tester.InitialChecker;
import dynamicReduce.tester.*;
import dynamicReduce.utils.Logger;

import java.io.File;

public class TestOutput {

    public static void main(String[] args){
        String currentPath = System.getProperty("user.dir");
        Logger.log("当前工作目录: " + currentPath);
        TestRecord.initialize("testcases"+ File.separator+"test1","Test.c","test.sh");
        if(InitialChecker.check()){
            Logger.log("Initial check passed");
        }

    }
}
