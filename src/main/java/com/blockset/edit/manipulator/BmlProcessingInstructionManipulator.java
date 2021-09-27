package com.blockset.edit.manipulator;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.CheckUtil;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.Factory;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.impl.source.tree.SharedImplUtil;
import com.intellij.psi.xml.XmlProcessingInstruction;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.CharTable;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import static com.intellij.xml.util.documentation.HtmlDescriptorsTable.LOG;

public class BmlProcessingInstructionManipulator extends AbstractElementManipulator<XmlProcessingInstruction> {

    @Override
    public XmlProcessingInstruction handleContentChange(@NotNull XmlProcessingInstruction element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        CheckUtil.checkWritable(element);
        final CompositeElement attrNode = (CompositeElement)element.getNode();
        final ASTNode valueNode = attrNode.findLeafElementAt(range.getStartOffset());
        LOG.assertTrue(valueNode != null, "Leaf not found in " + attrNode + " at offset " + range.getStartOffset() + " in element " + element);
        final PsiElement elementToReplace = valueNode.getPsi();

        String text;
        try {
            text = elementToReplace.getText();
            final int offsetInParent = elementToReplace.getStartOffsetInParent();
            String textBeforeRange = text.substring(0, range.getStartOffset() - offsetInParent);
            String textAfterRange = text.substring(range.getEndOffset() - offsetInParent);
            newContent = element.getText().startsWith("'") || element.getText().endsWith("'") ?
                    newContent.replace("'", "&apos;") : newContent.replace("\"", "&quot;");
            text = textBeforeRange + newContent + textAfterRange;
        } catch(StringIndexOutOfBoundsException e) {
            LOG.error("Range: " + range + " in text: '" + element.getText() + "'", e);
            throw e;
        }
        final CharTable charTableByTree = SharedImplUtil.findCharTableByTree(attrNode);
        final LeafElement newValueElement = Factory.createSingleLeafElement(XmlTokenType.XML_TAG_CHARACTERS, text, charTableByTree, element.getManager());

        attrNode.replaceChildInternal(valueNode, newValueElement);
        return element;
    }

}
