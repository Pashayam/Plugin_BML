package com.blockset.edit.lang;

import com.intellij.lang.CompositeLanguage;

public class BMLLanguage extends CompositeLanguage {
    public final static BMLLanguage INSTANCE = new BMLLanguage();

    public BMLLanguage() {
        super("BML");
    }

}
