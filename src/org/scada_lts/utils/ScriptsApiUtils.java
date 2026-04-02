package org.scada_lts.utils;

import br.org.scadabr.vo.scripting.ContextualizedScriptVO;
import br.org.scadabr.vo.scripting.ScriptVO;
import com.serotonin.db.IntValuePair;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.ScriptService;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.web.mvc.api.json.JsonScript;
import org.scada_lts.web.mvc.api.json.ScriptPoint;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.*;

import static org.scada_lts.serorepl.utils.StringUtils.isEmpty;
import static org.scada_lts.utils.UpdateValueUtils.setIf;
import static org.scada_lts.utils.ValidationUtils.*;
import static org.scada_lts.utils.ValidationUtils.msgIfNull;

public class ScriptsApiUtils {

    private static final Log LOG = LogFactory.getLog(ScriptsApiUtils.class);

    private ScriptsApiUtils() {}

    public static String validateScriptBody(JsonScript body) {
        String msg = msgIfNullOrInvalid("Correct xid;", body.getXid(), StringUtils::isEmpty);
        msg += msgIfInvalid("Xid cannot be longer than 50;", body.getXid(), 50, StringUtils::isLengthGreaterThan);
        msg += msgIfNullOrInvalid("Correct name;", body.getName(), StringUtils::isEmpty);
        msg += msgIfInvalid("Name cannot be longer than 40;", body.getName(), 40, StringUtils::isLengthGreaterThan);
        return msg;
    }

    public static String validateScriptUpdate(JsonScript body) {
        String msg = msgIfNonNullAndInvalid("Correct xid;", body.getXid(), StringUtils::isEmpty);
        msg += msgIfInvalid("Xid cannot be longer than 50;", body.getXid(), 50, StringUtils::isLengthGreaterThan);
        msg += msgIfNonNullAndInvalid("Correct name;", body.getName(), StringUtils::isEmpty);
        msg += msgIfInvalid("Name cannot be longer than 40;", body.getName(), 40, StringUtils::isLengthGreaterThan);
        return msg;
    }

    public static String validateScriptDelete(Integer id) {
        return msgIfNull("Correct id;", id);
    }

    @Deprecated(since = "2.8.1")
    public static ContextualizedScriptVO createScriptFromBody(JsonScript jsonBodyRequest, User user, DataPointService dataPointService) {
        ContextualizedScriptVO vo = new ContextualizedScriptVO();
        vo.setId(jsonBodyRequest.getId());
        vo.setXid(jsonBodyRequest.getXid());
        vo.setName(jsonBodyRequest.getName());
        vo.setScript(jsonBodyRequest.getScript());
        vo.setPointsOnContext(convertPointsOnContext(jsonBodyRequest.getPointsOnContext()));
        vo.setObjectsOnContext(convertObjectsOnContext(jsonBodyRequest));
        vo.setUserId(user.getId());
        return vo;
    }

    public static ContextualizedScriptVO createScriptFromBody(JsonScript jsonBodyRequest, User user) {
        ContextualizedScriptVO vo = new ContextualizedScriptVO();
        vo.setId(jsonBodyRequest.getId() == null ? -1 : jsonBodyRequest.getId());
        vo.setXid(jsonBodyRequest.getXid());
        vo.setName(jsonBodyRequest.getName());
        vo.setScript(jsonBodyRequest.getScript());
        vo.setPointsOnContext(convertPointsOnContext(jsonBodyRequest.getPointsOnContext()));
        vo.setObjectsOnContext(convertObjectsOnContext(jsonBodyRequest));
        vo.setUserId(user.getId());
        return vo;
    }

    private static List<IntValuePair> convertPointsOnContext(List<ScriptPoint> pointsOnContext) {
        List<IntValuePair> points = new ArrayList<>();
        for (ScriptPoint point : pointsOnContext) {
            points.add(new IntValuePair(point.getDataPointId(), point.getVarName()));
        }
        return points;
    }

    private static List<IntValuePair> convertObjectsOnContext(JsonScript jsonBodyRequest) {
        List<IntValuePair> objects = new ArrayList<>();
        objects.add(new IntValuePair(1, jsonBodyRequest.getDatasourceContext()));
        objects.add(new IntValuePair(2, jsonBodyRequest.getDatapointContext()));
        return objects;
    }

