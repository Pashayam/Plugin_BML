package com.blockset.edit.contributor;

import com.blockset.edit.BmlElementDescriptorImpl;
import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.completion.XmlTagInsertHandler;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RangeMarker;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.xml.TagNameVariantCollector;
import com.intellij.psi.meta.PsiPresentableMetaData;
import com.intellij.psi.search.EverythingGlobalScope;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.psi.xml.XmlTokenType;
import com.intellij.util.Processor;
import com.intellij.util.Processors;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.xml.XmlElementDescriptor;
import com.intellij.xml.XmlExtension;
import com.intellij.xml.XmlNamespaceHelper;
import com.intellij.xml.XmlTagNameProvider;
import com.intellij.xml.index.XmlNamespaceIndex;
import com.intellij.xml.index.XsdNamespaceBuilder;
import com.intellij.xml.util.XmlUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DefaultBmlTagNameProvider implements XmlTagNameProvider {
    private static final Logger logger = LoggerFactory.getLogger(DefaultBmlTagNameProvider.class);

    @Override
    public void addTagNameVariants(List<LookupElement> elements, @NotNull XmlTag tag, String prefix) {
        final List<String> namespaces;
        if (prefix.isEmpty()) {
            namespaces = new ArrayList<>(Arrays.asList(tag.knownNamespaces()));
            namespaces.add(XmlUtil.EMPTY_URI); // empty namespace
        }
        else {
            namespaces = new ArrayList<>(Collections.singletonList(tag.getNamespace()));
        }
        PsiFile psiFile = tag.getContainingFile();
        XmlExtension xmlExtension = XmlExtension.getExtension(psiFile);
        List<String> nsInfo = new ArrayList<>();
        List<XmlElementDescriptor> variants = TagNameVariantCollector.getTagDescriptors(tag, namespaces, nsInfo);

        if (variants.isEmpty() && psiFile instanceof XmlFile && ((XmlFile) psiFile).getRootTag() == tag) {
            getRootTagsVariants(tag, elements);
            return;
        }

        final Set<String> visited = new HashSet<>();
        for (int i = 0; i < variants.size(); i++) {
            XmlElementDescriptor descriptor = variants.get(i);
            String qname = descriptor.getName(tag);
            if (!visited.add(qname)) continue;
            if (!prefix.isEmpty() && qname.startsWith(prefix + ":")) {
                qname = qname.substring(prefix.length() + 1);
            }

            PsiElement declaration = descriptor.getDeclaration();
            if (declaration != null && !declaration.isValid()) {
                logger.error(descriptor + " contains invalid declaration: " + declaration);
            }
            LookupElementBuilder lookupElement = declaration == null ? LookupElementBuilder.create(qname) : LookupElementBuilder.create(declaration, qname);
            final int separator = qname.indexOf(':');
            if (separator > 0) {
                lookupElement = lookupElement.withLookupString(qname.substring(separator + 1));
            }
            String ns = nsInfo.get(i);
            if (StringUtil.isNotEmpty(ns)) {
                lookupElement = lookupElement.withTypeText(ns, true);
            }
            if (descriptor instanceof PsiPresentableMetaData) {
                lookupElement = lookupElement.withIcon(((PsiPresentableMetaData)descriptor).getIcon());
            }
            if (xmlExtension.useXmlTagInsertHandler()) {
                lookupElement = lookupElement.withInsertHandler(XmlTagInsertHandler.INSTANCE);
            }
            lookupElement = lookupElement.withCaseSensitivity(!(descriptor instanceof BmlElementDescriptorImpl));
            elements.add(PrioritizedLookupElement.withPriority(lookupElement, separator > 0 ? 0 : 1));
        }
    }

    private static void getRootTagsVariants(final XmlTag tag, final List<? super LookupElement> elements) {
        elements.add(
                LookupElementBuilder.create("bml>")
                .withPresentableText("<bml>").withInsertHandler(
                (context, item) -> {
                    int offset = context.getEditor().getCaretModel().getOffset();
                    context.getEditor().getCaretModel().moveToOffset(offset - 4);
                    AutoPopupController.getInstance(context.getProject()).scheduleAutoPopup(context.getEditor());
                })
        );
        final FileBasedIndex fbi = FileBasedIndex.getInstance();
        Collection<String> result = new ArrayList<>();
        Processor<String> processor = Processors.cancelableCollectProcessor(result);
        fbi.processAllKeys(XmlNamespaceIndex.NAME, processor, tag.getProject());

        final GlobalSearchScope scope = new EverythingGlobalScope(tag.getProject());
        for (final String ns : result) {
            if (ns.isEmpty()) continue;
            fbi.processValues(XmlNamespaceIndex.NAME, ns, null, new FileBasedIndex.ValueProcessor<XsdNamespaceBuilder>() {
                @Override
                public boolean process(@NotNull final VirtualFile file, XsdNamespaceBuilder value) {
                    List<String> tags = value.getRootTags();
                    for (String s : tags) {
                        elements.add(LookupElementBuilder.create(s).withTypeText(ns).withInsertHandler(new XmlTagInsertHandler() {
                            @Override
                            public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
                                final Editor editor = context.getEditor();
                                final Document document = context.getDocument();
                                final int caretOffset = editor.getCaretModel().getOffset();
                                final RangeMarker caretMarker = document.createRangeMarker(caretOffset, caretOffset);
                                caretMarker.setGreedyToRight(true);

                                XmlFile psiFile = (XmlFile)context.getFile();
                                boolean incomplete = XmlUtil.getTokenOfType(tag, XmlTokenType.XML_TAG_END) == null && XmlUtil.getTokenOfType(tag, XmlTokenType.XML_EMPTY_ELEMENT_END) == null;
                                XmlNamespaceHelper.getHelper(psiFile).insertNamespaceDeclaration(psiFile, editor, Collections.singleton(ns), null, null);
                                editor.getCaretModel().moveToOffset(caretMarker.getEndOffset());

                                XmlTag rootTag = psiFile.getRootTag();
                                if (incomplete) {
                                    XmlToken token = XmlUtil.getTokenOfType(rootTag, XmlTokenType.XML_EMPTY_ELEMENT_END);
                                    if (token != null) token.delete(); // remove tag end added by smart attribute adder :(
                                    PsiDocumentManager.getInstance(context.getProject()).doPostponedOperationsAndUnblockDocument(document);
                                    super.handleInsert(context, item);
                                }
                            }
                        }));
                    }
                    return true;
                }
            }, scope);
        }
    }

}
