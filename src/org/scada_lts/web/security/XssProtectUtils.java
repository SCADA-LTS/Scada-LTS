package org.scada_lts.web.security;

import org.springframework.web.util.HtmlUtils;

import java.util.HashMap;
import java.util.Map;

public final class XssProtectUtils {

    private static final Map<String,String> newLineAndWhitespaceCodes = new HashMap<>();

    static {
        newLineAndWhitespaceCodes.put("&#10;", "\n");
        newLineAndWhitespaceCodes.put("&#13;", "\r");

        newLineAndWhitespaceCodes.put("&emsp;", "\t");
        newLineAndWhitespaceCodes.put("&#11;", "\u000B");
        newLineAndWhitespaceCodes.put("&#12;", "\f");
        newLineAndWhitespaceCodes.put("&#28;", "\u001C");
        newLineAndWhitespaceCodes.put("&#29;", "\u001D");
        newLineAndWhitespaceCodes.put("&#30;", "\u001E");
        newLineAndWhitespaceCodes.put("&#31;", "\u001F");
    }

    public XssProtectUtils() {}

    public static String escapeHtml(String value) {
        if(value == null)
            return "";
        return escapeNewLineAndWhitespace(HtmlUtils.htmlEscape(value));
    }

    public static String unescapeHtml(String value) {
        if(value == null)
            return "";
        return HtmlUtils.htmlUnescape(unescapeNewLineAndWhitespace(value));
    }

    private static String escapeNewLineAndWhitespace(String content) {
        String result = content;
        for(Map.Entry<String, String> entry: newLineAndWhitespaceCodes.entrySet()) {
            result = result.replace(entry.getValue(), entry.getKey());
        }
        return result;
    }

    private static String unescapeNewLineAndWhitespace(String content) {
        String result = content;
        for(Map.Entry<String, String> entry: newLineAndWhitespaceCodes.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
