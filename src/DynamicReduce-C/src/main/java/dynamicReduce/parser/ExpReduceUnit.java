package dynamicReduce.parser;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import dynamicReduce.parser.rewriter.Rewriter;

import java.util.ArrayList;
import java.util.List;

public class ExpReduceUnit {
    IASTNode originNode;
    IASTNode replaceNode;
    String replaceString;
    List<int[]> deleteSpace;
    String text;

    public ExpReduceUnit(IASTNode originNode, IASTNode replaceNode) {
        this.originNode = originNode;
        this.replaceNode = replaceNode;
        replaceString=null;
    }
    public ExpReduceUnit(IASTNode originNode, String replaceString){
        this.originNode = originNode;
        this.replaceString=replaceString;
        replaceNode=null;
    }
    public ExpReduceUnit(int start,int end,String text){
        this.originNode = null;
        this.replaceString=null;
        replaceNode=null;
        deleteSpace=new ArrayList<>();
        deleteSpace.add(new int[]{start, end});
        this.text=text;
    }
    public ExpReduceUnit(List<int[]> deleteSpace, String text){
        this.deleteSpace=deleteSpace;
        this.originNode = null;
        this.replaceString=null;
        replaceNode=null;
        this.text=text;
    }
    public void apply(Rewriter rewriter){
        if(replaceString != null){
            rewriter.replaceNode(replaceString,originNode);
            return;
        }
        if(replaceNode != null){
            rewriter.replaceNode(replaceNode,originNode);
            return;
        }
        if(originNode != null){
            rewriter.removeNode(originNode);
            return;
        }
        for (int[] space:deleteSpace){
            rewriter.removePhrase(space[0],space[1]);
        }
    }
    public String getIdentifier(){
        if(originNode!=null){
            return originNode.getRawSignature();
        }
        return "";
    }
    @Override
    public String toString(){
        if(replaceString != null){
            return originNode.getRawSignature()+" -> "+replaceString;
        }
        if(replaceNode != null){
            return originNode.getRawSignature()+" -> "+replaceNode.getRawSignature();
        }
        if(originNode != null){
            return originNode.getRawSignature()+" -> ";
        }
        return text+" -> ";
    }
}
