package com.blockset.edit;

import com.intellij.html.impl.RelaxedHtmlFromSchemaElementDescriptor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.html.dtd.HtmlAttributeDescriptorImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlNSDescriptor;
import com.intellij.xml.impl.dtd.BaseXmlElementDescriptorImpl;
import com.intellij.xml.util.HtmlUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class BmlElementDescriptorImpl extends BaseXmlElementDescriptorImpl {
    private final XmlElementDescriptor myDelegate;
    private final boolean myRelaxed;
    private final boolean myCaseSensitive;

    public BmlElementDescriptorImpl(XmlElementDescriptor _delegate, boolean relaxed, boolean caseSensitive) {
        myDelegate = _delegate;
        myRelaxed = relaxed;
        myCaseSensitive = caseSensitive;
    }

    @Override
    public String getQualifiedName() {
        return myDelegate.getQualifiedName();
    }

    @Override
    public String getDefaultName() {
        return myDelegate.getDefaultName();
    }

    // Read-only calculation
    @Override
    protected final XmlElementDescriptor[] doCollectXmlDescriptors(final XmlTag context) {
        XmlElementDescriptor[] elementsDescriptors = myDelegate.getElementsDescriptors(context);
        XmlElementDescriptor[] temp = new XmlElementDescriptor[elementsDescriptors.length];

        for (int i = 0; i < elementsDescriptors.length; i++) {
            temp[i] = new BmlElementDescriptorImpl( elementsDescriptors[i], myRelaxed, myCaseSensitive );
        }
        return temp;
    }

    @Override
    public XmlElementDescriptor getElementDescriptor(XmlTag element, XmlTag contextTag) {
        String name = element.getName();
        if (!myCaseSensitive) name = StringUtil.toLowerCase(name);

        XmlElementDescriptor xmlElementDescriptor = getElementDescriptor(name, element);
        if (xmlElementDescriptor == null && "bml".equals(getName())) {
            XmlTag head = null;
            XmlTag body = null;

            for (XmlTag child : PsiTreeUtil.getChildrenOfTypeAsList(contextTag, XmlTag.class)) {
                if ("model".equals(child.getName())) head = child;
                if ("location".equals(child.getName())) body = child;
            }
            if (head == null) {
                if (body == null || element.getTextOffset() < body.getTextOffset()) {
                    XmlElementDescriptor headDescriptor = getElementDescriptor("head", contextTag);
                    if (headDescriptor != null) {
                        xmlElementDescriptor = headDescriptor.getElementDescriptor(element, contextTag);
                    }
                }
            }
            if (xmlElementDescriptor == null && body == null) {
                XmlElementDescriptor bodyDescriptor = getElementDescriptor("body", contextTag);
                if (bodyDescriptor != null) {
                    xmlElementDescriptor = bodyDescriptor.getElementDescriptor(element, contextTag);
                }
            }

        }
        if (xmlElementDescriptor == null && myRelaxed) {
            xmlElementDescriptor = RelaxedHtmlFromSchemaElementDescriptor.getRelaxedDescriptor(this, element);
        }

        return xmlElementDescriptor;
    }

    // Read-only calculation
    @Override
    protected HashMap<String, XmlElementDescriptor> collectElementDescriptorsMap(final XmlTag element) {
        final HashMap<String, XmlElementDescriptor> hashMap = new HashMap<>();
        final XmlElementDescriptor[] elementDescriptors = myDelegate.getElementsDescriptors(element);

        for (XmlElementDescriptor elementDescriptor : elementDescriptors) {
            hashMap.put(elementDescriptor.getName(), new BmlElementDescriptorImpl(elementDescriptor, myRelaxed, myCaseSensitive));
        }
        return hashMap;
    }

    // Read-only calculation
    @Override
    protected XmlAttributeDescriptor[] collectAttributeDescriptors(final XmlTag context) {
        final XmlAttributeDescriptor[] attributesDescriptors = myDelegate.getAttributesDescriptors(context);
        XmlAttributeDescriptor[] temp = new XmlAttributeDescriptor[attributesDescriptors.length];

        for (int i = 0; i < attributesDescriptors.length; i++) {
            temp[i] = new HtmlAttributeDescriptorImpl(attributesDescriptors[i], myCaseSensitive);
        }
        return temp;
    }

    @Override
    public XmlAttributeDescriptor getAttributeDescriptor(String attributeName, final XmlTag context) {
        String caseSensitiveAttributeName =  !myCaseSensitive ? StringUtil.toLowerCase(attributeName) : attributeName;
        XmlAttributeDescriptor descriptor = super.getAttributeDescriptor(caseSensitiveAttributeName, context);
        if (descriptor == null) descriptor = RelaxedHtmlFromSchemaElementDescriptor.getAttributeDescriptorFromFacelets(attributeName, context);

        if (descriptor == null && HtmlUtil.isHtml5Context(context)) {
            descriptor = myDelegate.getAttributeDescriptor(attributeName, context);
        }
        return descriptor;
    }

    // Read-only calculation
    @Override
    protected HashMap<String, XmlAttributeDescriptor> collectAttributeDescriptorsMap(final XmlTag context) {
        final HashMap<String, XmlAttributeDescriptor> hashMap = new HashMap<>();
        XmlAttributeDescriptor[] elementAttributeDescriptors = myDelegate.getAttributesDescriptors(context);

        for (final XmlAttributeDescriptor attributeDescriptor : elementAttributeDescriptors) {
            hashMap.put(
                    attributeDescriptor.getName(),
                    new HtmlAttributeDescriptorImpl(attributeDescriptor, myCaseSensitive)
            );
        }
        return hashMap;
    }

    @Override
    public XmlNSDescriptor getNSDescriptor() {
        return myDelegate.getNSDescriptor();
    }

    @Override
    public int getContentType() {
        return myDelegate.getContentType();
    }

    @Override
    public PsiElement getDeclaration() {
        return myDelegate.getDeclaration();
    }

    @Override
    public String getName(PsiElement context) {
        return myDelegate.getName(context);
    }

    @Override
    public String getName() {
        return myDelegate.getName();
    }

    @Override
    public void init(PsiElement element) {
        myDelegate.init(element);
    }

    @NotNull
    @Override
    public Object[] getDependencies() {
        return myDelegate.getDependencies();
    }

    @Override
    public XmlAttributeDescriptor[] getAttributesDescriptors(final XmlTag context) {
        return RelaxedHtmlFromSchemaElementDescriptor.addAttrDescriptorsForFacelets(context, getDefaultAttributeDescriptors(context));
    }

    public XmlAttributeDescriptor[] getDefaultAttributeDescriptors(XmlTag context) {
        return super.getAttributesDescriptors(context);
    }

    public boolean allowElementsFromNamespace(final String namespace, final XmlTag context) {
        return true;
    }

    @NotNull
    XmlElementDescriptor getDelegate() {
        return myDelegate;
    }

    @Override
    public String toString() {
        return myDelegate.toString();
    }

    public boolean isCaseSensitive() {
        return myCaseSensitive;
    }

}
