package org.scada_lts.dao.pointvalues;

import com.serotonin.mango.DataTypes;
import org.junit.Test;


import static org.junit.Assert.*;

public class PointValueAmChartDAOTest {

    private static int dataPointId = 123;
    private static long startTs = 123456789;
    private static long endTs = 234567890;
    private static long intervalMs = 4321;

    @Test
    public void when_toAggregationQuery_for_numeric_type_then_avg_query() {

        //given:
        int dataType = DataTypes.NUMERIC;
        int limit = 444;
        String queryExpected = "SELECT dataPointId, avg(pointValue) AS pointValue, max(ts) AS ts FROM pointValues WHERE dataPointId=? AND ts >=? AND ts <=? AND dataType=? GROUP BY dataPointId, floor(ts/?) ORDER BY ts ASC LIMIT ?";

        //when:
        PointValueAmChartDAO.QueryArgs queryArgs = PointValueAmChartDAO.toAggregationQuery(dataPointId, dataType, startTs, endTs, intervalMs, limit);

        //then:
        assertEquals(queryExpected, queryArgs.getQuery());
    }

    @Test
    public void when_toAggregationQuery_for_numeric_type_then_check_args() {

        //given:
        int dataType = DataTypes.NUMERIC;
        int limit = 444;
        Object[] argsExpected = new Object[]{dataPointId, startTs, endTs, dataType, intervalMs, limit};

        //when:
        PointValueAmChartDAO.QueryArgs queryArgs = PointValueAmChartDAO.toAggregationQuery(dataPointId, dataType, startTs, endTs, intervalMs, limit);

        //then:
        assertArrayEquals(argsExpected, queryArgs.getArgs());
    }

    @Test
    public void when_toAggregationQuery_for_multistate_type_then_limit_query() {

        //given:
        int dataType = DataTypes.MULTISTATE;
        int limit = 444;
        String queryExpected = "SELECT dataPointId, pointValue, ts FROM pointValues WHERE dataPointId=? AND ts >=? AND ts <=? AND dataType=? ORDER BY ts ASC LIMIT ?";

        //when:
        PointValueAmChartDAO.QueryArgs queryArgs = PointValueAmChartDAO.toAggregationQuery(dataPointId, dataType, startTs, endTs, intervalMs, limit);

        //then:
        assertEquals(queryExpected, queryArgs.getQuery());
    }

    @Test
    public void when_toAggregationQuery_for_multistate_type_then_check_args() {

        //given:
        int dataType = DataTypes.MULTISTATE;
        int limit = 444;
        Object[] argsExpected = new Object[]{dataPointId, startTs, endTs, dataType, limit};

        //when:
        PointValueAmChartDAO.QueryArgs queryArgs = PointValueAmChartDAO.toAggregationQuery(dataPointId, dataType, startTs, endTs, intervalMs, limit);

        //then:
        assertArrayEquals(argsExpected, queryArgs.getArgs());
    }

    @Test
    public void when_toAggregationQuery_for_binary_type_then_max_query() {

        //given:
        int dataType = DataTypes.BINARY;
        int limit = 444;
        String queryExpected = "SELECT dataPointId, max(pointValue) AS pointValue, max(ts) AS ts FROM pointValues WHERE dataPointId=? AND ts >=? AND ts <=? AND dataType=? GROUP BY dataPointId, floor(ts/?) ORDER BY ts ASC LIMIT ?";

        //when:
        PointValueAmChartDAO.QueryArgs queryArgs = PointValueAmChartDAO.toAggregationQuery(dataPointId, dataType, startTs, endTs, intervalMs, limit);

        //then:
        assertEquals(queryExpected, queryArgs.getQuery());
    }

    @Test
    public void when_toAggregationQuery_for_binary_type_then_check_args() {

        //given:
        int dataType = DataTypes.BINARY;
        int limit = 444;
        Object[] argsExpected =new Object[]{dataPointId, startTs, endTs, dataType, intervalMs, limit};

        //when:
        PointValueAmChartDAO.QueryArgs queryArgs = PointValueAmChartDAO.toAggregationQuery(dataPointId, dataType, startTs, endTs, intervalMs, limit);

        //then:
        assertArrayEquals(argsExpected, queryArgs.getArgs());
    }

    @Test
    public void when_toAggregationQuery_for_alphanumeric_type_then_limit_query() {

        //given:
        int dataType = DataTypes.ALPHANUMERIC;
        int limit = 444;
        String queryExpected = "SELECT dataPointId, pointValue, ts FROM pointValues WHERE dataPointId=? AND ts >=? AND ts <=? AND dataType=? ORDER BY ts ASC LIMIT ?";

        //when:
        PointValueAmChartDAO.QueryArgs queryArgs = PointValueAmChartDAO.toAggregationQuery(dataPointId, dataType, startTs, endTs, intervalMs, limit);

        //then:
        assertEquals(queryExpected, queryArgs.getQuery());
    }

    @Test
    public void when_toAggregationQuery_for_alphanumeric_type_then_check_args() {

        //given:
        int dataType = DataTypes.ALPHANUMERIC;
        int limit = 444;
        Object[] argsExpected = new Object[]{dataPointId, startTs, endTs, dataType, limit};

        //when:
        PointValueAmChartDAO.QueryArgs queryArgs = PointValueAmChartDAO.toAggregationQuery(dataPointId, dataType, startTs, endTs, intervalMs, limit);

        //then:
        assertArrayEquals(argsExpected, queryArgs.getArgs());
    }


