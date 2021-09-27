package com.blockset.ui.support;

import com.blockset.ui.SetTable;
import com.blockset.ui.SetsTree;
import com.blockset.ui.parser.BMLReader;
import com.blockset.ui.parser.domain.BML;
import com.intellij.ui.treeStructure.Tree;

import java.io.File;
import java.io.FileNotFoundException;

public class MainClass {
    static private BML bml = new BML();
    static private File file;
    static private SetsTree setsTree = new SetsTree();
    static private SetTable setTable = new SetTable();
    static private Tree modelTree;

    public static Tree getModelTree() {
        return modelTree;
    }

    public static void setModelTree(Tree modelTree1) {
        modelTree = modelTree1;
    }

    public static BML getBml(){
        return bml;
    };
    public static void generateBML(){
//        modelTree = new Tree();
        try {
            bml = BMLReader.read(file.getPath().toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void setFile(File file) {
        MainClass.file = file;
    }

    public static File getFile() {
        return file;
    }

    public static SetsTree getSetsTree() {
        return setsTree;
    }

    public static SetTable getSetTable() {
        return setTable;
    }
}
