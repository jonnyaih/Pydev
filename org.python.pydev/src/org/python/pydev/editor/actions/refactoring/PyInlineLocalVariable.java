/*
 * Created on Oct 19, 2004
 *
 * @author Fabio Zadrozny
 */
package org.python.pydev.editor.actions.refactoring;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.python.pydev.editor.refactoring.PyRefactoring;

/**
 * @author Fabio Zadrozny
 * 
 */
public class PyInlineLocalVariable extends PyRefactorAction {
    
    /**
     * we need:
     * 
	 *  def inlineLocalVariable(self,filename_path, line, col)
	 * 
     * @throws BadLocationException
     * @throws CoreException
     */
    protected String perform(IAction action, String name, Operation operation) throws BadLocationException, CoreException {
        File editorFile = getPyEdit().getEditorFile();
        
        //testing first with whole lines.
        int beginLine = getStartLine();
        int beginCol  = getStartCol();

        int endLine   = getEndLine();
        int endCol    = getEndCol();
        
        
        return PyRefactoring.getPyRefactoring().inlineLocalVariable(editorFile, beginLine, beginCol, operation);

    }
    
    protected String getInputMessage() {
        return null;
    }

}
