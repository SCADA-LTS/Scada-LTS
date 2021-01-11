package org.scada_lts.utils;

public final class QueryUtils {

    private QueryUtils() {}

    public static String getArgsIn(int size) {
        StringBuilder args = new StringBuilder();
        for(int i = 0 ; i < size; i++) {
            args.append("?").append(",");
        }
        args.delete(args.length() - 1, args.length());
        return args.toString();
    }
}
