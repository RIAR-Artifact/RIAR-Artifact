package DynamicReduce;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

public class DeclarationFiller {
    public static class AddAssignmentVisitor extends ASTVisitor {

        @Override
        public boolean visit(VariableDeclarationFragment fragment) {
            if (fragment.getInitializer() == null) {
                AST ast = fragment.getAST();
                ASTRewrite rewriter = ASTRewrite.create(ast);

                ITypeBinding typeBinding = fragment.resolveBinding().getType();
                Expression initializer = null;
                if (typeBinding.isPrimitive()) {
                    switch (typeBinding.getName()) {
                        case "int", "short", "byte","long" -> initializer = ast.newNumberLiteral("0");
                        case "double", "float" -> initializer = ast.newNumberLiteral("0.0");
                        case "boolean" -> initializer = ast.newBooleanLiteral(false);
                        case "char" -> {
                            CharacterLiteral charLiteral = ast.newCharacterLiteral();
                            charLiteral.setCharValue(' ');
                            initializer = charLiteral;
                        }
                    }
                } else {
                    initializer = ast.newNullLiteral();
                }

                rewriter.set(fragment, VariableDeclarationFragment.INITIALIZER_PROPERTY, initializer, null);
                TextEdit edits = rewriter.rewriteAST(MainReducer.document, null);
                multiEdit.addChild(edits);
            }
            return super.visit(fragment);
        }
    }
    static MultiTextEdit multiEdit = new MultiTextEdit();
    public static void start(){
        MainReducer.analyseCurrent();
        AddAssignmentVisitor visitor = new AddAssignmentVisitor();
        MainReducer.cu.accept(visitor);

        try {
            multiEdit.apply(MainReducer.document);
        } catch ( BadLocationException e) {
            throw new RuntimeException(e);
        }
// Apply changes
        TextEdit edits = null;
        MainReducer.tempOutput();
        if(!MainReducer.testOutput(false)){
            MainReducer.decentWorkingDir();
        }
    }
}
