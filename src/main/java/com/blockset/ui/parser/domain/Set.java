package com.blockset.ui.parser.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlType
public class Set {

    @XmlElement(name = "block")
    private List<Block> listBlock = new ArrayList<>();
    @XmlElement(name = "set")
    private List<Set> listSet = new ArrayList<>();

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String relation;

    @XmlAttribute
    private String clone;

    @XmlAttribute
    private String action;

    @XmlAttribute
    private String limit;

    @XmlAttribute
    private String selection;

    @XmlAttribute
    private String count;

    @XmlAttribute
    private String author;

    @XmlAttribute
    private String select;

    @XmlAttribute
    private String create;

    @XmlAttribute
    private String update;

    @XmlAttribute
    private String delete;

    @XmlAttribute
    private String missName;

    public List<Set> getListSet() {
        return listSet;
    }

    public String getName() {
        return name;
    }

    public Set() {  }

    public String getRelation() {
        return relation;
    }

    public String getClone() {
        return clone;
    }

    public String getAction() {
        return action;
    }

    public String getLimit() {
        return limit;
    }

    public String isSelection() {
        return selection;
    }

    public String isCount() {
        return count;
    }

    public String isAuthor() {
        return author;
    }

    public String isSelect() {
        return select;
    }

    public String isCreate() {
        return create;
    }

    public String isUpdate() {
        return update;
    }

    public String isDelete() {
        return delete;
    }

    public String isMissName() {
        return missName;
    }

    public void settName(String name) {
        this.name = name;
    }

    public void settRelation(String relation) {
        this.relation = relation;
    }

    public void settClone(String clone) {
        this.clone = clone;
    }

    public void settAction(String action) {
        this.action = action;
    }

    public void settLimit(String limit) {
        this.limit = limit;
    }

    public void settSelection(String selection) {
        this.selection = selection;
    }

    public void settCount(String count) {
        this.count = count;
    }

    public void settAuthor(String author) {
        this.author = author;
    }

    public void settSelect(String select) {
        this.select = select;
    }

    public void settCreate(String create) {
        this.create = create;
    }

    public void settUpdate(String update) {
        this.update = update;
    }

    public void settDelete(String delete) {
        this.delete = delete;
    }

    public void settMissName(String missName) {
        this.missName = missName;
    }
    public void addBlock(Block b){
        listBlock.add(b);
    }

    public void setListSets(List<Set> listSet) {
        this.listSet = listSet;
    }

    public Set(String name, String relation,
               String clone, String action, String limit,
               String selection, String count,
               String author, String select,
               String create, String update,
               String delete, String missName) {
        this.name = name;
        this.relation = relation;
        this.clone = clone;
        this.action = action;
        this.limit = limit;
        this.selection = selection;
        this.count = count;
        this.author = author;
        this.select = select;
        this.create = create;
        this.update = update;
        this.delete = delete;
        this.missName = missName;
    }

    public List<Block> getBlocks() {
        return listBlock;
    }

    public Set getSet(int i) {
        return listSet.get(i);
    }

    public void setListBlock(List<Block> listBlock) {
        this.listBlock = listBlock;
    }

    public Set addSet(String name, String relation,
                                     String clone, String action, String limit,
                                     String selection, String count,
                                     String author, String select,
                                     String create, String update,
                                     String delete, String missName) {
        Set set = new Set(name,relation,clone,action,limit,selection,count,author,select,create,update,delete,missName);
        listSet.add(set);
        return this;
    }

    public Set addBlock(String name, String type, String onCreate, String min, String max, String replacesrc, String replacedst, String method, String multiply) {
        listBlock.add(new Block(name, type, onCreate, min, max, replacesrc, replacedst, method, multiply));
        return this;
    }

    @Override
    public String toString() {
        return getName();
    }
}
