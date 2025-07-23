package dynamicReduce.parser;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import dynamicReduce.parser.rewriter.Rewriter;

import java.util.List;

public class ReversedDependencyRewriter extends DependencyRewriter{

    public ReversedDependencyRewriter(Rewriter rewriter, IASTTranslationUnit ast) {
        super(rewriter, ast);
    }
    @Override
    public boolean reduceNodes(List<IASTNode> nodes) {
        List<IASTNode> toDelete=dependencyGraph.expandDeletions(nodes);
        if(toDelete.isEmpty()){
            return false;
        }
        for (IASTNode node:toDelete
        ) {
            nodeUnwrapper.deleteNode(node);
        }
        nodeUnwrapper.finish();
        return true;
    }
}
