package com.blockset.edit.manipulator;

import com.intellij.lang.ASTFactory;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.impl.source.DummyHolderFactory;
import com.intellij.psi.impl.source.tree.FileElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class BmlTokenManipulator extends AbstractElementManipulator<XmlToken> {

    @Override
    public XmlToken handleContentChange(@NotNull XmlToken xmlToken, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        String oldText = xmlToken.getText();
        String newText = oldText.substring(0, range.getStartOffset()) + newContent + oldText.substring(range.getEndOffset());
        IElementType tokenType = xmlToken.getTokenType();
        FileElement holder = DummyHolderFactory.createHolder(xmlToken.getManager(), null).getTreeElement();
        LeafElement leaf = ASTFactory.leaf(tokenType, holder.getCharTable().intern(newText));
        holder.rawAddChildren(leaf);
        return (XmlToken)xmlToken.replace(leaf.getPsi());
    }

}
