package DynamicReduce;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

import static DynamicReduce.MainReducer.testCounter;

public class CoverageDelta {
    static List<ASTNode> uncoveredNodes=new ArrayList<>();
    static List<ASTNode> coveredNodes=new ArrayList<>();
    public static void start(){
        MainReducer.analyseCurrent();
        MainReducer.testOutput(true);
        MainReducer.cu.accept(new FindEle());
        GlobalDelta.DeltaDebugging(uncoveredNodes);
        uncoveredNodes.clear();
        coveredNodes.clear();
        MainReducer.analyseCurrent();
        MainReducer.testOutput(true);
        MainReducer.cu.accept(new FindEle());
        GlobalDelta.DeltaDebugging(uncoveredNodes);
        uncoveredNodes.clear();
        coveredNodes.clear();
        MainReducer.analyseCurrent();
        //MainReducer.testOutput(true);
        GlobalDelta.globalDD1Step();
    }
    public static void runOnce(){
        ConstraintAnalyst.findReturnConstraint=true;
        MainReducer.testOutput(true);
        MainReducer.analyseCurrent();
        MainReducer.cu.accept(new FindEle());
        for (ASTNode node:uncoveredNodes
        ) {
            System.out.println("uncovered "+node);
        }
        int step=uncoveredNodes.size();
        GlobalDelta.DeltaDebugging1StepWithPin(uncoveredNodes, step);
        uncoveredNodes.clear();
        coveredNodes.clear();


        ConstraintAnalyst.findReturnConstraint=false;
    }
    public static void run(){
        testCounter.startEffectCounter(2);
        MainReducer.testOutput(true);
        testCounter.endEffectCounter(2);
        MainReducer.analyseCurrent();
        MainReducer.cu.accept(new FindEle(true));
        int i=0;
        for (ASTNode node:uncoveredNodes
             ) {
            System.out.println("uncovered "+i+" :"+node);
            i++;
        }
        int step=uncoveredNodes.size();
        while (step>3){
            step = GlobalDelta.DeltaDebugging1StepWithPin(uncoveredNodes, step);
            if (step > 3) {
                uncoveredNodes.clear();
                coveredNodes.clear();
                MainReducer.testOutput(true);
                MainReducer.analyseCurrent();
                MainReducer.cu.accept(new FindEle());
                for (ASTNode node:uncoveredNodes
                ) {
                    System.out.println("uncovered "+node);
                }
            }
        }
    }

    private static class FindEle extends ASTVisitor {
        boolean blockTryStatement=false;
        public FindEle(boolean blockTryStatement){
            this.blockTryStatement=blockTryStatement;
        }
        public FindEle(){

        }
        @Override
        public void preVisit(ASTNode node) {
            if(node instanceof MethodDeclaration methodNode){
                for (Object annotation : methodNode.modifiers()) {
                    if (annotation instanceof MarkerAnnotation marker) {
                        if (marker.getTypeName().getFullyQualifiedName().equals("Override")) {
                            System.out.println("方法 " + methodNode.getName() + " 是接口或抽象类中的抽象方法实现");
                            return;
                        }
                    }
                }
                uncoveredNodes.add(node);

            }else if(node instanceof Initializer||node instanceof IfStatement
                    ||node instanceof TryStatement){

                uncoveredNodes.add(node);
            }else if(node instanceof Statement){
                if(node.getParent() instanceof MethodDeclaration){return;}
                if(node.getParent() instanceof Initializer){return;}
                if(node instanceof Block){
                    return;
                }
                if(node instanceof SwitchCase){
                    return;
                }
                int lineno=ConstraintAnalyst.getLineNumber(node);
                Boolean covered=CoverageResolver.covered.get(lineno);
                if(covered==null){
                    System.out.println("covered==null "+node);
                    coveredNodes.add(node);
                    return;
                }
                if(covered){
                    coveredNodes.add(node);
                    ASTNode parent=node.getParent();
                    while (!(parent instanceof CompilationUnit)){
                        if(parent instanceof MethodDeclaration||parent instanceof Initializer
                                ||parent instanceof TryStatement||parent instanceof IfStatement
                                ||parent instanceof DoStatement||parent instanceof ForStatement
                                ||parent instanceof WhileStatement
                        ){
                            uncoveredNodes.remove(parent);
                        }
                        parent=parent.getParent();
                    }
                }else {
                    ASTNode parent=node.getParent();
                    if(blockTryStatement){
                        while (!(parent instanceof CompilationUnit)){
                            if(parent instanceof TryStatement){
                                return;
                            }
                            parent=parent.getParent();
                        }
                    }
                    uncoveredNodes.add(node);
                }
                //System.out.println("node "+node);
            }
        }
    }
}
