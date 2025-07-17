package com.serotonin.mango.rt.dataImage.types;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class BinaryValueParseBinaryExceptionTest {

    @Parameterized.Parameters(name = "{index}: string value: {0}")
    public static Object[][] data() {
        return new Object[][] {
                {"1a"},
                {" 1'"},
                {" 1+ "},
                {"15"},
                {"true5"},
                {" true1"},
                {" trueI "},
                {"0-"},
                {" 0p"},
                {" 02 "},
                {"10 "},
                {"false2"},
                {" falsek"},
                {" false 1"},
        };
    }

    private final String stringValue;

    public BinaryValueParseBinaryExceptionTest(String stringValue) {
        this.stringValue = stringValue;
    }

    @Test(expected = NumberFormatException.class)
    public void parseBinary() {

        //when:
        BinaryValue result = BinaryValue.parseBinary(stringValue);
    }
}