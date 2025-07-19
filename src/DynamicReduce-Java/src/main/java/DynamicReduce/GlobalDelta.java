package DynamicReduce;

import org.eclipse.jdt.core.dom.*;

import java.util.ArrayList;
import java.util.List;

public class GlobalDelta {
    public static List<ASTNode> nodes=new ArrayList<>();
    public static boolean statementLevel=false;
    public static boolean blockUndone=false;
    public static int forceDepth=100;
    public static List<ASTNode> additionalNodes=new ArrayList<>();
    public static boolean outputFile=true;

    public static void test(){
        MainReducer.tempOutput();
        if(MainReducer.testOutput(false)){
            System.out.println("Test Success");
        }else {
            System.out.println("Test Failed");
        }
    }

    public static void globalDDPrev(){
        //Constraint.v.printConstraint();
        MainReducer.analyseCurrent();
        nodes.clear();
        findElement();
        DeltaDebugging(nodes);
    }

    public static void globalDD(){
        //Constraint.v.printConstraint();
        MainReducer.analyseCurrent();
        nodes.clear();
        findElement();
        DeltaSelector<ASTNode> deltaSelector=new DeltaSelector<>(nodes);
        while (!deltaSelector.ended){
            List<ASTNode> toReduce=deltaSelector.get();
            for (int i = 0; i < toReduce.size(); i++) {
                Constraint.v.deleteNodeForce(toReduce.get(i), forceDepth);
            }
            Constraint.v.applyDelete();

            MainReducer.tempOutput();

            boolean result=MainReducer.testOutput(false);
            if(result){
                System.out.println("DD success!");
                MainReducer.analyseCurrent();
                findElement();
                deltaSelector.success(nodes);
            }else {
                System.out.println("DD failed!");
                Constraint.v.undoDelete();
                MainReducer.decentWorkingDir();
                deltaSelector.fail();
            }
        }
    }








    public static void globalDD1Step(){
        MainReducer.analyseCurrent();
        nodes.clear();
        findElement();
        int step=nodes.size()/2;
        while (step>0){
            step=DeltaDebugging1Step(nodes,step);
            MainReducer.analyseCurrent();
            nodes.clear();
            findElement();
        }

    }


    public static boolean testScheme(List<ASTNode> nodesToDelete,List<Boolean> deleteNodes){
        StringBuilder text= new StringBuilder("start delete: ");
        for (int i = 0; i < deleteNodes.size(); i++) {
            if(deleteNodes.get(i)){
                text.append(",").append(i);
            }
        }
        System.out.println(text);
        deleteNodeList(nodesToDelete,deleteNodes);
        MainReducer.tempOutput();
        boolean res=MainReducer.testOutput(false);
        if(!res){
            //MainReducer.decentWorkingDir();
            System.out.println("DD Failed");
        }else {System.out.println("DD Success!!!");}
        Constraint.v.undoDelete();
        return res;
    }

    public static void deleteNodeList(List<ASTNode> nodesToDelete_arg, List<Boolean> deleteNodes_arg){
        List<ASTNode> nodesToDelete=new ArrayList<>();
        List<Boolean> deleteNodes=new ArrayList<>();
        for (ASTNode node:additionalNodes
             ) {
            nodesToDelete.add(0,node);
            deleteNodes.add(0,true);
        }
        nodesToDelete.addAll(nodesToDelete_arg);
        deleteNodes.addAll(deleteNodes_arg);
        List<Boolean> deleted=new ArrayList<>();
        List<Boolean> lastTime=new ArrayList<>();
        for (int i = 0; i < deleteNodes.size(); i++) {
            deleted.add(false);lastTime.add(false);
        }
        while (true) {
            for (int i = 0; i < nodesToDelete.size(); i++) {
                if (deleteNodes.get(i)&&!deleted.get(i)) {
                    deleted.set(i,Constraint.v.deleteNodeForce(nodesToDelete.get(i), forceDepth));
                }
            }
            if(deleteNodes.equals(deleted)){break;}
            if(lastTime.equals(deleted)){break;}
            for (int i = 0; i < deleteNodes.size(); i++) {
                lastTime.set(i,deleted.get(i));
            }
            System.out.println("another round");
        }
        Constraint.v.applyDelete();
    }

    public static List<ASTNode>  DeltaDebugging(List<ASTNode> nodesToDelete){
        //Constraint.v.printConstraint();
        List<Boolean> deleteNodes=new ArrayList<>();
        List<Boolean> fixedDelete=new ArrayList<>();
        for (ASTNode node:nodesToDelete
             ) {
            deleteNodes.add(false);
            fixedDelete.add(false);
        }
        int total=nodesToDelete.size();
        int step=total;
        while (step>0){
            System.out.println("step: "+step);

            boolean skip=false;/*
            for (int i = 0; i < total&&step>1;) {
                for (int j = 0; j < total; j++) {
                    deleteNodes.set(j,fixedDelete.get(j));
                }
                int j = 0;
                while (j < step && i<total) {
                    if (!deleteNodes.get(i)){
                        deleteNodes.set(i,true);
                        j++;
                    }
                    i++;
                }
                for (int k = 0; k < total; k++) {
                    if(fixedDelete.get(k)){continue;}
                    deleteNodes.set(k,!deleteNodes.get(k));
                }
                if(testScheme(nodesToDelete,deleteNodes)){
                    skip=true;
                    int fl=0;
                    for (int k = 0; k < total; k++) {
                        if(!deleteNodes.get(k)){
                            fl++;
                        }
                        fixedDelete.set(k,deleteNodes.get(k));
                    }
                    if (fl<=step){step=step/2;}
                    break;
                }
            }
            if(skip){continue;}

             */
            for (int i = 0; i < total;) {
                for (int j = 0; j < total; j++) {
                    deleteNodes.set(j,fixedDelete.get(j));
                }
                int j = 0;
                while (j < step && i<total) {
                    if (!deleteNodes.get(i)){
                        deleteNodes.set(i,true);
                        j++;
                    }
                    i++;
                }
                if(j==0){break;}
                /*
                for (int k = 0; k < total; k++) {
                    deleteNodes.set(k,false);
                }
                deleteNodes.set(8,true);
                //deleteNodes.set(5,true);

                 */
                if(testScheme(nodesToDelete,deleteNodes)){
                    skip=true;
                    int fl=0;
                    for (int k = 0; k < total; k++) {
                        if(!deleteNodes.get(k)){
                            fl++;
                        }
                        fixedDelete.set(k,deleteNodes.get(k));
                    }
                    while (fl<step){step=step/2;}
                    break;
                }
            }
            System.out.println("??/");
            if(!skip){
                if(step<=1){break;}
                step=step/2;
            }
        }
        if(outputFile){
            deleteNodeList(nodesToDelete,fixedDelete);
            MainReducer.tempOutput();
        }
        List<ASTNode> resNodes=new ArrayList<>();
        for (int i = 0; i < nodesToDelete.size(); i++) {
            if(fixedDelete.get(i)){
                resNodes.add(nodesToDelete.get(i));
            }
        }
        return resNodes;
    }


