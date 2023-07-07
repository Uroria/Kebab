package org.kebab.api.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class ValidUtils {

    public void notNull(@Nullable Object object, String message) {
        if (object == null) throw new NullPointerException(message);
    }

    public void checkBoolean(boolean value, String message) {
        if (!value) throw new IllegalStateException(message);
    }
}
