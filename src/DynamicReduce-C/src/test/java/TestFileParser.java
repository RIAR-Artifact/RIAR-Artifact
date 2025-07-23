import dynamicReduce.parser.FileParser;
import dynamicReduce.parser.RewriterType;
import dynamicReduce.tester.TestRecord;
import dynamicReduce.utils.Logger;
import org.eclipse.cdt.core.dom.ast.*;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestFileParser {
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        TestRecord.initialize("testcases"+ File.separator+"test1","Test.c","test.sh");

        FileParser fileParser=new FileParser(RewriterType.DEPENDENCY_REWRITER);
        final IASTNode[] node = new ASTNode[2];
        fileParser.getAST().accept(new ASTVisitor(true) {
            {
                shouldVisitDeclarations=true;
                shouldVisitDeclarators=true;
            }
            @Override
            public int visit(IASTDeclaration declaration) {
                if (declaration instanceof IASTSimpleDeclaration simpleDeclaration1) {
                    IASTNode parent=declaration.getParent();
                    if(parent instanceof IASTDeclarationStatement declarationStatement){
                    Logger.log(parent.getRawSignature());
                    Logger.log(simpleDeclaration1.getDeclarators()[0].getRawSignature());
                    }
                    String decl1=simpleDeclaration1.getDeclSpecifier().getRawSignature();
                    Logger.log("decl1 "+decl1);
                    boolean flag=true;
                    for (IASTNode child:parent.getChildren()){
                        if(flag){
                            if(child instanceof IASTSimpleDeclaration simpleDeclaration2){
                                String decl2=simpleDeclaration2.getDeclSpecifier().getRawSignature();
                                Logger.log("decl2 "+decl2);
                            }
                        }
                        if(child.getFileLocation().getNodeOffset()==declaration.getFileLocation().getNodeOffset()){flag=true;}
                    }
                }
                return PROCESS_CONTINUE;
            }

        });
        List<IASTNode> toDelete=new ArrayList<>();
        fileParser.getFileRewriter().reduceNodes(toDelete);
    }
}
