package com.blockset.ui.parser.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class Block {

    @XmlAttribute
    private String name;
    @XmlAttribute
    private String type;
    @XmlAttribute
    private String onCreate;
    @XmlAttribute
    private String min;
    @XmlAttribute
    private String max;
    @XmlAttribute
    private String replacesrc;
    @XmlAttribute
    private String replacedst;
    @XmlAttribute
    private String method;
    @XmlAttribute
    private String multiply;

    public Block() {  }

    public Block(String name, String type, String onCreate, String min, String max, String replacesrc, String replacedst, String method, String multiply) {
        this.name = name;
        this.type = type;
        this.onCreate = onCreate;
        this.min = min;
        this.max = max;
        this.replacesrc = replacesrc;
        this.replacedst = replacedst;
        this.method = method;
        this.multiply = multiply;
    }

    public void setttName(String name) {
        this.name = name;
    }

    public void setttType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
