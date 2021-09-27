package com.blockset.edit.manipulator;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlChildRole;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class BmlAttributeManipulator extends AbstractElementManipulator<XmlAttribute> {
    private static final Logger LOG = Logger.getInstance(BmlAttributeManipulator.class);

    @Override
    public XmlAttribute handleContentChange(@NotNull XmlAttribute attribute, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        String attr = attribute.getText();
        ASTNode astNode = XmlChildRole.ATTRIBUTE_NAME_FINDER.findChild(attribute.getNode());
        assert astNode != null;
        PsiElement namePsi = astNode.getPsi();
        int startOffsetInParent = namePsi.getStartOffsetInParent();
        TextRange nameRange = new TextRange(startOffsetInParent, startOffsetInParent + namePsi.getTextLength());

        if (nameRange.contains(range)) {
            String content = attr.substring(0, range.getStartOffset()) + newContent + attr.substring(range.getEndOffset(), nameRange.getEndOffset());

            attribute.setName(content);
        } else {
            final XmlAttributeValue value = attribute.getValueElement();

            if (value == null) {
                assert range.getStartOffset() == 0 && range.getEndOffset() == 0;
                attribute.setValue(newContent);
                return attribute;
            }
            final StringBuilder replacement = new StringBuilder(value.getText());
            int offset = value.getTextRange().getStartOffset() - attribute.getTextRange().getStartOffset();

            replacement.replace(
                    range.getStartOffset() - offset,
                    range.getEndOffset() - offset,
                    newContent
            );
            attribute.setValue(replacement.toString());
        }
        return attribute;
    }

    @Override
    @NotNull
    public TextRange getRangeInElement(@NotNull final XmlAttribute attribute) {
        final XmlAttributeValue value = attribute.getValueElement();
        if (value == null)
            return TextRange.from(0, 0);
        TextRange range = attribute.getValueTextRange();
        return range.shiftRight(value.getStartOffsetInParent());
    }
}
