package DynamicReduce;



import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;

import java.util.*;

public class ExpressionAnalyst {

    public static int step=-1;
    static class ReduceUnit{
        public ReduceUnit(ASTNode targetNode,ASTNode replaceNode){
            this.targetNode=targetNode;
            this.replaceNode=replaceNode;
        }
        ASTNode targetNode;
        ASTNode replaceNode;
        boolean blocked=false;
        public void reduce(){
            if(blocked){return;}
            for (ReduceUnit reduceUnit:reduceList){
                if(reduceUnit.blocked&&targetNode.getParent()==reduceUnit.targetNode){
                    System.out.println("parent node is blocked");
                    blocked=true;return;
                }
            }
            if(replaceNode==null){
                MainReducer.rewriter.remove(targetNode,null);
                System.out.println("remove "+targetNode);
            }else if(targetNode instanceof InfixExpression && !((InfixExpression)targetNode).extendedOperands().isEmpty()){
                reduceInfix();
            }else{
                MainReducer.rewriter.replace(targetNode,replaceNode,null);
                System.out.println("replace "+targetNode+" with "+replaceNode);
            }
            blocked=true;
            for (ReduceUnit reduceUnit:reduceList
                 ) {
                if(targetNode==reduceUnit.targetNode){
                    reduceUnit.blocked=true;
                }
            }
        }
        public String getIdentifier(){
            ASTNode parent=targetNode.getParent();
            String upperLimit="";
            while (parent!=null && !(parent instanceof CompilationUnit)){
                if(parent instanceof MethodDeclaration methodDeclaration){
                    upperLimit=methodDeclaration.resolveBinding().getKey();
                }
                parent=parent.getParent();
            }
            return upperLimit+":"+targetNode.toString();
        }
        void reduceInfix(){
            System.out.println("remove "+replaceNode+" from "+targetNode);
            InfixExpression infixExpression=(InfixExpression)targetNode;
            // 获取 leftOperand 和 rightOperand
            Expression left = infixExpression.getLeftOperand();
            Expression right = infixExpression.getRightOperand();
            ListRewrite extendedList = MainReducer.rewriter.getListRewrite(infixExpression, InfixExpression.EXTENDED_OPERANDS_PROPERTY);

            if (replaceNode==left) {
                // 用 rightOperand 代替 leftOperand
                MainReducer.rewriter.set(infixExpression, InfixExpression.LEFT_OPERAND_PROPERTY, right, null);

                // 清除 rightOperand，让 extendedOperands 成为新的部分
                if (!extendedList.getRewrittenList().isEmpty()) {
                    Expression newRight = (Expression) extendedList.getRewrittenList().get(0);
                    MainReducer.rewriter.set(infixExpression, InfixExpression.RIGHT_OPERAND_PROPERTY, newRight, null);
                    extendedList.remove(newRight, null);  // 从 extendedOperands 中移除它
                }
            }

            if (replaceNode==right) {
                if (!extendedList.getRewrittenList().isEmpty()) {
                    Expression newRight = (Expression) extendedList.getRewrittenList().get(0);
                    MainReducer.rewriter.set(infixExpression, InfixExpression.RIGHT_OPERAND_PROPERTY, newRight, null);
                    extendedList.remove(newRight, null);
                } else {
                    MainReducer.rewriter.remove(right, null);
                }
            }
            for (Object obj : extendedList.getRewrittenList()) {
                if (obj==replaceNode) {
                    Expression expr = (Expression) obj;
                    extendedList.remove(expr, null);
                    break;
                }
            }
        }
    }

