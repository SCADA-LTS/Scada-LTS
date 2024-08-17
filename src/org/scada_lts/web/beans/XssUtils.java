package org.scada_lts.web.beans;

import org.springframework.web.util.HtmlUtils;

public final class XssUtils {

    private XssUtils() {}

    public static String escape(String value) {
        String content = HtmlUtils.htmlEscape(value);
        return newLineHtmlCode(content);
    }

    private static String newLineHtmlCode(String content) {
        return content.replace("\n","&#10;").replace("\r","");
    }
}
