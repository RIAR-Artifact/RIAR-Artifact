package dynamicReduce.reducer;

import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import dynamicReduce.parser.rewriter.Rewriter;
import dynamicReduce.tester.TestRecord;

public class DeclPair {
    IASTSimpleDeclaration decl1;
    IASTSimpleDeclaration decl2;
    public DeclPair(IASTSimpleDeclaration decl1,IASTSimpleDeclaration decl2){
        this.decl1=decl1;
        this.decl2=decl2;
    }
    @Override
    public String toString(){
        return decl1.getFileLocation().getStartingLineNumber()+":"+decl1.getRawSignature()+"<>"
                +decl2.getFileLocation().getStartingLineNumber()+":"+decl2.getRawSignature();
    }

    public void inline(){
        Rewriter rewriter=new Rewriter(TestRecord.v().getLastSuccess());
        IASTDeclarator[] declarators1=decl1.getDeclarators();
        IASTDeclarator[] declarators2=decl2.getDeclarators();
        IASTFileLocation lastLocation1= declarators1[declarators1.length-1].getFileLocation();
        IASTFileLocation firstLocation2= declarators2[0].getFileLocation();
        IASTFileLocation lastLocation2= declarators2[declarators2.length-1].getFileLocation();
        int targetPoint=lastLocation1.getNodeOffset()+lastLocation1.getNodeLength();
        rewriter.insertAt(", "+rewriter.getText(firstLocation2.getNodeOffset(),lastLocation2.getNodeOffset()+lastLocation2.getNodeLength()),
                lastLocation1.getNodeOffset()+lastLocation1.getNodeLength());
        rewriter.removeNode(decl2);
        rewriter.finish();
    }


}
