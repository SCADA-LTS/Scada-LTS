package com.serotonin.mango.util;

import org.junit.Assert;
import org.junit.Test;

public class AddLimitIfWithoutSqlDataSourceUtilsTest {

    @Test(expected = IllegalArgumentException.class)
    public void when_addLimitIfWithout_with_empty_then_IllegalArgumentException() {

        //given:
        String query = "";

        //when:
        String queryResult =  SqlDataSourceUtils.addLimitIfWithout(query, 100);

    }

    @Test(expected = IllegalArgumentException.class)
    public void when_addLimitIfWithout_with_semicolon_then_IllegalArgumentException() {

        //given:
        String query = ";";

        //when:
        String queryResult =  SqlDataSourceUtils.addLimitIfWithout(query, 100);

    }

    @Test
    public void when_addLimitIfWithout_with_query_semicolon_end_then_query_with_limit() {

        //given:
        String query = "select * from table;";
        String queryExpected = "select * from table LIMIT 100";

        //when:
        String queryResult =  SqlDataSourceUtils.addLimitIfWithout(query, 100);

        //then:
        Assert.assertEquals(queryExpected, queryResult);

    }

    @Test
    public void when_addLimitIfWithout_with_query_without_semicolon_then_query_with_limit() {

        //given:
        String query = "select * from table";
        String queryExpected = "select * from table LIMIT 100";

        //when:
        String queryResult =  SqlDataSourceUtils.addLimitIfWithout(query, 100);

        //then:
        Assert.assertEquals(queryExpected, queryResult);

    }

}