package com.blockset.edit.psi;

import com.blockset.edit.lang.BMLLanguage;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.xml.XmlElementType;

public interface BmlElementType extends XmlElementType {
    IFileElementType BML_FILE = new IFileElementType(BMLLanguage.INSTANCE);

}
