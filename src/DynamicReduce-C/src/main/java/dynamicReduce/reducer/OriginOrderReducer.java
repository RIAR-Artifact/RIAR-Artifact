package dynamicReduce.reducer;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IBinding;
import dynamicReduce.parser.FileParser;
import dynamicReduce.parser.UniqueID;

import java.util.ArrayList;

public class OriginOrderReducer extends CallTraceReducer{

    public OriginOrderReducer(FileParser parser) {
        super(parser);
    }

    @Override
    protected void findFunctions() {
        functions=new ArrayList<>();
        parser.getAST().accept(new ASTVisitor(true) {
            @Override
            public int visit(IASTDeclaration declaration) {
                if(declaration instanceof IASTFunctionDefinition function){
                    IBinding binding=function.getDeclarator().getName().getBinding();
                    UniqueID id=new UniqueID(binding);
                    String idString=id.toString();
                    if(!functions.contains(idString)){
                        functions.add(idString);
                    }
                }
                return super.visit(declaration);
            }
        });
    }
}