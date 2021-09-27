package com.blockset.edit.highlighter;

import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import org.jetbrains.annotations.NotNull;

public class BmlSyntaxHighLighterFactory extends SingleLazyInstanceSyntaxHighlighterFactory {

    @NotNull
    @Override
    protected SyntaxHighlighter createHighlighter() {
        return new BmlFileHighlighter();
    }

}
