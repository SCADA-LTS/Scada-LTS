package org.scada_lts.web.security.dwr;

import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scada_lts.web.security.EmptyInstance;
import org.scada_lts.web.security.XssProtectUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.lang.reflect.Modifier.isStatic;

public final class XssBeanConverterUtils {

    private XssBeanConverterUtils() {}

    private static final Logger LOG = LogManager.getLogger(XssBeanConverterUtils.class);

    public static Object convertObjectEscaped(Object value) throws ScadaMarshallException {
        return doConvertIf(value, isSimpleJavaType().negate(),
                XssBeanConverterUtils::doConvert,
                object -> convertObject(object, XssBeanConverterUtils::escapeIfString));
    }

    public static Object convertObjectUnescaped(Object value) throws ScadaMarshallException {
        return doConvertIf(value, isSimpleJavaType().negate(),
                XssBeanConverterUtils::doConvert,
                object -> convertObject(object, XssBeanConverterUtils::unescapeIfString));
    }

    private static Object doConvertIf(Object object, Predicate<Object> doConvertIf,
                                      BiFunction<Object, Function<Object, Object>, Object> doConvert,
                                      Function<Object, Object> converter) throws ScadaMarshallException {
        if(doConvertIf.test(object)) {
            try {
                return doConvert.apply(object, converter);
            } catch (Exception e) {
                LOG.error(LoggingUtils.exceptionInfo(e));
                throw e;
            }
        } else {
            ScadaMarshallException marshallException = new ScadaMarshallException(object.getClass());
            LOG.error(LoggingUtils.exceptionInfo(marshallException));
            throw marshallException;
        }
    }

    public static Predicate<Object> isSimpleJavaType() {
        return object -> object instanceof Boolean || object instanceof String || object instanceof Character
                || object instanceof Number || object.getClass().isPrimitive();
    }

    private static Object doConvert(Object originObject, Function<Object, Object> convert) {
        Object object;
        try {
            object = newInstanceEmpty(originObject);
        } catch (Exception e) {
            throw new RuntimeException(new ScadaMarshallException(originObject.getClass(), e));
        }
        List<Field> fields = getAllFields(originObject);
        for(Field field: fields) {
            if(!isStatic(field.getModifiers())) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(originObject);
                    if(value != null) {
                        Object converted = convert.apply(value);
                        if(converted != null && !field.isAnnotationPresent(NoEscape.class))
                            field.set(object, converted);
                        else
                            field.set(object, value);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(new ScadaMarshallException(originObject.getClass(), e));
                } finally {
                    field.setAccessible(false);
                }
            }
        }
        return object;
    }

    private static List<Field> getAllFields(Object originObject) {
        List<Field> fields = new ArrayList<>();
        ReflectionUtils.doWithFields(originObject.getClass(), fields::add);
        return fields;
    }

    private static Object newInstanceEmpty(Object originObject) throws Exception {
        Object object;
        if(originObject instanceof LocalizableMessage) {
            object = new LocalizableMessage("");
        } else if(originObject instanceof EmptyInstance) {
            object = ((EmptyInstance) originObject).newInstanceEmpty();
        } else {
            object = originObject.getClass().getConstructor().newInstance();
        }
        return object;
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
        try {
            Map<?, ?> in = (Map<?, ?>) mapObject;
            Map<Object, Object> out = new HashMap<>();

            for(Object key: in.keySet().toArray()) {
                Object val = in.get(key);
                out.put(convert.apply(key), convert.apply(val));
            }
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
            LOG.error(LoggingUtils.exceptionInfo(e));
            return null;
        }
    }

    private static Object convertArrayObject(Object arrayObject, Function<Object, Object> convert) {
        try {
            int len = java.lang.reflect.Array.getLength(arrayObject);
            Object out = java.lang.reflect.Array.newInstance(arrayObject.getClass().getComponentType(), len);
            for (int i = 0; i < len; i++) {
                Object elem = java.lang.reflect.Array.get(arrayObject, i);
                java.lang.reflect.Array.set(out, i, convert.apply(elem));
            }
            return out;
        } catch (Exception e) {
            LOG.error(LoggingUtils.exceptionInfo(e));
            return null;
        }
    }

    private static Object escapeIfString(Object object) {
        if(object instanceof String) {
            return XssProtectUtils.escapeHtml((String) object);
        } else {
            return object;
        }
    }

    private static Object unescapeIfString(Object object) {
        if(object instanceof String) {
            return XssProtectUtils.unescapeHtml((String) object);
        } else {
            return object;
        }
    }
}
