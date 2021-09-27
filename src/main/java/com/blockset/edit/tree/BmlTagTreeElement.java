package com.blockset.edit.tree;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.xml.AbstractXmlTagTreeElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class BmlTagTreeElement extends AbstractXmlTagTreeElement<XmlTag> {
    @NonNls private static final String ID_ATTR_NAME = "id";
    @NonNls private static final String NAME_ATTR_NAME = "name";

    protected BmlTagTreeElement(XmlTag tag) {
        super(tag);
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        return getStructureViewTreeElements(getElement().getSubTags());
    }

    @Nullable
    @Override
    public String getPresentableText() {
        final XmlTag element = getElement();
        if (element == null) {
            return IdeBundle.message("node.structureview.invalid");
        }
        String id = element.getAttributeValue(ID_ATTR_NAME);
        if (id == null) {
            id = element.getAttributeValue(NAME_ATTR_NAME);
            if (id == null) {

            }
        }
        id = toCanonicalForm(id);
        return id != null ? id + ':' + element.getLocalName() : element.getName();
    }


    @Override
    public String getLocationString() {
        final StringBuilder buffer = new StringBuilder();
        final XmlTag element = getElement();
        assert element != null;
        String id = element.getAttributeValue(ID_ATTR_NAME);
        String usedAttrName = null;
        if (id == null) {
            id = element.getAttributeValue(NAME_ATTR_NAME);
            if (id != null) {
                usedAttrName = NAME_ATTR_NAME;
            }
        }
        else {
            usedAttrName = ID_ATTR_NAME;
        }

        id = toCanonicalForm(id);

        for (XmlAttribute attribute : element.getAttributes()) {
            if (buffer.length() != 0) {
                buffer.append(' ');
            }

            final String name = attribute.getName();
            if (usedAttrName != null && id != null && usedAttrName.equals(name)) {
                continue; // we output this name in name
            }

            buffer.append(name);
            buffer.append('=').append('"').append(attribute.getValue()).append('"');
        }
        return buffer.toString();
    }

    @Nullable
    public static String toCanonicalForm(@Nullable String id) {
        if (id != null) {
            id = id.trim();
            if (id.isEmpty()) {
                return null;
            }
        }
        return id;
    }

}
