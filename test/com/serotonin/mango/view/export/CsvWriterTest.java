package com.serotonin.mango.view.export;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;


@RunWith(Parameterized.class)
public class CsvWriterTest {

    @Parameterized.Parameters(name= "{index}: field: {0}, expected: {1}")
    public static Collection data() {
        return Arrays.asList(new Object[][]{
                {"=1+2\";=1+2", "\"'=1+2\"\";=1+2\""},
                {"=1+2'\" ;,=1+2", "\"'=1+2'\"\" ;,=1+2\""},
                {"=1+2\",=1+2", "\"'=1+2\"\",=1+2\""},
                {"=1+2", "\"'=1+2\""},
                {"@1+2", "\"'@1+2\""},
                {"+1+2", "\"'+1+2\""},
                {"-1+2", "\"'-1+2\""},
                {"\r1+2", "\"'\r1+2\""},
                {"\n1+2", "\"'\n1+2\""},
                {"\t1+2", "\"'\t1+2\""},
        });
    };

    private final String field;
    private final String expected;

    public CsvWriterTest(String field, String expected) {
        this.field = field;
        this.expected = expected;
    }

    @Test
    public void when_encodeValue() {

        //when:
        String result = new CsvWriter().encodeValue(field);

        //then:
        assertEquals(expected, result);
    }
}