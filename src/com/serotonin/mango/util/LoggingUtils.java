package com.serotonin.mango.util;

import br.org.scadabr.vo.scripting.ScriptVO;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.view.component.ScriptComponent;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.link.PointLinkVO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.text.MessageFormat;

public final class LoggingUtils {

    private LoggingUtils() {}

    public static String dataPointInfo(DataPointVO dataPoint) {
        if(dataPoint == null)
            return "";
        String info = "datapoint: {0} (id: {1}, xid: {2}, dataSourceId: {3}, dataSourceName: {4})";
        return MessageFormat.format(info, dataPoint.getName(), String.valueOf(dataPoint.getId()),
                dataPoint.getXid(), dataPoint.getDataSourceId(), dataPoint.getDataSourceName());
    }

    public static String dataSourceInfo(DataSourceRT dataSource) {
        if(dataSource == null)
            return "";
        DataSourceVO<?> dataSourceVO = dataSource.getVO();
        return dataSourceInfo(dataSourceVO);
    }

    public static String dataSourceInfo(DataSourceVO<?> dataSource) {
        if(dataSource == null)
            return "";
        String info = "datasource: {0} (id: {1}, xid: {2}, type: {3})";
        return MessageFormat.format(info, dataSource.getName(), String.valueOf(dataSource.getId()), dataSource.getXid(), dataSource.getType());
    }

    public static String pointLinkInfo(PointLinkVO pointLink) {
        if(pointLink == null)
            return "";
        String info = "pointLink: {0} (id: {1}, xid: {2})";
        return MessageFormat.format(info, pointLink.getTypeKey(), String.valueOf(pointLink.getId()), pointLink.getXid());
    }

    public static String scriptInfo(ScriptVO<?> script) {
        if(script == null)
            return "";
        String info = "script: {0} (id: {1}, xid: {2})";
        return MessageFormat.format(info, script.getName(), String.valueOf(script.getId()), script.getXid());
    }

    public static String exceptionInfo(Exception ex) {
        if(ex == null)
            return "";
        String info = "exception: {0} (msg: {1})";
        return MessageFormat.format(info, ex.getClass().getSimpleName(), ex.getMessage());
    }

    public static String scriptComponentInfo(ScriptComponent pointComponent) {
        if(pointComponent == null)
            return "";
        String info = "scriptComponent: {0} (id: {1}, type: {2}, x: {3}, y: {4})";
        return MessageFormat.format(info, pointComponent.getDefName(),
                pointComponent.getId(), pointComponent.getClass().getSimpleName(), pointComponent.getX(),
                pointComponent.getY());
    }

    public static String identifierInfo(ScadaObjectIdentifier identifier, String object) {
        if(identifier == null)
            return "";
        String info = object + ": {0} (id: {1}, xid: {2})";
        return MessageFormat.format(info, identifier.getName(), identifier.getId(), identifier.getXid());
    }

    public static String eventInfo(EventInstance event) {
        if(event == null)
            return "";
        String info = "event: {0} (id: {1}, active: {2}, type: {3})";
        if(event.getMessage() != null)
            return MessageFormat.format(info, event.getMessage().getLocalizedMessage(Common.getBundle()), event.getId(), event.getActiveTimestamp(), event.getEventType());
        return MessageFormat.format(info, "no message", event.getId(), event.getActiveTimestamp(), event.getEventType());
    }

    public static String eventHandlerInfo(EventHandlerVO eventHandler) {
        if(eventHandler == null)
            return "";
        String info =  "eventHandler: {0} (id: {1}, xid: {2}, type: {3}, script active id: {4}, script inactive id: {5})";
        return MessageFormat.format(info, eventHandler.getAlias(), eventHandler.getId(), eventHandler.getXid(), eventHandler.getHandlerType(),
                eventHandler.getActiveScriptCommand(), eventHandler.getInactiveScriptCommand());
    }

    public static String pointValueTimeInfo(PointValueTime pointValueTime, SetPointSource source) {
        String info = "pointValueTime: {0} (source: {1})";
        if(source != null)
            return MessageFormat.format(info, pointValueTime, source.getClass().getSimpleName());
        return MessageFormat.format(info, pointValueTime, "unknown");
    }

    public static String dataSourcePointInfo(DataSourceVO<?> dataSource, DataPointVO dataPoint) {
        return LoggingUtils.dataSourceInfo(dataSource) + ", " + LoggingUtils.dataPointInfo(dataPoint);
    }

    public static String dataSourcePointValueTimeInfo(DataSourceVO<?> dataSource, DataPointVO dataPoint, PointValueTime valueTime, SetPointSource source) {
        return LoggingUtils.dataSourceInfo(dataSource) + ", " + LoggingUtils.dataPointInfo(dataPoint) + ", " + LoggingUtils.pointValueTimeInfo(valueTime, source);
    }

    public static String causeInfo(Exception e) {
        return exceptionInfo(getCause(e));
    }

    public static Exception getCause(Exception e) {
        return e.getCause() != null ? (Exception) e.getCause() : e;
    }
}
