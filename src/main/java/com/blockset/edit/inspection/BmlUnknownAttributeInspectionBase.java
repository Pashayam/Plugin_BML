package com.blockset.edit.inspection;

import com.blockset.edit.BmlBundle;
import com.blockset.edit.BmlUtil;
import com.intellij.codeInsight.daemon.XmlErrorMessages;
import com.intellij.codeInsight.daemon.impl.analysis.RemoveAttributeIntentionFix;
import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.htmlInspections.HtmlUnknownElementInspection;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.text.EditDistance;
import com.intellij.xml.XmlAttributeDescriptor;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.impl.schema.AnyXmlElementDescriptor;
import com.intellij.xml.util.XmlUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BmlUnknownAttributeInspectionBase extends HtmlUnknownElementInspection {
    private static final Key<HtmlUnknownElementInspection> ATTRIBUTE_KEY = Key.create("BmlUnknownAttribute");
    private static final Logger LOG = Logger.getInstance(BmlUnknownTagInspectionBase.class);

    public BmlUnknownAttributeInspectionBase() {
        this("");
    }

    public BmlUnknownAttributeInspectionBase(String defaultValues) {
        super(defaultValues);
    }

    @Override
    @Nls
    @NotNull
    public String getDisplayName() {
        return BmlBundle.message("bml.inspections.unknown.attribute");
    }

    @Override
    @NonNls
    @NotNull
    public String getShortName() {
        return "BmlUnknownAttribute";
    }

    @Override
    protected String getCheckboxTitle() {
        return BmlBundle.message("bml.inspections.unknown.tag.attribute.checkbox.title");
    }

    @NotNull
    @Override
    protected String getPanelTitle() {
        return BmlBundle.message("bml.inspections.unknown.tag.attribute.title");
    }

    @Override
    @NotNull
    protected Logger getLogger() {
        return LOG;
    }

    @Override
    protected void checkAttribute(@NotNull final XmlAttribute attribute, @NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        final XmlTag tag = attribute.getParent();

        if (tag != null) {
            XmlElementDescriptor elementDescriptor = tag.getDescriptor();
            if (elementDescriptor == null || elementDescriptor instanceof AnyXmlElementDescriptor) {
                return;
            }

            XmlAttributeDescriptor attributeDescriptor = elementDescriptor.getAttributeDescriptor(attribute);

            final String name = attribute.getName();

            if (BmlUtil.isNotBmlAttribute(name) || attributeDescriptor == null && !attribute.isNamespaceDeclaration()) {
                if (!XmlUtil.attributeFromTemplateFramework(name, tag) && (!isCustomValuesEnabled() || !isCustomValue(name))) {
                    ArrayList<LocalQuickFix> quickfixes = new ArrayList<>(6);

                    quickfixes.add(new RemoveAttributeIntentionFix(name));

                    addSimilarAttributesQuickFixes(tag, name, quickfixes);

                    registerProblemOnAttributeName(attribute, XmlErrorMessages.message("attribute.is.not.allowed.here", attribute.getName()), holder,
                            quickfixes.toArray(LocalQuickFix.EMPTY_ARRAY));
                }
            }
        }
    }

    private static void addSimilarAttributesQuickFixes(XmlTag tag, String name, ArrayList<? super LocalQuickFix> quickfixes) {
        XmlElementDescriptor descriptor = tag.getDescriptor();
        if (descriptor == null) return;
        XmlAttributeDescriptor[] descriptors = descriptor.getAttributesDescriptors(tag);
        int initialSize = quickfixes.size();
        for (XmlAttributeDescriptor attr : descriptors) {
            if (EditDistance.optimalAlignment(name, attr.getName(), false) <= 1) {
                quickfixes.add(new BmlUnknownAttributeInspectionBase.RenameAttributeFix(attr));
            }
            if (quickfixes.size() >= initialSize + 3) break;
        }
    }

    private static class RenameAttributeFix implements LocalQuickFix, HighPriorityAction {
        private final String name;

        RenameAttributeFix(XmlAttributeDescriptor attr) {
            name = attr.getName();
        }

        @Nls
        @NotNull
        @Override
        public String getFamilyName() {
            return "Rename attribute";
        }

        @Nls
        @NotNull
        @Override
        public String getName() {
            return "Rename attribute to " + name;
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            XmlAttribute attribute = PsiTreeUtil.getParentOfType(descriptor.getPsiElement(), XmlAttribute.class);
            if (attribute == null) return;
            attribute.setName(name);
        }
    }

}
