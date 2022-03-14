package org.scada_lts.utils;

import java.util.List;

public final class SqlUtils {

    private SqlUtils() {}

    public static String joinAnd(List<String> conditions) {
        StringBuilder result = new StringBuilder();
        for (String c:conditions) {
            result.append(c);
            if(!isLast(conditions, c))
                result.append(" AND ");
        }
        return result.toString();
    }

    public static String joinOr(List<String> conditions) {
        StringBuilder result = new StringBuilder();
        for (String c:conditions) {
            result.append(c);
            if(!isLast(conditions, c))
                result.append(" OR ");
        }
        return result.toString();
    }

    private static boolean isLast(List<String> conditions, String c) {
        return conditions.indexOf(c) == conditions.size() - 1;
    }
}
