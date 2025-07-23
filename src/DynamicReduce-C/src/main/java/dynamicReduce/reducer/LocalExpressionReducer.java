package dynamicReduce.reducer;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import dynamicReduce.parser.FileParser;

public class LocalExpressionReducer extends ExpressionReducer{
    String functionID;
    public LocalExpressionReducer(FileParser parser, String functionID) {
        super(parser);
        this.functionID=functionID;
    }
    @Override
    protected IASTNode getRoot(){
        return CallTraceReducer.findFunctionNode(parser.getAST(),functionID);
    }
}
