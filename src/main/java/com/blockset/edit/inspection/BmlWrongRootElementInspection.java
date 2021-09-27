package com.blockset.edit.inspection;

import com.blockset.edit.BmlBundle;
import com.blockset.edit.highlighter.BmlHighlightVisitor;
import com.intellij.codeInsight.daemon.XmlErrorMessages;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.htmlInspections.XmlWrongRootElementInspection;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.*;
import com.intellij.xml.util.XmlUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class BmlWrongRootElementInspection extends XmlWrongRootElementInspection {
    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return BmlBundle.message("bml.inspections.group.name");
    }

    @Override
    @Nls
    @NotNull
    public String getDisplayName() {
        return BmlBundle.message("bml.inspection.wrong.root.element");
    }

    @Override
    @NonNls
    @NotNull
    public String getShortName() {
        return "BmlWrongRootElement";
    }

    @Override
    protected void checkTag(@NotNull XmlTag tag, @NotNull ProblemsHolder holder, boolean isOnTheFly) {
        if (!(tag.getParent() instanceof XmlTag)) {
            final PsiFile psiFile = tag.getContainingFile();
            if (!(psiFile instanceof XmlFile)) {
                return;
            }

            XmlFile xmlFile = (XmlFile) psiFile;

            final XmlDocument document = xmlFile.getDocument();
            if (document == null) {
                return;
            }

            XmlProlog prolog = document.getProlog();
            if (prolog == null || BmlHighlightVisitor.skipValidation(prolog)) {
                return;
            }

            final XmlDoctype doctype = prolog.getDoctype();

            if (doctype == null) {
                return;
            }

            XmlElement nameElement = doctype.getNameElement();

            if (nameElement == null) {
                return;
            }

            String name = tag.getName();
            String text = nameElement.getText();
            if (tag instanceof HtmlTag) {
                name = StringUtil.toLowerCase(name);
                text = StringUtil.toLowerCase(text);
            }

            if (!name.equals(text)) {
                name = XmlUtil.findLocalNameByQualifiedName(name);

                if (!name.equals(text)) {
                    if (tag instanceof HtmlTag) {
                        return; // it is legal to have html / head / body omitted
                    }
                    final LocalQuickFix localQuickFix = new MyLocalQuickFix(doctype.getNameElement().getText());

                    holder.registerProblem(XmlChildRole.START_TAG_NAME_FINDER.findChild(tag.getNode()).getPsi(),
                            XmlErrorMessages.message("wrong.root.element"),
                            ProblemHighlightType.LIKE_UNKNOWN_SYMBOL, localQuickFix
                    );

                    final ASTNode astNode = XmlChildRole.CLOSING_TAG_NAME_FINDER.findChild(tag.getNode());
                    if (astNode != null) {
                        holder.registerProblem(astNode.getPsi(),
                                XmlErrorMessages.message("wrong.root.element"),
                                ProblemHighlightType.LIKE_UNKNOWN_SYMBOL, localQuickFix
                        );
                    }
                }
            }
        }
    }

    private static class MyLocalQuickFix implements LocalQuickFix {
        private final String myText;

        MyLocalQuickFix(String text) {
            myText = text;
        }

        @Override
        @NotNull
        public String getFamilyName() {
            return BmlBundle.message("change.bml.root.element.to", myText);
        }

        @Override
        public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
            final XmlTag myTag = PsiTreeUtil.getParentOfType(descriptor.getPsiElement(), XmlTag.class);
            myTag.setName(myText);
        }
    }

}
