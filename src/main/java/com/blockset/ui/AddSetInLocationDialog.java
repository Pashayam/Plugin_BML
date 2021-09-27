package com.blockset.ui;

import com.blockset.ui.parser.domain.Block;
import com.blockset.ui.parser.domain.Model;
import com.blockset.ui.parser.domain.Set;
import com.blockset.ui.support.MainClass;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddSetInLocationDialog extends DialogWrapper {
    private JPanel jPanel;
    private JComboBox comboBox;
    private JButton button;
    private JBScrollPane scrollPane;
    private JPanel panel;
    private TreePath path;
    private ArrayList<String> arrayList;
    private List<Set> list;

    public JPanel getPanel() {
        return panel;
    }

    public List<Set> getList() {
        return list;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        panel.setLayout(new GridLayout(0,1));
        comboBox.addActionListener(e -> {
            panel.removeAll();
            Set infoNode = null;
            String s = (String) comboBox.getSelectedItem();
            List<Set> l = list;
//                Set me;
            for (Set se: l) {
                if (se.getName().equals(s)){
                    infoNode = se;
                }
            }
            List<Block> blockList = infoNode.getBlocks();
            for (Block b:blockList) {
                panel.add(new JBCheckBox(b.getName()));
            }
            pack();
            repaint();
        });

        return jPanel;
    }
    public Set find(List<Set> list, int count){
        Set findSet = null;
        if (arrayList.size()<=count+1){
            return null;
        }
        for (Set s:list) {
            if (s.getName().equals(arrayList.get(count+1))){
                if (count+2 ==arrayList.size()){
                    return s;
                }
               findSet = find(s.getListSet(),count+1);
            }
        }

        /*Enumeration<DefaultMutableTreeNode> e = s.children();
        while (e.hasMoreElements()){
            DefaultMutableTreeNode no = e.nextElement();
            if (no.get)
        }*/
        return findSet;
    }

    public AddSetInLocationDialog() {
        super(true);
        init();
        pack();
        setTitle("Add Set");
    }
    public void sda(){
        String string = path.toString().substring(1,path.toString().length()-1);
        arrayList = new ArrayList<>(Arrays.asList(string.split(", ")));

        Model model = MainClass.getBml().getttModel();
        list = model.getListSet();
        Set set;
        if (arrayList.size() == 1){

        }else {
            set = find(list, 0);
            list = set.getListSet();
        }
        for (Set se: list){
            comboBox.addItem(se.getName());
        }
    }

    public JComboBox getComboBox() {
        return comboBox;
    }

    public void setPath(TreePath path) {
        this.path = path;
    }
}
