package com.blockset.ui;

import com.blockset.ui.parser.domain.Set;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SetRefactor implements ToolWindowFactory {
    private JPanel jPanel;
    private JTable table1;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        SetTable t = new SetTable();
        Content content = contentFactory.createContent(t.getjPanel(new Set()), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
