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
public class JavaClassDeniedScriptExecutorTest {

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

    @Test(expected = ScriptException.class)
    public void when_execute_js_with_java_invoke_constructor_ProcessBuilder_then_ScriptException() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = new java.lang.ProcessBuilder(\"bash\",\"-c\",\"echo POC > /tmp/rce_should_not_be_possible\");" +
                "return t", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);
    }

    @Test(expected = ScriptException.class)
    public void when_execute_js_with_java_invoke_method_ProcessBuilder_start_then_ScriptException() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = new java.lang.ProcessBuilder(\"bash\",\"-c\",\"echo POC > /tmp/rce_should_not_be_possible\").start();" +
                "return t", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);
    }

    @Test(expected = ScriptException.class)
    public void when_execute_js_with_java_invoke_constructor_ClassLoader_then_ScriptException() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = new java.lang.ClassLoader();" +
                "return t", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);
    }

    @Test(expected = ScriptException.class)
    public void when_execute_js_with_java_invoke_constructor_Field_then_ScriptException() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = new java.lang.reflect.Field();" +
                "return t", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);
    }

    @Test(expected = ScriptException.class)
    public void when_execute_js_with_java_invoke_constructor_ProcessImpl_then_ScriptException() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = new java.lang.ProcessImpl();" +
                "return t", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);
    }

    @Test(expected = ScriptException.class)
    public void when_execute_js_with_java_invoke_constructor_Process_then_ScriptException() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = new java.lang.Process();" +
                "return t", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);
    }

    @Test(expected = ScriptException.class)
    public void when_execute_js_with_java_invoke_constructor_Thread_then_ScriptException() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = new java.lang.Thread();" +
                "return t", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);
    }

    @Test(expected = ScriptException.class)
    public void when_execute_js_with_java_invoke_constructor_ThreadGroup_then_ScriptException() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = new java.lang.ThreadGroup();" +
                "return t", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);
    }

    @Test(expected = ScriptException.class)
    public void when_execute_js_with_java_invoke_constructor_ThreadLocal_then_ScriptException() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = new java.lang.ThreadLocal();" +
                "return t", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);
    }

    @Test(expected = ScriptException.class)
    public void when_execute_js_with_java_invoke_System_out_println_then_ScriptException() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "java.lang.System.out.println(\"abc\");" +
                "return 0", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);
    }

    @Test(expected = ScriptException.class)
    public void when_execute_js_with_java_invoke_method_Runtime_getRuntime_then_ScriptException() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        Runtime.getRuntime().gc();

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = java.lang.Runtime.getRuntime();" +
                "return t", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);
    }

    @Test(expected = ScriptException.class)
    public void when_execute_js_with_java_invoke_method_Runtime_gc_then_ScriptException() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = java.lang.Runtime.getRuntime();" +
                "t.gc();" +
                "return 0", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);
    }

    @Test(expected = ScriptException.class)
    public void when_execute_js_with_java_invoke_method_getRuntime_gc_then_ScriptException() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = java.lang.Runtime.getRuntime().gc();" +
                "return 0", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);
    }

    @Test(expected = ScriptException.class)
    public void when_execute_js_with_java_invoke_method_Runtime_exec_then_ScriptException() throws Exception {

        //given:
        ScriptExecutor scriptExecutor = new ScriptExecutor();
        String userName = "user mock";

        mockStatic(Common.class);
        User user = new User();
        user.setUsername(userName);
        when(Common.getUser()).thenReturn(user);

        //when:
        PointValueTime pointValueTime = scriptExecutor.execute("" +
                "var t = java.lang.Runtime.getRuntime();" +
                "t.exec(\"cmd dir\");" +
                "return 0;", Collections.emptyMap(), 0, DataTypes.ALPHANUMERIC, 0);
    }
}
