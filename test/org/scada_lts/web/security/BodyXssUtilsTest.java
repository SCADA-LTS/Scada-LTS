package org.scada_lts.web.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class BodyXssUtilsTest {

    private final String input;
    private final boolean expectedResult;

    public BodyXssUtilsTest(String input, boolean expectedResult) {
        this.input = input;
        this.expectedResult = expectedResult;
    }

    @Parameters(name = "{index}: input: {0}, expectedResult: {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{

                // Test for a legitimate Query
                {"validUser", true},
                {"123", true},

                // Tests for queries containing banned patterns.
                {"javascript:alert(1)", false},
                {"onerror=alert(1)", false},
                {"<script>alert(1)</script>", false},
                {"onload=alert(1)", false},
                {"onmouseover=alert(1)", false},
                {"%6A%61%76%61%73%63%72%69%70%74", true},

                // Borderline cases
                {"=&", true},
                {"a”.repeat(33)", true},
                {"invalid!key", true},
                {"validKey", true},
                {"onerror=alert(1)&onload=alert(1)", false},
                {"!key=value", true},


                {"abc", true},
                {"ABC", true},
                {"abc=abc", true},
                {"ABC=ABC", true},
                {"abc=abc&ABC=ABC", true},
                {"ABC=ABC&abc=abc", true},
                {"abc=1", true},
                {"ABC=1", true},
                {"param123", true},
                {"param1&param2=23", true},
                {"param1=12&param2", true},
                {"param1=12&param2&param3", true},
                {"param1", true},
                {"startTs=123", true},
                {"startTs=1724326168402&endTs=1724329768507", true},
                {"startTs=123.", true},
                {"startTs=12345678901234567890123456789012", true},
                {"startTs=123456789012345678901234567890123", true},
                {"12345678901234567890123456789012=abc", true},
                {"123456789012345678901234567890123=abc", true},
                {"startTs=1724326168402&endTs=1724329768507&ids=101,70,97,84&configFromSystem=false&enabled=false&valuesLimit=10000&limitFactor=1", true},
                {"projectName=sagadf&includePointValues=true&includeUploadsFolder=true&includeGraphicsFolder=true&projectDescription=&pointValuesMaxZip=100", true},
                {"projectName=sagadf&includePointValues=true&includeUploadsFolder=true&includeGraphicsFolder=true&projectDescription=&pointValuesMaxZip=1005", true},
                {"projectName=sagadf&includePointValues=true&includeUploadsFolder=true&includeGraphicsFolder=true&projectDescription=&pointValuesMaxZip=100&abc=12", true},
                {"abc=", true},

                {"", false},
                {null, false},
                {"&param1", true},
                {"&param1&param2", true},
                {"&param1=123&param2", true},
                {"&param1&param=123", true},
                {"param1&param2=23&onerror", true},
                {"param1&param2=23onerror", true},
                {"param1&param2=23&onerror=", false},
                {"param1&param2=23onerror=", false},
                {"para!m1", true},
                {"onerror=alert(1)", false},
                {"onload=alert(1)", false},
                {"onerror=abc", false},
                {"onload=abc", false},
                {"onload=", false},
                {"onerror=", false},
                {"onload =", false},
                {"onerror =", false},
                {"onload    =", false},
                {"onerror   =", false},
                {"onload", true},
                {"onerror", true},
                {"!param1=value", true},
                {"<img src=x onerror=alert(document.location)>", false},
                {"param1=<img src=x onerror=alert(document.location)>", false},
                {"param1=<img src=x onerror=alert(document.location)>&param2=123", false},
                {"param1=123&param2=<img src=x onerror=alert(document.location)>", false},
                {"param1=123&param2=<img src=x onerror=document.location>", false},
                {"param1=alert(document.location)", false},
                {"=abc", true},
                {"param1=<script>alert(document.location)", false},
                {"param1=<scriptalert(document.location)", false},
                {"param1=script>alert(document.location)", false},
                {"param1=alert(document.location)</script>", false},
                {"param1=alert(document.location)/script>", false},
                {"param1=alert(document.location)</script", false},
                {"param1=<script>document.location", false},
                {"param1=<scriptdocument.location", false},
                {"param1=script>document.location", false},
                {"param1=document.location</script>", false},
                {"param1=document.location/script>", false},
                {"param1=document.location</script", false},
                {"#top-description-container {\n" +
                        "    display: flex;\n" +
                        "    align-items: flex-end;\n" +
                        "    justify-content: center;\n" +
                        "} \n" +
                        "\n" +
                        "#top-description-prefix { \n" +
                        "    color: red !important;\n" +
                        "    font-size: 2em !important;\n" +
                        "    margin-left: 0.5em !important;\n" +
                        "    margin-right: 0.5em !important;\n" +
                        "    display: inline-block !important;\n" +
                        "    vertical-align: bottom !important;\n" +
                        "    line-height: 1 !important;\n" +
                        "} \n" +
                        "\n" +
                        "#top-description {\n" +
                        "    color: #39B54A !important;\n" +
                        "    font-size: 2em !important;\n" +
                        "    display: inline-block !important;\n" +
                        "    line-height: 1 !important; \n" +
                        "}", true}
        });
    }

    @Test
    public void testValidate() {

        //when:
        boolean result = XssUtils.validateHttpBody(input);

        //then:
        assertEquals("Validation Body failed for input: " + input, expectedResult, result);
    }
}
