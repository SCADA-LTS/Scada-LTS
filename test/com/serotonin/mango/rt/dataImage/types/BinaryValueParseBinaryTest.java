package com.serotonin.mango.rt.dataImage.types;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class BinaryValueParseBinaryTest {

    @Parameterized.Parameters(name = "{index}: string value: {0}, expected value: {1}")
    public static Object[][] data() {
        return new Object[][] {
                {"1", BinaryValue.ONE},
                {" 1", BinaryValue.ONE},
                {" 1 ", BinaryValue.ONE},
                {"1 ", BinaryValue.ONE},
                {"true", BinaryValue.ONE},
                {" true", BinaryValue.ONE},
                {" true ", BinaryValue.ONE},
                {"true ", BinaryValue.ONE},
                {"tRue", BinaryValue.ONE},
                {" trUe", BinaryValue.ONE},
                {" True ", BinaryValue.ONE},
                {"truE ", BinaryValue.ONE},

                {"0", BinaryValue.ZERO},
                {" 0", BinaryValue.ZERO},
                {" 0 ", BinaryValue.ZERO},
                {"0 ", BinaryValue.ZERO},
                {"false", BinaryValue.ZERO},
                {" false", BinaryValue.ZERO},
                {" false ", BinaryValue.ZERO},
                {"false ", BinaryValue.ZERO},
                {"fAlse", BinaryValue.ZERO},
                {" faLse", BinaryValue.ZERO},
                {" False ", BinaryValue.ZERO},
                {"falsE ", BinaryValue.ZERO},
        };
    }

    private final String stringValue;
    private final BinaryValue expectedValue;

    public BinaryValueParseBinaryTest(String stringValue, BinaryValue expectedValue) {
        this.stringValue = stringValue;
        this.expectedValue = expectedValue;
    }

    @Test
    public void parseBinary() {

        //when:
        BinaryValue result = BinaryValue.parseBinary(stringValue);

        //then:
        Assert.assertEquals(expectedValue, result);
    }
}