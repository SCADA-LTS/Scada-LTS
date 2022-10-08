package org.scada_lts.utils;

import com.serotonin.mango.Common;
import com.serotonin.web.dwr.DwrResponseI18n;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public final class DataSourcePointApiUtils {

    private DataSourcePointApiUtils() {}

    public static Map<String, String> toMapMessages(DwrResponseI18n responseI18n) {
        if(responseI18n.getHasMessages()) {
            return responseI18n.getMessages().stream()
                    .collect(Collectors.toMap(a -> a.getContextKey() == null ? "message" : a.getContextKey(),
                            a -> Common.getMessage(a.getContextualMessage().getKey()), (a, b) -> b));
        }
        return Collections.emptyMap();
    }
}
