package org.scada_lts.web.security;

import java.util.Arrays;
import java.util.List;

public class XSSUtils {

    private static final List<String> FORBIDDEN_XSS_PATTERNS = Arrays.asList(
            "<", ">", "\"", "'", "(", ")", "javascript:", "onerror", "onload", "onmouseover"
    );

    public static boolean validate(String value) {
        for (String pattern : FORBIDDEN_XSS_PATTERNS) {
            if (value != null && value.toLowerCase().contains(pattern)) {
                return false;
            }
        }
        return true;
    }
}
