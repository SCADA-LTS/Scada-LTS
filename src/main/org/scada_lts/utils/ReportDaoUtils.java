package org.scada_lts.utils;

import com.serotonin.mango.Common;
import org.apache.commons.lang3.StringUtils;
import org.scada_lts.dao.QueryArgs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ReportDaoUtils {

    private ReportDaoUtils() {}

    public static QueryArgs searchQuery(int userId,
                                        Map<String, String> query,
                                        String selectReports) {
        List<Object> params = new ArrayList<>();
        String where = prefixWherePartQuery(userId, query, params);
        return new QueryArgs(selectReports + where, params.toArray(new Object[]{}));
    }

    private static String prefixWherePartQuery(int userId,
                                              Map<String, String> query,
                                              List<Object> params) {
        List<String> filterCondtions = new ArrayList<>();
        byUser(userId, filterCondtions, params);
        String keywords = query.get("keywords");
        byKeywords(keywords, filterCondtions, params);
        String ands = SqlUtils.joinAnd(filterCondtions);
        return ands.isEmpty() ? "" : " R WHERE " + ands;
    }

    private static void byKeywords(String keywords, List<String> filterCondtions, List<Object> params) {
        if (!StringUtils.isEmpty(keywords)) {
            List<String> keywordConditions = new ArrayList<>();
            for (String keyword : keywords.split(" ")) {
                keywordConditions.add("R.name LIKE ?");
                params.add("%" + keyword + "%");
            }
            filterCondtions.add("(" + SqlUtils.joinOr(keywordConditions) + ")");
        }
    }

    private static void byUser(int userId, List<String> filterCondtions, List<Object> params) {
        if(userId != Common.NEW_ID) {
            params.add(userId);
            filterCondtions.add("R.userId = ? ");
        }
    }
}
