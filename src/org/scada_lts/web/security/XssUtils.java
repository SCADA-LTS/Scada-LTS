package org.scada_lts.web.security;

import java.util.regex.Pattern;
import java.util.List;
import java.util.Arrays;

final public class XssUtils {

    private static final Pattern KEY_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\-]{1,32}$");

    private static final Pattern VALUE_PATTERN = Pattern.compile("^[\\p{L}\\p{N}.\\-/+=_ !$*?@%]*$");

    private static final List<String> FORBIDDEN_XSS_PATTERNS = Arrays.asList(
            "javascript:", "onerror=", "onload=", "onmouseover="
    );

    public static boolean validate(String query) {
        if (query == null || query.isEmpty()) {
            return true;
        }

        int questionMarkIndex = query.indexOf("?");
        if (questionMarkIndex >= 0) {
            query = query.substring(questionMarkIndex + 1);
        }

        String[] parameters = query.split("&");
        for (String parameter : parameters) {
            if (parameter.isEmpty()) {
                continue;
            }

            if (!KEY_PATTERN.matcher(parameter).matches()) {
                return false;
            }

            if (!VALUE_PATTERN.matcher(parameter).matches()) {
                return false;
            }

            String lowerCaseValue = parameter.toLowerCase();
            for (String forbiddenPattern : FORBIDDEN_XSS_PATTERNS) {
                if (lowerCaseValue.contains(forbiddenPattern)) {
                    return false;
                }
            }
        }

        return true;
    }
}