    public static boolean suppressTimeout=true;
    static List<ReduceUnit> reduceList = new ArrayList<>();
    static List<String> timeoutRecord = new ArrayList<>();
    public static void getAllExpression(){
        reduceList.clear();
        MainReducer.cu.accept(new ASTVisitor() {
            
            @Override
            public void preVisit(ASTNode node) {
                if(node instanceof TryStatement tryStatement){
                    addReduceUnit(node);
                }
                if(node instanceof Expression expression){

                    if (expression instanceof InfixExpression) {
                        System.out.println("Infix Expression: " + expression);
                    } else if (expression instanceof PrefixExpression) {
                        System.out.println("Prefix Expression: " + expression);
                    } else if (expression instanceof PostfixExpression) {
                        System.out.println("Postfix Expression: " + expression);
                    } else if (expression instanceof MethodInvocation) {
                        System.out.println("Method Invocation: " + expression);
                    } else if (expression instanceof FieldAccess) {
                        System.out.println("Field Access: " + expression);
                    } else if (expression instanceof ArrayAccess) {
                        System.out.println("Array Access: " + expression);
                    } else if (expression instanceof ArrayInitializer) {
                        System.out.println("Array Initializer: " + expression);
                    } else if (expression instanceof ArrayCreation) {
                        System.out.println("Array Creation: " + expression);
                    } else if (expression instanceof VariableDeclarationExpression) {
                        System.out.println("Variable Declaration: "+expression);
                    } else if (expression instanceof Assignment) {
                        System.out.println("Assignment: " + expression);
                    } else if (expression instanceof CastExpression) {
                        System.out.println("Cast Expression: " + expression);
                    } else if (expression instanceof ConditionalExpression) {
                        System.out.println("Conditional Expression: " + expression);
                    } else if (expression instanceof InstanceofExpression) {
                        System.out.println("Instanceof Expression: " + expression);
                    } else if (expression instanceof LambdaExpression) {
                        System.out.println("Lambda Expression: " + expression);
                    } else if (expression instanceof ParenthesizedExpression) {
                        System.out.println("Parenthesized Expression: " + expression);
                    } else if (expression instanceof NullLiteral) {
                        System.out.println("Null Literal: " + expression);
                    } else if (expression instanceof BooleanLiteral) {
                        System.out.println("Boolean Literal: " + expression);
                    } else if (expression instanceof CharacterLiteral) {
                        System.out.println("Character Literal: " + expression);
                    } else if (expression instanceof NumberLiteral) {
                        System.out.println("Number Literal: " + expression);
                    } else if (expression instanceof StringLiteral) {
                        System.out.println("String Literal: " + expression);
                    } else if (expression instanceof TypeLiteral) {
                        System.out.println("Type Literal: " + expression);
                    } else if (expression instanceof SimpleName) {
                        System.out.println("Simple Name: " + expression);
                    } else if (expression instanceof ClassInstanceCreation ) {
                        System.out.println("Class Instance Creation : " + expression);
                    } else {
                        System.out.println("Other Expression: " + expression);
                    }
                    addReduceUnit(expression);
                }
            }

        });
    }
    private static Expression getPlaceholderForBinding(ITypeBinding returnType) {
        if (returnType == null) {
            return null;
        }

        if (returnType.isPrimitive()) {
            String primitiveType=returnType.getName();
            switch (primitiveType) {
                case "boolean":
                    return MainReducer.cu.getAST().newBooleanLiteral(false);
                case "char":
                    CharacterLiteral charLiteral = MainReducer.cu.getAST().newCharacterLiteral();
                    charLiteral.setCharValue('\0'); // 空字符
                    return charLiteral;
                case "byte":
                case "short":
                case "int":
                case "long":
                case "float":
                case "double":
                    return MainReducer.cu.getAST().newNumberLiteral("0");
                default:
                    return null;
            }
        } else {
            return MainReducer.cu.getAST().newNullLiteral();
        }
    }
    static void addReduceUnit(ASTNode node){
        //不reduce的类型
        if(node instanceof SimpleName||
                node instanceof CharacterLiteral||
                node instanceof StringLiteral||
                node instanceof NullLiteral||
                node instanceof NumberLiteral||
                node instanceof TypeLiteral||
                node instanceof BooleanLiteral
        ){return;}
        boolean skipRemove=false;
        if(node instanceof TryStatement tryStatement){
            skipRemove=true;
            List<CatchClause> catchClauses = tryStatement.catchClauses();
            for (CatchClause catchClause:catchClauses
                 ) {
                reduceList.add(new ReduceUnit(catchClause,null));
            }
            if(tryStatement.getFinally()!=null){
                reduceList.add(new ReduceUnit(tryStatement.getFinally(),null));
            }
        }
        if(node instanceof InfixExpression){
            skipRemove=true;
        }
        if (node.getLocationInParent() == IfStatement.EXPRESSION_PROPERTY||
                node.getLocationInParent() == WhileStatement.EXPRESSION_PROPERTY) {
            skipRemove=true;
            BooleanLiteral trueLiteral = MainReducer.cu.getAST().newBooleanLiteral(true);
            BooleanLiteral falseLiteral = MainReducer.cu.getAST().newBooleanLiteral(false);
            reduceList.add(new ReduceUnit(node, trueLiteral));
            reduceList.add(new ReduceUnit(node, falseLiteral));
        }
        if (node.getLocationInParent() == ReturnStatement.EXPRESSION_PROPERTY){
            skipRemove=true;
            ASTNode parent = node.getParent();

            // 遍历父节点，查找 MethodDeclaration 或 LambdaExpression
            while (parent != null && !(parent instanceof MethodDeclaration) && !(parent instanceof LambdaExpression)) {
                parent = parent.getParent();
            }
            ITypeBinding returnType=null;
            // 方法声明情况
            if (parent instanceof MethodDeclaration methodDecl) {
                returnType = methodDecl.getReturnType2().resolveBinding();
            }
            // Lambda 表达式情况
            if (parent instanceof LambdaExpression lambdaExpr) {
                ITypeBinding lambdaType = lambdaExpr.resolveTypeBinding();
                if (lambdaType != null) {
                    IMethodBinding functionalMethod = lambdaType.getFunctionalInterfaceMethod();
                    if (functionalMethod != null) {
                        returnType = functionalMethod.getReturnType();

                    }
                }
            }
            Expression replaceNode=getPlaceholderForBinding(returnType);
            reduceList.add(new ReduceUnit(node, replaceNode));
        }


        if(!skipRemove){
            reduceList.add(new ReduceUnit(node, null));
        }
        if(node instanceof ArrayInitializer arrayInitializer){
            if(arrayInitializer.expressions().isEmpty()){return;}
            ArrayInitializer emptyInitializer=MainReducer.cu.getAST().newArrayInitializer();
            reduceList.add(new ReduceUnit(node, emptyInitializer));
        }

        if (node instanceof InfixExpression infixExpression) {

            // 获取 InfixExpression 的左侧或右侧子表达式
            Expression leftOperand = infixExpression.getLeftOperand();
            Expression rightOperand=infixExpression.getRightOperand();
            boolean left=true;
            boolean right=true;
            if(node.getParent() instanceof ExpressionStatement){
                left=canBeStandaloneStatement(leftOperand);
                right=canBeStandaloneStatement(rightOperand);
            }
            if(left) {
                reduceList.add(new ReduceUnit(node, leftOperand));
                System.out.println("InfixExpression replaced by its left operand");
            }
            if(right){
                reduceList.add(new ReduceUnit(node, rightOperand));
                System.out.println("InfixExpression replaced by its right operand");
            }
            for (Object ext : infixExpression.extendedOperands()) {
                reduceList.add(new ReduceUnit(node,(ASTNode) ext));
            }
        } else if(node instanceof MethodInvocation methodInvocation){
            List<Expression> arguments = methodInvocation.arguments();
            IMethodBinding methodBinding = methodInvocation.resolveMethodBinding();
            if (methodBinding == null) {
                return;
            }
            ITypeBinding returnType = methodBinding.getReturnType();
            System.out.println("Return type: " + returnType.getName());
            ITypeBinding[] parameterTypes = methodBinding.getParameterTypes();
            System.out.println("Method parameters:");
            for (ITypeBinding paramType : parameterTypes) {
                System.out.println("  Parameter type: " + paramType.getName());
            }
            int i = -1;
            // 判断每个参数的类型是否可以隐式转换为返回值类型
            for (ITypeBinding paramType : parameterTypes) {
                i++;
                if (returnType.isAssignmentCompatible(paramType)) {
                    System.out.println("Parameter type " + paramType.getName() + " can be assigned to return type.");
                    reduceList.add(new ReduceUnit(node, arguments.get(i)));
                    break;
                } else {
                    System.out.println("Parameter type " + paramType.getName() + " cannot be assigned to return type.");
                }
            }
        }else if (node instanceof PrefixExpression prefixExpression) {

            Expression operand=prefixExpression.getOperand();
            reduceList.add(new ReduceUnit(node, operand));
        } else if (node instanceof PostfixExpression postfixExpression) {
            Expression operand=postfixExpression.getOperand();
            reduceList.add(new ReduceUnit(node, operand));
        } else if (node instanceof CastExpression castExpression) {
            Expression operand=castExpression.getExpression();
            reduceList.add(new ReduceUnit(node, operand));
        } else if (node instanceof ConditionalExpression conditionalExpression) {
            Expression thenExpression=conditionalExpression.getThenExpression();
            if(thenExpression!=null){
                reduceList.add(new ReduceUnit(node, thenExpression));
            }
            Expression elseExpression=conditionalExpression.getElseExpression();
            if(elseExpression!=null){
                reduceList.add(new ReduceUnit(node, elseExpression));
            }
        }else if (node instanceof ParenthesizedExpression parenthesizedExpression) {
            Expression operand=parenthesizedExpression.getExpression();
            reduceList.add(new ReduceUnit(node, operand));
        }

    }
    static boolean canBeStandaloneStatement(Expression expr) {
        return expr instanceof Assignment ||
                expr instanceof MethodInvocation ||
                expr instanceof ClassInstanceCreation ||
                expr instanceof PostfixExpression ||
                expr instanceof PrefixExpression ||
                expr instanceof SuperMethodInvocation ||
                expr instanceof FieldAccess;
    }

