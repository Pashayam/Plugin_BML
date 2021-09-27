package com.blockset.ui.parser.domain;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlType
public class Model {

    @XmlElement(name = "set")
    private List<Set> listSet = new ArrayList<>();

    public Model() {  }

    public Set getSet(int i) {
        return listSet.get(i);
    }

    public List<Set> getListSet() {
        return listSet;
    }

    public Model addSet(String name, String relation,
                                       String clone, String action, String limit,
                                       String selection, String count,
                                       String author, String select,
                                       String create, String update,
                                       String delete, String missName) {
        Set set = new Set(name,relation,clone,action,limit,selection,count,author,select,create,update,delete,missName);
        listSet.add(set);
        return this;
    }
    public Model addSet(Set s) {

        listSet.add(s);
        return this;
    }

}

