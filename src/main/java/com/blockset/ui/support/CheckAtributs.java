package com.blockset.ui.support;

import com.blockset.ui.CreateItemDialog;
import com.blockset.ui.parser.domain.Block;
import com.blockset.ui.parser.domain.Set;

import javax.swing.*;
import java.awt.*;

public class CheckAtributs {
    public  static void check(Set s, CreateItemDialog dialog) {
        dialog.setAuthorCheckBox("" + s.isAuthor());
        dialog.setCheckBox1("" + s.isMissName());
        dialog.setComboBox2(s.getAction());
        dialog.setComboBox3(s.getClone());
        dialog.setCountCheckBox("" + s.isCount());
        dialog.setCreateCheckBox("" + s.isCreate());
        dialog.setDeleteCheckBox("" + s.isDelete());
        dialog.setSelectCheckBox("" + s.isSelect());
        dialog.setSelectionCheckBox("" + s.isSelection());
        dialog.setUpdateCheckBox("" + s.isUpdate());
        dialog.setSpinner1("" + s.getLimit());
        dialog.setComboBox(s.getRelation());
        dialog.setNameTextFiled(s.getName());

        JScrollPane scroll = dialog.getScroll();
        JPanel columNamesPanel = dialog.getColumNamesPanel();
        scroll.setVisible(true);
//        ArrayList<MyClass> blockList = new ArrayList<>();
        for (Block b : s.getBlocks()) {

            JPanel panel = new JPanel(new GridLayout(0, 3));
            columNamesPanel.add(panel);
            JLabel nameBlock = new JLabel(b.getName());
            JLabel nameType = new JLabel(b.getType());
            MyClass block = new MyClass("X", nameBlock, nameType, panel);
            dialog.getBlockList().add(block);
            panel.add(nameBlock);
            panel.add(nameType);
            panel.add(block);
            System.out.println();
            block.addActionListener(e1 -> {
                MyClass myClass = (MyClass) e1.getSource();
                columNamesPanel.remove(myClass.getPanel());
                dialog.getBlockList().remove(myClass);
                if (columNamesPanel.getComponents().length == 0) {
                    scroll.setVisible(false);
                }
                dialog.pack();
                dialog.repaint();
            });
        }
//        dialog.setBlockList(blockList);

    }
}
