package com.blockset.ui.support;

import javax.swing.*;

public class MyClass extends JButton {
    private JLabel nameTypeLabel,typeLabel;
    private JPanel panel;
    private String nameType,type;


    public void setType(JLabel type) {
        this.typeLabel = type;
    }

    public void setNameType(JLabel nameType) {
        this.nameTypeLabel = nameType;
    }

    public void setPanel(JPanel panel) {
        this.panel = panel;
    }

    public JPanel getPanel() {
        return panel;
    }

    public MyClass(String text, JLabel nameType, JLabel type, JPanel panel) {
        super(text);
        this.nameTypeLabel = nameType;
        this.typeLabel = type;
        this.panel = panel;
        this.nameType=nameType.getText();
        this.type=type.getText();
    }

    public JLabel getNameTypeLabel() {
        return nameTypeLabel;
    }

    public JLabel getTypeLabel() {
        return typeLabel;
    }

    public String getNameType() {
        return nameType;
    }

    public String getType() {
        return type;
    }
}
