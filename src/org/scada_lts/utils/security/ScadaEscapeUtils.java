package org.scada_lts.utils.security;

import org.apache.commons.lang3.StringEscapeUtils;

public final class ScadaEscapeUtils {

    private ScadaEscapeUtils(){}

    public static String escapeXml(String value) {
        return StringEscapeUtils.escapeXml(value);
    }
}
