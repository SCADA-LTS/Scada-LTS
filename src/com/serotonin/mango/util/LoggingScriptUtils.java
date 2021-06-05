package com.serotonin.mango.util;

import br.org.scadabr.vo.scripting.ScriptVO;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.link.PointLinkVO;
import org.apache.commons.logging.Log;

import java.text.MessageFormat;

public final class LoggingScriptUtils {

    private LoggingScriptUtils() {}

    public static void loggingErrorExecutionScript(Exception e, Log log,
                                                   DataPointRT dataPoint,
                                                   DataSourceRT dataSource) {
        String context = "";

        if(dataSource != null) {
            context = dataSourceInfo(dataSource);
        }

        if(dataPoint != null && dataPoint.getVO() != null) {
            DataPointVO dataPointVO = dataPoint.getVO();
            context += ", " + dataPointInfo(dataPointVO);
        }

        loggingErrorExecutionScript(e, log, context);
    }

    public static void loggingErrorExecutionScript(Exception e, Log log,
                                                   PointLinkVO vo,
                                                   DataPointRT target,
                                                   DataPointRT source) {
        String context = "";
        if(vo != null) {
            context = pointLinkInfo(vo);
        }

        if(target != null && target.getVO() != null) {
            context += ", target: " + dataPointInfo(target.getVO());
        }

        if(source != null && source.getVO() != null) {
            context += ", source: " + dataPointInfo(source.getVO());
        }

        loggingErrorExecutionScript(e, log, context);
    }

    public static void loggingErrorExecutionScript(Exception e, Log log, ScriptVO<?> vo) {
        String context = "";

        if(vo != null) {
            context = scriptInfo(vo);
        }

        loggingErrorExecutionScript(e, log, context);
    }

    public static void loggingErrorExecutionScript(Exception e, Log log, String context) {
        if(e != null) {
            String info = "Problem with execute script: context: {0}, {1}";
            log.error(MessageFormat.format(info, context, exceptionInfo(e)));
        } else {
            String info = "Problem with execute script: context: {0}";
            log.error(MessageFormat.format(info, context));
        }
    }

    private static String dataPointInfo(DataPointVO dataPointVO) {
        String dataPointInfo = "datapoint: {0} (id: {1}, xid: {2})";
        return MessageFormat.format(dataPointInfo, dataPointVO.getName(), String.valueOf(dataPointVO.getId()), dataPointVO.getXid());
    }

    private static String dataSourceInfo(DataSourceRT dataSource) {
        String dataSourceInfo = "datasource: {0} (id: {1})";
        return MessageFormat.format(dataSourceInfo, dataSource.getName(), String.valueOf(dataSource.getId()));
    }

    private static String pointLinkInfo(PointLinkVO pointLink) {
        String dataPointInfo = "pointLink: {0} (id: {1}, xid: {2})";
        return MessageFormat.format(dataPointInfo, pointLink.getTypeKey(), String.valueOf(pointLink.getId()), pointLink.getXid());
    }

    private static String scriptInfo(ScriptVO<?> script) {
        String dataPointInfo = "script: {0} (id: {1}, xid: {2})";
        return MessageFormat.format(dataPointInfo, script.getName(), String.valueOf(script.getId()), script.getXid());
    }

    private static String exceptionInfo(Exception ex) {
        String exceptionInfo = "exception: {0} (msg: {1})";
        return MessageFormat.format(exceptionInfo, ex.getClass().getSimpleName(), ex.getMessage());
    }
}
