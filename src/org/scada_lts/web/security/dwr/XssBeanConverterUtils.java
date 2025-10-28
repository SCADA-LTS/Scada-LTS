package org.scada_lts.web.security.dwr;

import com.serotonin.mango.util.LoggingUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scada_lts.web.security.XssProtectUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.lang.reflect.Modifier.isStatic;

public final class XssBeanConverterUtils {

    private XssBeanConverterUtils() {}

    private static final Logger LOG = LogManager.getLogger(XssBeanConverterUtils.class);

    public static void convertObjectEscaped(Object value) throws ScadaMarshallException {
        doConvertIf(value, isSimpleJavaType().negate(),
                XssBeanConverterUtils::doConvert,
                object -> convertObject(object, XssBeanConverterUtils::escapeIfString));
    }

    public static void convertObjectUnescaped(Object value) throws ScadaMarshallException {
        doConvertIf(value, isSimpleJavaType().negate(),
                XssBeanConverterUtils::doConvert,
                object -> convertObject(object, XssBeanConverterUtils::unescapeIfString));
    }

    private static void doConvertIf(Object object, Predicate<Object> doIf,
                                    BiConsumer<Object, Function<Object, Object>> doConvert,
                                    Function<Object, Object> converter) throws ScadaMarshallException {
        if(doIf.test(object)) {
            doConvert.accept(object, converter);
        } else {
            throw new ScadaMarshallException(object.getClass());
        }
    }

    public static Predicate<Object> isSimpleJavaType() {
        return object -> object instanceof Boolean || object instanceof String || object instanceof Character
                || object instanceof Number || object.getClass().isPrimitive();
    }

    private static void doConvert(Object object, Function<Object, Object> convert) {
        Field[] declaredFields = object.getClass().getDeclaredFields();
        for(Field field: declaredFields) {
            if(!isStatic(field.getModifiers()) && !field.isAnnotationPresent(NoEscape.class)) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(object);
                    if(value != null) {
                        Object converted = convert.apply(value);
                        if(converted != null)
                            field.set(object, converted);
                    }
                } catch (Exception e) {
                    LOG.error(LoggingUtils.exceptionInfo(e));
                } finally {
                    field.setAccessible(false);
                }
            }
        }
    }

    private static Object convertObject(Object object, Function<Object, Object> doConvert) {

        if (object instanceof String) {
            return convertStringObject(object, doConvert);
        }

        if (object.getClass().isArray()) {
            return convertArrayObject(object, doConvert);
        }

        if (object instanceof List) {
            return convertListObject(object, doConvert);
        }

        if (object instanceof Set) {
            return convertSetObject(object, doConvert);
        }

        if (object instanceof Map) {
            return convertMapObject(object, doConvert);
        }

        return object;
    }

    private static Object convertStringObject(Object stringObject, Function<Object, Object> convert) {
        return convert.apply(stringObject);
    }

    private static Object convertMapObject(Object mapObject, Function<Object, Object> convert) {
        Map<?, ?> in = (Map<?, ?>) mapObject;
        Map<Object, Object> out = new HashMap<>();

        for(Object key: in.keySet().toArray()) {
            Object val = in.get(key);
            out.put(convert.apply(key), convert.apply(val));
        }

        try {
            Constructor<?> ctor = mapObject.getClass().getConstructor(Map.class);
            return ctor.newInstance(out);
        } catch (Exception e) {
            LOG.error(LoggingUtils.exceptionInfo(e));
            return null;
        }
    }

    private static Object convertSetObject(Object setObject, Function<Object, Object> convert) {
        try {
            Collection<?> in = (Collection<?>) setObject;
            Collection<Object> out = new HashSet<>();

            for(Object obj: in.toArray()) {
                out.add(convert.apply(obj));
            }

            Constructor<?> ctor = setObject.getClass().getConstructor(Collection.class);
            return ctor.newInstance(out);
        } catch (Exception e) {
            LOG.error(LoggingUtils.exceptionInfo(e));
            return null;
        }
    }

    private static Object convertListObject(Object listObject, Function<Object, Object> convert) {
        try {
            Collection<?> in = (Collection<?>) listObject;
            Collection<Object> out = new ArrayList<>();

            for(Object obj: in.toArray()) {
                out.add(convert.apply(obj));
            }

            Constructor<?> ctor = listObject.getClass().getConstructor(Collection.class);
            return ctor.newInstance(out);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object convertArrayObject(Object arrayObject, Function<Object, Object> convert) {
        int len = java.lang.reflect.Array.getLength(arrayObject);
        Object out = java.lang.reflect.Array.newInstance(arrayObject.getClass().getComponentType(), len);
        for (int i = 0; i < len; i++) {
            Object elem = java.lang.reflect.Array.get(arrayObject, i);
            java.lang.reflect.Array.set(out, i, convert.apply(elem));
        }
        return out;
    }

    private static Object escapeIfString(Object object) {
        if(object instanceof String) {
            return XssProtectUtils.escapeHtml((String) object);
        } else {
            return object;
        }
    }

    private static Object unescapeIfString(Object o) {
        if(o instanceof String) {
            return XssProtectUtils.unescapeHtml((String) o);
        } else {
            return o;
        }
    }
}
