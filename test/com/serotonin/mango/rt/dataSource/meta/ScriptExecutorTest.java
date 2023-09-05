package com.serotonin.mango.rt.dataSource.meta;

import br.org.scadabr.rt.scripting.ContextualizedScriptRT;
import br.org.scadabr.rt.scripting.context.DPCommandsScriptContextObject;
import br.org.scadabr.rt.scripting.context.ScriptContextObject;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.RuntimeManager;
import org.junit.*;
import org.mozilla.javascript.ContextFactory;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.scada_lts.scripting.SandboxContextFactory;
import org.scada_lts.web.beans.ApplicationBeans;
import utils.ScriptTestUtils;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.IDataPoint;
import com.serotonin.mango.rt.dataImage.PointValueCache;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.*;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.dao.DAO;

import javax.script.ScriptException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
public class ScriptExecutorTest {

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
    public void test_execute_js_with_return_state_double() throws ScriptException, ResultTypeException {
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        PointValueTime pointValueTime = scriptExecutor.execute("return 2.2;", Collections.emptyMap(),
                0, DataTypes.NUMERIC, 0);
        double result = pointValueTime.getDoubleValue();
        Assert.assertEquals(2.2, result, DELTA);
    }

    @Test
    public void test_execute_js_with_return_state_int() throws ScriptException, ResultTypeException {
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        PointValueTime pointValueTime = scriptExecutor.execute("return 2;", Collections.emptyMap(),
                0, DataTypes.MULTISTATE, 0);
        int result = pointValueTime.getIntegerValue();
        Assert.assertEquals(2, result);
    }

    @Test
    public void test_execute_js_with_return_state_string() throws ScriptException, ResultTypeException {
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        PointValueTime pointValueTime = scriptExecutor.execute("return 'abc';", Collections.emptyMap(),
                0, DataTypes.ALPHANUMERIC, 0);
        String result = pointValueTime.getStringValue();
        Assert.assertEquals("abc", result);
    }

    @Test
    public void test_execute_js_with_return_state_boolean() throws ScriptException, ResultTypeException {
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        PointValueTime pointValueTime = scriptExecutor.execute("return true;", Collections.emptyMap(),
                0, DataTypes.BINARY, 0);
        boolean result = pointValueTime.getBooleanValue();
        Assert.assertEquals(true, result);
    }

    @Test
    public void test_execute_js_with_point_context_binary() throws Exception {

        //given:
        DataPointRT p1 = ScriptTestUtils.createDataPointRT(0, new BinaryValue(true));
        Map<String, IDataPoint> context = new HashMap<>();
        context.put("p1", p1);

        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "if (p1.value) return 5; " +
                "return 3;", context, 0, DataTypes.MULTISTATE, 0);

