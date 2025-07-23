package dynamicReduce.reducer;

import org.eclipse.cdt.core.dom.ast.*;
import dynamicReduce.parser.FileParser;
import dynamicReduce.parser.NodeUnwrapper;
import dynamicReduce.parser.RewriterType;
import dynamicReduce.parser.UniqueID;
import dynamicReduce.tester.*;
import dynamicReduce.utils.Logger;
import dynamicReduce.utils.Record;
import dynamicReduce.utils.RecordType;

import java.util.*;

public class CallTraceReducer extends Reducer{
    protected List<String> functions;
    private String currentFunctionID;
    private DeltaSelector<IASTNode> nodeSelector;
    public CallTraceReducer(FileParser parser) {
        super(parser);
    }
    public static boolean enableReduce=false;

    protected void findFunctions(){
        functions=new CallTraceHelper(parser.getAST()).getFunctionTrace();
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

    public static IASTNode findFunctionNode(IASTNode ast,String targetID){
        final IASTNode[] target = new IASTNode[1];
        ast.accept(new ASTVisitor(true) {
            @Override
            public int visit(IASTDeclaration declaration) {
                if(declaration instanceof IASTFunctionDefinition function){
                    IBinding binding=function.getDeclarator().getName().getBinding();
                    UniqueID id=new UniqueID(binding);
                    if(targetID.equals(id.toString())){
                        target[0] =declaration;
                    }
                }
                return super.visit(declaration);
            }
        });
        return target[0];
    }
    private List<IASTNode> findFunctionNodes(){
        List<IASTNode> result=new ArrayList<>();
        findFunctionNode(parser.getAST(),currentFunctionID).accept(new ASTVisitor(true) {
            @Override
            public int visit(IASTStatement statement) {
                if(!(statement.getParent() instanceof IASTFunctionDefinition)&& NodeUnwrapper.canDelete(statement)) {
                    result.add(statement);
                }
                return super.visit(statement);
            }
        });
        if(nodeSelector!=null&&enableReduce){
            TestRecord.v().newFileOutput(TestRecord.v().getLastSuccess());
            Record.v().startEffectRecord(1);
            CoverageResolver coverageResolver=new CoverageTester().run().getCoverageResolver();
            Record.v().endEffectRecord(1);
            Map<IASTNode,Integer> indexMap=new HashMap<>();
            for (int i=0;i<result.size();i++){
                indexMap.put(result.get(i),i);
            }
            if(coverageResolver!=null) {
                result.sort(Comparator.comparingInt(o -> ( -coverageResolver.getExecutionCount(o))));
                //result.sort((o1, o2) -> Math.min(100,indexMap.get(o1) - indexMap.get(o2))-coverageResolver.getExecutionCount(o1) + coverageResolver.getExecutionCount(o2));
            }
        }
        return result;
    }
    @Override
    public void run() {
        Record.v().startEffectRecord(0);
        findFunctions();
        Record.v().endEffectRecord(0);
        for (String func:functions){
            currentFunctionID=func;
            nodeSelector=null;
            Logger.log("Working in function "+currentFunctionID);
            Logger.logFile("Working in function "+currentFunctionID);
            List<IASTNode> oneDeletion=new ArrayList<>();
            IASTNode functionNode=findFunctionNode(parser.getAST(),currentFunctionID);
            if(functionNode==null){
                Logger.log("Can't find function");
                continue;
            }
            oneDeletion.add(functionNode);
            boolean rewriteSuccess=parser.getFileRewriter().reduceNodes(oneDeletion);
            if(rewriteSuccess){
                TestContext testContext=new OutputTester().run();
                if(testContext.getTestStatus().succeed()) {
                    Logger.log("Deleted whole function at once.");
                    success();
                    continue;
                }
            }
            nodeSelector=new DeltaSelector<>(findFunctionNodes());
            resetReduceProcess(nodeSelector, this::findFunctionNodes);
            LocalExpressionReducer localExpressionReducer=new LocalExpressionReducer(new FileParser(RewriterType.EXPRESSION_REWRITER),currentFunctionID);
            Record.v().startRecord(RecordType.LOCAL_EXPRESSION_REDUCER);
            localExpressionReducer.run();
            Record.v().endRecord(RecordType.LOCAL_EXPRESSION_REDUCER);
            parser.refresh();
        }

    }
}
