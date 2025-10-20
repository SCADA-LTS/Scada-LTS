package org.scada_lts.web.dwr.security.utils;

import com.serotonin.mango.vo.GetExtendedName;
import org.junit.Assert;
import org.junit.Test;
import utils.mock.TestGetExtendedName;

public class XssBeanConverterUtilsTest {

    private final GetExtendedName object = new TestGetExtendedName("<script>alert(1)</script>");

    private final GetExtendedName objectEscaped = new TestGetExtendedName("&lt;script&gt;alert(1)&lt;/script&gt;");

    @Test
    public void convertObjectEscaped() {

        //when:
        XssBeanConverterUtils.convertObjectEscaped(object);

        //then:
        Assert.assertEquals(objectEscaped, object);
    }

    @Test
    public void convertObjectUnescaped() {
        //when:
        XssBeanConverterUtils.convertObjectUnescaped(objectEscaped);

        //then:
        Assert.assertEquals(object, objectEscaped);
    }
}