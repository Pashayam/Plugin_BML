package com.blockset.ui.parser.domain;

import org.apache.commons.io.FilenameUtils;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlType
public class Location {

    @XmlElement(name = "set")
    private List<Set> listSet = new ArrayList<>();
    @XmlAttribute
    private String base;
    @XmlAttribute
    private String template;

    public Location() {  }

    public String getBase() {
        return base;
    }

    public void setBBase(String base) {
        this.base = base;
    }

    public List<Set> getListSet() {
        return listSet;
    }

    public Location(String base) {
        this.base = base;
    }

    public Location(String base, String template) {
        this.base = base;
        this.template = template;
    }

    public Set getSet(int i) {
        return listSet.get(i);
    }

    public Location addSet(String name, String relation,
                                          String clone, String action, String limit,
                                          String selection, String count,
                                          String author, String select,
                                          String create, String update,
                                          String delete, String missName) {
        Set set = new Set(name,relation,clone,action,limit,selection,count,author,select,create,update,delete,missName);
        listSet.add(set);
        return this;
    }
    public void addSet(Set set){
        listSet.add(set);
    }

    @Override
    public String toString() {
        return FilenameUtils.getBaseName(base);
    }
}