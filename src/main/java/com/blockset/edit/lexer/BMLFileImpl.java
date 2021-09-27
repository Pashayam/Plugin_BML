package com.blockset.edit.lexer;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeRegistry;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.impl.source.html.ScriptSupportUtil;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BMLFileImpl extends PsiFileImpl implements XmlFile {

    public BMLFileImpl(FileViewProvider viewProvider, IElementType elementType) {
        super(elementType, elementType, viewProvider);
    }

    @Nullable
    @Override
    public XmlDocument getDocument() {
        PsiElement child = getFirstChild();
        while (child != null) {
            if (child instanceof XmlDocument)
                return (XmlDocument) child;
            child = child.getNextSibling();
        }
        return null;
    }

    @Nullable
    @Override
    public XmlTag getRootTag() {
        XmlDocument document = getDocument();
        return document == null ? null : document.getRootTag();
    }

    @Override
    public boolean processElements(PsiElementProcessor processor, PsiElement place) {
        final XmlDocument document = getDocument();
        return document == null || document.processElements(processor, place);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof XmlElementVisitor) {
            ((XmlElementVisitor)visitor).visitXmlFile(this);
        }
        else {
            visitor.visitFile(this);
        }
    }

    @Override
    public String toString() {
        return "BmlFile:" + getName();
    }

    private FileType myType = null;
    @Override
    @NotNull
    public FileType getFileType() {
        if (myType == null) {
            myType = getLanguage().getAssociatedFileType();
            if (myType == null) {
                VirtualFile virtualFile = getOriginalFile().getVirtualFile();
                myType = virtualFile == null ? FileTypeRegistry.getInstance().getFileTypeByFileName(getName()) : virtualFile.getFileType();
            }
        }
        return myType;
    }

    @Override
    public void clearCaches() {
        super.clearCaches();
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        return super.processDeclarations(processor, state, lastParent, place) &&
                (ScriptSupportUtil.processDeclarations(this, processor, state, lastParent, place));

    }

    @NotNull
    @Override
    public GlobalSearchScope getFileResolveScope() {
        return ProjectScope.getAllScope(getProject());
    }

    @Override
    public boolean ignoreReferencedElementAccessibility() {
        return true;
    }

}
