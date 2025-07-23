package dynamicReduce;
import org.apache.commons.cli.*;
import dynamicReduce.reducer.ExpressionReducer;

public class Main {
    static String baseDirectoryOpt="baseDirectory";
    static String targetFileNameOpt="targetFileName";
    static String testBashNameOpt="testBashName";
    static String suppressTimeoutOpt="suppressTimeout";
    static String enableCoverageOpt="enableCoverage";
    static String callTraceRandomOpt="callTraceRandom";
    static String enableCallTraceOpt="callTrace";
    static String enableLinearOrderOpt="enableLinearOrder";
    static String enableExtendedReduceOpt="enableExtendedReduce";
    static String helpText="help text not implemented..";
    public static void main(String[] args) throws ParseException {
        Options options=new Options();
        options.addOption(Option.builder("h").longOpt("help").build());
        options.addOption(Option.builder("bd").longOpt(baseDirectoryOpt).hasArg().build());
        options.addOption(Option.builder("f").longOpt(targetFileNameOpt).hasArg().build());
        options.addOption(Option.builder("b").longOpt(testBashNameOpt).hasArg().build());
        options.addOption(Option.builder("s").longOpt(suppressTimeoutOpt).hasArg().build());
        options.addOption(Option.builder("cov").longOpt(enableCoverageOpt).hasArg().build());
        options.addOption(Option.builder("rd").longOpt(callTraceRandomOpt).hasArg().build());
        options.addOption(Option.builder("lo").longOpt(enableLinearOrderOpt).hasArg().build());
        options.addOption(Option.builder("ext").longOpt(enableExtendedReduceOpt).hasArg().build());
        options.addOption(Option.builder("ct").longOpt(enableCallTraceOpt).hasArg().build());
        CommandLineParser parser=new DefaultParser();
        CommandLine cmd = parser.parse(options,args);
        if(cmd.hasOption("help")){
            System.out.println(helpText);
            return;
        }
        String baseDirectory=".";
        String targetFileName;
        String testBashName;
        if(cmd.hasOption(baseDirectoryOpt)){
            baseDirectory=cmd.getOptionValue(baseDirectoryOpt);
        }
        if(cmd.hasOption(targetFileNameOpt)){
            targetFileName=cmd.getOptionValue(targetFileNameOpt);
        }else {
            System.out.println(helpText);
            return;
        }
        if(cmd.hasOption(testBashNameOpt)){
            testBashName=cmd.getOptionValue(testBashNameOpt);
        }else {
            System.out.println(helpText);
            return;
        }
        if(cmd.hasOption(suppressTimeoutOpt)){
            ExpressionReducer.suppressTimeout=(cmd.getOptionValue(suppressTimeoutOpt).equals("1") ||cmd.getOptionValue(suppressTimeoutOpt).equals("true"));
        }
        Processor processor=new Processor(baseDirectory,targetFileName,testBashName);
        if(cmd.hasOption(enableCoverageOpt)){
            processor.setEnableCoverage(cmd.getOptionValue(enableCoverageOpt).equals("1") ||cmd.getOptionValue(enableCoverageOpt).equals("true"));
        }
        if(cmd.hasOption(callTraceRandomOpt)){
            processor.setCallTraceRandom(cmd.getOptionValue(callTraceRandomOpt).equals("1") ||cmd.getOptionValue(callTraceRandomOpt).equals("true"));
        }
        if(cmd.hasOption(enableLinearOrderOpt)){
            processor.setLinearOrderOpt(cmd.getOptionValue(enableLinearOrderOpt).equals("1") ||cmd.getOptionValue(enableLinearOrderOpt).equals("true"));
        }
        if(cmd.hasOption(enableExtendedReduceOpt)){
            processor.setExtendedReduce(cmd.getOptionValue(enableExtendedReduceOpt).equals("1") ||cmd.getOptionValue(enableExtendedReduceOpt).equals("true"));
        }
        if(cmd.hasOption(enableCallTraceOpt)){
            processor.setEnableCallTrace(cmd.getOptionValue(enableCallTraceOpt).equals("1") ||cmd.getOptionValue(enableCallTraceOpt).equals("true"));
        }
        processor.run();
    }
}
