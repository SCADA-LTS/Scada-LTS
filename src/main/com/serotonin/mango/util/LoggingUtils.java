package com.serotonin.mango.util;

import br.org.scadabr.vo.scripting.ScriptVO;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.view.View;
import com.serotonin.mango.view.component.ScriptComponent;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.mango.vo.link.PointLinkVO;
import com.serotonin.mango.vo.mailingList.MailingList;
import com.serotonin.mango.vo.report.ReportInstance;
import com.serotonin.mango.vo.report.ReportVO;
import org.apache.commons.lang3.StringUtils;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.mango.service.PointValueService;

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
        String dataSourceInfo = "datasource: {0} (id: {1})";
        return MessageFormat.format(dataSourceInfo, dataSource.getName(), String.valueOf(dataSource.getId()));
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
        String info = "pointLink: {0} (id: {1}, xid: {2}, event: {3}, sourcePointId: {4}, targetPointId: {5}, script: {6}, disabled: {7}, new: {8})";
        return MessageFormat.format(info, pointLink.getTypeKey(), String.valueOf(pointLink.getId()), pointLink.getXid(),
                pointLink.getEvent(), pointLink.getSourcePointId(), pointLink.getTargetPointId(), pointLink.getScript(),
                pointLink.isDisabled(), pointLink.isNew());
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
        if(event.getMessage() != null) {
            String message = event.getMessage().getLocalizedMessage(Common.getBundle());
            if(message != null) {
                return MessageFormat.format(info, StringUtils.abbreviate(message.trim(), 160), event.getId(), event.getActiveTimestamp(), event.getEventType());
            }
        }
        return MessageFormat.format(info, "no message", event.getId(), event.getActiveTimestamp(), event.getEventType());
    }

    public static String eventHandlerInfo(EventHandlerVO eventHandler) {
        if(eventHandler == null)
            return "";
        String info =  "eventHandler: {0} (id: {1}, xid: {2}, type: {3}, script active id: {4}, script inactive id: {5})";
        return MessageFormat.format(info, msg(eventHandler), eventHandler.getId(), eventHandler.getXid(), eventHandler.getHandlerType(),
                eventHandler.getActiveScriptCommand(), eventHandler.getInactiveScriptCommand());
    }

    public static String pointEventDetectorInfo(PointEventDetectorVO pointEventDetector) {
        if(pointEventDetector == null)
            return "";
        String info =  "pointEventDetector: {0} (id: {1}, xid: {2}, type: {3}, key: {4}, event type: {5})";
        return MessageFormat.format(info, pointEventDetector.getAlias(), pointEventDetector.getId(), pointEventDetector.getXid(), pointEventDetector.getDetectorType(),
                pointEventDetector.getEventDetectorKey(), pointEventDetector.getEventType());
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

    public static String userInfo(User user) {
        if(user == null)
            return "";
        String info = "user: {0} (id: {1}, userProfileId: {2})";
        return MessageFormat.format(info, user.getUsername(), user.getId(), user.getUserProfile());
    }

    public static String reportInfo(ReportVO report) {
        if(report == null)
            return "";
        String info =  "report: {0} (id: {1}, xid: {2}, username: {3}, userId: {4})";
        return MessageFormat.format(info, report.getName(), report.getId(), report.getXid(), report.getUsername(),
                report.getUserId());
    }

    public static String reportInstanceInfo(ReportInstance reportInstance) {
        if(reportInstance == null)
            return "";
        String info =  "reportInstance: {0} (id: {1}, userId: {2})";
        return MessageFormat.format(info, reportInstance.getName(), reportInstance.getId(), reportInstance.getUserId());
    }

    public static String mailingListInfo(MailingList mailingList) {
        if(mailingList == null)
            return "";
        String info =  "mailingList: {0} (id: {1}, xid: {2})";
        return MessageFormat.format(info, mailingList.getName(), mailingList.getId(), mailingList.getXid());
    }

    public static String eventTypeInfo(EventTypeVO event) {
        if(event == null)
            return "";
        String info = "event type: {0} (typeId: {1}, typeRef1: {2}, typeRef2: {3}, alarmLevel: {4}, eventDetectorKey: {4})";
        return MessageFormat.format(info, event.getDescription(), event.getTypeId(), event.getTypeRef1(), event.getTypeRef2(), event.getAlarmLevel(), event.getEventDetectorKey());
    }

    public static String systemEventTypInfo(SystemEventType event) {
        if(event == null)
            return "";
        String info = "system event type (systemEventTypeId: {0}, dataSourceId: {1}, dataPointId: {2}, eventSourceId: {3}, " +
                "duplicateHandling: {4}, referenceId1: {5}, referenceId2: {6}, compoundEventDetectorId: {7}, scheduleId: {8}, " +
                "publisherId: {9}, systemMessage: {10})";
        return MessageFormat.format(info, event.getSystemEventTypeId(), event.getDataSourceId(), event.getDataPointId(),
                event.getEventSourceId(), event.getDuplicateHandling(), event.getReferenceId1(), event.getReferenceId2(),
                event.getCompoundEventDetectorId(), event.getScheduleId(), event.getPublisherId(), event.isSystemMessage());
    }

    public static String eventTypeInfo(int type, int alarmLevel) {
        String info = "event type: {0} (alarmLevel: {1})";
        return MessageFormat.format(info, type, alarmLevel);
    }

    public static String viewInfo(View view) {
        if(view == null)
            return "";
        String info =  "view: {0} (id: {1}, xid: {2}, userId: {3})";
        return MessageFormat.format(info, view.getName(), view.getId(), view.getXid(), view.getUserId());
    }

    public static String entryInfo(PointValueService.BatchWriteBehindEntry entry) {
        if(entry == null) {
            return "";
        }
        String info = "batchWriteBehindEntry: pointId: {0}, dataType: {1}, time: {2}, dvalue: {3}";
        return MessageFormat.format(info, entry.getPointId(), entry.getDataType(), entry.getTime(), entry.getDvalue());
    }

    private static String msg(EventHandlerVO eventHandler) {
        return StringUtils.isEmpty(eventHandler.getAlias()) && eventHandler.getMessage() != null ? eventHandler.getMessage().getLocalizedMessage(Common.getBundle()) : eventHandler.getAlias();
    }
}