    public static void expressionDelta(){
        MainReducer.analyseCurrent();
        getAllExpression();
        DeltaSelector<ReduceUnit> deltaSelector;
        if(step<=0){
            deltaSelector=new DeltaSelector<>(reduceList);
        }else {
            deltaSelector=new DeltaSelector<>(reduceList,step);
        }
        while (!deltaSelector.ended){
            List<ReduceUnit> toReduce=deltaSelector.get();
            for (ReduceUnit reduceUnit:reduceList
            ) {
                reduceUnit.blocked=false;
            }
            for (ReduceUnit reduceUnit:toReduce
                 ) {
                reduceUnit.reduce();
            }
            UndoEdit undoEdit=null;
            boolean reduceSuccess=true;
            try {
                //multiEdit.apply(MainReducer.document);
                TextEdit textEdit= MainReducer.rewriter.rewriteAST(MainReducer.document, null);
                if (textEdit.getChildrenSize() == 0) {
                    System.out.println("AST 修改无效");
                    reduceSuccess=false;
                } else {
                    System.out.println("AST 修改成功");
                }
                MainReducer.rewriter= ASTRewrite.create(MainReducer.cu.getAST());
                undoEdit=textEdit.apply(MainReducer.document);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            MainReducer.tempOutput();

            boolean result=false;
            if(reduceSuccess){
                result=MainReducer.testOutput(false);
                boolean timeout=OutputTester.timeoutController.getStatus();
                if(timeout){
                    for (ReduceUnit reduceUnit:toReduce
                    ) {
                        timeoutRecord.add(reduceUnit.getIdentifier());
                    }
                }
            }
            if(result){
                System.out.println("DD success!");
                MainReducer.analyseCurrent();
                getAllExpression();
                List<ReduceUnit> timeoutList=new ArrayList<>();
                if(suppressTimeout){
                    reduceList.sort((o1, o2) -> {
                        int i1=timeoutRecord.contains(o1.getIdentifier())?1:0;
                        int i2=timeoutRecord.contains(o2.getIdentifier())?1:0;
                        return i1-i2;
                    });
                    for (String timeout:timeoutRecord){
                        System.out.println("Timeout record: "+timeout);
                    }
                }
                deltaSelector.success(reduceList);
                int i=0;
                for (ReduceUnit reduceUnit:reduceList
                     ) {
                    System.out.println(i+": "+reduceUnit.targetNode+" - "+reduceUnit.replaceNode);
                    i++;
                }
            }else {
                System.out.println("DD failed!");
                if (undoEdit!=null){
                    try {
                        undoEdit.apply(MainReducer.document);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
                MainReducer.decentWorkingDir();
                deltaSelector.fail();
            }
        }
    }

    public static void main(String[] args) {
        MainReducer.init();
//        MainReducer.analyseCurrent();
//        getAllExpression();
//        reduceExpression(expressions.get(10));
        expressionDelta();
    }
}
