package com.blockset.edit;

import com.blockset.edit.icons.BmlIcons;
import com.blockset.edit.lang.BMLLanguage;
import com.intellij.ide.highlighter.DomSupportEnabled;
import com.intellij.ide.highlighter.XmlLikeFileType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class BmlFileType extends XmlLikeFileType implements DomSupportEnabled {
    public static final BmlFileType INSTANCE = new BmlFileType();

    @NonNls
    public static final String DEFAULT_EXTENSION = "bml";

    public BmlFileType() {
        super(BMLLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "BML";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "BML file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return BmlIcons.FILE;
    }

}
