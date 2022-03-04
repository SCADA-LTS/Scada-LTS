package org.scada_lts.utils;

import java.util.List;

public class SqlUtils {
    public static String joinAnd(List<String> conditions) {
        StringBuilder result = new StringBuilder();
        result.append(" 1 ");
        for (String c:conditions) {
            result.append(" AND (");
            result.append(c);
            result.append(") ");
        }
        return result.toString();
    }

    public static String joinOr(List<String> conditions) {
        StringBuilder result = new StringBuilder();
        result.append(" 0 ");
        for (String c:conditions) {
            result.append(" OR (");
            result.append(c);
            result.append(") ");
        }
        return result.toString();
    }
}
