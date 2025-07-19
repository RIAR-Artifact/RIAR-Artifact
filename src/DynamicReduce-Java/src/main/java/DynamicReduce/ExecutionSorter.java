package DynamicReduce;

import org.eclipse.jdt.core.dom.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static DynamicReduce.MainReducer.decentWorkingDir;
import static DynamicReduce.MainReducer.workingDir;

public class ExecutionSorter {

    public static boolean isInsideMethodBody(ASTNode node) {
        ASTNode current = node;
        while (current != null) {
            if (current instanceof MethodDeclaration) {
                MethodDeclaration method = (MethodDeclaration) current;
                Block body = method.getBody();
                if (body == null) return false;
                return node != body && isDescendantOf(node, body);
            }
            current = current.getParent();
        }
        return false;
    }

    private static boolean isDescendantOf(ASTNode node, ASTNode root) {
        ASTNode parent = node.getParent();
        while (parent != null) {
            if (parent == root) return true;
            parent = parent.getParent();
        }
        return false;
    }
    public static Map<Integer, Integer> countFromFile() throws IOException {
        String outFile = workingDir+"out1.txt";
        String content = Files.readString(Path.of(outFile));
        Pattern pattern = Pattern.compile("<<\\s*(\\d+)\\s*,\\s*(\\d+)\\s*>>");
        Matcher matcher = pattern.matcher(content);

        Map<Integer, Integer> bucket = new HashMap<>();
        boolean find=false;
        while (matcher.find()) {
            find=true;
            int number = Integer.parseInt(matcher.group(1));
            int d = Integer.parseInt(matcher.group(2));
            bucket.put(number, d);
        }
        if(find){
            System.out.println("execution count found");
        }

        return bucket;
    }
    private static boolean isMainMethod(MethodDeclaration method) {
        if (!"main".equals(method.getName().getIdentifier())) return false;
        if (!Modifier.isStatic(method.getModifiers()) || !Modifier.isPublic(method.getModifiers())) return false;

        // 返回类型必须是 void
        if (!method.getReturnType2().isPrimitiveType() ||
                ((PrimitiveType) method.getReturnType2()).getPrimitiveTypeCode() != PrimitiveType.VOID) {
            return false;
        }

        return true;
    }

    static int getMainHook(){
        final Integer[] result = new Integer[1]; // 用数组容器以便修改

        MainReducer.cu.accept(new ASTVisitor() {
            @Override
            public boolean visit(MethodDeclaration node) {
                if (isMainMethod(node)) {
                    Block body = node.getBody();
                    if (body != null && !body.statements().isEmpty()) {
                        Statement firstStmt = (Statement) body.statements().get(0);
                        result[0] = firstStmt.getStartPosition();
                    }
                    return false; // 找到就停止
                }
                return true;
            }
        });
        return result[0];
    }

    static int getGlobalHook(){
        final Integer[] result = new Integer[1]; // 用数组容器以便修改

        MainReducer.cu.accept(new ASTVisitor() {
            @Override
            public boolean visit(MethodDeclaration node) {
                if (isMainMethod(node)) {
                    result[0] = node.getStartPosition();
                    return false; // 找到就停止
                }
                return true;
            }
        });
        return result[0];
    }

    public static String getSourceOfNode(ASTNode node) {
        int start = node.getStartPosition();
        int length = node.getLength();
        return MainReducer.sourceCode.substring(start, start + length);
    }

    Map<String,Integer> executionCount=new HashMap<>();
    public ExecutionSorter(List<ASTNode> nodes){
        System.out.println("execution count");
        Map<Integer,Integer> posCont=new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            if(isInsideMethodBody(nodes.get(i))){
                posCont.put(nodes.get(i).getStartPosition(),i);
            }
        }
        int mainHook=getMainHook();
        int globalHook=getGlobalHook();
        String source=MainReducer.sourceCode;
        StringBuilder output=new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            if(i==mainHook){
                output.append("        Runtime.getRuntime().addShutdownHook(new Thread(() -> {\n" +
                        "            for(int i_347=0;i_347<"+nodes.size()+";i_347++){" +
                        "                System.out.println(\"<<\"+i_347+\",\"+bucket_347[i_347]+\">>\");"+
                        "        }}));");
            }
            if(i==globalHook){int a;
                output.append("static int[] bucket_347=new int["+nodes.size()+"];\n");
                //                        "static void count_347(int c){bucket_347[c]++;}\n"
            }
            if(posCont.containsKey(i)){
                output.append("bucket_347[").append(posCont.get(i)).append("]++;");
            }
            output.append(source.charAt(i));
        }
        MainReducer.tempOutput(output.toString());
        OutputTester.runBashForce();
        Map<Integer, Integer> execCount=null;
        try {
            execCount=countFromFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        decentWorkingDir();
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            if(execCount.containsKey(i)){
                String sourceName=getSourceOfNode(nodes.get(i));
                if(executionCount.containsKey(sourceName)){
                    executionCount.put(sourceName,  Math.max(execCount.get(i),executionCount.get(sourceName)));
                }else {
                    executionCount.put(sourceName, execCount.get(i));
                }
            }
        }
    }

    private int getExecCount(ASTNode node){
        int[] count=new int[1];
        node.accept(new ASTVisitor() {
            @Override
            public void preVisit(ASTNode node) {
                if(node instanceof Statement){
                    String sourceName=getSourceOfNode(node);
                    if(executionCount.containsKey(sourceName)){
                        count[0]=Math.max(count[0],executionCount.get(sourceName));
                    }
                }
                super.preVisit(node);
            }
        });
        if(count[0]>0){return count[0];}
        String sourceName=getSourceOfNode(node);
        if(executionCount.containsKey(sourceName)){
            return executionCount.get(sourceName);
        }
        return 0;
    }

    public List<ASTNode> rearrange(List<ASTNode> nodes){

        Map<Integer, Integer> execCount=new HashMap<>();
        for (int i = 0; i < nodes.size(); i++) {
            execCount.put(i,getExecCount(nodes.get(i)));
        }

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            indices.add(i);
        }

        indices.sort((a, b) -> Integer.compare(
                execCount.getOrDefault(b, 0),
                execCount.getOrDefault(a, 0)
        ));
        List<ASTNode> sortedNodes = new ArrayList<>();
        for (int i : indices) {
            sortedNodes.add(nodes.get(i));
        }
        return sortedNodes;
    }
}
