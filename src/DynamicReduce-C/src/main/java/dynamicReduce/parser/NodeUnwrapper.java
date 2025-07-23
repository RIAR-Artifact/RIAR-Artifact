package dynamicReduce.parser;

import org.eclipse.cdt.core.dom.ast.*;
import dynamicReduce.parser.rewriter.Rewriter;

public class NodeUnwrapper {
    public static boolean canUnwrap(IASTNode node){
        return node instanceof IASTCompoundStatement || node instanceof IASTForStatement
                || node instanceof IASTIfStatement || node instanceof IASTWhileStatement
                || node instanceof IASTSwitchStatement || node instanceof IASTDoStatement
                ;
    }
    private static boolean isOnlyNode(IASTNode statement){
        if(statement instanceof IASTCompoundStatement){return false;}
        IASTNode parent=statement.getParent();
        if(parent instanceof IASTForStatement forStatement){
            if(statement==forStatement.getBody()){return true;}
        }
        if(parent instanceof IASTIfStatement ifStatement){
            if(statement==ifStatement.getThenClause()){return true;}
            if(statement==ifStatement.getElseClause()){return true;}
        }
        if(parent instanceof IASTWhileStatement whileStatement){
            if(statement==whileStatement.getBody()){return true;}
        }
        if(parent instanceof IASTSwitchStatement switchStatement){
            if(statement==switchStatement.getBody()){return true;}
        }
        if(parent instanceof IASTDoStatement doStatement){
            if(statement==doStatement.getBody()){return true;}
        }
        return false;
    }
    public static boolean canDelete(IASTStatement statement){
        IASTNode parent=statement.getParent();
        if(statement instanceof IASTCompoundStatement compoundStatement){
            if(compoundStatement.getStatements().length==1){return true;}
            if(parent instanceof IASTForStatement forStatement){
                if(compoundStatement==forStatement.getBody()){return false;}
            }
            if(parent instanceof IASTIfStatement ifStatement){
                if(compoundStatement==ifStatement.getThenClause()){return false;}
                if(compoundStatement==ifStatement.getElseClause()){return false;}
            }
            if(parent instanceof IASTWhileStatement whileStatement){
                if(compoundStatement==whileStatement.getBody()){return false;}
            }
            if(parent instanceof IASTSwitchStatement switchStatement){
                if(compoundStatement==switchStatement.getBody()){return false;}
            }
            if(parent instanceof IASTDoStatement doStatement){
                if(compoundStatement==doStatement.getBody()){return false;}
            }
            return true;
        }
        return true;
    }
    Rewriter rewriter;
    public NodeUnwrapper(Rewriter rewriter){
        this.rewriter=rewriter;
    }

    private void unwrapSingleBlock(int start,int end,IASTStatement[] statements){
        if(statements.length==0){
            rewriter.removePhrase(start,end);
            return;
        }
        rewriter.removePhrase(start,statements[0].getFileLocation().getNodeOffset());
        IASTFileLocation lastStmt=statements[statements.length-1].getFileLocation();
        rewriter.removePhrase(lastStmt.getNodeOffset()+lastStmt.getNodeLength(),end);
    }
    public static IASTStatement[] resolveStatements(IASTStatement statement){
        if(statement instanceof IASTCompoundStatement iastCompoundStatement){
            return iastCompoundStatement.getStatements();
        }else {
            IASTStatement[] statements=new IASTStatement[1];
            statements[0]=statement;
            return statements;
        }
    }
    private void unwrapNode(IASTNode node){
        IASTFileLocation nodeLocation = node.getFileLocation();
        int startPos=nodeLocation.getNodeOffset();
        int endPos=nodeLocation.getNodeOffset()+nodeLocation.getNodeLength();
        if(node instanceof IASTCompoundStatement iastCompoundStatement){
            IASTStatement[] statements=iastCompoundStatement.getStatements();
            unwrapSingleBlock(startPos,endPos,statements);
            return;
        }
        if(node instanceof IASTForStatement iastForStatement){
            IASTStatement innerStatement=iastForStatement.getBody();
            unwrapSingleBlock(startPos,endPos,resolveStatements(innerStatement));
        }
        if(node instanceof IASTIfStatement iastIfStatement){
            IASTStatement thenClause=iastIfStatement.getThenClause();
            IASTStatement elseClause=iastIfStatement.getElseClause();
            if(elseClause==null){
                unwrapSingleBlock(startPos,endPos,resolveStatements(thenClause));
            }else {
                IASTFileLocation thenLocation = thenClause.getFileLocation();
                int dividePos=thenLocation.getNodeOffset()+thenLocation.getNodeLength();
                unwrapSingleBlock(startPos,dividePos,resolveStatements(thenClause));
                unwrapSingleBlock(dividePos,endPos,resolveStatements(elseClause));
            }
        }
        if(node instanceof IASTWhileStatement iastWhileStatement){
            IASTStatement innerStatement=iastWhileStatement.getBody();
            unwrapSingleBlock(startPos,endPos,resolveStatements(innerStatement));
        }
        if(node instanceof IASTDoStatement iastDoStatement){
            IASTStatement innerStatement=iastDoStatement.getBody();
            unwrapSingleBlock(startPos,endPos,resolveStatements(innerStatement));
        }
        if(node instanceof IASTSwitchStatement iastSwitchStatement){
            IASTStatement innerStatement=iastSwitchStatement.getBody();
            IASTStatement[] statements=resolveStatements(innerStatement);
            unwrapSingleBlock(startPos,endPos,statements);
            for (IASTStatement statement : statements) {
                if (statement instanceof IASTCaseStatement || statement instanceof IASTDefaultStatement || statement instanceof IASTBreakStatement) {
                    rewriter.removeNode(statement);
                }
            }
        }
    }
    public void deleteNodeUnwrap(IASTNode node){
        if(canUnwrap(node)){
            unwrapNode(node);
        }else {
            deleteNode(node);
        }
    }
    public void deleteNode(IASTNode node){
        IASTNode parent=node.getParent();
        boolean flag=true;
        if(parent instanceof IASTForStatement forStatement){
            if(node==forStatement.getInitializerStatement()){
                flag=false;
                int start=node.getFileLocation().getNodeOffset();
                rewriter.removePhrase(start,start+node.getRawSignature().lastIndexOf(";"));
            }
        }
        if(flag){
            rewriter.removeNode(node);
            if(isOnlyNode(node)){
                rewriter.insertAt("{}",node.getFileLocation().getNodeOffset());
            }
        }

    }
    public void finish(){
        rewriter.finish();
    }
}
