package com.serotonin.mango.rt.scripting;

import br.org.scadabr.rt.scripting.ContextualizedScriptRT;
import br.org.scadabr.rt.scripting.context.DSCommandsScriptContextObject;
import br.org.scadabr.rt.scripting.context.ScriptContextObject;
import br.org.scadabr.vo.scripting.ContextualizedScriptVO;
import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.RuntimeManager;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.scada_lts.web.beans.ApplicationBeans;
import utils.ScriptTestUtils;
import com.serotonin.mango.rt.dataImage.PointValueCache;
import com.serotonin.mango.vo.permission.Permissions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.scada_lts.dao.DAO;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;
import static utils.ScriptTestUtils.createContext;
import static utils.Scripts.createScriptWithObjectContextDisableDataSource;
import static utils.Scripts.createScriptWithObjectContextEnableDataSource;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DAO.class, PointValueCache.class, Permissions.class,
        ContextualizedScriptRT.class, ScriptContextObject.class,
        Common.class, ApplicationBeans.class})
// resources/org/powermock/extensions/configuration.properties is not working
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class ScriptWithObjectContextEnableDisableDataSourceTest {

    private static final String sourceToChangeXid = "DP_093765";

    private List<IntValuePair> objectContext;

    private RuntimeManager runtimeManager;
    private DSCommandsScriptContextObject scriptContextObject;

    @Before
    public void config() throws Exception {
        objectContext = Arrays.asList(new IntValuePair(2, "ds"));
        runtimeManager = mock(RuntimeManager.class);
        scriptContextObject = mock(DSCommandsScriptContextObject.class);
        ScriptTestUtils.configScriptMock(runtimeManager, scriptContextObject);
    }

    @After
    public void resetMock() {
        reset(scriptContextObject);
        reset(runtimeManager);
    }

    @Test
    public void test_execute_js_with_object_context_enableDataSource() throws Exception {

        //given:
        String script = createScriptWithObjectContextEnableDataSource(sourceToChangeXid);
        ContextualizedScriptVO contextualizedScriptVO = createContext(Collections.emptyList(), objectContext, script);
        ContextualizedScriptRT contextualizedScriptRT = new ContextualizedScriptRT(contextualizedScriptVO);

        //when
        contextualizedScriptRT.execute();

        //then
        verify(scriptContextObject, times(1))
                .enableDataSource(eq(sourceToChangeXid));
        verify(scriptContextObject, times(0))
                .disableDataSource(eq(sourceToChangeXid));
    }

    @Test
    public void test_execute_js_with_object_context_disableDataSource() throws Exception {

        //given:
        String script = createScriptWithObjectContextDisableDataSource(sourceToChangeXid);
        ContextualizedScriptVO contextualizedScriptVO = createContext(Collections.emptyList(), objectContext, script);
        ContextualizedScriptRT contextualizedScriptRT = new ContextualizedScriptRT(contextualizedScriptVO);

        //when
        contextualizedScriptRT.execute();

        //then
        verify(scriptContextObject, times(1))
                .disableDataSource(eq(sourceToChangeXid));
        verify(scriptContextObject, times(0))
                .enableDataSource(eq(sourceToChangeXid));
    }

    @Test
    public void test_execute_js_with_object_context_enableDataSource_disableDataSource() throws Exception {

        //given:
        String script = createScriptWithObjectContextEnableDataSource(sourceToChangeXid) + "\n" +
                createScriptWithObjectContextDisableDataSource(sourceToChangeXid);
        ContextualizedScriptVO contextualizedScriptVO = createContext(Collections.emptyList(), objectContext, script);
        ContextualizedScriptRT contextualizedScriptRT = new ContextualizedScriptRT(contextualizedScriptVO);

        //when
        contextualizedScriptRT.execute();

        //then
        verify(scriptContextObject, times(1))
                .enableDataSource(eq(sourceToChangeXid));
        verify(scriptContextObject, times(1))
                .disableDataSource(eq(sourceToChangeXid));
    }
}