        //then:
        int result = pointValueTime.getIntegerValue();
        Assert.assertEquals(5, result);
    }

    @Test(expected = ScriptException.class)
    public void test_execute_js_with_broken_point_context_binary() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "if (p1.value) return 5; " +
                "return 3;", Collections.emptyMap(), 0, DataTypes.MULTISTATE, 0);

    }

    @Test
    public void test_execute_js_with_point_context_numeric() throws Exception {

        //given:
        DataPointRT p1 = ScriptTestUtils.createDataPointRT(0, new NumericValue(1.1));

        Map<String, IDataPoint> context = new HashMap<>();
        context.put("p1", p1);

        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "if (p1.value == 1.1) return 5; " +
                "return 3;", context, 0, DataTypes.NUMERIC, 0);

        //then:
        int result = pointValueTime.getIntegerValue();
        Assert.assertEquals(5, result);
    }

    @Test
    public void test_execute_js_with_point_context_numeric_toFixed() throws Exception {

        //given:
        DataPointRT p1 = ScriptTestUtils.createDataPointRT(0, new NumericValue(1.12345));

        Map<String, IDataPoint> context = new HashMap<>();
        context.put("p1", p1);

        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute(
                "return p1.value.toFixed(2);", context, 0, DataTypes.ALPHANUMERIC, 0);

        //then:
        String result = pointValueTime.getStringValue();
        Assert.assertEquals("1.12", result);
    }

    @Test
    public void test_execute_js_with_point_context_alphanumeric() throws Exception {

        //given:
        DataPointRT p1 = ScriptTestUtils.createDataPointRT(0, new AlphanumericValue("alphanumeric test"));
        Map<String, IDataPoint> context = new HashMap<>();
        context.put("p1", p1);

        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "if (p1.value == 'alphanumeric test') return 5; " +
                "return 3;", context, 0, DataTypes.NUMERIC, 0);

        //then:
        int result = pointValueTime.getIntegerValue();
        Assert.assertEquals(5, result);
    }


    @Test
    public void test_execute_js_with_point_context_multistatic() throws Exception {

        //given:
        DataPointRT p1 = ScriptTestUtils.createDataPointRT(0, new MultistateValue(1234));

        Map<String, IDataPoint> context = new HashMap<>();
        context.put("p1", p1);

        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "if (p1.value == 1234) return 5; " +
                "return 3;", context, 0, DataTypes.NUMERIC, 0);

        //then:
        int result = pointValueTime.getIntegerValue();
        Assert.assertEquals(5, result);
    }


    @Test
    public void test_execute_js_with_java_PointValueTime() throws Exception {

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
    public void test_execute_js_with_java_PointValueTime_new() throws Exception {

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
    public void test_execute_js_with_java_create_ViewDwr() throws Exception {

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
    public void test_execute_js_with_java_invoke_method_getLoggedUser_in_ViewDwr() throws Exception {

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
    public void test_execute_js_with_point_context_alphanumeric_return_value() throws Exception {

        //given:
        String value = "alphanumeric test";
        DataPointRT p1 = ScriptTestUtils.createDataPointRT(0, new AlphanumericValue(value));
        Map<String, IDataPoint> context = new HashMap<>();
        context.put("p1", p1);

        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute(
                "return p1.value;", context, 0, DataTypes.ALPHANUMERIC, 0);

        //then:
        String result = pointValueTime.getStringValue();
        Assert.assertEquals(value, result);
    }

    @Test
    public void test_execute_js_with_point_context_alphanumeric_return_value_plus_value() throws Exception {

        //given:
        String value = "alphanumeric test";
        String expected = value + value;
        DataPointRT p1 = ScriptTestUtils.createDataPointRT(0, new AlphanumericValue(value));
        Map<String, IDataPoint> context = new HashMap<>();
        context.put("p1", p1);

        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute(
                "return p1.value + p1.value;", context, 0, DataTypes.ALPHANUMERIC, 0);

        //then:
        String result = pointValueTime.getStringValue();
        Assert.assertEquals(expected, result);
    }

    @Test
    public void test_execute_js_with_point_context_alphanumeric_return_value_plus_abc() throws Exception {

        //given:
        String value = "alphanumeric test";
        String diff = "_abc";
        String expected = "alphanumeric test" + diff;
        DataPointRT p1 = ScriptTestUtils.createDataPointRT(0, new AlphanumericValue(value));
        Map<String, IDataPoint> context = new HashMap<>();
        context.put("p1", p1);

        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute(
                "return p1.value + '" + diff + "';", context, 0, DataTypes.ALPHANUMERIC, 0);

        //then:
        String result = pointValueTime.getStringValue();
        Assert.assertEquals(expected, result);
    }

    @Test
    public void test_execute_js_with_point_context_multistate_return_value() throws Exception {

        //given:
        int value = 12;
        DataPointRT p1 = ScriptTestUtils.createDataPointRT(0, new MultistateValue(value));
        Map<String, IDataPoint> context = new HashMap<>();
        context.put("p1", p1);

        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute(
                "return p1.value;", context, 0, DataTypes.MULTISTATE, 0);

        //then:
        int result = pointValueTime.getIntegerValue();
        Assert.assertEquals(value, result);
    }

    @Test
    public void test_execute_js_with_point_context_multistate_return_value_plus_value() throws Exception {

        //given:
        int value = 12;
        int expected = value + value;
        DataPointRT p1 = ScriptTestUtils.createDataPointRT(0, new MultistateValue(value));
        Map<String, IDataPoint> context = new HashMap<>();
        context.put("p1", p1);

        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute(
                "return p1.value + p1.value;", context, 0, DataTypes.MULTISTATE, 0);

        //then:
        int result = pointValueTime.getIntegerValue();
        Assert.assertEquals(expected, result);
    }

    @Test
    public void test_execute_js_with_point_context_multistate_return_value_plus_4() throws Exception {

        //given:
        int value = 12;
        int diff = 4;
        int expected = value + diff;
        DataPointRT p1 = ScriptTestUtils.createDataPointRT(0, new MultistateValue(value));
        Map<String, IDataPoint> context = new HashMap<>();
        context.put("p1", p1);

        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute(
                "return p1.value + " + diff + ";", context, 0, DataTypes.MULTISTATE, 0);

        //then:
        int result = pointValueTime.getIntegerValue();
        Assert.assertEquals(expected, result);
    }

    @Test
    public void test_execute_js_with_point_context_numeric_return_value() throws Exception {

        //given:
        double value = 12.12;
        DataPointRT p1 = ScriptTestUtils.createDataPointRT(0, new NumericValue(value));
        Map<String, IDataPoint> context = new HashMap<>();
        context.put("p1", p1);

        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute(
                "return p1.value;", context, 0, DataTypes.NUMERIC, 0);

        //then:
        double result = pointValueTime.getDoubleValue();
        Assert.assertEquals(value, result, DELTA);
    }

    @Test
    public void test_execute_js_with_point_context_numeric_return_value_plus_value() throws Exception {

        //given:
        double value = 12.12;
        double expected = value + value;
        DataPointRT p1 = ScriptTestUtils.createDataPointRT(0, new NumericValue(value));
        Map<String, IDataPoint> context = new HashMap<>();
        context.put("p1", p1);

        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute(
                "return p1.value + p1.value;", context, 0, DataTypes.NUMERIC, 0);

        //then:
        double result = pointValueTime.getDoubleValue();
        Assert.assertEquals(expected, result, DELTA);
    }

    @Test
    public void test_execute_js_with_point_context_numeric_return_value_plus_10_1() throws Exception {

        //given:
        double value = 12.12;
        double diff = 10.1;
        double expected = value + diff;
        DataPointRT p1 = ScriptTestUtils.createDataPointRT(0, new NumericValue(value));
        Map<String, IDataPoint> context = new HashMap<>();
        context.put("p1", p1);

        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute(
                "return p1.value + " + diff + ";", context, 0, DataTypes.NUMERIC, 0);

        //then:
        double result = pointValueTime.getDoubleValue();
        Assert.assertEquals(expected, result, DELTA);
    }

    @Test
    public void test_execute_js_with_point_context_binary_return_value() throws Exception {

        //given:
        boolean value = true;
        DataPointRT p1 = ScriptTestUtils.createDataPointRT(0, new BinaryValue(value));
        Map<String, IDataPoint> context = new HashMap<>();
        context.put("p1", p1);

        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute(
                "return p1.value;", context, 0, DataTypes.BINARY, 0);

        //then:
        boolean result = pointValueTime.getBooleanValue();
        Assert.assertEquals(value, result);
    }

    @Test
    public void test_execute_js_with_point_context_binary_return_value_or_value() throws Exception {

        //given:
        boolean value = true;
        DataPointRT p1 = ScriptTestUtils.createDataPointRT(0, new BinaryValue(value));
        Map<String, IDataPoint> context = new HashMap<>();
        context.put("p1", p1);

        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute(
                "return p1.value || p1.value;", context, 0, DataTypes.BINARY, 0);

        //then:
        boolean result = pointValueTime.getBooleanValue();
        Assert.assertEquals(value, result);
    }

    @Test
    public void test_execute_js_with_point_context_binary_return_value_or_false() throws Exception {

        //given:
        boolean value = true;
        DataPointRT p1 = ScriptTestUtils.createDataPointRT(0, new BinaryValue(value));
        Map<String, IDataPoint> context = new HashMap<>();
        context.put("p1", p1);

        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute(
                "return p1.value || false;", context, 0, DataTypes.BINARY, 0);

        //then:
        boolean result = pointValueTime.getBooleanValue();
        Assert.assertEquals(value, result);
    }

    @Test
    public void test_execute_js_with_point_context_binary_return_value_and_false() throws Exception {

        //given:
        boolean value = true;
        boolean expected = value && false;
        DataPointRT p1 = ScriptTestUtils.createDataPointRT(0, new BinaryValue(value));
        Map<String, IDataPoint> context = new HashMap<>();
        context.put("p1", p1);

        ScriptExecutor scriptExecutor = new ScriptExecutor();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute(
                "return p1.value && false;", context, 0, DataTypes.BINARY, 0);

        //then:
        boolean result = pointValueTime.getBooleanValue();
        Assert.assertEquals(expected, result);
    }
}
