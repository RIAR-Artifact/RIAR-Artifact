package DynamicReduce;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditGroup;
import org.eclipse.text.edits.UndoEdit;

import java.util.*;

import static DynamicReduce.ConstraintAnalyst.getLineNumber;

public class Constraint {
    public class SingleConstraint{
        public boolean contain=false;
        public ASTNode node;
        public SingleConstraint(ASTNode node){
            this.node=node;
        }
        public SingleConstraint(ASTNode node,boolean contain){
            this.node=node;
            this.contain=contain;
        }
    }
    public static Constraint v=new Constraint();
    public static NodeUnwrapper nodeUnwrapper=new NodeUnwrapper();
    Map<ASTNode, List<SingleConstraint>> dependencies;
    Set<ASTNode> deletedNodes;
    List<ASTNode> toDelete;

    private UndoEdit undoEdit;
    MultiTextEdit multiEdit = new MultiTextEdit();
    Constraint(){
        dependencies=new HashMap<>();
        deletedNodes=new HashSet<>();
        toDelete =new ArrayList<>();
    }
    public void clear(){
        dependencies.clear();
        deletedNodes.clear();
        toDelete.clear();
    }
    public void addConstraint(ASTNode defNode,ASTNode useNode){
        if(defNode==useNode){return;}
        if(!dependencies.containsKey(defNode)){
            dependencies.put(defNode,new ArrayList<>());
        }
        dependencies.get(defNode).add(new SingleConstraint(useNode));
    }
    public void addContainConstraint(ASTNode defNode,ASTNode useNode){
        if(defNode==useNode){return;}
        if(!dependencies.containsKey(defNode)){
            dependencies.put(defNode,new ArrayList<>());
        }
        dependencies.get(defNode).add(new SingleConstraint(useNode,true));
    }
    public List<SingleConstraint> getConstraint(ASTNode defNode){
        return dependencies.get(defNode);
    }
    private void deleteNode(ASTNode node)  {
        if(deletedNodes.contains(node)){return;}
        //System.out.println("delete node: "+node);

        if(nodeUnwrapper.canUnwrap(node)) {
            nodeUnwrapper.unwrapStmt(node);
        }else {
            MainReducer.rewriter.remove(node,new TextEditGroup("node remove"));
        }
        //multiEdit.addChild(textEdit);
        deletedNodes.add(node);
    }
    public boolean deleteNodeForce(ASTNode node,int depth){
        toDelete.clear();
        boolean flag=checkNodeForce(node,depth);
        if(flag){
            for (ASTNode rmNode:toDelete
                 ) {
                deleteNode(rmNode);
            }
        }else {
            System.out.println("delete failed for: "+node);
        }
        return flag;
    }
    public boolean deleteNodeForcePrev(ASTNode node,int depth){
        if (depth<0){return false;}
        if(deletedNodes.contains(node)){return true;}
        if(!dependencies.containsKey(node)){
            deleteNode(node);
            return true;
        }
        //System.out.println("deleteNodeForce: "+node);
        boolean flag=true;
        for (SingleConstraint sc:dependencies.get(node)
             ) {
            if(sc.contain){
                deletedNodes.add(sc.node);
                if(!checkNodeForce(sc.node,depth)){
                    flag=false;
                }
                continue;
            }
            if(!deleteNodeForcePrev(sc.node,depth-1)){
                flag=false;
            }
        }
        if(flag){
            deleteNode(node);
        }
        return flag;
    }
    private boolean checkNodeForce(ASTNode node,int depth){
        if (depth<0){return false;}
        toDelete.add(node);
        if(!dependencies.containsKey(node)){
            return true;
        }
        boolean flag=true;
        for (SingleConstraint sc:dependencies.get(node)
        ) {
            if(toDelete.contains(sc.node)){continue;}
            if(nodeUnwrapper.canUnwrap(node)&&sc.contain){continue;}
            //if(sc.contain){continue;}
            if(!checkNodeForce(sc.node,depth-1)){
                flag=false;
                System.out.println("delete failed at: "+sc.node);
                break;
            }
        }
        return flag;
    }
    public void applyDelete(){
        nodeUnwrapper.apply(deletedNodes);
        try {
            //multiEdit.apply(MainReducer.document);
            TextEdit textEdit= MainReducer.rewriter.rewriteAST(MainReducer.document, null);
            MainReducer.rewriter=ASTRewrite.create(MainReducer.cu.getAST());
            undoEdit=textEdit.apply(MainReducer.document);
        } catch (BadLocationException | NullPointerException e) {
            e.printStackTrace();System.exit(1);
        }
        deletedNodes.clear();
    }
    public void undoDelete(){
        if(undoEdit!=null){
            try {

                undoEdit.apply(MainReducer.document);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }
    public void printConstraint(){
        Set<ASTNode> keys=dependencies.keySet();
        for (ASTNode def:keys
             ) {
            String text="node "+getLineNumber(def)+" "+def;
            if(text.charAt(text.length()-1)=='\n'){
                text=text.substring(0,text.length()-1);
            }
            System.out.println(text);
            for (SingleConstraint sc:dependencies.get(def)
                 ) {
                if(sc.contain){
                    text="    cotains "+getLineNumber(sc.node)+" ";
                }else {
                    text="    depends "+getLineNumber(sc.node)+" ";
                }
                System.out.println(text+sc.node);
            }
        }
    }
}
