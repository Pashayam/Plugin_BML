package com.blockset.edit;

import com.blockset.edit.lang.BMLLanguage;
import com.intellij.codeInspection.InspectionProfile;
import com.intellij.codeInspection.htmlInspections.XmlEntitiesInspection;
import com.intellij.javaee.ExternalResourceManagerEx;
import com.intellij.lang.Language;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.openapi.util.registry.RegistryValue;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.profile.codeInspection.InspectionProjectProfileManager;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.XmlRecursiveElementWalkingVisitor;
import com.intellij.psi.html.HtmlTag;
import com.intellij.psi.impl.source.html.HtmlDocumentImpl;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.templateLanguages.OuterLanguageElement;
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.*;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xml.*;
import com.intellij.xml.impl.schema.XmlAttributeDescriptorImpl;
import com.intellij.xml.impl.schema.XmlElementDescriptorImpl;
import com.intellij.xml.util.HTMLControls;
import com.intellij.xml.util.HtmlPsiUtil;
import com.intellij.xml.util.XmlUtil;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.jetbrains.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BmlUtil {
    private static final Logger logger = LoggerFactory.getLogger(BmlUtil.class);

    public static final String ATTRIBUTE_NAME = "name";

    private BmlUtil() {  }

    private static final Set<String> EMPTY_TAGS_MAP = new THashSet<>();

    @NonNls private static final String[] BLOCK_TAGS = {"bml", "location", "model", "set"};

    @NonNls private static final String[] POSSIBLY_INLINE_TAGS = {"block"};
    private static final Set<String> POSSIBLY_INLINE_TAGS_MAP = new THashSet<>();

    private static final String[] ALL_ATTRIBUTES = {
            "name", "type", "default", "condition", "onread", "oncreate", "onupdate", "relation", "trash", "min", "max", "scope"
    };
    private static final Set<String> ALL_ATTRIBUTES_MAP = new THashSet<>();

    private static final Set<String> BLOCK_TAGS_MAP = new THashSet<>();

    private static final Map<String, Set<String>> AUTO_CLOSE_BY_MAP = new THashMap<>();

    static {
        for (HTMLControls.Control control : HTMLControls.getControls()) {
            final String tagName = StringUtil.toLowerCase(control.name);
            if (control.endTag == HTMLControls.TagState.FORBIDDEN) EMPTY_TAGS_MAP.add(tagName);
            AUTO_CLOSE_BY_MAP.put(tagName, new THashSet<>(control.autoClosedBy));
        }
        ContainerUtil.addAll(BLOCK_TAGS_MAP, BLOCK_TAGS);
        ContainerUtil.addAll(POSSIBLY_INLINE_TAGS_MAP, POSSIBLY_INLINE_TAGS);
        ContainerUtil.addAll(ALL_ATTRIBUTES_MAP, ALL_ATTRIBUTES);
    }

    public static boolean isSingleBmlTag(@NotNull XmlTag tag, boolean lowerCase) {
        final XmlExtension extension = XmlExtension.getExtensionByElement(tag);
        final String name = tag.getName();
        boolean result = EMPTY_TAGS_MAP.contains(!lowerCase || tag.isCaseSensitive()
                ? name : StringUtil.toLowerCase(name));
        return result && (extension == null || !extension.isSingleTagException(tag));
    }

    public static boolean isSingleBmlTag(String tagName) {
        return EMPTY_TAGS_MAP.contains(StringUtil.toLowerCase(tagName));
    }

    public static boolean isSingleHtmlTagL(String tagName) {
        return EMPTY_TAGS_MAP.contains(tagName);
    }

    public static boolean canTerminate(final String childTagName, final String tagName) {
        final Set<String> closingTags = AUTO_CLOSE_BY_MAP.get(tagName);
        return closingTags != null && closingTags.contains(childTagName);
    }

    public static boolean isBmlBlockTag(String tagName) {
        return BLOCK_TAGS_MAP.contains(StringUtil.toLowerCase(tagName));
    }

    public static boolean isPossiblyInlineTag(String tagName) {
        return POSSIBLY_INLINE_TAGS_MAP.contains(tagName);
    }

    @Nullable
    public static XmlDocument getRealXmlDocument(@Nullable XmlDocument doc) {
        return HtmlPsiUtil.getRealXmlDocument(doc);
    }

    public static boolean isShortNotationOfBooleanAttributePreferred() {
        return Registry.is("html.prefer.short.notation.of.boolean.attributes", true);
    }

    @TestOnly
    public static void setShortNotationOfBooleanAttributeIsPreferred(boolean value, Disposable parent) {
        final boolean oldValue = isShortNotationOfBooleanAttributePreferred();
        final RegistryValue registryValue = Registry.get("html.prefer.short.notation.of.boolean.attributes");
        registryValue.setValue(value);
        Disposer.register(parent, () -> registryValue.setValue(oldValue));
    }

    public static boolean isBooleanAttribute(@NotNull XmlAttributeDescriptor descriptor, @Nullable PsiElement context) {
        if (descriptor.isEnumerated()) {
            final String[] values = descriptor.getEnumeratedValues();
            if (values == null) {
                return false;
            }
            if (values.length == 2) {
                return values[0].isEmpty() && values[1].equals(descriptor.getName())
                        || values[1].isEmpty() && values[0].equals(descriptor.getName());
            }
            else if (values.length == 1) {
                return descriptor.getName().equals(values[0]);
            }
        }
        return context != null && isCustomBooleanAttribute(descriptor.getName(), context);
    }

    public static boolean isCustomBooleanAttribute(@NotNull String attributeName, @NotNull PsiElement context) {
        final String entitiesString = getEntitiesString(context, XmlEntitiesInspection.BOOLEAN_ATTRIBUTE_SHORT_NAME);
        if (entitiesString != null) {
            StringTokenizer tokenizer = new StringTokenizer(entitiesString, ",");
            while (tokenizer.hasMoreElements()) {
                if (tokenizer.nextToken().equalsIgnoreCase(attributeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static XmlAttributeDescriptor[] getCustomAttributeDescriptors(XmlElement context) {
        String entitiesString = getEntitiesString(context, XmlEntitiesInspection.ATTRIBUTE_SHORT_NAME);
        if (entitiesString == null) return XmlAttributeDescriptor.EMPTY;

        StringTokenizer tokenizer = new StringTokenizer(entitiesString, ",");
        XmlAttributeDescriptor[] descriptors = new XmlAttributeDescriptor[tokenizer.countTokens()];
        int index = 0;

        while (tokenizer.hasMoreElements()) {
            final String customName = tokenizer.nextToken();
            if (customName.isEmpty()) continue;

            descriptors[index++] = new XmlAttributeDescriptorImpl() {
                @Override
                public String getName(PsiElement context) {
                    return customName;
                }

                @Override
                public String getName() {
                    return customName;
                }
            };
        }

        return descriptors;
    }

    public static XmlElementDescriptor[] getCustomTagDescriptors(@Nullable PsiElement context) {
        String entitiesString = getEntitiesString(context, XmlEntitiesInspection.TAG_SHORT_NAME);
        if (entitiesString == null) return XmlElementDescriptor.EMPTY_ARRAY;

        StringTokenizer tokenizer = new StringTokenizer(entitiesString, ",");
        XmlElementDescriptor[] descriptors = new XmlElementDescriptor[tokenizer.countTokens()];
        int index = 0;

        while (tokenizer.hasMoreElements()) {
            final String tagName = tokenizer.nextToken();
            if (tagName.isEmpty()) continue;

            descriptors[index++] = new CustomBmlTagDescriptor(tagName);
        }

        return descriptors;
    }

    @Nullable
    public static String getEntitiesString(@Nullable PsiElement context, @NotNull String inspectionName) {
        if (context == null) return null;
        PsiFile containingFile = context.getContainingFile().getOriginalFile();

        final InspectionProfile profile = InspectionProjectProfileManager.getInstance(context.getProject()).getCurrentProfile();
        XmlEntitiesInspection inspection = (XmlEntitiesInspection)profile.getUnwrappedTool(inspectionName, containingFile);
        if (inspection != null) {
            return inspection.getAdditionalEntries();
        }
        return null;
    }

    public static boolean isHtmlTag(@NotNull XmlTag tag) {
        if (!tag.getLanguage().isKindOf(BMLLanguage.INSTANCE)) return false;

        XmlDocument doc = PsiTreeUtil.getParentOfType(tag, XmlDocument.class);

        String doctype = null;
        if (doc != null) {
            doctype = XmlUtil.getDtdUri(doc);
        }
        doctype = doctype == null ? ExternalResourceManagerEx.getInstanceEx().getDefaultHtmlDoctype(tag.getProject()) : doctype;
        return XmlUtil.XHTML4_SCHEMA_LOCATION.equals(doctype) ||
                !StringUtil.containsIgnoreCase(doctype, "xhtml");
    }

    public static boolean isNotBmlAttribute(@NotNull String name) {
        return !ALL_ATTRIBUTES_MAP.contains(name);
    }

    @Nullable
    public static String getHrefBase(XmlFile file) {
        final XmlTag root = file.getRootTag();
        final XmlTag head = root != null ? root.findFirstSubTag("head") : null;
        final XmlTag base = head != null ? head.findFirstSubTag("base") : null;
        return base != null ? base.getAttributeValue("href") : null;
    }

    public static boolean isOwnHtmlAttribute(XmlAttributeDescriptor descriptor) {
        // common html attributes are defined mostly in common.rnc, core-scripting.rnc, etc
        // while own tag attributes are defined in meta.rnc
        final PsiElement declaration = descriptor.getDeclaration();
        final PsiFile file = declaration != null ? declaration.getContainingFile() : null;
        final String name = file != null ? file.getName() : null;
        return "meta.rnc".equals(name);
    }

    public static boolean tagHasHtml5Schema(@NotNull XmlTag context) {
        XmlElementDescriptor descriptor = context.getDescriptor();
        if (descriptor != null) {
            XmlNSDescriptor nsDescriptor = descriptor.getNSDescriptor();
            XmlFile descriptorFile = nsDescriptor != null ? nsDescriptor.getDescriptorFile() : null;
            String descriptorPath = descriptorFile != null ? descriptorFile.getVirtualFile().getPath() : null;
            return Comparing.equal(Html5SchemaProvider.getHtml5SchemaLocation(), descriptorPath) ||
                    Comparing.equal(Html5SchemaProvider.getXhtml5SchemaLocation(), descriptorPath);
        }
        return false;
    }

    private static class TerminateException extends RuntimeException {
        private static final BmlUtil.TerminateException INSTANCE = new BmlUtil.TerminateException();
    }

    public static boolean isTagWithoutAttributes(@NonNls String tagName) {
        return "bml".equalsIgnoreCase(tagName);
    }

    public static boolean hasHtml(@NotNull PsiFile file) {
        return isBmlFile(file) || file.getViewProvider() instanceof TemplateLanguageFileViewProvider;
    }

    public static boolean supportsXmlTypedHandlers(@NotNull PsiFile file) {
        Language language = file.getLanguage();
        while (language != null) {
            language = language.getBaseLanguage();
        }
        return false;
    }

    public static boolean isBmlFile(@NotNull PsiElement element) {
        Language language = element.getLanguage();
        return language.isKindOf(BMLLanguage.INSTANCE) || language == BMLLanguage.INSTANCE;
    }

    public static boolean isBmlFile(@NotNull VirtualFile file) {
        FileType fileType = file.getFileType();
        return fileType == BmlFileType.INSTANCE;
    }

    public static boolean isHtmlTagContainingFile(PsiElement element) {
        if (element == null) {
            return false;
        }
        final PsiFile containingFile = element.getContainingFile();
        if (containingFile != null) {
            final XmlTag tag = PsiTreeUtil.getParentOfType(element, XmlTag.class, false);
            if (tag instanceof HtmlTag) {
                return true;
            }
            final XmlDocument document = PsiTreeUtil.getParentOfType(element, XmlDocument.class, false);
            if (document instanceof HtmlDocumentImpl) {
                return true;
            }
            final FileViewProvider provider = containingFile.getViewProvider();
            Language language;
            if (provider instanceof TemplateLanguageFileViewProvider) {
                language = ((TemplateLanguageFileViewProvider)provider).getTemplateDataLanguage();
            }
            else {
                language = provider.getBaseLanguage();
            }

            return language == BMLLanguage.INSTANCE;
        }
        return false;
    }

    public static class CustomBmlTagDescriptor extends XmlElementDescriptorImpl {
        private final String myTagName;

        CustomBmlTagDescriptor(String tagName) {
            super(null);
            myTagName = tagName;
        }

        @Override
        public String getName(PsiElement context) {
            return myTagName;
        }

        @Override
        public String getDefaultName() {
            return myTagName;
        }

        @Override
        public boolean allowElementsFromNamespace(final String namespace, final XmlTag context) {
            return true;
        }
    }

    @NotNull
    public static Iterable<String> splitClassNames(@Nullable String classAttributeValue) {
        // comma is useduse as separator because class name cannot contain comma but it can be part of JSF classes attributes
        return classAttributeValue != null ? StringUtil.tokenize(classAttributeValue, " \t,") : Collections.emptyList();
    }

    @Contract("!null -> !null")
    public static String getTagPresentation(@Nullable XmlTag tag) {
        if (tag == null) return null;
        StringBuilder builder = new StringBuilder(tag.getLocalName());
        String idValue = getAttributeValue(tag);
        if (idValue != null) {
            builder.append('#').append(idValue);
        }
        return builder.toString();
    }

    @Nullable
    private static String getAttributeValue(@NotNull XmlTag tag) {
        XmlAttribute classAttribute = getAttributeByName(tag);
        if (classAttribute != null && !containsOuterLanguageElements(classAttribute)) {
            String value = classAttribute.getValue();
            if (!StringUtil.isEmptyOrSpaces(value)) return value;
        }
        return null;
    }

    @Nullable
    private static XmlAttribute getAttributeByName(@NotNull XmlTag tag) {
        PsiElement child = tag.getFirstChild();
        while (child != null) {
            if (child instanceof XmlAttribute) {
                PsiElement nameElement = child.getFirstChild();
                if (nameElement != null &&
                        nameElement.getNode().getElementType() == XmlTokenType.XML_NAME &&
                        BmlUtil.ATTRIBUTE_NAME.equalsIgnoreCase(nameElement.getText())) {
                    return (XmlAttribute)child;
                }
            }
            child = child.getNextSibling();
        }
        return null;
    }

    private static boolean containsOuterLanguageElements(@NotNull PsiElement element) {
        PsiElement child = element.getFirstChild();
        while (child != null) {
            if (child instanceof CompositeElement) {
                return containsOuterLanguageElements(child);
            }
            if (child instanceof OuterLanguageElement) {
                return true;
            }
            child = child.getNextSibling();
        }
        return false;
    }

    public static List<XmlAttributeValue> getIncludedPathsElements(@NotNull final XmlFile file) {
        final List<XmlAttributeValue> result = new ArrayList<>();
        file.acceptChildren(new XmlRecursiveElementWalkingVisitor() {
            @Override
            public void visitXmlTag(XmlTag tag) {
                XmlAttribute attribute = null;
                if ("block".equalsIgnoreCase(tag.getName()) || "set".equalsIgnoreCase(tag.getName())) {
                    attribute = tag.getAttribute("name");
                }
                if (attribute != null) result.add(attribute.getValueElement());
                super.visitXmlTag(tag);
            }

            @Override
            public void visitElement(PsiElement element) {
                if (element.getLanguage() instanceof BMLLanguage) {
                    super.visitElement(element);
                }
            }
        });
        return result.isEmpty() ? Collections.emptyList() : result;
    }

}
