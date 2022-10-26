package utils;

import br.org.scadabr.rt.scripting.context.ScriptContextObject;
import br.org.scadabr.vo.scripting.ContextualizedScriptVO;
import com.serotonin.db.IntValuePair;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.DataPointVO.LoggingTypes;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualPointLocatorVO;
import com.serotonin.mango.vo.permission.Permissions;
import utils.mock.MockUtils;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

public class ScriptTestUtils {

    public static ContextualizedScriptVO createContext(List<IntValuePair> pointContext,
                                                       List<IntValuePair> objectContext,
                                                       String script) {
        ContextualizedScriptVO contextualizedScriptVO = new ContextualizedScriptVO();
        contextualizedScriptVO.setPointsOnContext(pointContext);
        contextualizedScriptVO.setObjectsOnContext(objectContext);
        contextualizedScriptVO.setScript(script);
        return contextualizedScriptVO;
    }


    public static DataPointRT createDataPointRT(int pointFromContextId, MangoValue mangoValue) {
        PointLocatorVO locatorFromContext = new VirtualPointLocatorVO();
        ((VirtualPointLocatorVO) locatorFromContext).setDataTypeId(mangoValue.getDataType());

        DataPointVO pointFromContextVO = new DataPointVO(LoggingTypes.ON_CHANGE);
        pointFromContextVO.setPointLocator(locatorFromContext);
        pointFromContextVO.setEventDetectors(Collections.emptyList());
        pointFromContextVO.setId(pointFromContextId);
        pointFromContextVO.setName("point test");

        DataPointRT pointFromContext = new DataPointRT(pointFromContextVO, null);
        pointFromContext.initialize();
        pointFromContext.setPointValue(new PointValueTime(mangoValue,0), null);

        return pointFromContext;
    }

    public static void configMock(RuntimeManager runtimeManager, ScriptContextObject scriptContextObject) throws Exception {
        mockStatic(Permissions.class);
        String userName = "user mock";
        User user = new User();
        user.setUsername(userName);
        MockUtils.configMock(runtimeManager, user);

        mockStatic(ScriptContextObject.Type.class);
        ScriptContextObject.Type type = mock(ScriptContextObject.Type.class);
        when(ScriptContextObject.Type.valueOf(anyInt()))
                .thenReturn(type);
        when(type.createScriptContextObject()).thenReturn(scriptContextObject);
    }

}
