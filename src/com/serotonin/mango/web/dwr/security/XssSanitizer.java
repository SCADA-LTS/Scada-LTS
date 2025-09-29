package com.serotonin.mango.web.dwr.security;

/**
 * Tiny, fast HTML escaper for XSS prevention.
 * - Escapes only the 5 dangerous chars: & < > " '
 * - Does NOT escape '/' to avoid breaking URLs.
 * - Linear, allocation-conscious, early-out when no specials.
 */
public final class XssSanitizer {

    private XssSanitizer() {}

    /** Returns escaped text or null if input is null. */
    public static String escape(String in) {
        if (in == null) return null;

        // Fast path: scan and bail if nothing to escape
        int len = in.length();
        boolean needs = false;
        for (int i = 0; i < len; i++) {
            char c = in.charAt(i);
            if (c == '&' || c == '<' || c == '>' || c == '"' || c == '\'') {
                needs = true;
                break;
            }
        }
        if (!needs) return in; // zero-copy, avoids allocations

        // Slow path: escape
        StringBuilder sb = new StringBuilder(len + 16);
        for (int i = 0; i < len; i++) {
            char c = in.charAt(i);
            switch (c) {
                case '&': sb.append("&amp;");  break;
                case '<': sb.append("&lt;");   break;
                case '>': sb.append("&gt;");   break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&#39;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
}
