package com.blockset.edit;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class BmlErrorMessages extends AbstractBundle {

    private static final BmlErrorMessages ourInstance = new BmlErrorMessages();
    @NonNls
    private static final String BUNDLE = "messages.BmlErrorMessages";

    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, @NotNull Object... params) {
        return ourInstance.getMessage(key, params);
    }

    private BmlErrorMessages() {
        super(BUNDLE);
    }

}
