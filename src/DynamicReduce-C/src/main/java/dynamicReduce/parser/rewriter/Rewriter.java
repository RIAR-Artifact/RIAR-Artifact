package dynamicReduce.parser.rewriter;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import dynamicReduce.tester.TestRecord;

import java.util.*;

public class Rewriter {
    enum CharTransformation{
        UNCHANGED, MOVED, REMOVED
    }
    class Insertion {
        String text=null;
        int targetPos;//the inserted text will start on target pos
        int index;
        InsertionWrapping wrapping;
        public Insertion(String text, int to,InsertionWrapping wrapping){
            index=insertionIndex++;
            this.text=text;
            targetPos=to;
            this.wrapping=wrapping;
        }

        public int getTargetPos() {
            return targetPos;
        }

        public int getIndex() {
            return index;
        }

        String getText(){
            switch (wrapping){
                case NO_WRAPPING : {return text;}
                case PRE_SPACE : {return " "+text;}
                case POST_SPACE : {return text+" ";}
                case ROUND_SPACE : {return " "+text+" ";}
                case POST_RETURN : {return text+"\n";}
                default : {return text;}
            }
        }
    }
    int insertionIndex=0;
    String originalText;
    List<CharTransformation> charTransformations;
    List<Insertion> insertions;
    public Rewriter(String fileText){
        originalText=fileText;
        charTransformations =new ArrayList<>(Collections.nCopies(fileText.length(), CharTransformation.UNCHANGED));
        insertions=new ArrayList<>();
    }

    public void reset(){
        Collections.fill(charTransformations, CharTransformation.UNCHANGED);
        insertions.clear();
    }
    public void removeNode(IASTNode node){
        IASTFileLocation loc = node.getFileLocation();
        int offset = loc.getNodeOffset();
        int length = loc.getNodeLength();
        for (int i = 0; i < length; i++) {
            if(charTransformations.get(i+offset)==CharTransformation.UNCHANGED){
                charTransformations.set(i+offset,CharTransformation.REMOVED);
            }
        }
    }
    public void removePhrase(int start, int end){
        for (int i = start; i < end; i++) {
            charTransformations.set(i,CharTransformation.REMOVED);
        }
    }
    public void insertAt(String text,int pos){
        insertAt(text,pos,InsertionWrapping.NO_WRAPPING);
    }
    public void insertAt(String text,int pos,InsertionWrapping wrapping){
        insertions.add(new Insertion(text,pos,wrapping));
    }

    public void insertBefore(String text,IASTNode target){
        insertBefore(text,target,InsertionWrapping.NO_WRAPPING);
    }
    public void insertBefore(String text,IASTNode target,InsertionWrapping wrapping){
        IASTFileLocation targetLocation = target.getFileLocation();
        insertions.add(new Insertion(text,targetLocation.getNodeOffset(),wrapping));
    }
    public void insertAfter(String text,IASTNode target){
        insertAfter(text,target,InsertionWrapping.NO_WRAPPING);
    }
    public void insertAfter(String text,IASTNode target,InsertionWrapping wrapping){
        IASTFileLocation targetLocation = target.getFileLocation();
        insertions.add(new Insertion(text,targetLocation.getNodeOffset()+targetLocation.getNodeLength(),wrapping));
    }

    public void replaceNode(String text,IASTNode target){
        replaceNode(text,target,InsertionWrapping.NO_WRAPPING);
    }
    public void replaceNode(String text,IASTNode target,InsertionWrapping wrapping){

        IASTFileLocation targetLocation = target.getFileLocation();
        if(targetLocation.getNodeLength()>0){
            insertAt(text,targetLocation.getNodeOffset()+1,wrapping);
        }else {
            insertBefore(text,target,wrapping);
        }
        removeNode(target);
    }
    public void replaceNode(IASTNode source,IASTNode target){
        replaceNode(source,target,InsertionWrapping.NO_WRAPPING);
    }
    public void replaceNode(IASTNode source,IASTNode target,InsertionWrapping wrapping){
        IASTFileLocation targetLocation = target.getFileLocation();
        if(targetLocation.getNodeLength()>0){
            insertAt(getNodeText(source),targetLocation.getNodeOffset()+1,wrapping);
        }else {
            insertBefore(getNodeText(source),target,wrapping);
        }
        removeNode(target);
    }
    public void replacePhase(int start,int end,String text){
        replacePhase(start,end,text,InsertionWrapping.NO_WRAPPING);
    }
    public void replacePhase(int start,int end,String text,InsertionWrapping wrapping){
        if(end-start>0){
            insertAt(text,start+1,wrapping);
        }else {
            insertAt(text,start,wrapping);
        }
        removePhrase(start,end);
    }
    void sortInsertions(){
        insertions.sort(Comparator.comparing(Insertion::getTargetPos).thenComparing(Insertion::getIndex));
    }

    public String getNodeText(IASTNode node){
        IASTFileLocation fileLocation=node.getFileLocation();
        return getText(fileLocation.getNodeOffset(),fileLocation.getNodeOffset()+fileLocation.getNodeLength());
    }

    public String getText(int start,int end){
        return getText(start,end,false);
    }
    public String getText(int start,int end,boolean clean){
        sortInsertions();
        int insertionIndex=0;
        while (insertionIndex<insertions.size()&&insertions.get(insertionIndex).targetPos<start){
            insertionIndex++;
        }
        StringBuilder sb=new StringBuilder();
        for (int i = start; i < end; i++) {
            while (insertionIndex<insertions.size()&&i==insertions.get(insertionIndex).targetPos){
                sb.append(insertions.get(insertionIndex).getText());
                insertions.remove(insertionIndex);
            }
            if(charTransformations.get(i)==CharTransformation.UNCHANGED){
                sb.append(originalText.charAt(i));
            }else if(clean){
                charTransformations.set(i,CharTransformation.UNCHANGED);
            }
        }
        while (insertionIndex<insertions.size()&&end==insertions.get(insertionIndex).targetPos){
            sb.append(insertions.get(insertionIndex).getText());
            insertions.remove(insertionIndex);
        }
        return sb.toString();
    }
    protected String applyDelete(){
        return getText(0,originalText.length());
    }

    public void finish(){
        String rewroteText=applyDelete();
        TestRecord.v().newFileOutput(rewroteText);
        reset();
    }
}
