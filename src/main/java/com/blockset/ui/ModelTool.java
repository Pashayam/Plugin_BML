package com.blockset.ui;

import com.blockset.ui.parser.BMLWriter;
import com.blockset.ui.parser.domain.Block;
import com.blockset.ui.parser.domain.Model;
import com.blockset.ui.parser.domain.Set;
import com.blockset.ui.support.CheckAtributs;
import com.blockset.ui.support.MainClass;
import com.blockset.ui.support.MyClass;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.packageDependencies.ui.TreeModel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ModelTool implements ToolWindowFactory {
    private JPanel jPanel;
    private Tree tree;
    private JBScrollPane scrollPane;
    private JButton button;
    CreateItemDialog createItemDialog;
    ArrayList<String> arrayList;
    List<Set> list;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        Content content = contentFactory.createContent(jPanel, "", false);
        toolWindow.getContentManager().addContent(content);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BMLWriter.write(MainClass.getBml(), MainClass.getFile().getPath());
            }
        });
        if (MainClass.getModelTree() != null) {
            TreeModel model = (TreeModel) MainClass.getModelTree().getModel();
            if (model != null) {
                tree.setModel(model);
                MainClass.setModelTree(tree);
            }
        }else {
            MainClass.setModelTree(tree);
        }


        final JBPopupMenu popupMenu = createPopupMenu();
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Object o = tree.getLastSelectedPathComponent();
                if (o != null && e.getButton() == MouseEvent.BUTTON3) {
                    DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) o;
                    tree.add(popupMenu);
                    popupMenu.show(tree, e.getX(), e.getY());
                }
                if(e.getButton() == MouseEvent.BUTTON1 && o!= null){
                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) o;
                    ToolWindowManager manager = ToolWindowManager.getInstance(project);
                    ToolWindow toolWindow1 = manager.getToolWindow("SetRefactor");

                    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                    SetTable tb = new SetTable();
                    if (selectedNode.getUserObject() instanceof Set) {
                        Content content1 = contentFactory.createContent(tb.getjPanel((Set) selectedNode.getUserObject()), "", false);
                        toolWindow1.getContentManager().removeAllContents(true);
                        toolWindow1.getContentManager().addContent(content1);
                    }
                }
            }
        });
        reloadTree(tree);

    }


    private TreeModel getDefaultRoot(){
        Model model = new Model();
        MainClass.getBml().setModel(model);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Model");
        return new TreeModel(root);
    }


    private JBPopupMenu createPopupMenu( ) {
        final JBPopupMenu popup = new JBPopupMenu();
        addNewItem(popup);
        JBMenuItem refItem = new JBMenuItem("Refactor");
        refItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath pathRefactor = tree.getSelectionPath();
                createItemDialog = new CreateItemDialog();
                Object o = tree.getLastSelectedPathComponent();
                DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) o;
                Set set = (Set) defaultMutableTreeNode.getUserObject();
                CheckAtributs.check(set,createItemDialog);
                createItemDialog.show();
                if (createItemDialog.getExitCode()== 0) {
                    Set setNode =createNode();
                    List<Set> list = set.getListSet();
                    setNode.setListSets(list);
                    defaultMutableTreeNode.setUserObject(setNode);
                    String string = pathRefactor.toString().substring(1,pathRefactor.toString().length()-1);
                    arrayList = new ArrayList<>(Arrays.asList(string.split(", ")));
                    Model model = MainClass.getBml().getttModel();
                    list = model.getListSet();
                    Set set1;
                    Set set2;
                    if (arrayList.size() == 1){

                    }else {
                        set1 = find(list, 0);
                        if(arrayList.size() == 2){
                            model.getListSet().remove(set1);
                            model.getListSet().add(setNode);
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
                Model model = MainClass.getBml().getttModel();
                list = model.getListSet();
                Set set;
                Set set2;
                if (arrayList.size() == 1){

                }else {
                    set = find(list, 0);
                    if(arrayList.size() == 2){
                        model.getListSet().remove(set);
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

    private Set createNode(){
        Set infoNode = new Set(""+createItemDialog.getTextField().getText(),
                ""+(String) createItemDialog.getComboBox().getSelectedItem(),
                ""+(String) createItemDialog.getComboBox3().getSelectedItem(),
                ""+(String)createItemDialog.getComboBox2().getSelectedItem(),
                ""+(Integer) createItemDialog.getSpinner1().getValue(),
                ""+createItemDialog.getSelectionCheckBox().isSelected(),
                ""+createItemDialog.getCountCheckBox().isSelected(),
                ""+ createItemDialog.getAuthorCheckBox().isSelected(),
                ""+createItemDialog.getSelectCheckBox().isSelected(),
                ""+ createItemDialog.getCreateCheckBox().isSelected(),
                ""+  createItemDialog.getUpdateCheckBox().isSelected(),
                ""+ createItemDialog.getDeleteCheckBox().isSelected(),
                ""+ createItemDialog.getCheckBox1().isSelected());
        ArrayList<MyClass> list = createItemDialog.getBlockList();
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

    private void addNewItem( JBPopupMenu popup ) {
        JBMenuItem newItem = new JBMenuItem("New");
        newItem.addActionListener(e -> {
            createItemDialog = new CreateItemDialog();
            createItemDialog.show();
            if (createItemDialog.getExitCode()== 0) {
                System.out.println(createItemDialog.getTextField().getText());
                if (tree.getSelectionModel().getSelectionCount()>0) {
                    System.out.println(tree.getSelectionPath());
                    TreePath path = tree.getSelectionPath();
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    Set set =createNode();
                    node.add(new DefaultMutableTreeNode(set, true));
                    Object d = node.getUserObject();
                    if (d instanceof Set) {
                        Set data = (Set) d;
                        data.getListSet().add(set);
                    }else {
                        MainClass.getBml().getttModel().addSet(set);
                    }
                    tree.getExpandsSelectedPaths();
                    reloadTree(tree);
                }
            }
        });
        popup.add( newItem );
        popup.addSeparator();
    }

}
