package dynamicReduce.reducer;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import dynamicReduce.parser.FileParser;
import dynamicReduce.parser.NodeUnwrapper;
import dynamicReduce.utils.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GlobalReducer extends Reducer {
    List<IASTNode> toDelete;
    public GlobalReducer(FileParser parser) {
        super(parser);
        toDelete=new ArrayList<>();
    }

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
    @Override
    public void run() {
        findAllStatements();
        DeltaSelector<IASTNode> nodeSelector=new DeltaSelector<>(toDelete);
        standardReduceProcess(nodeSelector, this::findAllStatements);
    }
}
