package org.scada_lts.web.security.dwr;

import com.serotonin.mango.vo.GetExtendedName;
import org.junit.Assert;
import org.junit.Test;
import utils.mock.TestGetExtendedName;

public class XssBeanConverterUtilsEscapedTest {

    private final GetExtendedName object = new TestGetExtendedName("<script>alert(1)</script>");

    private final GetExtendedName objectEscaped = new TestGetExtendedName("&lt;script&gt;alert(1)&lt;/script&gt;");

    @Test
    public void when_convertObjectEscaped_then_String_escaped() throws ScadaMarshallException {

        //when:
        XssBeanConverterUtils.convertObjectEscaped(object);

        //then:
        Assert.assertEquals(objectEscaped, object);
    }

    @Test
    public void when_convertObjectUnescaped_then_String_unescaped() throws ScadaMarshallException {

        //when:
        XssBeanConverterUtils.convertObjectUnescaped(objectEscaped);

        //then:
        Assert.assertEquals(object, objectEscaped);
    }

}