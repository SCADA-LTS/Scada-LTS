package org.scada_lts.web.security;

import java.util.regex.Pattern;
import java.util.List;
import java.util.Arrays;

final public class XssUtils {

    private static final Pattern HTTP_QUERY_STRING = Pattern.compile("^(([a-zA-Z0-9_\\-]{1,32}=[\\p{L}\\p{N}.\\-/+=_ !$*?@%,]*&?)|([a-zA-Z0-9_\\-]{1,32}&?))*$");
    private static final List<String> FORBIDDEN_XSS_PATTERNS = Arrays.asList("javascript:", "onerror", "onload", "onmouseover", "alert(");

    public static boolean validateHttpQuery(String query) {
        if (query == null || query.isEmpty()) {
            return false;
        }

        for(String pattern: FORBIDDEN_XSS_PATTERNS) {
            if(query.contains(pattern)) {
                return false;
            }
        }

        boolean matches = HTTP_QUERY_STRING.matcher(query).matches();
        return matches;
    }
}
