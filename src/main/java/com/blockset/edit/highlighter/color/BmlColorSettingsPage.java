package com.blockset.edit.highlighter.color;

import com.blockset.edit.highlighter.BmlFileHighlighter;
import com.blockset.edit.icons.BmlIcons;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.pages.XMLColorsPage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BmlColorSettingsPage extends XMLColorsPage {

    @NotNull
    @Override
    public String getDisplayName() {
        return "BML";
    }

    @Override
    public Icon getIcon() {
        return BmlIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new BmlFileHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "<bml>\n" +
                "    <model>\n" +
                "        <set name=\"set_name\" trash=\"1\" relation=\"some_relation\">\n" +
                "            <!-- trash - Some comment -->\n" +
                "            <block name=\"block_name\" type=\"some_type\" />\n" +
                "            <set name=\"caticons\" relation=\"child\">\n" +
                "                <block name=\"icon\" type=\"image\" />\n" +
                "            </set>\n" +
                "    </model>\n" +
                "    <location base=\"@all\">\n" +
                "        <set name=\"some_name\" clone=\"some_attribute\">\n" +
                "            <block name=\"_login\" />\n" +
                "        </set>\n" +
                "    </location>\n" +
                "<bml>";
    }

}
