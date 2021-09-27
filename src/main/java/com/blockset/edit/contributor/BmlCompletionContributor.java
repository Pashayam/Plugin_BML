package com.blockset.edit.contributor;

import com.blockset.edit.lang.BMLLanguage;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.*;
import com.intellij.util.ArrayUtilRt;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class BmlCompletionContributor extends CompletionContributor implements DumbAware {

    public static final String[] RELATION = {"parent", "child", "multi"};
    public static final String[] TYPE = {"string", "sort", "image", "number", "permissions", "datetime", "ip"};
    public static final String[] ATTRIBUTES = {"name", "relation", "type", "min", "max"};


    public BmlCompletionContributor() {
        extend(CompletionType.BASIC, psiElement().withElementType(XmlElementType.XML_ATTRIBUTE).inside(XmlPatterns.xmlTag().withLocalName("block", "set")),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                        final PsiElement position = parameters.getPosition();
                        if (!hasBmlAttributesCompletion(position)) {
                            return;
                        }
                        final XmlAttribute attributeValue = PsiTreeUtil.getParentOfType(position, XmlAttribute.class, false);
                        if (attributeValue != null) {
                            for (String element : ATTRIBUTES) {
                                result.addElement(LookupElementBuilder.create(element));
                            }
                        }
                    }
                });
        extend(CompletionType.BASIC, psiElement().withElementType(XmlElementType.XML_ATTRIBUTE_VALUE_TOKEN),
                new CompletionProvider<>() {
                    @Override
                    protected void addCompletions(@NotNull CompletionParameters parameters,
                                                  @NotNull ProcessingContext context,
                                                  @NotNull CompletionResultSet result) {
                        final PsiElement position = parameters.getPosition();
                        if (!hasBmlAttributesCompletion(position)) {
                            return;
                        }
                        final XmlAttributeValue attributeValue = PsiTreeUtil.getParentOfType(position, XmlAttributeValue.class, false);
                        if (attributeValue != null && attributeValue.getParent() instanceof XmlAttribute) {
                            for (String element : addSpecificCompletions((XmlAttribute) attributeValue.getParent())) {
                                result.addElement(LookupElementBuilder.create(element));
                            }
                        }
                    }
                });
    }

    public static boolean hasBmlAttributesCompletion(PsiElement position) {
        if (PsiTreeUtil.getParentOfType(position, XmlTag.class, false) != null) {
            return true;
        }
        XmlTag xmlTag = PsiTreeUtil.getParentOfType(position, XmlTag.class, false);
        return xmlTag != null && xmlTag.getLanguage() == BMLLanguage.INSTANCE;
    }

    @NotNull
    @NonNls
    public static String[] addSpecificCompletions(final XmlAttribute attribute) {
        @NonNls String name = attribute.getName();
        final XmlTag tag = attribute.getParent();
        if (tag == null) return ArrayUtilRt.EMPTY_STRING_ARRAY;

        name = StringUtil.toLowerCase(name);
        if ("relation".equals(name)) {
            return RELATION;
        }
        else if ("type".equals(name)) {
            return TYPE;
        }
        return ArrayUtilRt.EMPTY_STRING_ARRAY;
    }

}
