/**
 * Copyright (c) 20015 by Brainwy Software Ltda. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.python.pydev.analysis.search_index;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;

import com.python.pydev.analysis.search.SearchMessages;

/**
 * This is still a work in progress!!!
 */
public class SearchIndexPage extends DialogPage implements ISearchPage {

    private SearchIndexDataHistory searchIndexDataHistory = new SearchIndexDataHistory();
    private Text fPattern;
    private ISearchPageContainer fContainer;
    private boolean fFirstTime = true;

    private Button fIsCaseSensitiveCheckbox;

    // Scope
    private Button fModulesScopeRadio;
    private Button fOpenEditorsScopeRadio;
    private Button fWorkspaceScopeRadio;
    private Button fProjectsScopeRadio;
    private Button fExternalFilesRadio;

    // Scope data
    private Text fModuleNames;
    private Text fProjectNames;
    private Text fExternalFolders;

    private Button fHistory;
    private Button fSelectProjects;
    private Button fSelectFolders;

    @Override
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        searchIndexDataHistory.readConfiguration();

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());
        GridLayout layout = new GridLayout(10, false);
        composite.setLayout(layout);

        // Line 1
        createLabel(composite, SWT.LEAD,
                "&Text  (* = any string, ? = any character, \\\\ = escape). Exact match by default. Add * to begin/end for sub-matches (slower).",
                10);

        if (acceptExternalFoldersAndOpenEditors()) {
            // Line 2
            fPattern = createText(composite, SWT.SINGLE | SWT.BORDER, 4, 50);

            fHistory = createButton(composite, SWT.PUSH, "...", 1);
            ((GridData) fHistory.getLayoutData()).widthHint = 25;

            fIsCaseSensitiveCheckbox = createButton(composite, SWT.CHECK, SearchMessages.SearchPage_caseSensitive, 5);

            // Line 3
            createLabel(composite, SWT.LEAD, "Scope", 10);

            // Line 4
            fModulesScopeRadio = createButton(composite, SWT.RADIO, "&Module(s)", 1);

            fModuleNames = createText(composite, SWT.SINGLE | SWT.BORDER, 3, 50);

            fWorkspaceScopeRadio = createButton(composite, SWT.RADIO, "&Workspace", 3);

            fOpenEditorsScopeRadio = createButton(composite, SWT.RADIO, "&Open Editors", 3);

            // Line 5
            fProjectsScopeRadio = createButton(composite, SWT.RADIO, "&Project(s)", 1);

            fProjectNames = createText(composite, SWT.SINGLE | SWT.BORDER, 1, 50);

            fSelectProjects = createButton(composite, SWT.PUSH, "...", 2);
            ((GridData) fSelectProjects.getLayoutData()).widthHint = 25;

            fExternalFilesRadio = createButton(composite, SWT.RADIO, "External &Folder(s)", 2);

            fExternalFolders = createText(composite, SWT.SINGLE | SWT.BORDER, 3, 50);

            fSelectFolders = createButton(composite, SWT.PUSH, "...", 1);
            ((GridData) fSelectFolders.getLayoutData()).widthHint = 25;
        } else {
            // Line 2
            fPattern = createText(composite, SWT.SINGLE | SWT.BORDER, 4, 50);

            fHistory = createButton(composite, SWT.PUSH, "...", 1);
            ((GridData) fHistory.getLayoutData()).widthHint = 25;

            fIsCaseSensitiveCheckbox = createButton(composite, SWT.CHECK, SearchMessages.SearchPage_caseSensitive, 5);

            // Line 3
            createLabel(composite, SWT.LEAD, "Scope", 1);
            fWorkspaceScopeRadio = createButton(composite, SWT.RADIO, "&Workspace", 1);

            fModulesScopeRadio = createButton(composite, SWT.RADIO, "&Module(s)", 1);
            fModuleNames = createText(composite, SWT.SINGLE | SWT.BORDER, 2, 50);
            createLabel(composite, SWT.NONE, "", 5);

            // Line 4
            createLabel(composite, SWT.NONE, "", 1);
            fProjectsScopeRadio = createButton(composite, SWT.RADIO, "&Project(s)", 1);

            fProjectNames = createText(composite, SWT.SINGLE | SWT.BORDER, 2, 50);

            fSelectProjects = createButton(composite, SWT.PUSH, "...", 1);
            ((GridData) fSelectProjects.getLayoutData()).widthHint = 25;

            createLabel(composite, SWT.LEAD,
                    "\n\nNote: only modules in the PyDev index will be searched (valid modules below a source folder).",
                    10);
            createLabel(composite, SWT.LEAD,
                    "Note: wildcards may be used for modules and project matching.",
                    10);
        }

        setControl(composite);
        Dialog.applyDialogFont(composite);
    }

    public boolean acceptExternalFoldersAndOpenEditors() {
        return false;
    }

    private Text createText(Composite composite, int style, int cols, int charsLen) {
        Text text = new Text(composite, style);
        text.setFont(composite.getFont());
        GridData data = new GridData(GridData.FILL, GridData.FILL, true, false, cols, 1);
        data.widthHint = convertWidthInCharsToPixels(charsLen);
        text.setLayoutData(data);
        return text;
    }

    private Label createLabel(Composite composite, int style, String string, int cols) {
        Label label = new Label(composite, style);
        label.setText(string);
        label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, cols, 1));
        label.setFont(composite.getFont());
        return label;
    }

    private Button createButton(Composite composite, int style, String string, int cols) {
        Button bt = new Button(composite, style);
        bt.setText(string);
        bt.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            }
        });
        bt.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, cols, 1));
        bt.setFont(composite.getFont());
        return bt;
    }

    @Override
    public boolean performAction() {
        ScopeAndData scopeAndData = getScopeAndData();
        SearchIndexData data = new SearchIndexData(fPattern.getText(), fIsCaseSensitiveCheckbox.getSelection(),
                scopeAndData.scope, scopeAndData.scopeData);
        SearchIndexQuery query = new SearchIndexQuery(data);
        NewSearchUI.runQueryInBackground(query);
        searchIndexDataHistory.add(data);
        searchIndexDataHistory.writeConfiguration();
        return true;
    }

    private ScopeAndData getScopeAndData() {
        if (fModulesScopeRadio.getSelection()) {
            return new ScopeAndData(SearchIndexData.SCOPE_MODULES, fModuleNames.getText());
        }

        if (fOpenEditorsScopeRadio != null && fOpenEditorsScopeRadio.getSelection()) {
            return new ScopeAndData(SearchIndexData.SCOPE_OPEN_EDITORS, "");
        }

        if (fWorkspaceScopeRadio.getSelection()) {
            return new ScopeAndData(SearchIndexData.SCOPE_WORKSPACE, "");
        }

        if (fProjectsScopeRadio.getSelection()) {
            return new ScopeAndData(SearchIndexData.SCOPE_PROJECTS, fProjectNames.getText());
        }

        if (fExternalFilesRadio != null && fExternalFilesRadio.getSelection()) {
            return new ScopeAndData(SearchIndexData.SCOPE_EXTERNAL_FOLDERS, fExternalFolders.getText());
        }

        // If nothing works, use workspace!
        return new ScopeAndData(SearchIndexData.SCOPE_WORKSPACE, "");
    }

    @Override
    public void setContainer(ISearchPageContainer container) {
        fContainer = container;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible && fPattern != null) {
            if (fFirstTime) {
                fFirstTime = false;

                // Load settings from last activation
                initializeFromLast();

                // Override some settings from the current selection
                initializeFromSelection();
            }
            fPattern.setFocus();
        }
        super.setVisible(visible);

        updateOKStatus();
    }

    private void initializeFromLast() {
        SearchIndexData last = searchIndexDataHistory.getLast();
        if (last != null) {
            String text = last.textPattern;
            if (text != null && text.length() > 0) {
                fPattern.setText(text);
            }
        }
    }

    private void updateOKStatus() {
        fContainer.setPerformActionEnabled(true);
    }

    private boolean initializeFromSelection() {
        ISelection selection = fContainer.getSelection();
        if (selection instanceof ITextSelection && !selection.isEmpty()
                && ((ITextSelection) selection).getLength() > 0) {
            String text = ((ITextSelection) selection).getText();
            if (text != null) {
                fPattern.setText(text);
                return true;
            }
        }

        IEditorInput editorInput = fContainer.getActiveEditorInput();
        if (editorInput != null) {
            IFile currentFile = editorInput.getAdapter(IFile.class);
            //TODO: Use it for the scoping...

        }

        return false;
    }

}