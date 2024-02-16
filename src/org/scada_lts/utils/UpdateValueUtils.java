package org.scada_lts.utils;

import java.util.function.Consumer;
import java.util.function.Predicate;

public final class UpdateValueUtils {

    private UpdateValueUtils() {}

    public static <T> void setIf(T value, Consumer<T> setter, Predicate<T> setIf) {
        if(setIf.test(value))
            setter.accept(value);
    }
}
