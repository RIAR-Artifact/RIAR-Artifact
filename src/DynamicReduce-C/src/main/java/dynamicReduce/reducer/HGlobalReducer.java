package dynamicReduce.reducer;

import org.eclipse.cdt.core.dom.ast.*;
import dynamicReduce.parser.FileParser;
import dynamicReduce.parser.NodeUnwrapper;
import dynamicReduce.utils.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HGlobalReducer extends GlobalReducer{
    public HGlobalReducer(FileParser parser) {
        super(parser);
    }
    @Override
    protected List<IASTNode> findAllStatements(){
        Set<IASTNode> toDelete = new HashSet<>(List.of(parser.getAST().getAllPreprocessorStatements()));

        parser.getAST().accept(new ASTVisitor(true) {
            @Override
            public int visit(IASTStatement statement) {
                if(NodeUnwrapper.canDelete(statement)) {
                    toDelete.add(statement);
                }
                return super.visit(statement);
            }

            @Override
            public int visit(IASTDeclaration declaration) {
                toDelete.add(declaration);
                if(declaration instanceof IASTFunctionDefinition){
                    return PROCESS_SKIP;
                }
                return super.visit(declaration);
            }
        });
        Logger.log("reducing ");
        for (IASTNode node:toDelete
        ) {
            Logger.log(node.getRawSignature());
            Logger.log("---");
        }
        this.toDelete.clear();
        this.toDelete.addAll(toDelete);
        return this.toDelete;
    }
}
