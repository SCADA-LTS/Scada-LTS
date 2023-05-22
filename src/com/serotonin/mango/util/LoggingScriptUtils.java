package com.serotonin.mango.util;

import br.org.scadabr.vo.scripting.ScriptVO;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.view.component.ScriptComponent;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.link.PointLinkVO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.text.MessageFormat;
import java.util.Map;

import static com.serotonin.mango.util.LoggingUtils.*;

public final class LoggingScriptUtils {

    public static final String VIEW_IDENTIFIER = "viewIdentifier";

    private LoggingScriptUtils() {}

    public static String infoErrorExecutionScript(Exception e,
                                                  Map<String, Object> model,
                                                  ScriptComponent scriptComponent) {
        if(model != null && !model.isEmpty()) {
            Object identifier = model.get(LoggingScriptUtils.VIEW_IDENTIFIER);
            if (identifier instanceof ScadaObjectIdentifier)
                return infoErrorExecutionScript(e, (ScadaObjectIdentifier) identifier, scriptComponent);
            else
                return infoErrorExecutionScript(e, scriptComponent);
        }
        return "[no model in context]";
    }

    public static String infoErrorExecutionScript(Map<String, Object> model,
                                                  ScriptComponent scriptComponent) {
        return infoErrorExecutionScript(null, model, scriptComponent);
    }

    public static String infoErrorExecutionScript(Exception e,
                                                  DataPointRT dataPoint,
                                                  DataSourceRT dataSource) {
        String context = generateContext(dataPoint, dataSource);
        return infoErrorExecutionScript(e, context);
    }

    public static String infoErrorInitializationScript(Exception e,
                                                  DataPointRT dataPoint,
                                                  DataSourceRT dataSource) {
        String context = generateContext(dataPoint, dataSource);
        return infoErrorInitializationScript(e, context);
    }

    public static String infoErrorExecutionScript(Exception e,
                                                  ScadaObjectIdentifier viewIdentifier,
                                                  ScriptComponent scriptComponent) {
        String context = generateContext(viewIdentifier, scriptComponent);
        return infoErrorExecutionScript(e, context);
    }

    public static String infoErrorExecutionScript(Exception e,
                                                  ScriptComponent scriptComponent) {
        String context = generateContext(null, scriptComponent);
        return infoErrorExecutionScript(e, context);
    }

    public static String infoErrorExecutionScript(Exception e,
                                                  PointLinkVO vo,
                                                  DataPointRT target,
                                                  DataPointRT source) {
        String context = generateContext(vo, target, source);

        return infoErrorExecutionScript(e, context);
    }

    public static String infoErrorExecutionScript(Exception e, ScriptVO<?> vo) {
        String context = generateContext(vo);
        return infoErrorExecutionScript(e, context);
    }

    public static String infoErrorInitializationScript(Exception e, EventHandlerVO handler, EventInstance event) {
        String context = generateContext(event, handler);
        return infoErrorInitializationScript(e, context);
    }

    public static String infoErrorExecutionScript(Exception e, String context) {
        String info = "Problem with executing script: context: {0} {1}";
        return info(e, context, info);
    }

    public static String infoErrorInitializationScript(Exception e, String context) {
        String info = "Problem with initialization script: context: {0} {1}";
        return info(e, context, info);
    }

    public static String generateContext(DataPointRT dataPoint, DataSourceRT dataSource) {
        String context;
        if(dataSource != null) {
            context = dataSourceInfo(dataSource);
        } else {
            context = "[no dataSource in context]";
        }

        if(dataPoint != null && dataPoint.getVO() != null) {
            DataPointVO dataPointVO = dataPoint.getVO();
            context += ", " + dataPointInfo(dataPointVO);
        }
        return context;
    }

    private static String info(Exception e, String context, String info) {
        if (e != null) {
            return MessageFormat.format(info, context, ", " + exceptionInfo(e));
        }
        return MessageFormat.format(info, context, "");
    }

    private static String generateContext(ScadaObjectIdentifier view, ScriptComponent scriptComponent) {
        String context;
        if(view != null) {
            context = identifierInfo(view, "view");
        } else {
            context = "[no view in context]";
        }

        if(scriptComponent != null) {
            context += ", " + scriptComponentInfo(scriptComponent);
        }

        if(scriptComponent != null && scriptComponent.tgetDataPoint() != null) {
            context += ", " + dataPointInfo(scriptComponent.tgetDataPoint());
        }

        return context;
    }

    private static String generateContext(PointLinkVO vo, DataPointRT target, DataPointRT source) {
        String context;
        if(vo != null) {
            context = pointLinkInfo(vo);
        } else {
            context = "[no pointLink in context]";
        }

        if(target != null && target.getVO() != null) {
            context += ", target: " + dataPointInfo(target.getVO());
        }

        if(source != null && source.getVO() != null) {
            context += ", source: " + dataPointInfo(source.getVO());
        }
        return context;
    }

    private static String generateContext(ScriptVO<?> vo) {
        String context = "";

        if(vo != null) {
            context = scriptInfo(vo);
        }
        return context;
    }

    public static String generateContext(EventInstance event, EventHandlerVO handler) {
        String context;
        if(event != null) {
            context = eventInfo(event);
        } else {
            context = "[no event in context]";
        }

        if(handler != null) {
            context += ", " + eventHandlerInfo(handler);
        }
        return context;
    }
}
