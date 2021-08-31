package org.scada_lts.utils;

import org.apache.commons.logging.LogFactory;
import org.scada_lts.config.ScadaConfig;

public final class QueryUtils {

    private static final String DB_QUERY_READ_VALUES_ENABLED_KEY = "dbquery.values.read.enabled";
    private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(QueryUtils.class);

    private QueryUtils() {}

    public static String getArgsIn(int size) {
        StringBuilder args = new StringBuilder();
        for(int i = 0 ; i < size; i++) {
            args.append("?").append(",");
        }
        args.delete(args.length() - 1, args.length());
        return args.toString();
    }

    public static boolean isReadEnabled() {
        try {
            return ScadaConfig.getInstance().getBoolean(DB_QUERY_READ_VALUES_ENABLED_KEY, false);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
    }
}
