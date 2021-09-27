package com.blockset.ui;

import com.blockset.ui.parser.domain.BML;
import com.blockset.ui.parser.domain.Location;
import com.blockset.ui.support.MainClass;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBMenuItem;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.packageDependencies.ui.TreeModel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class LocationTree {
    private JPanel jPanel;
    private Tree tree;
    private JBScrollPane scrollPane;
    DialogForLocation createItemDialog;
    BML bmlMain;
    public JPanel getjPanel(Project project) {
        tree.setModel(getDefaultRoot());
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
                if(e.getButton() == MouseEvent.BUTTON1 && o!= null){
                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());

                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) o;
                    ToolWindowManager manager = ToolWindowManager.getInstance(project);
                    ToolWindow toolWindow1 = manager.getToolWindow("SetsTool");

                    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                    SetsTree tb = new SetsTree();
                    if (selectedNode.getUserObject() instanceof Location) {
                        Content content1 = contentFactory.createContent(tb.getjPanel((Location) selectedNode.getUserObject(),project,selPath), "", false);
                        toolWindow1.getContentManager().removeAllContents(true);
                        toolWindow1.getContentManager().addContent(content1);
                    }
                }
            }
        });


        return jPanel;
    }

    public JPanel getjPanel(Project project, String s) {
        tree.setModel(getDefaultRoot());
        JBPopupMenu popupMenu = createPopupMenu();
        tree.setModel(getRoot(s));
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
                    ToolWindow toolWindow1 = manager.getToolWindow("SetsTool");

                    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                    SetsTree tb = new SetsTree();
                    if (selectedNode.getUserObject() instanceof Location) {
                        Content content1 = contentFactory.createContent(tb.getjPanel((Location) selectedNode.getUserObject(),project,selPath), "", false);
                        toolWindow1.getContentManager().removeAllContents(true);
                        toolWindow1.getContentManager().addContent(content1);
                    }
                }
            }
        });


        return jPanel;
    }
    private TreeModel getRoot(String file){
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        try {

            List<Location> l = MainClass.getBml().getttLocations();
            ArrayList<String> list = new ArrayList<>();
            for (Location loc: l) {
                list.add(loc.getBase());
            }
            ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
            for (String s: list) {
                ArrayList<String> ls = new ArrayList<>();
                arrayLists.add(new ArrayList<String>(Arrays.asList(s.split("/"))));
            }
            HashMap<Location, HashMap> map = new HashMap<>();

            for (int j = 0; j < arrayLists.size(); j++) {
                HashMap<Location, HashMap> arrayTree = map;
                Location location = null;
                StringBuilder locationString = new StringBuilder("");
                for (int i = 1; i < arrayLists.get(j).size(); i++) {
                    locationString.append("/"+arrayLists.get(j).get(i));
                    if (isFind(locationString.toString(),l)){
                        location = find(locationString.toString(),l);
                    }else {
                        location = new Location(locationString.toString());
                    }
                    if (isFindLoc(arrayTree,locationString.toString())){
                        arrayTree = FindLoc(arrayTree,locationString.toString());
                    }else {
                        HashMap<Location, HashMap> arrayTreeTmp = new HashMap<>();
                        arrayTree.put(location,arrayTreeTmp);
                        arrayTree = arrayTreeTmp;
                    }
                }
            }
            for (Map.Entry<Location, HashMap> en:map.entrySet()) {
                DefaultMutableTreeNode cur = new DefaultMutableTreeNode(en.getKey(),true);
                root.add(cur);
                addSet(en.getValue(),cur);
            }

        }catch (Exception e){
            System.out.println("Maa");
        }
        return new TreeModel(root);
    }
    private boolean isFindLoc(HashMap<Location, HashMap> m, String s){
        for (Map.Entry<Location, HashMap> e: m.entrySet()) {
            if (e.getKey().getBase().equals(s)){
                return true;
            }
        }
        return false;
    }
    private HashMap<Location, HashMap> FindLoc(HashMap<Location, HashMap> m, String s){
        for (Map.Entry<Location, HashMap> e: m.entrySet()) {
            if (e.getKey().getBase().equals(s)){
                return e.getValue();
            }
        }
        return new HashMap<>();
    }
    private boolean isFind(String s, List<Location> locations){
        for (Location w:locations) {
            if (w.getBase().equals(s)){
                return true;
            };
        }
        return false;
    }
    private Location find(String s, List<Location> locations){
        for (Location w:locations) {
            if (w.getBase().equals(s)){
                return w;
            };
        }
        return new Location();
    }
    private void addSet(HashMap<Location , HashMap> s, DefaultMutableTreeNode defaultMutableTreeNode) {
        for (Map.Entry<Location, HashMap> a : s.entrySet()) {

            DefaultMutableTreeNode cur = new DefaultMutableTreeNode(a.getKey(), true);
            defaultMutableTreeNode.add(cur);
            addSet(a.getValue(), cur);
        }
    }

    private TreeModel getDefaultRoot(){
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
        return new TreeModel(root);
    }

    private void addNewItem( JBPopupMenu popup ) {
        JBMenuItem newItem = new JBMenuItem("New");
        newItem.addActionListener(e -> {
            createItemDialog = new DialogForLocation();
            createItemDialog.show();
            if (createItemDialog.getExitCode()== 0) {
                System.out.println(createItemDialog.getTextField1().getText());
                if (tree.getSelectionModel().getSelectionCount()>0) {
                    System.out.println(tree.getSelectionPath());
                    TreePath path = tree.getSelectionPath();
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    Location location;
                    if (path.toString().equals("[Root]")){
                        location = new Location ("/"+createItemDialog.getTextField1().getText());
                    }else {
                        Location l =(Location) node.getUserObject();
                        location = new Location (l.getBase()+"/"+createItemDialog.getTextField1().getText());
                    }
                    MainClass.getBml().addLocation(location);
                    node.add(new DefaultMutableTreeNode(location, true));
                    tree.getExpandsSelectedPaths();
                    reloadTree(tree);
                }
            }
        });
        popup.add( newItem );
        popup.addSeparator();
    }


    private JBPopupMenu createPopupMenu( ) {
        final JBPopupMenu popup = new JBPopupMenu();
        addNewItem(popup);
        JBMenuItem refItem = new JBMenuItem("Refactor");
        refItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object o = tree.getLastSelectedPathComponent();
                DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) o;
                Location location = (Location) defaultMutableTreeNode.getUserObject();
                createItemDialog = new DialogForLocation();
                createItemDialog.show();
                if (createItemDialog.getExitCode()== 0) {
                    String s = createItemDialog.getTextField1().getText();
                    List<Location> locationList = MainClass.getBml().getttLocations();
                    String base = location.getBase();
                    ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(base.split("/")));
                    arrayList.set(arrayList.size()-1,s);
                    StringBuilder newBase = new StringBuilder();
                    for (int i = 0; i < arrayList.size()-1; i++) {
                        if (i==0) continue;
                        newBase.append("/"+arrayList.get(i));
                    }
                    newBase.append("/"+s);
                    for (Location l: locationList) {
                       if(l.getBase().contains(base)){
                           l.setBBase(newBase.toString()+l.getBase().substring(base.length(),l.getBase().length()));
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
                Object o = tree.getLastSelectedPathComponent();
                DefaultMutableTreeNode defaultMutableTreeNode = (DefaultMutableTreeNode) o;
                try {
                    String base = ((Location) defaultMutableTreeNode.getUserObject()).getBase();
                    List<Location> locationList = MainClass.getBml().getttLocations();
                    List<Location> locationListRemove = new ArrayList<>();
                    for (Location l : locationList) {
                        if (l.getBase().contains(base)) {
                            locationListRemove.add(l);
                        }
                    }
                    for (Location l : locationListRemove) {
                        locationList.remove(l);
                    }
                    DefaultMutableTreeNode defaultMutableTreeNode1 = (DefaultMutableTreeNode) defaultMutableTreeNode.getParent();
                    defaultMutableTreeNode1.remove(defaultMutableTreeNode);
                    reloadTree(tree);
                }catch (Exception excep){
                    excep.printStackTrace();
                }
            }
        });
        popup.add( refItem );
        popup.addSeparator();
        popup.add( deleteItem );

        return popup;
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
}
