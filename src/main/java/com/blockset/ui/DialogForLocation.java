package com.blockset.ui;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class DialogForLocation extends DialogWrapper {
    private JPanel jPanel;
    private JTextField textField1;

    public DialogForLocation() {
        super(true);
        init();
        pack();
        setTitle("Create Location");
    }

    public JTextField getTextField1() {
        return textField1;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return jPanel;
    }
}
