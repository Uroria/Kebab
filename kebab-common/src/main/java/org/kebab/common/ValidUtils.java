package org.kebab.common;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@UtilityClass
public class ValidUtils {

    @NotNull
    public <T> T notNull(@Nullable T object, @NonNull String message) {
        if (object == null) throw new NullPointerException(message);
        return object;
    }

    @NotNull
    public <T> T notNull(@NonNull Optional<T> optionalObject, @NonNull String message) {
        return optionalObject.orElseThrow(() -> new NullPointerException(message));
    }

    public void checkBoolean(boolean condition, @NonNull String message) {
        if (condition) return;
        throw new IllegalStateException(message);
    }
}
