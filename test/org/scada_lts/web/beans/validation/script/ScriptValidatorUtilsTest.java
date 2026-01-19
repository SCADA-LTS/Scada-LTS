package org.scada_lts.web.beans.validation.script;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ScriptValidatorUtilsTest {

    @Parameterized.Parameters(name= "{index}: script: {0}, valid: {1}")
    public static Object[][] data() {
        return new Object[][] {
            new Object[] {
                    "var t = java.lang.ProcessBuilder(\"cmd\",\"/c\",\"echo POC>%TEMP%\\rce_should_not_be_possible.txt\");return t;",
                    false
            },
            new Object[] {
                    "var t = java.lang.ProcessBuilder(\"cmd\",\"/c\",\"echo POC>%TEMP%\\rce_should_not_be_possible.txt\");\r\nreturn t;",
                    false
            },
            new Object[] {
                    "var t = new java.lang.ProcessBuilder(\"bash\",\"-c\",\"echo POC > /tmp/rce_should_not_be_possible\").start();return t;",
                    false
            },
            new Object[] {
                    "var t = new java.lang.ProcessBuilder(\"bash\",\"-c\",\"echo POC > /tmp/rce_should_not_be_possible\").start();\r\nreturn t;",
                    false
            },
            new Object[] {
                    "var t = new java.lang.ProcesisBuilder(\"bash\",\"-c\",\"echo POC > /tmp/rce_should_not_be_possible\").start();\r\nreturn t;",
                    true
            },
            new Object[] {
                    "var t = new java.lang.ProcesisBuilder(\"bash\",\"-c\",\"echo POC > /tmp/rce_should_not_be_possible\").start();return t;",
                    true
            },
        };
    }

    private final String script;
    private final boolean excepted;

    public ScriptValidatorUtilsTest(String script, boolean valid) {
        this.script = script;
        this.excepted = valid;
    }

    @Test
    public void validate() {

        //when:
        boolean result = ScriptValidatorUtils.validate(script);

        //then:
        Assert.assertEquals(excepted, result);
    }
}