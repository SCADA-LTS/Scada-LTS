package com.serotonin.mango.rt.dataImage;

import org.junit.Test;

import static org.junit.Assert.*;

public class DataPointSyncModeTest {

    @Test
    public void when_typeOf_for_true_then_High() {

        //given:
        String value = "true";
        DataPointSyncMode expected = DataPointSyncMode.HIGH;

        //when:
        DataPointSyncMode result = DataPointSyncMode.typeOf(value);

        //then:
        assertEquals(expected, result);
    }

    @Test
    public void when_typeOf_for_false_then_Low() {

        //given:
        String value = "false";
        DataPointSyncMode expected = DataPointSyncMode.LOW;

        //when:
        DataPointSyncMode result = DataPointSyncMode.typeOf(value);

        //then:
        assertEquals(expected, result);
    }

    @Test
    public void when_typeOf_for_TRUE_then_High() {

        //given:
        String value = "TRUE";
        DataPointSyncMode expected = DataPointSyncMode.HIGH;

        //when:
        DataPointSyncMode result = DataPointSyncMode.typeOf(value);

        //then:
        assertEquals(expected, result);
    }

    @Test
    public void when_typeOf_for_FALSE_then_Low() {

        //given:
        String value = "FALSE";
        DataPointSyncMode expected = DataPointSyncMode.LOW;

        //when:
        DataPointSyncMode result = DataPointSyncMode.typeOf(value);

        //then:
        assertEquals(expected, result);
    }

    @Test
    public void when_typeOf_for_partial_then_Medium() {

        //given:
        String value = "partial";
        DataPointSyncMode expected = DataPointSyncMode.MEDIUM;

        //when:
        DataPointSyncMode result = DataPointSyncMode.typeOf(value);

        //then:
        assertEquals(expected, result);
    }

    @Test
    public void when_typeOf_for_PARTIAL_then_Medium() {

        //given:
        String value = "PARTIAL";
        DataPointSyncMode expected = DataPointSyncMode.MEDIUM;

        //when:
        DataPointSyncMode result = DataPointSyncMode.typeOf(value);

        //then:
        assertEquals(expected, result);
    }

    @Test
    public void when_typeOf_for_none_then_Low() {

        //given:
        String value = "none";
        DataPointSyncMode expected = DataPointSyncMode.LOW;

        //when:
        DataPointSyncMode result = DataPointSyncMode.typeOf(value);

        //then:
        assertEquals(expected, result);
    }

    @Test
    public void when_typeOf_for_NONE_then_Low() {

        //given:
        String value = "NONE";
        DataPointSyncMode expected = DataPointSyncMode.LOW;

        //when:
        DataPointSyncMode result = DataPointSyncMode.typeOf(value);

        //then:
        assertEquals(expected, result);
    }

    @Test
    public void when_typeOf_for_all_then_High() {

        //given:
        String value = "all";
        DataPointSyncMode expected = DataPointSyncMode.HIGH;

        //when:
        DataPointSyncMode result = DataPointSyncMode.typeOf(value);

        //then:
        assertEquals(expected, result);
    }

    @Test
    public void when_typeOf_for_ALL_then_High() {

        //given:
        String value = "ALL";
        DataPointSyncMode expected = DataPointSyncMode.HIGH;

        //when:
        DataPointSyncMode result = DataPointSyncMode.typeOf(value);

        //then:
        assertEquals(expected, result);
    }

    @Test
    public void when_typeOf_for_aBC_then_Low_default() {

        //given:
        String value = "aBC";
        DataPointSyncMode expected = DataPointSyncMode.LOW;

        //when:
        DataPointSyncMode result = DataPointSyncMode.typeOf(value);

        //then:
        assertEquals(expected, result);
    }

    @Test
    public void when_typeOf_for_null_then_Low() {

        //given:
        String value = null;
        DataPointSyncMode expected = DataPointSyncMode.LOW;

        //when:
        DataPointSyncMode result = DataPointSyncMode.typeOf(value);

        //then:
        assertEquals(expected, result);
    }
}