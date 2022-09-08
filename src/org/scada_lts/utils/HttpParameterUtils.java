package org.scada_lts.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.function.Function;

public final class HttpParameterUtils {

    private HttpParameterUtils() {}

    public static <R> Optional<R> getValue(String key, HttpServletRequest request, Function<String, R> parser) {
        String parameter = request.getParameter(key);
        if(parameter != null)
            return Optional.of(parser.apply(parameter));
        Object attribute = request.getAttribute(key);
        if(attribute != null)
            return Optional.of(parser.apply(String.valueOf(attribute)));
        attribute = request.getSession().getAttribute(key);
        if(attribute != null)
            return Optional.of(parser.apply(String.valueOf(attribute)));
        return Optional.empty();
    }

    public static <T> Optional<T> getObject(String key, HttpServletRequest request, Class<T> clazz) {
        Object attribute = request.getAttribute(key);
        if(attribute != null)
            return Optional.of(clazz.cast(attribute));
        attribute = request.getSession().getAttribute(key);
        if(attribute != null)
            return Optional.of(clazz.cast(attribute));
        return Optional.empty();
    }

    public static <R> Optional<R> getValueOnlyRequest(String key, HttpServletRequest request, Function<String, R> parser) {
        String parameter = request.getParameter(key);
        if(parameter != null)
            return Optional.of(parser.apply(parameter));
        Object attribute = request.getAttribute(key);
        if(attribute != null)
            return Optional.of(parser.apply(String.valueOf(attribute)));
        return Optional.empty();
    }

    public static <T> Optional<T> getObjectOnlyRequest(String key, HttpServletRequest request, Class<T> clazz) {
        Object attribute = request.getAttribute(key);
        if(attribute != null)
            return Optional.of(clazz.cast(attribute));
        return Optional.empty();
    }
}
