package dynamicReduce.parser;

import dynamicReduce.parser.rewriter.Rewriter;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import dynamicReduce.utils.Logger;

import java.util.List;

public class DependencyRewriter implements FileRewriter<IASTNode>{
    DependencyGraph dependencyGraph;
    NodeUnwrapper nodeUnwrapper;


    public DependencyRewriter(Rewriter rewriter, IASTTranslationUnit ast) {
        nodeUnwrapper=new NodeUnwrapper(rewriter);
        dependencyGraph=new DependencyGraph(ast);
    }

    @Override
    public boolean reduceNodes(List<IASTNode> nodes) {
        List<IASTNode> toDelete=dependencyGraph.expandDeletions(nodes);
        if(nodes.size()==1){
            Logger.logFile("deleting "+Logger.lessText(nodes.get(0)));
        }
        for (IASTNode node:toDelete
             ) {
            if(nodes.size()==1){
                Logger.logFile("Actually deleting "+Logger.lessText(node));
            }
            nodeUnwrapper.deleteNodeUnwrap(node);
        }
        nodeUnwrapper.finish();
        return true;
    }
}
