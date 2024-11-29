package org.scada_lts.utils;
import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.svg.SvgUtils;
import org.scada_lts.utils.security.SafeFile;
import org.scada_lts.web.mvc.api.exceptions.BadRequestException;
import org.scada_lts.web.mvc.api.exceptions.UnauthorizedException;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static br.org.scadabr.vo.exporter.util.FileUtil.createSvgTempFile;

public final class ValidationUtils {

    private static final Logger LOG = LogManager.getLogger(SafeFile.class);


    private ValidationUtils() {}

    public static String validId(Integer id) {
        return msgIfNull("Correct id;", id);
    }

    public static String validId(String id) {
        return msgIfNull("Correct id;", id);
    }

    public static String validXid(String xid) {
        return msgIfNullOrInvalid("Correct xid;", xid, StringUtils::isEmpty);
    }

    public static String validXid(String xidExpected, String xid) {
        return msgIfNonNullAndInvalid("Correct xid;", xid, a -> !StringUtils.isEmpty(a) && !a.equals(xidExpected));
    }

    public static String validId(Integer id, String xid) {
        String errorId = validId(id);
        if(errorId.isEmpty())
            return "";
        String errorXid = validXid(xid);
        if (errorXid.isEmpty())
            return "";
        return "Correct id or xid;";
    }

    public static String validSvg(String xmlContent) {
        SafeFile safeFile = null;
        try {
            File temp = createSvgTempFile(xmlContent);
            safeFile = SafeFile.safe(temp);
            if (SvgUtils.isSvg(safeFile)) {
                return "";
            }
        } catch (Exception ex) {
            LOG.error(LoggingUtils.exceptionInfo(ex));
        } finally {
            if(safeFile != null) {
                safeFile.delete();
            }
        }
        return "Invalid image";
    }

    static <T> String msgIfNull(String msg, T value) {
        return msgIfNullOrInvalid(msg, value, a -> false);
    }

    static <T> String msgIfNonNullAndInvalid(String msg, T value, Predicate<T> invalidIf) {
        if(Objects.nonNull(value) && invalidIf.test(value)) {
            return MessageFormat.format(msg, String.valueOf(value));
        }
        return "";
    }

    static <T, U> String msgIfInvalid(String msg, T value1, U value2, BiPredicate<T, U> invalidIf) {
        if(invalidIf.test(value1, value2)) {
            return MessageFormat.format(msg, String.valueOf(value1), String.valueOf(value2));
        }
        return "";
    }

   public static <T> String msgIfNullOrInvalid(String msg, T value, Predicate<T> invalidIf) {
        if(Objects.isNull(value) || invalidIf.test(value)) {
            return MessageFormat.format(msg, String.valueOf(value));
        }
        return "";
    }

    public static String formatErrorsJson(String errors) {
        return "{\"errors\": \"" + errors + "\"}";
    }

    public static void checkArgsIfEmptyThenBadRequest(HttpServletRequest request, String message, Object... args) {
        for(Object arg: args) {
            if(arg == null) {
                throw new BadRequestException(message, request.getRequestURI());
            }
            if((arg instanceof String) && StringUtils.isEmpty((String)arg)) {
                throw new BadRequestException(message, request.getRequestURI());
            }
        }
    }

    public static void checkArgsIfTwoEmptyThenBadRequest(HttpServletRequest request, String message, Object arg1, Object arg2) {
        if(arg1 == null && arg2 == null) {
            throw new BadRequestException(message, request.getRequestURI());
        }
        if((arg1 instanceof String) && StringUtils.isEmpty((String)arg1)) {
            throw new BadRequestException(message, request.getRequestURI());
        }
        if((arg2 instanceof String) && StringUtils.isEmpty((String)arg2)) {
            throw new BadRequestException(message, request.getRequestURI());
        }
    }

    public static void checkIfNonAdminThenUnauthorized(HttpServletRequest request) {
        User user = Common.getUser(request);
        if (user == null || !user.isAdmin()) {
            throw new UnauthorizedException(request.getRequestURI());
        }
    }

    public static boolean isCyclicDependency(int starDataPointId, int checkDataPointId, Map<Integer, DataPointVO> dataPoints, int safe) {
        if(safe < 0) {
            return false;
        }
        if(starDataPointId == checkDataPointId) {
            return true;
        }
        DataPointVO dataPoint = dataPoints.get(starDataPointId);
        PointLocatorVO pointLocator = dataPoint.getPointLocator();
        if(pointLocator instanceof MetaPointLocatorVO) {
            MetaPointLocatorVO metaPointLocator = (MetaPointLocatorVO) pointLocator;
            List<IntValuePair> pairs = metaPointLocator.getContext();
            if (pairs.isEmpty()) {
                return false;
            }
            for(IntValuePair pair: pairs) {
                int id = pair.getKey();
                if(id == checkDataPointId) {
                    return true;
                } else {
                    DataPointVO dp = dataPoints.get(id);
                    if(dp.getPointLocator() instanceof MetaPointLocatorVO) {
                        return isCyclicDependency(id, checkDataPointId, dataPoints, --safe);
                    }
                }
            }
        }
        return false;
    }
}
