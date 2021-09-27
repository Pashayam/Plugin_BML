package com.blockset.edit.handler;

import com.intellij.application.options.editor.WebEditorOptions;
import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.codeInsight.editorActions.XmlEditUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BmlEqTypedHandler extends TypedHandlerDelegate {
    private boolean needToInsertQuotes = false;

    @NotNull
    @Override
    public Result beforeCharTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file, @NotNull FileType fileType) {
        if (c == '=' && WebEditorOptions.getInstance().isInsertQuotesForAttributeValue()) {
            if (BmlGtTypedHandler.fileContainsBmlLanguage(file)) {
                PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());

                PsiElement atParent = getAttributeCandidate(editor, file, false);
                if (atParent instanceof XmlAttribute && ((XmlAttribute)atParent).getValueElement() == null) {
                    needToInsertQuotes = ((XmlAttribute)atParent).getValueElement() == null;
                }
            }
        }

        return super.beforeCharTyped(c, project, editor, file, fileType);
    }

    @Nullable
    private static PsiElement getAttributeCandidate(@NotNull Editor editor, @NotNull PsiFile file, boolean typed) {
        int newOffset = editor.getCaretModel().getOffset() - (typed ? 2 : 1);
        if (newOffset < 0) return null;

        PsiElement at = file.findElementAt(newOffset);
        return at != null ? at.getParent() : null;
    }

    @NotNull
    @Override
    public Result charTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        if (needToInsertQuotes) {
            int offset = editor.getCaretModel().getOffset();
            PsiElement fileContext = file.getContext();
            String toInsert = tryCompleteQuotes(fileContext);
            boolean showPopup = true;
            if (toInsert == null) {
                final String quote = getDefaultQuote(file);
                XmlExtension.AttributeValuePresentation presentation = getValuePresentation(editor, file, quote);
                toInsert = presentation.getPrefix() + presentation.getPostfix();
                showPopup = presentation.showAutoPopup();
            }
            editor.getDocument().insertString(offset, toInsert);
            editor.getCaretModel().moveToOffset(offset + toInsert.length() / 2);
            if (showPopup) {
                AutoPopupController.getInstance(project).scheduleAutoPopup(editor);
            }
            needToInsertQuotes = false;
        }

        return super.charTyped(c, project, editor, file);
    }

    @Nullable
    private static String tryCompleteQuotes(@Nullable PsiElement fileContext) {
        if (fileContext != null) {
            if (fileContext.getText().startsWith("\"")) return "''";
            if (fileContext.getText().startsWith("'")) return "\"\"";
        }
        return null;
    }

    @NotNull
    private static String getDefaultQuote(@NotNull PsiFile file) {
        return XmlEditUtil.getAttributeQuote(file);
    }

    @NotNull
    private static XmlExtension.AttributeValuePresentation getValuePresentation(@NotNull Editor editor, @NotNull PsiFile file, @NotNull String quote) {
        PsiElement atParent = getAttributeCandidate(editor, file, true);
        if (atParent instanceof XmlAttribute) {
            XmlTag parent = ((XmlAttribute)atParent).getParent();
            return XmlExtension.getExtension(file).getAttributeValuePresentation(parent, ((XmlAttribute) atParent).getName(), quote);
        }
        return XmlExtension.getExtension(file).getAttributeValuePresentation(null, "", quote);
    }

}
