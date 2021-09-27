package com.blockset.edit.index;

import com.blockset.edit.lexer.BmlLexer;
import com.intellij.lexer.Lexer;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.impl.cache.impl.id.LexerBasedIdIndexer;
import com.intellij.psi.impl.cache.impl.idCache.XmlFilterLexer;
import org.jetbrains.annotations.NotNull;

public class BmlIdIndexer extends LexerBasedIdIndexer {
    @NotNull
    @Override
    public Lexer createLexer(@NotNull final OccurrenceConsumer consumer) {
        return createIndexingLexer(consumer);
    }

    static XmlFilterLexer createIndexingLexer(OccurrenceConsumer consumer) {
        return new XmlFilterLexer(new BmlLexer(), consumer);
    }
}
