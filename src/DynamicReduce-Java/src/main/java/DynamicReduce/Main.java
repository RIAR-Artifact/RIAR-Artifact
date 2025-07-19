package DynamicReduce;

import org.apache.commons.cli.*;

import java.util.Objects;

public class Main {
    static String helpText="-d --targetDir : directory of target file\n" +
            "-j -jdkPath : path of one jdk that can run the target\n" +
            "-cp -classpath : classpath of target testcase\n" +
            "-coverage --enableCoverage : 1 or ture for coverage reduction\n" +
            "-r --reverse : 1 or ture for reversing call trace delta order\n" +
            "-es --expressionStep : start step of expression reduction\n" +
            "-cn --className : the name of public class in testcase\n" +
            "-fcn --fullClassName : full class name of entry class\n" +
            "-sd --subDirectory : sub directory required, for example \"package/\"\n"+
            "-ro --randomOrder: 1 or true for random order of call trace delta\n"+
            "-st --suppressTimeout: 1 or true to enable timeout suppression\n";
    public static void main(String[] args) {
        Options options=new Options();
        Option targetDir=Option.builder("d").longOpt("targetDir").hasArg().build();
        Option jdkPath=Option.builder("j").longOpt("jdkPath").hasArg().build();
        Option classPath=Option.builder("cp").longOpt("classPath").hasArg().build();
        Option enableCoverage=Option.builder("coverage").longOpt("enableCoverage").hasArg().build();
        Option reverse=Option.builder("r").longOpt("reverse").hasArg().build();
        Option expressionStep=Option.builder("es").longOpt("expressionStep").hasArg().build();
        Option className=Option.builder("cn").longOpt("className").hasArg().build();
        Option classFullName=Option.builder("fcn").longOpt("fullClassName").hasArg().build();
        Option subDirectory=Option.builder("sd").longOpt("subDirectory").hasArg().build();
        Option help=Option.builder("h").longOpt("help").build();
        Option randomOrder=Option.builder("ro").longOpt("randomOrder").hasArg().build();
        Option suppressTimeout=Option.builder("st").longOpt("suppressTimeout").hasArg().build();
        Option callTrace=Option.builder("ct").longOpt("callTrace").hasArg().build();
        Option linearOrder=Option.builder("lo").longOpt("linearOrder").hasArg().build();
        options.addOption(targetDir);
        options.addOption(jdkPath);
        options.addOption(classPath);
        options.addOption(enableCoverage);
        options.addOption(reverse);
        options.addOption(expressionStep);
        options.addOption(className);
        options.addOption(classFullName);
        options.addOption(subDirectory);
        options.addOption(help);
        options.addOption(randomOrder);
        options.addOption(suppressTimeout);
        options.addOption(callTrace);
        options.addOption(linearOrder);
        CommandLineParser parser=new DefaultParser();
        try {
            CommandLine cmd=parser.parse(options,args);
            if(cmd.hasOption("targetDir")){
                MainReducer.baseDir=cmd.getOptionValue("targetDir");
                MainReducer.workingDir=MainReducer.baseDir;
            }
            if(cmd.hasOption("jdkPath")){
                OutputTester.JdkPath=cmd.getOptionValue("jdkPath");
            }
            if(cmd.hasOption("classPath")){
                OutputTester.classPath=cmd.getOptionValue("classPath");
            }
            if(cmd.hasOption("enableCoverage")){
                MainReducer.enableCoverage=(Objects.equals(cmd.getOptionValue("enableCoverage"), "1") ||Objects.equals(cmd.getOptionValue("enableCoverage"), "true"));
            }
            if(cmd.hasOption("reverse")){
                CallTraceDelta.reverse=(Objects.equals(cmd.getOptionValue("reverse"), "1") ||Objects.equals(cmd.getOptionValue("reverse"), "true"));
            }
            if(cmd.hasOption("expressionStep")){
                String step=cmd.getOptionValue("expressionStep");
                ExpressionAnalyst.step=Integer.parseInt(step);
            }
            if(cmd.hasOption("className")){
                MainReducer.targetClass=cmd.getOptionValue("className");
            }
            if(cmd.hasOption("fullClassName")){
                MainReducer.fullClassName=cmd.getOptionValue("fullClassName");
            }
            if(cmd.hasOption("subDirectory")){
                OutputTester.subDirectory=cmd.getOptionValue("subDirectory");
            }
            if(cmd.hasOption("randomOrder")){
                CallTraceDelta.randomOrder=(Objects.equals(cmd.getOptionValue("randomOrder"), "1") ||Objects.equals(cmd.getOptionValue("randomOrder"), "true"));
            }
            if(cmd.hasOption("suppressTimeout")){
                ExpressionAnalyst.suppressTimeout=(Objects.equals(cmd.getOptionValue("suppressTimeout"), "1") ||Objects.equals(cmd.getOptionValue("suppressTimeout"), "true"));
            }
            if(cmd.hasOption("callTrace")){
                CallTraceDelta.enabled=(Objects.equals(cmd.getOptionValue("callTrace"), "1") ||Objects.equals(cmd.getOptionValue("callTrace"), "true"));
            }
            if(cmd.hasOption("linearOrder")){
                CallTraceDelta.linearOrder=(Objects.equals(cmd.getOptionValue("linearOrder"), "1") ||Objects.equals(cmd.getOptionValue("linearOrder"), "true"));
            }
            if(cmd.hasOption("help")){
                System.out.println(helpText);
                return;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        MainReducer.run();
    }
}