    public static int DeltaDebugging1Step(List<ASTNode> nodesToDelete,int step){
        int total=nodesToDelete.size();
        if(step>total){step=total/2;if(total==1){step=1;}}
        System.out.println("step: "+step);
        boolean success=false;
        for (int i = 0; i < total&&step>1;i+=step) {
            List<Boolean> deleteNodes=new ArrayList<>();
            for (int j = 0; j < total; j++) {
                deleteNodes.add(!(j>=i && j<i+step));
            }
            success=testScheme(nodesToDelete,deleteNodes);
            if(success){
                return step/2;
            }
        }
        for (int i = 0; i < total;i+=step) {

            List<Boolean> deleteNodes=new ArrayList<>();
            for (int j = 0; j < total; j++) {
                deleteNodes.add(j>=i && j<i+step);
            }
            success=testScheme(nodesToDelete,deleteNodes);
            if(success){
                if(step==1){return 1;}
                if(total>2*step){
                    return step;
                }
                return step/2;
            }
        }
        MainReducer.tempOutput();
        if(step==1){return 0;}
        return step/2;
    }
    public static int startPin=0;
    public static int DeltaDebugging1StepWithPin(List<ASTNode> nodesToDelete,int step){
        int total=nodesToDelete.size();
        if(step>total){step=total/2;if(total==1){step=1;}}
        System.out.println("step: "+step);
        boolean success=false;
        if(startPin>=total){startPin=0;}
        int i = startPin;
        while(true){
            List<Boolean> deleteNodes=new ArrayList<>();
            for (int j = 0; j < total; j++) {
                deleteNodes.add(j>=i && j<i+step);
            }
            success=testScheme(nodesToDelete,deleteNodes);

            if(success){
                startPin=i;
                if(step==1){return 1;}
                if(total>2*step){
                    return step;
                }
                return step/2;
            }else {
                MainReducer.decentWorkingDir();
            }
            if(i+step>=startPin&&i<startPin){
                break;
            }
            i+=step;
            if(i>=total){
                i=0;
            }
            if(i==startPin){
                break;
            }
        }
        startPin=0;
        MainReducer.tempOutput();
        if(step==1){return 0;}
        return step/2;
    }
    public static void findElement(){
        System.out.println("---StartFindElement---");
        nodes.clear();
        MainReducer.cu.accept(new FindEle());
    }

    public static boolean statementFilter(Statement statement){
        return statement.getLocationInParent() != ForStatement.BODY_PROPERTY && statement.getLocationInParent() != WhileStatement.BODY_PROPERTY
                && statement.getLocationInParent() != IfStatement.THEN_STATEMENT_PROPERTY && statement.getLocationInParent() != IfStatement.ELSE_STATEMENT_PROPERTY
                && statement.getLocationInParent() != TryStatement.BODY_PROPERTY && statement.getLocationInParent() != CatchClause.BODY_PROPERTY
                && statement.getLocationInParent() != TryStatement.FINALLY_PROPERTY && statement.getLocationInParent() != DoStatement.BODY_PROPERTY;
    }
    private static class FindEle extends ASTVisitor{
        @Override
        public void preVisit(ASTNode node) {
            //if(node instanceof Statement||node instanceof FieldDeclaration||node instanceof Initializer||node instanceof MethodDeclaration
            //||node instanceof ImportDeclaration){
            if(blockUndone){
                if(Constraint.nodeUnwrapper.loopBody(node)){
                    System.out.println("Statement"+nodes.size()+" :"+node);
                    nodes.add(node);
                }
                return;
            }
            if(node instanceof FieldDeclaration||node instanceof ImportDeclaration||node instanceof Initializer||node instanceof MethodDeclaration
                    ||node instanceof TypeDeclaration){
                if(node.getParent() instanceof MethodDeclaration){return;}
                System.out.println("HStatement"+nodes.size()+" :"+node);
                nodes.add(node);
            }else{
                if(!statementLevel){return;}
                if(node instanceof Statement statement){
                    if(node.getParent() instanceof MethodDeclaration){return;}
                    if(!statementFilter(statement)
                    ){
                        return;
                    }
                    System.out.println("Statement"+nodes.size()+" :"+node);
                    nodes.add(node);
                }
            }

        }
    }
}
