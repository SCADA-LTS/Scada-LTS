package com.serotonin.mango.util;

import br.org.scadabr.vo.scripting.ScriptVO;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.view.component.ScriptComponent;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.link.PointLinkVO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.text.MessageFormat;

public final class LoggingUtils {

    private LoggingUtils() {}

    public static String dataPointInfo(DataPointVO dataPoint) {
        if(dataPoint == null)
            return "";
        String dataPointInfo = "datapoint: {0} (id: {1}, xid: {2})";
        return MessageFormat.format(dataPointInfo, dataPoint.getName(), String.valueOf(dataPoint.getId()), dataPoint.getXid());
    }

    public static String dataSourceInfo(DataSourceRT dataSource) {
        if(dataSource == null)
            return "";
        String dataSourceInfo = "datasource: {0} (id: {1})";
        return MessageFormat.format(dataSourceInfo, dataSource.getName(), String.valueOf(dataSource.getId()));
    }

    public static String pointLinkInfo(PointLinkVO pointLink) {
        if(pointLink == null)
            return "";
        String dataPointInfo = "pointLink: {0} (id: {1}, xid: {2})";
        return MessageFormat.format(dataPointInfo, pointLink.getTypeKey(), String.valueOf(pointLink.getId()), pointLink.getXid());
    }

    public static String scriptInfo(ScriptVO<?> script) {
        if(script == null)
            return "";
        String dataPointInfo = "script: {0} (id: {1}, xid: {2})";
        return MessageFormat.format(dataPointInfo, script.getName(), String.valueOf(script.getId()), script.getXid());
    }

    public static String exceptionInfo(Exception ex) {
        if(ex == null)
            return "";
        String exceptionInfo = "exception: {0} (msg: {1})";
        return MessageFormat.format(exceptionInfo, ex.getClass().getSimpleName(), ex.getMessage());
    }

    public static String scriptComponentInfo(ScriptComponent pointComponent) {
        if(pointComponent == null)
            return "";
        String pointComponentInfo = "scriptComponent: {0} (id: {1}, type: {2}, x: {3}, y: {4})";
        return MessageFormat.format(pointComponentInfo, pointComponent.getDefName(),
                pointComponent.getId(), pointComponent.getClass().getSimpleName(), pointComponent.getX(),
                pointComponent.getY());
    }

    public static String identifierInfo(ScadaObjectIdentifier identifier, String object) {
        if(identifier == null)
            return "";
        String viewInfo = object + ": {0} (id: {1}, xid: {2})";
        return MessageFormat.format(viewInfo, identifier.getName(), identifier.getId(), identifier.getXid());
    }

    public static String eventInfo(EventInstance event) {
        if(event == null)
            return "";
        String viewInfo = "event: {0} (id: {1}, active: {2}, type: {3})";
        if(event.getMessage() != null)
            return MessageFormat.format(viewInfo, event.getMessage().getLocalizedMessage(Common.getBundle()), event.getId(), event.getActiveTimestamp(), event.getEventType());
        return MessageFormat.format(viewInfo, "no message", event.getId(), event.getActiveTimestamp(), event.getEventType());
    }

    public static String eventHandlerInfo(EventHandlerVO eventHandler) {
        if(eventHandler == null)
            return "";
        String viewInfo =  "eventHandler: {0} (id: {1}, xid: {2}, type: {3}, script active id: {4}, script inactive id: {5})";
        return MessageFormat.format(viewInfo, eventHandler.getAlias(), eventHandler.getId(), eventHandler.getXid(), eventHandler.getHandlerType(),
                eventHandler.getActiveScriptCommand(), eventHandler.getInactiveScriptCommand());
    }
}
