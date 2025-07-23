package dynamicReduce.reducer;

import org.eclipse.cdt.core.dom.ast.*;
import dynamicReduce.parser.UniqueID;
import dynamicReduce.parser.rewriter.Rewriter;
import dynamicReduce.tester.CallTraceTester;
import dynamicReduce.tester.TestContext;
import dynamicReduce.tester.TestRecord;

import java.util.List;

public class CallTraceHelper {
    IASTTranslationUnit ast;
    static final String include="extern long write(long fd, const void *buf, long count);\n";
    static final String prefix="Entering function ?+?";
    static String getPrintStmt(UniqueID id){
        return "const char message44347[] = \"\\n"+prefix+id.toString()+"\\n\";write(1, message44347, sizeof(message44347) - 1);";
    }

    public CallTraceHelper(IASTTranslationUnit ast) {
        this.ast=ast;
    }
    private void removeConflicts(Rewriter rewriter){
        ast.accept(new ASTVisitor(true) {
            @Override
            public int visit(IASTDeclaration declaration) {
                if (declaration instanceof IASTSimpleDeclaration simpleDeclaration) {
                    if (simpleDeclaration.getDeclSpecifier() instanceof IASTSimpleDeclSpecifier &&
                            simpleDeclaration.getDeclSpecifier().getStorageClass() == IASTDeclSpecifier.sc_typedef) {

                        IASTDeclarator[] declarators = simpleDeclaration.getDeclarators();
                        for (IASTDeclarator d : declarators) {
                            String typedefName = d.getName().toString();
                            if ("size_t".equals(typedefName)) {
                                rewriter.removeNode(declaration);
                            }
                        }
                    }
                }
                return PROCESS_CONTINUE;
            }
        });
    }
    private void insertTrace(){
        Rewriter rewriter=new Rewriter(TestRecord.v().getLastSuccess());
        rewriter.insertAt(include,0);
        //removeConflicts(rewriter);
        ast.accept(new ASTVisitor(true) {
            @Override
            public int visit(IASTDeclaration declaration) {
                if(declaration instanceof IASTFunctionDefinition function){
                    IASTStatement body=function.getBody();
                    IBinding binding=function.getDeclarator().getName().getBinding();
                    UniqueID id=new UniqueID(binding);
                    int targetPos=0;
                    if(body instanceof IASTCompoundStatement){
                        targetPos=body.getFileLocation().getNodeOffset()+1;
                    }else {
                        targetPos=body.getFileLocation().getNodeOffset();
                    }
                    rewriter.insertAt(getPrintStmt(id),targetPos);
                }
                return super.visit(declaration);
            }
        });
        rewriter.finish();
    }


    public List<String> getFunctionTrace(){
        insertTrace();
        TestContext context=new CallTraceTester().run();
        return context.getCallTraceResolver().resolveTrace(prefix);
    }


}
