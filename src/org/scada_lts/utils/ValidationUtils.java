package org.scada_lts.utils;
import org.scada_lts.serorepl.utils.StringUtils;

import java.text.MessageFormat;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public final class ValidationUtils {

    private ValidationUtils() {}

    public static String validId(Integer id) {
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
}
