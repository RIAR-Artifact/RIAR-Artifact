package dynamicReduce.parser;

import org.eclipse.cdt.core.dom.ast.*;
import dynamicReduce.utils.Logger;


import java.util.*;

public class DependencyGraph {

    static class DependencyUnit{
        DependencyUnit(IASTNode node,IBinding binding){
            deletionNode=new WrappedASTNode(getTopStatement(node));
            id=new UniqueID(binding);
        }
        DependencyUnit(IASTNode node){
            deletionNode=new WrappedASTNode(getTopStatement(node));
            id=null;
        }
        WrappedASTNode deletionNode;
        UniqueID id;
        @Override
        public boolean equals(Object other){
            if(other instanceof DependencyUnit dependencyUnit){
                if(id==null){
                    return deletionNode.equals(dependencyUnit.deletionNode);
                }
                return id.equals(dependencyUnit.id);
            }
            return false;
        }
        @Override
        public int hashCode(){
            if(id==null){return deletionNode.hashCode();}
            return id.hashCode();
        }
    }

    record WrappedASTNode(IASTNode node) {
        @Override
        public boolean equals(Object other) {
            if (other instanceof WrappedASTNode wrappedASTNode) {
                return wrappedASTNode.hashCode() == hashCode();
            }
            return false;
        }

        @Override
        public int hashCode() {
            IASTFileLocation fileLocation = node.getFileLocation();
            return (int) (((long) fileLocation.getNodeOffset() * (fileLocation.getNodeOffset() + fileLocation.getNodeLength())) % Integer.MAX_VALUE);
        }

    }


    Map<WrappedASTNode,Set<DependencyUnit>> keys;
    Map<DependencyUnit,Set<IASTNode>> dependencies;//for one def node, get all use nodes
    IASTTranslationUnit ast;
    public DependencyGraph(IASTTranslationUnit ast){
        this.ast=ast;
        keys=new HashMap<>();
        dependencies=new HashMap<>();
        resolveDependencies();
        //logDependencies();
    }
    public List<IASTNode> expandDeletions(List<IASTNode> toDelete){
        Set<IASTNode> resolvedNodes=new HashSet<>();
        Set<IASTNode> toResolveNodes = new HashSet<>(toDelete);
        while (!toResolveNodes.isEmpty()){
            Set<IASTNode> newlyFound=new HashSet<>();
            for (IASTNode node:toResolveNodes
                 ) {
                newlyFound.addAll(getDependentNodes(node));
            }
            resolvedNodes.addAll(toResolveNodes);
            toResolveNodes.clear();
            for (IASTNode node:newlyFound
                 ) {
                if(!resolvedNodes.contains(node)){
                    toResolveNodes.add(node);
                }
            }
        }
        return new ArrayList<>(resolvedNodes);
    }

