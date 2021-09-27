package com.blockset.edit.fix;

import com.intellij.codeInsight.daemon.impl.analysis.CreateNSDeclarationIntentionFix;
import com.intellij.codeInsight.daemon.impl.analysis.InsertRequiredAttributeFix;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.codeInspection.htmlInspections.AddAttributeValueIntentionFix;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BmlQuickFixFactoryImpl extends BmlQuickFixFactory {

    @NotNull
    @Override
    public LocalQuickFixAndIntentionActionOnPsiElement insertRequiredAttributeFix(@NotNull XmlTag tag, @NotNull String attrName, @NotNull String... values) {
        return new InsertRequiredAttributeFix(tag, attrName, values);
    }

    @NotNull
    @Override
    public LocalQuickFix createNSDeclarationIntentionFix(@NotNull PsiElement element, @NotNull String namespacePrefix, @Nullable XmlToken token) {
        return new CreateNSDeclarationIntentionFix(element, namespacePrefix, token);
    }

    @NotNull
    @Override
    public LocalQuickFixAndIntentionActionOnPsiElement addAttributeValueFix(@NotNull XmlAttribute attribute) {
        return new AddAttributeValueIntentionFix(attribute);
    }

}
