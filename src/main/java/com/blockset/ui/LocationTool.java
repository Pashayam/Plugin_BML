package com.blockset.ui;

import com.blockset.ui.parser.domain.BML;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LocationTool implements ToolWindowFactory {
    private JPanel jPanel;
    private Tree tree;
    DialogForLocation createItemDialog;
    BML bmlMain;
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(jPanel, "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
