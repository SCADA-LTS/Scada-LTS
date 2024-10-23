package org.scada_lts.web.security;

import org.springframework.web.util.HtmlUtils;

public final class XssProtectHtmlEscapeUtils {

    private XssProtectHtmlEscapeUtils() {}

    public static String escape(String value) {
        if(value == null)
            return "";
        String content = HtmlUtils.htmlEscape(value);
        return whiteSpaceHtmlCode(newLineHtmlCode(content));
    }

    private static String newLineHtmlCode(String content) {
        if(content.contains("\n")) {
            return content.replace("\n", "&#10;").replace("\r", "");
        }
        return content.replace("\r", "&#13;");
    }

    private static String whiteSpaceHtmlCode(String content) {
        return content.replace("\t", "&emsp;")
                .replace("\u000B","&#11;")
                .replace("\f", "&#12;")
                .replace("\u001C", "&#28;")
                .replace("\u001D", "&#29;")
                .replace("\u001E", "&#30;")
                .replace("\u001F", "&#31;");
    }
}
