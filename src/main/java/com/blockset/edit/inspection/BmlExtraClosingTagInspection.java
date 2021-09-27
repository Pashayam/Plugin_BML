package com.blockset.edit.inspection;

import com.blockset.edit.BmlBundle;
import com.blockset.edit.BmlUtil;
import com.blockset.edit.lang.BMLLanguage;
import com.intellij.codeInsight.daemon.XmlErrorMessages;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.htmlInspections.HtmlLocalInspectionTool;
import com.intellij.codeInspection.htmlInspections.RemoveExtraClosingTagIntentionAction;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.util.XmlTagUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class BmlExtraClosingTagInspection extends HtmlLocalInspectionTool {

    @Override
    @Nls
    @NotNull
    public String getDisplayName() {
        return BmlBundle.message("bml.inspection.extra.closing.tag");
    }

    @Override
    @NonNls
    @NotNull
    public String getShortName() {
        return "BmlExtraClosingTag";
    }

    @Override
    protected void checkTag(@NotNull final XmlTag tag, @NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        final TextRange range = XmlTagUtil.getEndTagRange(tag);

        if (range != null && BmlUtil.isPossiblyInlineTag(tag.getName()) && tag.getLanguage().isKindOf(BMLLanguage.INSTANCE)) {
            holder.registerProblem(tag, XmlErrorMessages.message("extra.closing.tag.for.empty.element"),
                    ProblemHighlightType.LIKE_UNUSED_SYMBOL, range.shiftRight(-tag.getTextRange().getStartOffset()), new RemoveExtraClosingTagIntentionAction());
        }
    }

}
