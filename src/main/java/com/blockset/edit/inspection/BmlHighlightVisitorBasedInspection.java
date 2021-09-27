package com.blockset.edit.inspection;

import com.blockset.edit.highlighter.BmlHighlightVisitor;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder;
import com.intellij.codeInspection.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.XmlRecursiveElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BmlHighlightVisitorBasedInspection extends GlobalSimpleInspectionTool {
    @NotNull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.ERROR;
    }

    @Override
    public void checkFile(@NotNull final PsiFile file,
                          @NotNull final InspectionManager manager,
                          @NotNull ProblemsHolder problemsHolder,
                          @NotNull final GlobalInspectionContext globalContext,
                          @NotNull final ProblemDescriptionsProcessor problemDescriptionsProcessor) {
        HighlightInfoHolder myHolder = new HighlightInfoHolder(file) {
            @Override
            public boolean add(@Nullable HighlightInfo info) {
                if (info != null) {
                    GlobalInspectionUtil.createProblem(
                            file,
                            info,
                            new TextRange(info.startOffset, info.endOffset),
                            null,
                            manager,
                            problemDescriptionsProcessor,
                            globalContext
                    );
                }
                return true;
            }
        };
        final BmlHighlightVisitor highlightVisitor = new BmlHighlightVisitor();
        highlightVisitor.analyze(file, true, myHolder, new Runnable() {
            @Override
            public void run() {
                file.accept(new XmlRecursiveElementVisitor() {
                    @Override
                    public void visitElement(PsiElement element) {
                        highlightVisitor.visit(element);
                        super.visitElement(element);
                    }
                });
            }
        });

    }

    @Nls
    @NotNull
    @Override
    public String getGroupDisplayName() {
        return InspectionsBundle.message("inspection.general.tools.group.name");
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "BML highlighting";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "BmlHighlighting";
    }
}
