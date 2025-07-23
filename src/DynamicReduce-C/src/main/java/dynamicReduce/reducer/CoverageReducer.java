package dynamicReduce.reducer;

import dynamicReduce.utils.Record;
import org.eclipse.cdt.core.dom.ast.*;
import dynamicReduce.parser.FileParser;
import dynamicReduce.parser.NodeUnwrapper;
import dynamicReduce.tester.CoverageResolver;
import dynamicReduce.tester.OutputTester;
import dynamicReduce.tester.TestContext;
import dynamicReduce.utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dynamicReduce.parser.NodeUnwrapper.resolveStatements;

public class CoverageReducer extends Reducer{
    List<IASTNode> toDelete;
    public CoverageReducer(FileParser parser) {
        super(parser);
        toDelete=new ArrayList<>();
    }

    List<IASTNode> getMoreUncoveredNodes(){
        Record.v().startEffectRecord(2);
        getUncoveredNodes();
        Record.v().endEffectRecord(2);
        parser.getAST().accept(new ASTVisitor(true) {
            @Override
            public int visit(IASTStatement statement) {
                if(!NodeUnwrapper.canDelete(statement)){return super.visit(statement);}
                List<IASTStatement> childNodes=new ArrayList<>();
                if(statement instanceof IASTCompoundStatement iastCompoundStatement){
                    IASTStatement[] statements=iastCompoundStatement.getStatements();
                    childNodes.addAll(Arrays.asList(statements));
                }else if(statement instanceof IASTForStatement iastForStatement){
                    IASTStatement innerStatement=iastForStatement.getBody();
                    childNodes.addAll(Arrays.asList(resolveStatements(innerStatement)));
                }else if(statement instanceof IASTIfStatement iastIfStatement){
                    IASTStatement thenClause=iastIfStatement.getThenClause();
                    IASTStatement elseClause=iastIfStatement.getElseClause();
                    childNodes.addAll(Arrays.asList(resolveStatements(thenClause)));
                    if(elseClause!=null){
                        childNodes.addAll(Arrays.asList(resolveStatements(elseClause)));
                    }
                }else if(statement instanceof IASTWhileStatement iastWhileStatement){
                    IASTStatement innerStatement=iastWhileStatement.getBody();
                    childNodes.addAll(Arrays.asList(resolveStatements(innerStatement)));
                }else if(statement instanceof IASTDoStatement iastDoStatement){
                    IASTStatement innerStatement=iastDoStatement.getBody();
                    childNodes.addAll(Arrays.asList(resolveStatements(innerStatement)));
                }else if(statement instanceof IASTSwitchStatement iastSwitchStatement){
                    IASTStatement innerStatement=iastSwitchStatement.getBody();
                    childNodes.addAll(Arrays.asList(resolveStatements(innerStatement)));
                }else {
                    return super.visit(statement);
                }
                boolean flag=true;
                for (IASTStatement childStatement:childNodes
                     ) {
                    if(!toDelete.contains(childStatement)){
                        flag=false;
                        break;
                    }
                }
                if(flag){
                    toDelete.add(statement);
                }
                return super.visit(statement);
            }
        });
        return toDelete;
    }

    List<IASTNode> getUncoveredNodes(){
        toDelete.clear();
        CoverageHelper coverageHelper=new CoverageHelper(parser.getAST());
        CoverageResolver coverageResolver=coverageHelper.getCoverageResolver();
        /*
        Logger.log("uncoveredLines");
        for (Integer line:coverageResolver.getUnexecutedLines()
             ) {
            Logger.log(line+"");
        }*/
        parser.getAST().accept(new ASTVisitor(true) {
            @Override
            public int visit(IASTStatement statement) {
                if(coverageResolver.nodeExecuted(statement)&& NodeUnwrapper.canDelete(statement)&&!toDelete.contains(statement)){
                    toDelete.add(statement);
                }
                return super.visit(statement);
            }

            @Override
            public int visit(IASTDeclaration declaration) {
                if(coverageResolver.nodeExecuted(declaration)&&!toDelete.contains(declaration)){
                    toDelete.add(declaration);
                }
                return super.visit(declaration);
            }
        });
        return toDelete;
    }


    @Override
    public void run() {
        getMoreUncoveredNodes();
        /*
        for (int i = 0; i < toDelete.size(); i++) {
            Logger.log("--- uncovered "+i+" ---");
            Logger.log(toDelete.get(i).getRawSignature());
        }*/
        boolean succeed=parser.getFileRewriter().reduceNodes(toDelete);
        if (succeed){
            TestContext testContext=new OutputTester().run();
            if(testContext.getTestStatus().succeed()) {
                Logger.log("Enhanced coverage reduce succeed!");
                success();
            }
        }
        getMoreUncoveredNodes();
        DeltaSelector<IASTNode> nodeSelector=new DeltaSelector<>(toDelete);
        nodeSelector.stepLimit=toDelete.size()/1024;
        Logger.logFile("initial coverage size is "+toDelete.size());
        standardReduceProcess(nodeSelector, this::getMoreUncoveredNodes);
    }

}
