package org.scada_lts.serorepl.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ReplaceMacroStringUtilsWithProdParametersTest {

    private final static String timestampSql = "and ${field}>=? and ${field}<?";
    private final static String toReplace = "field";


    @Test
    public void when_replaceMacro_with_field_ts_then_replaced_field_to_ts() {
        //given:
        String replacement = "ts";
        String excpeted = com.serotonin.util.StringUtils.replaceMacro(timestampSql, toReplace, replacement);

        //when:
        String result = org.scada_lts.serorepl.utils.StringUtils.replaceMacro(timestampSql, toReplace, replacement);

        //then:
        assertEquals(excpeted, result);

    }

    @Test
    public void when_replaceMacro_with_field_ucts_then_replaced_field_to_ucts() {

        //given:
        String replacement = "uc.ts";
        String excpeted = com.serotonin.util.StringUtils.replaceMacro(timestampSql, toReplace, replacement);

        //when:
        String result = org.scada_lts.serorepl.utils.StringUtils.replaceMacro(timestampSql, toReplace, replacement);

        //then:
        assertEquals(excpeted, result);

    }
    @Test
    public void when_replaceMacro_with_field_eactiveTs_then_replaced_field_to_eactiveTs() {
        //given:
        String replacement = "e.activeTs";
        String excpeted = com.serotonin.util.StringUtils.replaceMacro(timestampSql, toReplace, replacement);

        //when:
        String result = org.scada_lts.serorepl.utils.StringUtils.replaceMacro(timestampSql, toReplace, replacement);

        //then:
        assertEquals(excpeted, result);

    }

    @Test
    public void when_replaceMacro_with_field_eactiveTs_then_replaced_field_to_Tsactive_assert_not_equal() {
        //given:
        String excpeted = com.serotonin.util.StringUtils.replaceMacro(timestampSql, toReplace, "e.activeTs");

        //when:
        String result = org.scada_lts.serorepl.utils.StringUtils.replaceMacro(timestampSql, toReplace, "activeTS.e");

        //then:
        assertNotEquals(excpeted, result);

    }

}
