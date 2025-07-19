package DynamicReduce;

import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

import java.util.List;

public class CommentAnalyst {
    public static void run(){
        System.out.println("start comment reduce");
        MainReducer.analyseCurrent();
        List<?> commentList = MainReducer.cu.getCommentList();
        TextEdit edits = new MultiTextEdit();
        for (Object obj : commentList) {
            if (obj instanceof Comment comment) {
                int start = comment.getStartPosition();
                int length = comment.getLength();
                edits.addChild(new org.eclipse.text.edits.DeleteEdit(start, length));
            }
        }

        try {
            edits.apply(MainReducer.document);
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        MainReducer.tempOutput();
        if(!MainReducer.testOutput(false)){
            System.out.println("comment reduce failed");
            //MainReducer.decentWorkingDir();
        }else {

            System.out.println("comment reduce success");
        }
    }

    public static void main(String[] args) {
        MainReducer.init();
        run();
    }
}
