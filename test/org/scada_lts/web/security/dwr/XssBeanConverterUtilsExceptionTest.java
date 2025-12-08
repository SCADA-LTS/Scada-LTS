package org.scada_lts.web.security.dwr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class XssBeanConverterUtilsExceptionTest {

    @Parameterized.Parameters(name = "{index}: value: {0}, type: {1}")
    public static Object[][] data() {
        return new Object[][] {
                {"", String.class},
                {'o', Character.class},
                {1.0, Double.class},
                {1, Integer.class},
                {Short.valueOf("1"), Short.class},
                {1L, Long.class},
                {true, Boolean.class},
                {false, Boolean.class},
        };
    }

    private final Object value;

    public XssBeanConverterUtilsExceptionTest(Object value, Class<?> type) {
        this.value = value;
    }

    @Test(expected = ScadaMarshallException.class)
    public void when_convertObjectEscaped_for_object_with_type_no_source_then_IllegalArgumentException() throws ScadaMarshallException {

        //when:
        XssBeanConverterUtils.convertObjectEscaped(value);
    }

    @Test(expected = ScadaMarshallException.class)
    public void when_convertObjectUnescaped_for_object_with_type_no_source_then_IllegalArgumentException() throws ScadaMarshallException {

        //when:
        XssBeanConverterUtils.convertObjectUnescaped(value);
    }

}