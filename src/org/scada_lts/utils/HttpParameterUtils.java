package org.scada_lts.utils;

import com.serotonin.mango.web.dwr.ViewDwr;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.function.Function;

public final class HttpParameterUtils {

    private static final Log LOG = LogFactory.getLog(HttpParameterUtils.class);

    private HttpParameterUtils() {}

    public static <R> Optional<R> getValue(String key, HttpServletRequest request, Function<String, R> parser) {
        String parameter = request.getParameter(key);
        if(parameter != null)
            return Optional.ofNullable(parse(parser, parameter));
        Object attribute = request.getAttribute(key);
        if(attribute != null)
            return Optional.ofNullable(parse(parser, String.valueOf(attribute)));
        attribute = request.getSession().getAttribute(key);
        if(attribute != null)
            return Optional.ofNullable(parse(parser, String.valueOf(attribute)));
        return Optional.empty();
    }

    public static <T> Optional<T> getObject(String key, HttpServletRequest request, Class<T> clazz) {
        Object attribute = request.getAttribute(key);
        if(attribute != null)
            return Optional.ofNullable(cast(clazz, attribute));
        attribute = request.getSession().getAttribute(key);
        if(attribute != null)
            return Optional.ofNullable(cast(clazz, attribute));
        return Optional.empty();
    }

    public static <R> Optional<R> getValueOnlyRequest(String key, HttpServletRequest request, Function<String, R> parser) {
        String parameter = request.getParameter(key);
        if(parameter != null)
            return Optional.ofNullable(parse(parser, parameter));
        Object attribute = request.getAttribute(key);
        if(attribute != null)
            return Optional.ofNullable(parse(parser, String.valueOf(attribute)));
        return Optional.empty();
    }

    public static <T> Optional<T> getObjectOnlyRequest(String key, HttpServletRequest request, Class<T> clazz) {
        Object attribute = request.getAttribute(key);
        if(attribute != null)
            return Optional.ofNullable(cast(clazz, attribute));
        return Optional.empty();
    }

    private static <R> R parse(Function<String, R> parser, String parameter) {
        try {
            return parser.apply(parameter);
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            return null;
        }
    }

    private static <T> T cast(Class<T> clazz, Object attribute) {
        try {
            return clazz.cast(attribute);
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            return null;
        }
    }
}
