package org.scada_lts.utils;

import java.util.Set;
import java.util.stream.Collectors;

public final class EmailToSmsUtils {

    private EmailToSmsUtils() {}

    public static Set<String> addedAtDomain(Set<String> addresses, String domain) {
        return addresses.stream()
                .map(a -> a.contains("@") ? a : a + "@" + domain)
                .collect(Collectors.toSet());
    }
}
