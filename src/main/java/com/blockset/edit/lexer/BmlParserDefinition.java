package com.blockset.edit.lexer;

import com.blockset.edit.psi.BmlElementType;
import com.intellij.lang.PsiParser;
import com.intellij.lang.xml.XMLParserDefinition;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import org.jetbrains.annotations.NotNull;

public class BmlParserDefinition extends XMLParserDefinition {

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new BmlLexer();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return BmlElementType.BML_FILE;
    }

    @NotNull
    @Override
    public PsiParser createParser(Project project) {
        return new BmlParser();
    }

    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new BMLFileImpl(viewProvider, BmlElementType.BML_FILE);
    }

}
