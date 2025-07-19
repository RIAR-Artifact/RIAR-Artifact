package DynamicReduce;

import org.eclipse.jdt.core.dom.ASTNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeConstraint {
    public static NodeConstraint v=new NodeConstraint();
    public Map<ASTNode, List<ASTNode>> constraints;

    public NodeConstraint(){
        constraints=new HashMap<>();
    }

    public void addConstraint(ASTNode unfixed,ASTNode anchor){
        constraints.putIfAbsent(unfixed,new ArrayList<>());
        constraints.get(unfixed).add(anchor);
    }

    public List<ASTNode> getConstraint(ASTNode node){
        return constraints.get(node);
    }
}
