package org.scada_lts.utils;

import com.serotonin.mango.web.dwr.EventsDwr;
import org.scada_lts.dao.event.EventDAO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public static class EventSearchQuery {
        private final String sql;
        private final List<Object> params;

        public EventSearchQuery(String sql, List<Object> params) {
            this.sql = sql;
            this.params = params;
        }

        public String getSql() {
            return sql;
        }

        public Object[] getParamsArray() {
            return params.toArray();
        }
    }

    public static String buildInClause(String columnName, int length) {
        StringBuilder sb = new StringBuilder(columnName).append(" IN (");
        for (int i = 0; i < length; i++) {
            if (i > 0) sb.append(", ");
            sb.append("?");
        }
        sb.append(")");
        return sb.toString();
    }

    public static EventSearchQuery buildSearchSql(
            int userId,
            String[] eventSourceTypes,
            String[] statuses,
            String[] alarmLevels,
            Date startDate,
            Date endDate,
            String[] keywordArr
    ) {
        StringBuilder sql = new StringBuilder();
        List<String> where = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        sql.append(EventDAO.getBasicEventSelect());
        sql.append(" WHERE ue.userId=?");
        params.add(userId);

        addEventSourceTypesCondition(eventSourceTypes, where, params);
        addStatusesCondition(statuses, where, params);
        addAlarmLevelsCondition(alarmLevels, where, params);
        addDateCondition(startDate, endDate, where, params);
        addKeywordsCondition(keywordArr, where, params);

        for (String condition : where) {
            sql.append(" AND ").append(condition);
        }
        sql.append(" ORDER BY e.activeTs DESC");

        return new EventSearchQuery(sql.toString(), params);
    }

    private static void addEventSourceTypesCondition(String[] eventSourceTypes,
                                                     List<String> where,
                                                     List<Object> params) {
        if (eventSourceTypes != null && eventSourceTypes.length > 0) {
            String inClause = buildInClause("e.typeId", eventSourceTypes.length);
            where.add(inClause);
            for (String src : eventSourceTypes) {
                params.add(Integer.valueOf(src));
            }
        }
    }

    private static void addStatusesCondition(String[] statuses,
                                             List<String> where,
                                             List<Object> params) {
        if (statuses != null && statuses.length > 0) {
            List<String> orClauses = new ArrayList<>();
            for (String s : statuses) {
                if (EventsDwr.STATUS_ACTIVE.equals(s)) {
                    orClauses.add("( e.rtnApplicable='Y' AND e.rtnTs=0 )");
                } else if (EventsDwr.STATUS_RTN.equals(s)) {
                    orClauses.add("( e.rtnApplicable='Y' AND e.rtnTs>0 )");
                } else if (EventsDwr.STATUS_NORTN.equals(s)) {
                    orClauses.add("( e.rtnApplicable='N' )");
                } else if (EventsDwr.STATUS_ASSIGNEE.equals(s)) {
                    orClauses.add("( " + EventDAO.getStatusActiveCondition() + " )");
                }
            }
            if (!orClauses.isEmpty()) {
                String statusBlock = "(" + String.join(" OR ", orClauses) + ")";
                where.add(statusBlock);
            }
        }
    }

    private static void addAlarmLevelsCondition(String[] alarmLevels,
                                                List<String> where,
                                                List<Object> params) {
        if (alarmLevels != null && alarmLevels.length > 0) {
            String inClause = buildInClause("e.alarmLevel", alarmLevels.length);
            where.add(inClause);
            for (String lvl : alarmLevels) {
                params.add(Integer.valueOf(lvl));
            }
        }
    }

    private static void addDateCondition(Date startDate,
                                         Date endDate,
                                         List<String> where,
                                         List<Object> params) {
        if (startDate != null) {
            where.add("e.activeTs >= ?");
            params.add(startDate.getTime());
        }
        if (endDate != null) {
            where.add("e.activeTs <= ?");
            params.add(endDate.getTime());
        }
    }

    private static void addKeywordsCondition(String[] keywordArr,
                                             List<String> where,
                                             List<Object> params) {
        if (keywordArr != null && keywordArr.length > 0) {
            List<String> orKeywords = new ArrayList<>();
            for (String kw : keywordArr) {
                orKeywords.add(" e.message LIKE ? ");
                params.add("%" + kw + "%");
            }
            if (!orKeywords.isEmpty()) {
                where.add("(" + String.join("OR", orKeywords) + ")");
            }
        }
    }
}
