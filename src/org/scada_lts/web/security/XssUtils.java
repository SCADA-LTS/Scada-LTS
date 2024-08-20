package org.scada_lts.web.security;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.Arrays;

public class XssUtils {

    private XssUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static final Pattern SAFE_STRING_PATTERN = Pattern.compile("^[.\\p{Alnum}\\p{Space}]{0,1024}$");

    private static final List<String> FORBIDDEN_XSS_PATTERNS = Arrays.asList(
            "javascript:", "onerror=", "onload=", "onmouseover="
    );

    public static boolean validate(String value) {
        if (value == null) {
            return true;
        }

        Matcher matcher = SAFE_STRING_PATTERN.matcher(value);
        if (!matcher.matches()) {
            return false;
        }

        for (String pattern : FORBIDDEN_XSS_PATTERNS) {
            if (value.toLowerCase().contains(pattern)) {
                return false;
            }
        }

        return true;
    }
}
