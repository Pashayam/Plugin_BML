package com.blockset.edit.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.impl.source.tree.FileElement;
import com.intellij.psi.tree.ChildRoleBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlChildRole;
import com.intellij.psi.xml.XmlElementType;
import org.jetbrains.annotations.NotNull;

import static com.blockset.edit.psi.BmlElementType.BML_FILE;

public class BmlFileElement extends FileElement implements XmlElementType {
    private static final Logger LOG = Logger.getInstance("#com.diploma.bml.psi.BmlFileElement");

    public BmlFileElement(CharSequence text) {
        super(BML_FILE, text);
    }

    public BmlFileElement(IElementType type, CharSequence text) {
        super(type, text);
    }

    @Override
    public int getChildRole(@NotNull ASTNode child) {
        LOG.assertTrue(child.getTreeParent() == this);
        if (child.getElementType() == XML_DOCUMENT ||
                child.getElementType() == HTML_DOCUMENT) {
            return XmlChildRole.XML_DOCUMENT;
        }
        else {
            return ChildRoleBase.NONE;
        }
    }

}
