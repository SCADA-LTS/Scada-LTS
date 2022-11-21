package org.scada_lts.utils;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.web.mvc.api.exceptions.BadRequestException;
import org.scada_lts.web.mvc.api.exceptions.UnauthorizedException;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public final class ValidationUtils {

    private ValidationUtils() {}

    public static String validId(Integer id) {
        return msgIfNull("Correct id;", id);
    }

    public static String validId(String id) {
        return msgIfNull("Correct id;", id);
    }

    public static String validXid(String xidExpected, String xid) {
        return msgIfNonNullAndInvalid("Correct xid;", xid, a -> !StringUtils.isEmpty(a) && !a.equals(xidExpected));
    }

    public static String validId(Integer id, String xid) {
        return msgIfNullAndInvalid("Correct id or xid;", id, a -> StringUtils.isEmpty(xid));
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

    @Deprecated
    static <T> String msgIfNullAndInvalid(String msg, T value, Predicate<T> invalidIf) {
        if(Objects.isNull(value) && invalidIf.test(value)) {
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
        if (!user.isAdmin()) {
            throw new UnauthorizedException(request.getRequestURI());
        }
    }
}
