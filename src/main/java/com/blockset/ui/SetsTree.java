package com.blockset.ui;

import com.blockset.ui.parser.domain.Block;
import com.blockset.ui.parser.domain.Location;
import com.blockset.ui.parser.domain.Set;
import com.blockset.ui.support.CheckAtributs;
import com.blockset.ui.support.MyClass;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.packageDependencies.ui.TreeModel;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SetsTree {
    private JPanel jPanel;
    private Tree tree;
    private JBScrollPane scrollPane;
    AddSetInLocationDialog createItemDialog;
    CreateItemDialog dialog;
    Project project;
    Location location;
    TreePath treePath;
    ArrayList<String> arrayList;
    List<Set> list;

    private TreeModel getRoot(Location l){

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Location sets");
            List<Set> list = l.getListSet();
            for (Set s: list) {
                addSet(s,root);
            }
        return new TreeModel(root);
    }
    private void addSet(Set s, DefaultMutableTreeNode defaultMutableTreeNode){
        DefaultMutableTreeNode cur = new DefaultMutableTreeNode(s,true);
        defaultMutableTreeNode.add(cur);
        List<Set> list = s.getListSet();
        if (list != null && list.size()!=0){
            for (Set set: list) {
                addSet(set,cur);
            }
        }
    }
    public JPanel getjPanel(Location location, Project project, TreePath treePath) {
        this.treePath = treePath;
        this.project = project;
        this.location = location;
        tree.setModel(getRoot(location));
        JBPopupMenu popupMenu = createPopupMenu();
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Object o = tree.getLastSelectedPathComponent();
                if (o != null && e.getButton() == MouseEvent.BUTTON3) {
                    DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) o;
                    tree.add(popupMenu);
                    popupMenu.show(tree, e.getX(), e.getY());
                }
            }
        });

        return jPanel;
    }




    public void reloadTree(Tree jYourTree) {
        ArrayList<TreePath> expanded= new ArrayList<>();
        for (int i = 0; i < jYourTree.getRowCount() - 1; i++) {
            TreePath currPath = jYourTree.getPathForRow(i);
            TreePath nextPath = jYourTree.getPathForRow(i + 1);
            if (currPath.isDescendant(nextPath)) {
                expanded.add(currPath);
            }
        }
        ((DefaultTreeModel)jYourTree.getModel()).reload();
        for (TreePath path : expanded) {
            jYourTree.expandPath(path);
        }
    }




    private JBPopupMenu createPopupMenu( ) {
        final JBPopupMenu popup = new JBPopupMenu();
        addNewItem(popup);
        JBMenuItem refItem = new JBMenuItem("Refactor");
        refItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                TreePath pathRefactor = tree.getSelectionPath();
                dialog = new CreateItemDialog();
                Object o = tree.getLastSelectedPathComponent();
                DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) o;
                Set set = (Set) defaultMutableTreeNode.getUserObject();
                CheckAtributs.check(set,dialog);
                dialog.show();
                if (dialog.getExitCode()== 0) {
                    Set setNode =createN();
                    List<Set> list = set.getListSet();
                    setNode.setListSets(list);
                    defaultMutableTreeNode.setUserObject(setNode);
                    String string = pathRefactor.toString().substring(1,pathRefactor.toString().length()-1);
                    arrayList = new ArrayList<>(Arrays.asList(string.split(", ")));
                    list = location.getListSet();
                    Set set1;
                    Set set2;
                    if (arrayList.size() == 1){

                    }else {
                        set1 = find(list, 0);
                        if(arrayList.size() == 2){
                            location.getListSet().remove(set1);
                            location.getListSet().add(setNode);
                        }else{
                            arrayList.remove(arrayList.size()-1);
                            set2 = find(list,0);
                            set2.getListSet().remove(set1);
                            set2.getListSet().add(setNode);
                        }
                    }

                    reloadTree(tree);
                }
            }
        });
        JBMenuItem deleteItem = new JBMenuItem("Delete");
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath path = tree.getSelectionPath();
                String string = path.toString().substring(1,path.toString().length()-1);
                arrayList = new ArrayList<>(Arrays.asList(string.split(", ")));
                list = location.getListSet();
                Set set;
                Set set2;
                if (arrayList.size() == 1){

                }else {
                    set = find(list, 0);
                    if(arrayList.size() == 2){
                        location.getListSet().remove(set);
                    }else{
                        arrayList.remove(arrayList.size()-1);
                        set2 = find(list,0);
                        set2.getListSet().remove(set);
                    }
                    Object o = tree.getLastSelectedPathComponent();
                    DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) o;
                    DefaultMutableTreeNode defaultMutableTreeNode1 = (DefaultMutableTreeNode) defaultMutableTreeNode.getParent();
                    defaultMutableTreeNode1.remove(defaultMutableTreeNode);
                    reloadTree(tree);
                }
            }
        });
        popup.add( refItem );
        popup.addSeparator();
        popup.add( deleteItem );
        return popup;
    }


    public Set find(List<Set> list, int count) {
        Set findSet = null;
        if (arrayList.size() <= count + 1) {
            return null;
        }
        for (Set s : list) {
            if (s.getName().equals(arrayList.get(count + 1))) {
                if (count + 2 == arrayList.size()) {
                    return s;
                }
                findSet = find(s.getListSet(), count + 1);
            }
        }
        return findSet;
    }
    private Set createN(){
        Set infoNode = new Set(""+dialog.getTextField().getText(),
                ""+(String) dialog.getComboBox().getSelectedItem(),
                ""+(String) dialog.getComboBox3().getSelectedItem(),
                ""+(String)dialog.getComboBox2().getSelectedItem(),
                ""+(Integer) dialog.getSpinner1().getValue(),
                ""+dialog.getSelectionCheckBox().isSelected(),
                ""+dialog.getCountCheckBox().isSelected(),
                ""+ dialog.getAuthorCheckBox().isSelected(),
                ""+dialog.getSelectCheckBox().isSelected(),
                ""+ dialog.getCreateCheckBox().isSelected(),
                ""+  dialog.getUpdateCheckBox().isSelected(),
                ""+ dialog.getDeleteCheckBox().isSelected(),
                ""+ dialog.getCheckBox1().isSelected());
        ArrayList<MyClass> list = dialog.getBlockList();
        List<Block> blocks = new ArrayList<>();
        for (MyClass myClass: list){
            Block block = new Block();
            block.setttName(myClass.getNameType());
            block.setttType(myClass.getType());
            blocks.add(block);
        }
        infoNode.setListBlock(blocks);
        return infoNode;
    }
    private Set createNode(){
        Set infoNode = new Set();
        String s = (String)createItemDialog.getComboBox().getSelectedItem();
        List<Set> l = createItemDialog.getList();
        Set me = null;
        for (Set se: l) {
            if (se.getName().equals(s)){
                me = se;
            }
        }
        infoNode.settAction(me.getAction());
        infoNode.settAuthor(me.isAuthor());
        infoNode.settClone(me.getClone());
        infoNode.settCount(me.isCount());
        infoNode.settCreate(me.isCreate());
        infoNode.settDelete(me.isDelete());
        infoNode.settLimit(me.getLimit());
        infoNode.settMissName(me.isMissName());
        infoNode.settName(me.getName());
        infoNode.settRelation(me.getRelation());
        infoNode.settSelect(me.isSelect());
        infoNode.settSelection(me.isSelection());
        infoNode.settUpdate(me.isUpdate());
        JPanel panel = createItemDialog.getPanel();
        Component[] components =panel.getComponents();
        List<Block> blockList = me.getBlocks();
        for (Component c:components) {
            JBCheckBox cb = (JBCheckBox) c;
            System.out.println(cb.getText());
            System.out.println(cb.getName());
            for (Block b:blockList) {
                if (b.getName().equals(cb.getText())&& cb.isSelected()){
                    infoNode.addBlock(b);
                }
            }
        }

        return infoNode;
    }

    private void addNewItem( JBPopupMenu popup ) {
        JBMenuItem newItem = new JBMenuItem("New");
        newItem.addActionListener(e -> {
            createItemDialog = new AddSetInLocationDialog();
            createItemDialog.setPath(tree.getSelectionPath());
            createItemDialog.sda();
            createItemDialog.show();
            if (createItemDialog.getExitCode()== 0) {
                if (tree.getSelectionModel().getSelectionCount()>0) {
                    TreePath path = tree.getSelectionPath();
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    Set set = createNode();
                    node.add(new DefaultMutableTreeNode(set, true));
                    Object d = node.getUserObject();
                    if (d instanceof Set) {
                        Set data = (Set) d;
                        data.getListSet().add(set);
                        System.out.println("А я сет");
                    } else {
                        System.out.println("А я локация");
                        location.addSet(set);
                    }
                }
                reloadTree(tree);
            }
        });
        popup.add( newItem );
        popup.addSeparator();
    }
}
