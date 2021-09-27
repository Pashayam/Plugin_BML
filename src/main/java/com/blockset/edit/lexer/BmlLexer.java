package com.blockset.edit.lexer;

import com.intellij.lexer.*;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.xml.XmlTokenType;


public class BmlLexer extends MergingLexerAdapter {
    private final static TokenSet TOKENS_TO_MERGE = TokenSet.create(XmlTokenType.XML_DATA_CHARACTERS,
            XmlTokenType.XML_TAG_CHARACTERS,
            XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN,
            XmlTokenType.XML_PI_TARGET,
            XmlTokenType.XML_COMMENT_CHARACTERS);

    public BmlLexer() {
        this(false);
    }

    public BmlLexer(final boolean conditionalCommentsSupport) {
        this(new _XmlLexer(new __XmlLexer(null), conditionalCommentsSupport));
    }

    public BmlLexer(Lexer baseLexer) {
        super(baseLexer, TOKENS_TO_MERGE);
    }

}
