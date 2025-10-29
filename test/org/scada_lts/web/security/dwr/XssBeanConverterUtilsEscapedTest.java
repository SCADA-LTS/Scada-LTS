package org.scada_lts.web.security.dwr;

import br.org.scadabr.vo.scripting.ContextualizedScriptVO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class XssBeanConverterUtilsEscapedTest {

    private ContextualizedScriptVO objectUnescaped;
    private ContextualizedScriptVO objectEscaped;

    @Before
    public void config() {
        objectUnescaped = new ContextualizedScriptVO();
        objectUnescaped.setScript("<script>alert(1)</script>");
        objectEscaped = new ContextualizedScriptVO();
        objectEscaped.setScript("&lt;script&gt;alert(1)&lt;/script&gt;");
    }

    @Test
    public void when_convertObjectEscaped_then_Object_escaped() throws ScadaMarshallException {

        //when:
        Object result = XssBeanConverterUtils.convertObjectEscaped(objectUnescaped);

        //then:
        Assert.assertEquals(objectEscaped, result);
    }

    @Test
    public void when_convertObjectUnescaped_then_Object_unescaped() throws ScadaMarshallException {

        //when:
        Object result = XssBeanConverterUtils.convertObjectUnescaped(objectEscaped);

        //then:
        Assert.assertEquals(objectUnescaped, result);
    }

    @Test
    public void when_convertObjectUnescaped_then_not_same_arg() throws ScadaMarshallException {

        //when:
        Object result = XssBeanConverterUtils.convertObjectUnescaped(objectEscaped);

        //then:
        Assert.assertNotSame(objectEscaped, result);
    }

    @Test
    public void when_convertObjectEscaped_then_not_same_arg() throws ScadaMarshallException {

        //when:
        Object result = XssBeanConverterUtils.convertObjectEscaped(objectUnescaped);

        //then:
        Assert.assertNotSame(objectUnescaped, result);
    }

}