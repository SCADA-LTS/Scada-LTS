package org.scada_lts.scripting;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class RegexReplaceTest {
    private static final Pattern REGEX_XID = Pattern.compile("[^a-zA-Z0-9_]");

    private String inputXid;
    private String expectedResult;

    public RegexReplaceTest(String inputXid, String expectedResult) {
        this.inputXid = inputXid;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection primeNumbers() {
        return Arrays.asList(new String[][] {
                {"DP_847532", "DP_847532"},
                {"DP_847532_", "DP_847532_"},
                {"#DP_847532_^%", "DP_847532_"},
                {"DP_847532_test", "DP_847532_test"},
                {"1@3$5^7*9)DP_847532", "13579DP_847532"},
                {"DP~_~8*$47532_", "DP_847532_"},
                {"test#DP_847532_", "testDP_847532_"},
                {"\"D|0|P_847532_", "D0P_847532_"},
                {"DP_847(test)532_", "DP_847test532_"},
                {"DP _8 47 53 2_", "DP_847532_"}
        });
    }

    @Test
    public void testRegexReplace() {
        assertEquals(expectedResult, REGEX_XID.matcher(inputXid).replaceAll(""));
    }
}
