package com.blockset.edit.handler;

import com.blockset.edit.lang.BMLLanguage;
import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.lang.Language;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.util.HtmlUtil;
import com.intellij.xml.util.XmlUtil;
import org.jetbrains.annotations.NotNull;

public class BmlAutoPopupHandler extends TypedHandlerDelegate {

    @NotNull
    @Override
    public Result checkAutoPopup(final char charTyped, @NotNull final Project project, @NotNull final Editor editor, @NotNull final PsiFile file) {
        final boolean isXmlLikeFile = BmlGtTypedHandler.fileContainsBmlLanguage(file);
        boolean spaceInTag = isXmlLikeFile && charTyped == ' ';

        if (spaceInTag) {
            spaceInTag = false;
            final PsiElement at = file.findElementAt(editor.getCaretModel().getOffset());

            if (at != null) {
                final PsiElement parent = at.getParent();
                if (parent instanceof XmlTag) {
                    spaceInTag = true;
                }
            }
        }

        if ((charTyped == '<' || charTyped == '/' || spaceInTag) && isXmlLikeFile) {
            autoPopupXmlLookup(project, editor);
            return Result.STOP;
        }
        return Result.CONTINUE;
    }

    public static void autoPopupXmlLookup(final Project project, final Editor editor){
        AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, file -> {
            int offset = editor.getCaretModel().getOffset();

            PsiElement lastElement = InjectedLanguageUtil.findElementAtNoCommit(file, offset - 1);
            if (lastElement instanceof PsiFile) { //the very end of an injected file
                lastElement = file.findElementAt(offset - 1);
            }
            if (lastElement == null || !lastElement.isValid()) return false;

            if (doCompleteIfNeeded(offset, file, lastElement)) {
                return true;
            }

            FileViewProvider fileViewProvider = file.getViewProvider();
            Language templateDataLanguage;

            final PsiElement parent = lastElement.getParent();
            if (fileViewProvider instanceof TemplateLanguageFileViewProvider &&
                    (templateDataLanguage = ((TemplateLanguageFileViewProvider)fileViewProvider).getTemplateDataLanguage()) != parent.getLanguage()) {
                lastElement = fileViewProvider.findElementAt(offset - 1, templateDataLanguage);
                if (lastElement == null || !lastElement.isValid()) return false;
                return doCompleteIfNeeded(offset, file, lastElement);
            }
            return false;
        });
    }

    private static boolean doCompleteIfNeeded(int offset, PsiFile file, PsiElement lastElement) {
        final Ref<Boolean> isRelevantLanguage = new Ref<>();
        final Ref<Boolean> isAnt = new Ref<>();
        String text = lastElement.getText();
        final int len = offset - lastElement.getTextRange().getStartOffset();
        if (len < text.length()) {
            text = text.substring(0, len);
        }
        if (text.equals("<") && isLanguageRelevant(lastElement, file, isRelevantLanguage, isAnt) ||
                text.equals(" ") && isLanguageRelevant(lastElement, file, isRelevantLanguage, isAnt) ||
                text.endsWith("</") && isLanguageRelevant(lastElement, file, isRelevantLanguage, isAnt)) {
            return true;
        }
        return false;
    }

    private static boolean isLanguageRelevant(final PsiElement element,
                                              final PsiFile file,
                                              final Ref<Boolean> isRelevantLanguage,
                                              final Ref<Boolean> isAnt) {
        Boolean isAntFile = isAnt.get();
        if (isAntFile == null) {
            isAntFile = XmlUtil.isAntFile(file);
            isAnt.set(isAntFile);
        }
        Boolean result = isRelevantLanguage.get();
        if (result == null) {
            Language language = element.getLanguage();
            PsiElement parent = element.getParent();
            if (element instanceof PsiWhiteSpace && parent != null) {
                language = parent.getLanguage();
            }
            result = language instanceof BMLLanguage || HtmlUtil.supportsXmlTypedHandlers(file) || isAntFile;
            isRelevantLanguage.set(result);
        }
        return result;
    }

}
