package org.scada_lts.utils;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class QueryUtilsTest {

    @Test
    public void buildSearchSql_noFilters_exceptUserId() {
        int userId = 123;
        String[] eventSourceTypes = null;
        String[] statuses = null;
        String[] alarmLevels = null;
        Date startDate = null;
        Date endDate = null;
        String[] keywordArr = null;
        int eventId = 0;

        QueryUtils.EventSearchQuery query = QueryUtils.buildSearchSql(
                userId,
                eventSourceTypes,
                statuses,
                alarmLevels,
                startDate,
                endDate,
                keywordArr,
                eventId
        );

        String sql = query.getSql();
        Object[] params = query.getParamsArray();

        assertNotNull(sql);
        assertNotNull(params);

        assertEquals(1, params.length);
        assertEquals(123, params[0]);

        assertTrue(sql.contains("WHERE ue.userId=?"));
        assertTrue(sql.contains("ORDER BY e.activeTs DESC"));

        assertFalse(sql.contains("typeId IN"));
        assertFalse(sql.contains("e.id = ?"));
    }

    @Test
    public void buildSearchSql_withEventSourceTypes() {
        int userId = 999;
        String[] eventSourceTypes = {"1", "3"};
        String[] statuses = null;
        String[] alarmLevels = null;
        Date startDate = null;
        Date endDate = null;
        String[] keywordArr = null;
        int eventId = 0;

        QueryUtils.EventSearchQuery query = QueryUtils.buildSearchSql(
                userId,
                eventSourceTypes,
                statuses,
                alarmLevels,
                startDate,
                endDate,
                keywordArr,
                eventId
        );

        String sql = query.getSql();
        Object[] params = query.getParamsArray();
        assertEquals(3, params.length);
        assertEquals(999, params[0]);
        assertEquals(1, params[1]);
        assertEquals(3, params[2]);

        assertTrue(sql.contains("WHERE ue.userId=?"));
        assertTrue(sql.contains(" e.typeId IN ("));

        assertFalse(sql.contains("rtnTs=0"));
        assertFalse(sql.contains("e.id = ?"));
    }

    @Test
    public void buildSearchSql_withStatuses() {
        int userId = 100;
        String[] eventSourceTypes = null;
        String[] statuses = { "A", "N" };
        String[] alarmLevels = null;
        Date startDate = null;
        Date endDate = null;
        String[] keywordArr = null;
        int eventId = 0;

        QueryUtils.EventSearchQuery query = QueryUtils.buildSearchSql(
                userId,
                eventSourceTypes,
                statuses,
                alarmLevels,
                startDate,
                endDate,
                keywordArr,
                eventId
        );

        String sql = query.getSql();
        Object[] params = query.getParamsArray();

        assertEquals(1, params.length);
        assertEquals(100, params[0]);

        assertTrue(sql.contains("( e.rtnApplicable='Y' AND e.rtnTs=0 )"));
        assertTrue(sql.contains("( e.rtnApplicable='N' )"));
        assertFalse(sql.contains("e.id = ?"));
    }

    @Test
    public void buildSearchSql_withAlarmLevels() {
        int userId = 111;
        String[] eventSourceTypes = null;
        String[] statuses = null;
        String[] alarmLevels = {"2", "4"};
        Date startDate = null;
        Date endDate = null;
        String[] keywordArr = null;
        int eventId = 0;

        QueryUtils.EventSearchQuery query = QueryUtils.buildSearchSql(
                userId,
                eventSourceTypes,
                statuses,
                alarmLevels,
                startDate,
                endDate,
                keywordArr,
                eventId
        );

        String sql = query.getSql();
        Object[] params = query.getParamsArray();

        assertEquals(3, params.length);
        assertEquals(111, params[0]);
        assertEquals(2, params[1]);
        assertEquals(4, params[2]);

        assertTrue(sql.contains("e.alarmLevel IN ("));
        assertFalse(sql.contains("e.id = ?"));
    }

    @Test
    public void buildSearchSql_withDateRange() {
        int userId = 222;
        String[] eventSourceTypes = null;
        String[] statuses = null;
        String[] alarmLevels = null;
        Date startDate = new Date(1672531200000L); // 2023-01-01
        Date endDate   = new Date(1673308800000L); // 2023-01-10
        String[] keywordArr = null;
        int eventId = 0;

        QueryUtils.EventSearchQuery query = QueryUtils.buildSearchSql(
                userId,
                eventSourceTypes,
                statuses,
                alarmLevels,
                startDate,
                endDate,
                keywordArr,
                eventId
        );

        String sql = query.getSql();
        Object[] params = query.getParamsArray();

        assertEquals(3, params.length);
        assertEquals(222, params[0]);
        assertEquals(1672531200000L, params[1]);
        assertEquals(1673308800000L, params[2]);

        assertTrue(sql.contains("e.activeTs >= ?"));
        assertTrue(sql.contains("e.activeTs <= ?"));
        // Bez e.id = ?
        assertFalse(sql.contains("e.id = ?"));
    }

    @Test
    public void buildSearchSql_withKeywords() {
        int userId = 333;
        String[] eventSourceTypes = null;
        String[] statuses = null;
        String[] alarmLevels = null;
        Date startDate = null;
        Date endDate = null;
        String[] keywordArr = { "error", "temp" };
        int eventId = 0;

        QueryUtils.EventSearchQuery query = QueryUtils.buildSearchSql(
                userId,
                eventSourceTypes,
                statuses,
                alarmLevels,
                startDate,
                endDate,
                keywordArr,
                eventId
        );

        String sql = query.getSql();
        Object[] params = query.getParamsArray();

        assertEquals(3, params.length);
        assertEquals(333, params[0]);
        assertEquals("%error%", params[1]);
        assertEquals("%temp%", params[2]);

        assertTrue(sql.contains("( e.message LIKE ? "));
        assertTrue(sql.contains(" OR e.message LIKE ? "));
        assertFalse(sql.contains("e.id = ?"));
    }


    @Test
    public void buildSearchSql_withEventId() {
        int userId = 444;
        String[] eventSourceTypes = null;
        String[] statuses = null;
        String[] alarmLevels = null;
        Date startDate = null;
        Date endDate = null;
        String[] keywordArr = null;
        int eventId = 555;

        QueryUtils.EventSearchQuery query = QueryUtils.buildSearchSql(
                userId,
                eventSourceTypes,
                statuses,
                alarmLevels,
                startDate,
                endDate,
                keywordArr,
                eventId
        );

        String sql = query.getSql();
        Object[] params = query.getParamsArray();

        assertEquals(2, params.length);
        assertEquals(444, params[0]);
        assertEquals(555, params[1]);

        assertTrue(sql.contains(" e.id = ? "));
        assertTrue(sql.contains("WHERE ue.userId=?"));
        assertTrue(sql.contains("ORDER BY e.activeTs DESC"));
    }
}
