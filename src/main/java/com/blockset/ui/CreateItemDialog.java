package com.blockset.ui;

import com.blockset.ui.support.MyClass;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.colorpicker.CommonButton;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class CreateItemDialog extends DialogWrapper {

    private JPanel mainPanel;
    private JTextField nameTextFiled;
    private JBLabel setName;
    private JBLabel relation;
    private ComboBox<String> comboBox;
    private JPanel topPanel;
    private JPanel jPanel11;
    private JPanel jPanel12;
    private JPanel jPanel13;
    private CommonButton showPanelButton;
    private JTextField blockName;
    private JComboBox<String> blockType;
    private CommonButton plussButton;
    private JPanel bottomPanel;
    private JPanel centerPanel;
    private JBLabel blocksLabel;
    private JPanel jPanel31;
    private JBLabel actionLabel;
    private JBLabel limitLabel;
    private JBLabel functionLabel;
    private JBLabel additionActionLabel;
    private JBLabel cloneLabel;
    private JBLabel missNameLabel;
    private JComboBox<String> comboBox2;
    private JSpinner spinner1;
    private JCheckBox selectionCheckBox;
    private JCheckBox countCheckBox;
    private JCheckBox authorCheckBox;
    private JCheckBox selectCheckBox;
    private JCheckBox createCheckBox;
    private JCheckBox updateCheckBox;
    private JCheckBox deleteCheckBox;
    private JComboBox<String> comboBox3;
    private JCheckBox checkBox1;
    private JPanel actionPanel;
    private JPanel limitPanel;
    private JPanel functionPanel;
    private JPanel additionActionPanel;
    private JPanel clonePanel;
    private JPanel missNamePanel;
    private JPanel columNamesPanel;
    private JScrollPane scroll;

    public void setBlockList(ArrayList<MyClass> blockList) {
        this.blockList = blockList;
    }

    public JScrollPane getScroll() {
        return scroll;
    }

    public JPanel getColumNamesPanel() {
        return columNamesPanel;
    }

    private JTextPane textPane1;
    private JTextPane textPane2;
    private ArrayList<MyClass> blockList = new ArrayList<>();

    public CreateItemDialog() {
        super(true);
        init();
        pack();
        setTitle("Create Item");
    }

    public JTextField getTextField() {
        return nameTextFiled;
    }

    private void setLiseners(){
        columNamesPanel.setLayout(new VerticalFlowLayout());

        blockName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (blockName.getText().length() >= 15 ) // limit to 3 characters
                    e.consume();
            }
        });


        plussButton.addActionListener(e -> {

            if (blockList.stream().noneMatch(s->s.getNameType().equals(blockName.getText())) && !((String)blockType.getSelectedItem()).equals("Choose type") && blockName.getText().length()>0){

                scroll.setVisible(true);
                JPanel panel = new JPanel(new GridLayout(0,3));
                columNamesPanel.add(panel);
                JLabel nameBlock= new JLabel(blockName.getText());
                JLabel nameType= new JLabel((String) blockType.getSelectedItem());
                MyClass block = new MyClass("X",nameBlock,nameType,panel);
                blockList.add(block);
                panel.add(nameBlock);
                panel.add(nameType);
                panel.add(block);
                System.out.println();
                pack();

                block.addActionListener(e1 -> {
                    MyClass b = (MyClass) e1.getSource();
                    columNamesPanel.remove(b.getPanel());
                    blockList.remove(b);
                    if (columNamesPanel.getComponents().length==0){
                        scroll.setVisible(false);
                    }
                    pack();
                    repaint();
                    pack();
                });

            }
        });
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        centerPanel.setVisible(true);
        scroll.setVisible(false);
        showPanelButton.setIcon(AllIcons.Diff.ArrowRight);

        showPanelButton.addActionListener(e -> {
            if (centerPanel.isVisible())
            {
                centerPanel.setVisible(false);
                mainPanel.remove(centerPanel);
                pack();
                repaint();
                pack();
            }
            else
            {
                centerPanel.setVisible(true);
                mainPanel.add(centerPanel);
                pack();
                repaint();
                pack();
            }
            System.out.println(mainPanel.getSize());
        } );
        setLiseners();


        return mainPanel;
    }

    public ArrayList<MyClass> getBlockList() {
        return blockList;
    }

    public JTextField getNameTextFiled() {
        return nameTextFiled;
    }

    public ComboBox<String> getComboBox() {
        return comboBox;
    }

    public JComboBox<String> getBlockType() {
        return blockType;
    }

    public JComboBox<String> getComboBox2() {
        return comboBox2;
    }

    public JSpinner getSpinner1() {

        return spinner1;
    }

    public JCheckBox getSelectionCheckBox() {
        return selectionCheckBox;
    }

    public void setComboBox2(String s ){
        if(s == null){
            return;
        }
        switch (s){
            case "read":
                this.comboBox2.setSelectedItem("read");
                break;
            case "write":
                this.comboBox2.setSelectedItem("write");
                break;
            default:
                this.comboBox2.setEnabled(false);
                break;
        }
    }

    public void setNameTextFiled(String s) {

        if(s == null){
            return;
        }
        this.nameTextFiled.setText(s);
    }

    public void setComboBox(String s) {
        if(s == null){
            return;
        }
        switch (s){
            case "Not selected":
            case "Child":
            case "Parent":
            case "Both":
                this.comboBox.setSelectedItem(s);
                break;
            default:
                this.comboBox.setEnabled(false);
                break;
        }
    }

    public void setSpinner1(String s)
    {
        if(s == null){
            return;
        }
        try {
            Integer.parseInt(s);
            this.spinner1.setValue(Integer.parseInt(s));
        }catch (NumberFormatException e){
            this.spinner1.setEnabled(false);
        }
    }

    public void setSelectionCheckBox(String s) {
        if(s == null){
            return;
        }
        if (s.equals("true")){
            this.selectCheckBox.setSelected(true);
        }else  if (s.equals("false")){
            this.selectionCheckBox.setSelected(false);
        }else {
//            selectCheckBox.setIcon(new ImageIcon("resources/icons/xml.svg"));
            selectionCheckBox.setEnabled(false);
            System.out.println("hhhdshsdhfs");
        }
    }

    public void setCountCheckBox(String s) {
        if(s == null){
            return;
        }
        if (s.equals("true")){
            this.countCheckBox.setSelected(true);
        }else  if (s.equals("false")){
            this.countCheckBox.setSelected(false);
        }else {
            countCheckBox.setEnabled(false);
        }
    }

    public void setAuthorCheckBox(String s) {
        if(s == null){
            return;
        }
        if (s.equals("true")){
            this.authorCheckBox.setSelected(true);
        }else if (s.equals("false")){
            this.authorCheckBox.setSelected(false);
        }else {
            authorCheckBox.setEnabled(false);
        }
    }

    public void setSelectCheckBox(String s) {
        if(s == null){
            return;
        }
        if (s.equals("true")){
            this.selectCheckBox.setSelected(true);
        }else  if (s.equals("false")){
            this.selectCheckBox.setSelected(false);
        }else {
            selectCheckBox.setEnabled(false);
        }
    }

    public void setCreateCheckBox(String s) {
        if(s == null){
            return;
        }
        if (s.equals("true")){
            this.createCheckBox.setSelected(true);
        }else  if (s.equals("false")){
            this.createCheckBox.setSelected(false);
        }else {
            createCheckBox.setEnabled(false);
        }
    }

    public void setUpdateCheckBox(String s) {
        if(s == null){
            return;
        }
        if (s.equals("true")){
            this.updateCheckBox.setSelected(true);
        }else if (s.equals("false")){
            this.updateCheckBox.setSelected(false);
        }else {
            updateCheckBox.setEnabled(false);
        }
    }

    public void setDeleteCheckBox(String s) {
        if(s == null){
            return;
        }
        if (s.equals("true")){
            this.deleteCheckBox.setSelected(true);
        }else  if (s.equals("false")){
            this.deleteCheckBox.setSelected(false);
        }else {
            deleteCheckBox.setEnabled(false);
        }
    }

    public void setComboBox3(String s) {

        if(s == null){
            return;
        }
        this.comboBox3.setSelectedItem(s);
    }

    public void setCheckBox1(String s) {
        if(s == null){
            return;
        }
        if (s.equals("true")){
            this.checkBox1.setSelected(true);
        }else  if (s.equals("false")){
            this.checkBox1.setSelected(false);
        }else {
            checkBox1.setEnabled(false);
        }
    }

    public JCheckBox getCountCheckBox() {
        return countCheckBox;
    }

    public JCheckBox getAuthorCheckBox() {
        return authorCheckBox;
    }

    public JCheckBox getSelectCheckBox() {
        return selectCheckBox;
    }

    public JCheckBox getCreateCheckBox() {
        return createCheckBox;
    }

    public JCheckBox getUpdateCheckBox() {
        return updateCheckBox;
    }

    public JCheckBox getDeleteCheckBox() {
        return deleteCheckBox;
    }

    public JComboBox<String> getComboBox3() {
        return comboBox3;
    }

    public JCheckBox getCheckBox1() {
        return checkBox1;
    }
}
