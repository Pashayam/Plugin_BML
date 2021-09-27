package com.blockset.edit.inspection;

import com.blockset.edit.BmlBundle;
import com.blockset.edit.BmlElementDescriptorImpl;
import com.blockset.edit.BmlErrorMessages;
import com.blockset.edit.BmlUtil;
import com.blockset.edit.highlighter.BmlHighlightVisitor;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.htmlInspections.HtmlUnknownElementInspection;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.util.XmlTagUtil;
import com.intellij.xml.util.XmlUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BmlUnknownTagInspectionBase extends HtmlUnknownElementInspection {
    public static final Key<HtmlUnknownElementInspection> TAG_KEY = Key.create("BmlUnknownTag");
    private static final Logger logger = Logger.getInstance(BmlUnknownTagInspectionBase.class);

    public BmlUnknownTagInspectionBase(@NotNull String defaultValues) {
        super(defaultValues);
    }

    public BmlUnknownTagInspectionBase() {
        this("");
    }

    private static boolean isAbstractDescriptor(XmlElementDescriptor descriptor) {
        return descriptor == null || descriptor instanceof BmlElementDescriptorImpl;
    }

    @Override
    @Nls
    @NotNull
    public String getDisplayName() {
        return BmlBundle.message("bml.inspections.unknown.tag");
    }

    @Override
    @NonNls
    @NotNull
    public String getShortName() {
        return "BmlUnknownTag";
    }

    @Override
    @NotNull
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected String getCheckboxTitle() {
        return BmlBundle.message("bml.inspections.unknown.tag.checkbox.title");
    }

    @Override
    @NotNull
    protected String getPanelTitle() {
        return BmlBundle.message("bml.inspections.unknown.tag.title");
    }

    @Override
    protected void checkTag(@NotNull final XmlTag tag, @NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        if (!BmlHighlightVisitor.shouldBeValidated(tag)) {
            return;
        }

        XmlElementDescriptor descriptorFromContext = XmlUtil.getDescriptorFromContext(tag);

        PsiElement parent = tag.getParent();
        XmlElementDescriptor parentDescriptor = parent instanceof XmlTag ? ((XmlTag)parent).getDescriptor() : null;

        XmlElementDescriptor ownDescriptor = isAbstractDescriptor(descriptorFromContext)
                ? tag.getDescriptor()
                : descriptorFromContext;
        final String name = tag.getName();

        if (!BmlUtil.isBmlBlockTag(name) && !BmlUtil.isPossiblyInlineTag(name) || isAbstractDescriptor(ownDescriptor) || parentDescriptor != null && isAbstractDescriptor(descriptorFromContext)) {

            if (!isCustomValuesEnabled() || !isCustomValue(name)) {
                final String message = BmlErrorMessages.message("unknown.bml.tag", name);

                final PsiElement startTagName = XmlTagUtil.getStartTagNameElement(tag);
                assert startTagName != null;
                final PsiElement endTagName = XmlTagUtil.getEndTagNameElement(tag);

                ProblemHighlightType highlightType = tag.getContainingFile().getContext() == null ?
                        ProblemHighlightType.ERROR :
                        ProblemHighlightType.INFORMATION;
                if (startTagName.getTextLength() > 0) {
                    holder.registerProblem(startTagName, message, highlightType, LocalQuickFix.EMPTY_ARRAY);
                }

                if (endTagName != null) {
                    holder.registerProblem(endTagName, message, highlightType, LocalQuickFix.EMPTY_ARRAY);
                }
            }
        }
    }

    @Nullable
    protected LocalQuickFix createChangeTemplateDataFix(PsiFile file) {
        return null;
    }

}