    @Deprecated(since = "2.8.1")
    public static void updateValueScript(ContextualizedScriptVO toUpdate, JsonScript source, DataPointService dataPointService) {
        setIf(source.getXid(), toUpdate::setXid, a -> !isEmpty(a));
        setIf(source.getId(), toUpdate::setId, Objects::nonNull);
        setIf(source.getName(), toUpdate::setName, Objects::nonNull);
        setIf(source.getScript(), toUpdate::setScript, Objects::nonNull);
        setIf(convertPointsOnContext(source.getPointsOnContext()), toUpdate::setPointsOnContext, Objects::nonNull);
        setIf(convertObjectsOnContext(source), toUpdate::setObjectsOnContext, Objects::nonNull);
        setIf(source.getUserId(), toUpdate::setUserId, Objects::nonNull);
    }

    public static void updateValueScript(ContextualizedScriptVO toUpdate, JsonScript source) {
        setIf(source.getXid(), toUpdate::setXid, a -> !isEmpty(a));
        setIf(source.getId(), toUpdate::setId, Objects::nonNull);
        setIf(source.getName(), toUpdate::setName, Objects::nonNull);
        setIf(source.getScript(), toUpdate::setScript, Objects::nonNull);
        setIf(convertPointsOnContext(source.getPointsOnContext()), toUpdate::setPointsOnContext, Objects::nonNull);
        setIf(convertObjectsOnContext(source), toUpdate::setObjectsOnContext, Objects::nonNull);
        setIf(source.getUserId(), toUpdate::setUserId, Objects::nonNull);
    }

    public static Optional<ScriptVO<?>> getScript(String xid, ScriptService scriptService) {
        try {
            ScriptVO<?> script = scriptService.getScript(xid);
            return Optional.ofNullable(script);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public static Optional<ContextualizedScriptVO> getScript(Integer id, ScriptService scriptService) {
        try {
            ContextualizedScriptVO script = (ContextualizedScriptVO) scriptService.getScript(id);
            return Optional.ofNullable(script);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public static Optional<DataPointVO> getDataPointByXid(String xid, DataPointService dataPointService) {
        try {
            DataPointVO dataPointVO = dataPointService.getDataPoint(xid);
            return Optional.ofNullable(dataPointVO);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public static Optional<DataPointVO> getDataPoint(int id, DataPointService dataPointService) {
        try {
            DataPointVO dataPointVO = dataPointService.getDataPoint(id);
            return Optional.ofNullable(dataPointVO);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public static boolean isScriptPresent(String xid, ScriptService scriptService){
        return xid != null && getScript(xid, scriptService).isPresent();
    }

    public static boolean isScriptPresent(Integer id, ScriptService scriptService){
        return id != null && getScript(id, scriptService).isPresent();
    }

    public static boolean isDataPointPresent(String xid, DataPointService dataPointService){
        return xid != null && getDataPointByXid(xid, dataPointService).isPresent();
    }

    public static boolean isDataPointPresent(Integer id, DataPointService dataPointService){
        return id != null && getDataPoint(id, dataPointService).isPresent();
    }

    public static boolean isSamePointIdAndXid(ScriptPoint scirpt, DataPointService dataPointService) {
        if(scirpt.getDataPointId() == null)
            return true;
        if(scirpt.getDataPointXid() == null)
            return true;
        DataPointVO dataPoint1 = getDataPointByXid(scirpt.getDataPointXid(), dataPointService).orElse(null);
        if(dataPoint1 == null)
            return true;
        DataPointVO dataPoint2 = getDataPoint(scirpt.getDataPointId(), dataPointService).orElse(null);
        if(dataPoint2 == null)
            return true;
        return dataPoint1.equals(dataPoint2);
    }

    public static String validatePointsOnContext(List<ScriptPoint> pointsOnContext, DataPointService dataPointService){
        StringBuilder msg = new StringBuilder();
        for (ScriptPoint point : pointsOnContext) {
            msg.append(msgIfNullOrInvalid("Invalid Xid: {0};", point.getDataPointXid(), a -> !isDataPointPresent(a, dataPointService)));
            msg.append(msgIfNullOrInvalid("Invalid Id: {0};", point.getDataPointId(), a -> !isDataPointPresent(a, dataPointService)));
            msg.append(msgIfNullOrInvalid("Invalid Id and Xid - may refer to another point: {0};", point, a -> !isSamePointIdAndXid(a, dataPointService)));
        }
        return msg.toString();
    }
}
