package com.blockset.ui.support;

import com.blockset.ui.LocationTree;
import com.blockset.ui.parser.domain.Model;
import com.blockset.ui.parser.domain.Set;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerAdapter;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.packageDependencies.ui.TreeModel;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.util.List;

public class MyPostStartupActivity implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
//        Tree ge = new Tree();
//        ge.setModel(new TreeModel(new DefaultMutableTreeNode("EEEEE")));
//        MainClass.setModelTree(new Tree());
        MessageBus messageBus = project.getMessageBus();
        messageBus.connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerAdapter() {
            @Override
            public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                super.fileOpened(source, file);
                if (file.getFileType().getName().equals("BML")){
                    System.out.println(file.getPath());
                    MainClass.setFile(new File(file.getPath()));
                    MainClass.generateBML();
                    Tree tree = MainClass.getModelTree();
                    if(tree == null){
                        tree = new Tree();
                    }
                    tree.setModel(getRoot());
                    MainClass.setModelTree(tree);
//                    MainClass.setModelTree(tree);
                    ToolWindowManager manager = ToolWindowManager.getInstance(project);
                    ToolWindow toolWindow1 = manager.getToolWindow("Location");
                    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                    LocationTree locationTree = new LocationTree();
                    Content content1 = contentFactory.createContent(locationTree.getjPanel(project,file.getPath()), "", false);
                    toolWindow1.getContentManager().removeAllContents(true);
                    toolWindow1.getContentManager().addContent(content1);

                    ToolWindow toolWindow2 = manager.getToolWindow("SetsTool");
                    Content content2 = contentFactory.createContent(new JPanel(), "", false);
                    toolWindow2.getContentManager().removeAllContents(true);
                    toolWindow2.getContentManager().addContent(content2);
                }
            }

            @Override
            public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                super.fileClosed(source, file);
            }


            @Override
            public void selectionChanged(@NotNull FileEditorManagerEvent event) {
                super.selectionChanged(event);
            }
        });
    }
    private TreeModel getRoot(){
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Model");
        try {
            Model model = MainClass.getBml().getttModel();
            List<Set> list = model.getListSet();
            for (Set s: list) {
                addSet(s,root);
            }

        }catch (Exception e){
            System.out.println("Maa");
        }
        return new TreeModel(root);
    }
    private void addSet(Set s, DefaultMutableTreeNode defaultMutableTreeNode){
        DefaultMutableTreeNode cur = new DefaultMutableTreeNode(s,true);
        defaultMutableTreeNode.add(cur);
        List<Set> list = s.getListSet();
        if (list.size()!=0){
            for (Set set: list) {
                addSet(set,cur);
            }
        }
    }
}
