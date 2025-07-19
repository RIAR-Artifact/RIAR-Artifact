package DynamicReduce;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static DynamicReduce.MainReducer.rewriter;

public class NodeUnwrapper {
    class StmtMovement {
        StmtMovement(ASTNode source, ListRewrite body, Statement pos){
            this.source=source;
            targetBody=body;targetPos=pos;
        }
        public ASTNode source;
        public ListRewrite targetBody;
        public Statement targetPos;
        public void insert(){
            targetBody.insertBefore(source, targetPos, null);
        }
    }
    List<StmtMovement> stmtMovements=new LinkedList<>();

    public boolean loopBody(ASTNode node){
        if(!GlobalDelta.blockUndone){return false;}
        if(!(node instanceof Block)){return false;}
        Block block=(Block) node;
        if(!(block.getParent() instanceof ForStatement||block.getParent() instanceof WhileStatement||block.getParent() instanceof IfStatement)){
            return false;
        }
        return (block.statements().size()==1);
    }
    boolean canUnwrap(ASTNode node){
        if(loopBody(node)){return true;}
        if(!(node.getParent() instanceof Block)){return false;}
        return node instanceof IfStatement
                || node instanceof ForStatement
                || node instanceof WhileStatement
                || node instanceof DoStatement
                || node instanceof SwitchStatement
                || node instanceof Block
                || node instanceof TryStatement
                ;
    }

    void apply(Set<ASTNode> deletedNodes){
        for (StmtMovement stmtMovement:stmtMovements
             ) {
            if(deletedNodes.contains(stmtMovement.source)){
                continue;
            }
            stmtMovement.insert();
        }
        stmtMovements.clear();
    }

    void unwrapStmt(ASTNode node){
        Block body1=null,body2=null;
        List<Statement> statements = new ArrayList<>();
        if(node instanceof IfStatement){
            Statement thenStmt=((IfStatement)node).getThenStatement();
            Statement elseStmt=((IfStatement)node).getElseStatement();
            if(thenStmt!=null){
                if(thenStmt instanceof Block){
                    body1=(Block) thenStmt;
                } else {
                    statements.add(thenStmt);
                }
            }
            if(elseStmt!=null) {
                if (elseStmt instanceof Block) {
                    body2 = (Block) elseStmt;
                } else {
                    statements.add(elseStmt);
                }
            }
        }
        if(node instanceof ForStatement){
            if(((ForStatement)node).getBody() instanceof Block){
                body1=(Block) ((ForStatement)node).getBody();
            } else {
                statements.add(((ForStatement)node).getBody());
            }
        }
        if(node instanceof WhileStatement){
            if(((WhileStatement)node).getBody() instanceof Block){
                body1=(Block) ((WhileStatement)node).getBody();
            } else {
                statements.add(((WhileStatement)node).getBody());
            }
        }
        if(node instanceof DoStatement){
            if(((DoStatement)node).getBody() instanceof Block){
                body1=(Block) ((DoStatement)node).getBody();
            } else {
                statements.add(((DoStatement)node).getBody());
            }
        }
        if(node instanceof TryStatement tryStatement){
            if(tryStatement.getBody() != null){
                body1=tryStatement.getBody();
            }
        }
        if(node instanceof SwitchStatement){
            if(((SwitchStatement)node).statements()!=null){
                statements.addAll( ((SwitchStatement)node).statements());
                statements.removeIf(statement -> statement instanceof SwitchCase);
                statements.removeIf(statement -> statement instanceof BreakStatement);
            }
        }
        if(node instanceof SynchronizedStatement){
            body1=((SynchronizedStatement)node).getBody();
        }
        if(node instanceof Block && node.getParent() instanceof Block){
            System.out.println("Block unwrapped: "+node);
            body1=(Block) node;
        }else if(loopBody(node)){
            rewriter.replace(node,(ASTNode) ((Block)node).statements().get(0),null);
            return;
        }


        if(body1!=null){
            statements.addAll(body1.statements());
        }
        if(body2!=null){
            statements.addAll(body2.statements());
        }
        for (int i = 0; i < stmtMovements.size(); i++) {
            if(stmtMovements.get(i).source==node){
                StmtMovement stmtMovement=stmtMovements.get(i);
                stmtMovements.remove(i);
                for (Statement statement : statements) {
                    stmtMovements.add(i,new StmtMovement(statement,stmtMovement.targetBody,stmtMovement.targetPos));
                    i++;
                }
                return;
            }
        }

        Block method = (Block) node.getParent();
        ListRewrite targetBody = rewriter.getListRewrite(method, Block.STATEMENTS_PROPERTY);
        for (Statement statement : statements) {
            stmtMovements.add(new StmtMovement(statement,targetBody,(Statement) node));
        }
        rewriter.remove(node, null);
    }
}
