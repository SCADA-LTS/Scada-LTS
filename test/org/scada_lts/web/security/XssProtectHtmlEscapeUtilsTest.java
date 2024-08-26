package org.scada_lts.web.security;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;


@RunWith(Parameterized.class)
public class XssProtectHtmlEscapeUtilsTest {

    @Parameterized.Parameters(name = "{index}: content: {0}, expected: {1}")
    public static List<Object[]> data() {
        List<Object[]> datas = new ArrayList<>();

        datas.add(new Object[] {"\\n - \n", "\\n - &#10;"});
        datas.add(new Object[] {"\\r - \r", "\\r - &#13;"});

        datas.add(new Object[] {"\\t - \t", "\\t - &emsp;"});
        datas.add(new Object[] {"\\u000B - \u000B", "\\u000B - &#11;"});
        datas.add(new Object[] {"\\f - \f", "\\f - &#12;"});
        datas.add(new Object[] {"\\u001C - \u001C", "\\u001C - &#28;"});
        datas.add(new Object[] {"\\u001D - \u001D", "\\u001D - &#29;"});
        datas.add(new Object[] {"\\u001E - \u001E", "\\u001E - &#30;"});
        datas.add(new Object[] {"\\u001F - \u001F", "\\u001F - &#31;"});

        datas.add(new Object[] {"<script>alert('abc!')</script>", "&lt;script&gt;alert(&#39;abc!&#39;)&lt;/script&gt;"});
        datas.add(new Object[] {"body {\n" +
                "\tfont-size: 62.5%;\n" +
                "}\n" +
                "\n" +
                "table {\n" +
                "\tfont-size: 1em;\n" +
                "}", "body {&#10;&emsp;font-size: 62.5%;&#10;}&#10;&#10;table {&#10;&emsp;font-size: 1em;&#10;}"});
        datas.add(new Object[] {"<img src=x onerror=document.location>", "&lt;img src=x onerror=document.location&gt;"});
        datas.add(new Object[] {"<img src=x onerror=alert(document.location)>", "&lt;img src=x onerror=alert(document.location)&gt;"});
        datas.add(new Object[] {"alert(document.location)", "alert(document.location)"});

        return datas;
    }

    private final String content;
    private final String expected;

    public XssProtectHtmlEscapeUtilsTest(String content, String expected) {
        this.content = content;
        this.expected = expected;
    }

    @Test
    public void escape() {

        //when
        String result = XssProtectHtmlEscapeUtils.escape(content);

        //then:
        Assert.assertEquals(expected, result);
    }
}