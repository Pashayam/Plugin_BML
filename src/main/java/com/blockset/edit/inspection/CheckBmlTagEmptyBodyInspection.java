package com.blockset.edit.inspection;

import com.blockset.edit.BmlBundle;
import com.blockset.edit.lang.BMLLanguage;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.XmlSuppressableInspectionTool;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlChildRole;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.IncorrectOperationException;
import com.intellij.xml.XmlExtension;
import com.intellij.xml.util.CheckEmptyTagInspection;
import com.intellij.xml.util.CollapseTagIntention;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class CheckBmlTagEmptyBodyInspection extends XmlSuppressableInspectionTool {
    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Override
    @NotNull
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new XmlElementVisitor() {
            @Override public void visitXmlTag(final XmlTag tag) {
                if (!CheckEmptyTagInspection.isTagWithEmptyEndNotAllowed(tag)) {
                    final ASTNode child = XmlChildRole.START_TAG_END_FINDER.findChild(tag.getNode());

                    if (child != null) {
                        final ASTNode node = child.getTreeNext();

                        if (node != null &&
                                node.getElementType() == XmlTokenType.XML_END_TAG_START) {
                            holder.registerProblem(
                                    tag,
                                    BmlBundle.message("bml.inspections.tag.empty.body"),
                                    isCollapsibleTag(tag) ? new CheckBmlTagEmptyBodyInspection.Fix(tag) : null
                            );
                        }
                    }
                }
            }
        };
    }

    static boolean isCollapsibleTag(final XmlTag tag) {
        final String name = StringUtil.toLowerCase(tag.getName());
        return tag.getLanguage() == BMLLanguage.INSTANCE ||
                "block".equals(name) || XmlExtension.isCollapsible(tag);
    }

    @Override
    @NotNull
    public String getGroupDisplayName() {
        return BmlBundle.message("bml.inspections.group.name");
    }

    @Override
    @NotNull
    public String getDisplayName() {
        return BmlBundle.message("bml.inspections.check.tag.empty.body");
    }

    @Override
    @NotNull
    @NonNls
    public String getShortName() {
        return "CheckBmlTagEmptyBody";
    }

    public static class Fix extends CollapseTagIntention {
        private final SmartPsiElementPointer<XmlTag> myPointer;

        public Fix(XmlTag tag) {
            myPointer = SmartPointerManager.getInstance(tag.getProject()).createSmartPsiElementPointer(tag);
        }

        @Override
        public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
            XmlTag tag = myPointer.getElement();
            if (tag == null) {
                return;
            }
            applyFix(project, tag);
        }

        @Override
        public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
            return true;
        }
    }

}