    ////

    @Test
    public void when_toAggregationQuery_for_alphanumeric_and_0_limit_then_non_limit_query() {

        //given:
        int dataType = DataTypes.ALPHANUMERIC;
        int limit = 0;
        String queryExpected = "SELECT dataPointId, pointValue, ts FROM pointValues WHERE dataPointId=? AND ts >=? AND ts <=? AND dataType=? ORDER BY ts ASC";

        //when:
        PointValueAmChartDAO.QueryArgs queryArgs = PointValueAmChartDAO.toAggregationQuery(dataPointId, dataType, startTs, endTs, intervalMs, limit);

        //then:
        assertEquals(queryExpected, queryArgs.getQuery());
    }


    @Test
    public void when_toAggregationQuery_for_alphanumeric_and_0_limit_then_args_without_limit() {

        //given:
        int dataType = DataTypes.ALPHANUMERIC;
        int limit = 0;
        Object[] argsExpected = new Object[]{dataPointId, startTs, endTs, dataType};

        //when:
        PointValueAmChartDAO.QueryArgs queryArgs = PointValueAmChartDAO.toAggregationQuery(dataPointId, dataType, startTs, endTs, intervalMs, limit);

        //then:
        assertArrayEquals(argsExpected, queryArgs.getArgs());
    }

    @Test
    public void when_toAggregationQuery_for_numeric_and_0_limit_then_non_limit_query() {

        //given:
        int dataType = DataTypes.NUMERIC;
        int limit = 0;
        String queryExpected = "SELECT dataPointId, avg(pointValue) AS pointValue, max(ts) AS ts FROM pointValues WHERE dataPointId=? AND ts >=? AND ts <=? AND dataType=? GROUP BY dataPointId, floor(ts/?) ORDER BY ts ASC";

        //when:
        PointValueAmChartDAO.QueryArgs queryArgs = PointValueAmChartDAO.toAggregationQuery(dataPointId, dataType, startTs, endTs, intervalMs, limit);

        //then:
        assertEquals(queryExpected, queryArgs.getQuery());
    }


    @Test
    public void when_toAggregationQuery_for_numeric_and_0_limit_then_args_without_limit() {

        //given:
        int dataType = DataTypes.NUMERIC;
        int limit = 0;
        Object[] argsExpected = new Object[]{dataPointId, startTs, endTs, dataType, intervalMs};

        //when:
        PointValueAmChartDAO.QueryArgs queryArgs = PointValueAmChartDAO.toAggregationQuery(dataPointId, dataType, startTs, endTs, intervalMs, limit);

        //then:
        assertArrayEquals(argsExpected, queryArgs.getArgs());
    }

    @Test
    public void when_toAggregationQuery_for_binary_and_0_limit_then_non_limit_query() {

        //given:
        int dataType = DataTypes.BINARY;
        int limit = 0;
        String queryExpected = "SELECT dataPointId, max(pointValue) AS pointValue, max(ts) AS ts FROM pointValues WHERE dataPointId=? AND ts >=? AND ts <=? AND dataType=? GROUP BY dataPointId, floor(ts/?) ORDER BY ts ASC";

        //when:
        PointValueAmChartDAO.QueryArgs queryArgs = PointValueAmChartDAO.toAggregationQuery(dataPointId, dataType, startTs, endTs, intervalMs, limit);

        //then:
        assertEquals(queryExpected, queryArgs.getQuery());
    }


    @Test
    public void when_toAggregationQuery_for_binary_and_0_limit_then_args_without_limit() {

        //given:
        int dataType = DataTypes.BINARY;
        int limit = 0;
        Object[] argsExpected = new Object[]{dataPointId, startTs, endTs, dataType, intervalMs};

        //when:
        PointValueAmChartDAO.QueryArgs queryArgs = PointValueAmChartDAO.toAggregationQuery(dataPointId, dataType, startTs, endTs, intervalMs, limit);

        //then:
        assertArrayEquals(argsExpected, queryArgs.getArgs());
    }

    @Test
    public void when_toAggregationQuery_for_multistate_and_0_limit_then_non_limit_query() {

        //given:
        int dataType = DataTypes.MULTISTATE;
        int limit = 0;
        String queryExpected = "SELECT dataPointId, pointValue, ts FROM pointValues WHERE dataPointId=? AND ts >=? AND ts <=? AND dataType=? ORDER BY ts ASC";

        //when:
        PointValueAmChartDAO.QueryArgs queryArgs = PointValueAmChartDAO.toAggregationQuery(dataPointId, dataType, startTs, endTs, intervalMs, limit);

        //then:
        assertEquals(queryExpected, queryArgs.getQuery());
    }


    @Test
    public void when_toAggregationQuery_for_multistate_and_0_limit_then_args_without_limit() {

        //given:
        int dataType = DataTypes.MULTISTATE;
        int limit = 0;
        Object[] argsExpected = new Object[]{dataPointId, startTs, endTs, dataType};

        //when:
        PointValueAmChartDAO.QueryArgs queryArgs = PointValueAmChartDAO.toAggregationQuery(dataPointId, dataType, startTs, endTs, intervalMs, limit);

        //then:
        assertArrayEquals(argsExpected, queryArgs.getArgs());
    }

}