    public List<IASTNode> shrinkDeletions(List<IASTNode> toDelete){
        List<IASTNode> toResolveNodes=new ArrayList<>(toDelete);
        for (int i = 0; i < toResolveNodes.size(); ) {
            IASTNode node=toResolveNodes.get(i);
            Set<IASTNode> nodes=getDependentNodes(node);
            boolean flag=true;
            for (IASTNode dependentNode:nodes){
                if(!toResolveNodes.contains(dependentNode)){
                    flag=false;
                    break;
                }
            }
            if(flag){
                i++;
            }else {
                toResolveNodes.remove(i);
            }
        }
        return toResolveNodes;
    }
    void resolveDependencies(){
        ast.accept(new ASTVisitor() {
            {
                shouldVisitDeclarators = true;
                shouldVisitDeclarations = true;
            }
            @Override
            public int visit(IASTDeclarator declarator) {
                if(declarator.getParent() instanceof IASTSimpleDeclaration ){
                    if (declarator instanceof IASTFunctionDeclarator) {
                        return PROCESS_SKIP;
                    }
                }
                IASTName name = declarator.getName();
                if (name != null) {
                    IBinding binding = name.resolveBinding();
                    if (binding != null) {
                        DependencyUnit unit= new DependencyUnit(declarator, binding);
                        dependencies.put(unit,new HashSet<>());
                        if(!keys.containsKey(unit.deletionNode)){
                            keys.put(unit.deletionNode,new HashSet<>());
                        }
                        keys.get(unit.deletionNode).add(unit);
                    }
                }
                return super.visit(declarator);
            }

            @Override
            public int visit(IASTDeclaration declaration) {
                if (declaration instanceof IASTFunctionDefinition func) {
                    IBinding binding = func.getDeclarator().getName().resolveBinding();
                    if (binding instanceof IFunction) {
                        IType returnType = ((IFunction) binding).getType();
                        if (returnType != null) {
                            if(returnType.toString().equals("void")){
                                return super.visit(declaration);
                            }
                        }
                    }
                    if(binding.getName().equals("main")){
                        return super.visit(declaration);
                    }
                    /*
                    IASTStatement body = func.getBody();
                    if (body instanceof IASTCompoundStatement) {
                        IASTStatement[] statements = ((IASTCompoundStatement) body).getStatements();
                        for (int i = statements.length - 1; i >= 0; i--) {
                            IASTStatement stmt = statements[i];
                            if (stmt instanceof IASTNullStatement) {
                                continue;
                            }
                            if (stmt instanceof IASTReturnStatement) {

                                DependencyUnit unit= new DependencyUnit(stmt);
                                if(!dependencies.containsKey(unit)){
                                    dependencies.put(unit,new HashSet<>());
                                }
                                dependencies.get(unit).add(declaration);
                                if(!keys.containsKey(unit.deletionNode)){
                                    keys.put(unit.deletionNode,new HashSet<>());
                                }
                                keys.get(unit.deletionNode).add(unit);
                            }
                            break;
                        }
                    }*/
                }
                return super.visit(declaration);
            }
        });
        ast.accept(new ASTVisitor(true) {
            {
                shouldVisitNames = true;
            }
            @Override
            public int visit(IASTName name) {
                IBinding binding = name.resolveBinding();
                if (binding != null) {
                    DependencyUnit unit= new DependencyUnit(name, binding);
                    if(dependencies.containsKey(unit)){
                        Set<IASTNode> dependencyNodes=dependencies.get(unit);
                        dependencyNodes.add(unit.deletionNode.node());
                    }
                }
                return super.visit(name);
            }
        });
        for (DependencyUnit unit:dependencies.keySet()
             ) {
            Set<IASTNode> dependentNodes=dependencies.get(unit);
            dependentNodes.remove(unit.deletionNode.node());
        }
    }

    Set<IASTNode> getDependentNodes(IASTNode node){
        if(!keys.containsKey(new WrappedASTNode(node))){return new HashSet<>();}
        Set<IASTNode> uses=new HashSet<>();
        for (DependencyUnit defs:keys.get(new WrappedASTNode(node))
        ) {
            uses.addAll(dependencies.get(defs));
        }
        /*
        Logger.logFile("def: "+Logger.lessText(node)+node);
        for (DependencyUnit defs:keys.get(new WrappedASTNode(node))){
            Logger.logFile("-> "+defs.id.toString());
        }
        for (IASTNode node1:uses){
            if(node!=node1){
                Logger.logFile("use: "+Logger.lessText(node1));
            }
        }*/
        return uses;
    }


    void logDependencies(){
        for (WrappedASTNode node:keys.keySet()){
            StringBuilder sb=new StringBuilder(node.node().getRawSignature());
            sb.append("\n---dependency id---");
            for (DependencyUnit defs:keys.get(node)
                 ) {
                if(defs.id==null){continue;}
                sb.append("\n").append(defs.id);
            }
            sb.append("\n---dependent nodes---");
            Set<IASTNode> uses=getDependentNodes(node.node());
            if(uses.isEmpty()){
                continue;
            }
            for (IASTNode use:uses
                 ) {
                sb.append("\n").append(use.getRawSignature());
            }
            Logger.log(sb.toString());
        }
    }

    static IASTNode getTopStatement(IASTNode node){
        if(node instanceof IASTStatement||node instanceof IASTDeclaration){
            return node;
        }
        if(node.getParent()==null){
            return node;
        }
        return getTopStatement(node.getParent());
    }
}
