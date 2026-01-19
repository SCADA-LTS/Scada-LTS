package com.serotonin.mango.rt.dataSource.meta;

import br.org.scadabr.rt.scripting.ContextualizedScriptRT;
import br.org.scadabr.rt.scripting.context.DPCommandsScriptContextObject;
import br.org.scadabr.rt.scripting.context.ScriptContextObject;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.PointValueCache;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mozilla.javascript.ContextFactory;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.dao.DAO;
import org.scada_lts.scripting.SandboxContextFactory;
import org.scada_lts.web.beans.ApplicationBeans;
import utils.ScriptTestUtils;

import javax.script.ScriptException;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DAO.class, PointValueCache.class, Permissions.class,
        ContextualizedScriptRT.class, ScriptContextObject.class,
        ScriptExecutor.class, Common.class, ApplicationBeans.class})
// resources/org/powermock/extensions/configuration.properties is not working
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class JavaClassGrantedScriptExecutorTest {

    private static final double DELTA = .01;

    @BeforeClass
    public static void initGlobal() {
        ContextFactory.initGlobal(new SandboxContextFactory());
    }

    @Before
    public void config() throws Exception {

        RuntimeManager runtimeManager = mock(RuntimeManager.class);
        DPCommandsScriptContextObject scriptContextObject = mock(DPCommandsScriptContextObject.class);
        ScriptTestUtils.configMock(runtimeManager, scriptContextObject);
    }

    @Test
    public void when_execute_js_with_java_PointValueTime() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var pointValueTime = com.serotonin.mango.rt.dataImage.PointValueTime(new com.serotonin.mango.rt.dataImage.types.NumericValue(12345.5),0);" +
                "return pointValueTime.getDoubleValue();", Collections.emptyMap(), 0, DataTypes.NUMERIC, 0);

        //then:
        double result = pointValueTime.getDoubleValue();
        Assert.assertEquals(12345.5, result, DELTA);

    }

    @Test
    public void when_execute_js_with_java_PointValueTime_new() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var pointValueTime = new com.serotonin.mango.rt.dataImage.PointValueTime(new com.serotonin.mango.rt.dataImage.types.NumericValue(12345.5),0);" +
                "return pointValueTime.getDoubleValue();", Collections.emptyMap(), 0, DataTypes.NUMERIC, 0);

        //then:
        double result = pointValueTime.getDoubleValue();
        Assert.assertEquals(12345.5, result, DELTA);

    }

    @Test
    public void when_execute_js_with_java_create_ViewDwr() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var mydwr=new com.serotonin.mango.web.dwr.ViewDwr();" +
                "return 'cde';", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);

        //then:
        String result = pointValueTime.getStringValue();
        Assert.assertEquals("cde", result);
    }

    @Test
    public void when_execute_js_with_java_invoke_method_getLoggedUser_in_ViewDwr() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var mydwr=new com.serotonin.mango.web.dwr.ViewDwr();" +
                "var user=mydwr.getLoggedUser();" +
                "return user + '';", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);

        //then:
        String result = pointValueTime.getStringValue();
        Assert.assertEquals(userName, result);
    }

    @Test
    public void when_execute_js_with_java_invoke_constructor_JdbcTemplate() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = new org.springframework.jdbc.core.JdbcTemplate();" +
                "return t", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);
    }

    @Test
    public void when_execute_js_with_java_invoke_constructor_ExtendedJdbcTemplate() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = new com.serotonin.db.spring.ExtendedJdbcTemplate();" +
                "return t", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);
    }

    @Test
    public void when_execute_js_with_java_invoke_constructor_Byte_with_123() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = new java.lang.Byte(123);" +
                "return t", Collections.emptyMap(), 0, DataTypes.NUMERIC, 0);

        //then:
        double result = pointValueTime.getDoubleValue();
        Assert.assertEquals(123, result, DELTA);
    }

    @Test
    public void when_execute_js_with_java_invoke_constructor_Integer_with_123() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = new java.lang.Integer(123);" +
                "return t", Collections.emptyMap(), 0, DataTypes.NUMERIC, 0);

        //then:
        double result = pointValueTime.getDoubleValue();
        Assert.assertEquals(123, result, DELTA);
    }

    @Test
    public void when_execute_js_with_java_invoke_constructor_Long_with_123() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = new java.lang.Long(123);" +
                "return t", Collections.emptyMap(), 0, DataTypes.NUMERIC, 0);

        //then:
        double result = pointValueTime.getDoubleValue();
        Assert.assertEquals(123, result, DELTA);
    }

    @Test
    public void when_execute_js_with_java_invoke_constructor_Double_with_123() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = new java.lang.Double(123);" +
                "return t", Collections.emptyMap(), 0, DataTypes.NUMERIC, 0);

        //then:
        double result = pointValueTime.getDoubleValue();
        Assert.assertEquals(123, result, DELTA);
    }

    @Test
    public void when_execute_js_with_java_invoke_constructor_Float_with_123() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = new java.lang.Float(123);" +
                "return t", Collections.emptyMap(), 0, DataTypes.NUMERIC, 0);

        //then:
        double result = pointValueTime.getDoubleValue();
        Assert.assertEquals(123, result, DELTA);
    }

    @Test
    public void when_execute_js_with_java_invoke_constructor_String_with_abc() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = new java.lang.String(\"abc\");" +
                "return t", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);

        //then:
        String result = pointValueTime.getStringValue();
        Assert.assertEquals("abc", result);
    }

    @Test
    public void when_execute_js_with_java_invoke_constructor_Exception() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = new java.lang.Exception(\"abc\");" +
                "return t", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);
    }

}
