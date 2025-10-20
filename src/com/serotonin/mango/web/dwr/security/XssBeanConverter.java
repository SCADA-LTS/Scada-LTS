package com.serotonin.mango.web.dwr.security;

import org.directwebremoting.convert.BeanConverter;
import org.directwebremoting.extend.*;
import org.scada_lts.web.security.XssProtectUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class XssBeanConverter extends BeanConverter {

    @Override
    public OutboundVariable convertOutbound(Object value, OutboundContext outctx) throws MarshallException {
        convert(value, (object, noEscape) -> convertObject(object, noEscape, this::escapeIfString));
        return super.convertOutbound(value, outctx);
    }

    @Override
    public Object convertInbound(Class paramType, InboundVariable iv, InboundContext inctx) throws MarshallException {
        convert(iv.getValue(), (object, noEscape) -> convertObject(object, noEscape, this::unescapeIfString));
        return super.convertInbound(paramType, iv, inctx);
    }

    private void convert(Object object, BiFunction<Object, Boolean, Object> convert) {
        Field[] declaredFields = object.getClass().getDeclaredFields();
        for(Field field: declaredFields) {
            if(!java.lang.reflect.Modifier.isStatic(field.getModifiers()) && !java.lang.reflect.Modifier.isFinal(field.getModifiers())) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(object);
                    if(value != null)
                        field.set(object, convert.apply(value, field.isAnnotationPresent(NoEscape.class)));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    field.setAccessible(false);
                }
            }
        }
    }

    private Object convertObject(Object object, boolean noConvert, Function<Object, Object> doConvert) {

        if(noConvert)
            return object;

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

    private Object convertStringObject(Object stringObject, Function<Object, Object> convert) {
        return convert.apply(stringObject);
    }

    private Object convertMapObject(Object mapObject, Function<Object, Object> convert) {
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
            throw new RuntimeException(e);
        }
    }

    private Object convertSetObject(Object setObject, Function<Object, Object> convert) {
        try {
            Collection<?> in = (Collection<?>) setObject;
            Collection<Object> out = new HashSet<>();

            for(Object obj: in.toArray()) {
                out.add(convert.apply(obj));
            }

            Constructor<?> ctor = setObject.getClass().getConstructor(Collection.class);
            return ctor.newInstance(out);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object convertListObject(Object listObject, Function<Object, Object> convert) {
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

    private Object convertArrayObject(Object arrayObject, Function<Object, Object> convert) {
        int len = java.lang.reflect.Array.getLength(arrayObject);
        Object out = java.lang.reflect.Array.newInstance(arrayObject.getClass().getComponentType(), len);
        for (int i = 0; i < len; i++) {
            Object elem = java.lang.reflect.Array.get(arrayObject, i);
            java.lang.reflect.Array.set(out, i, convert.apply(elem));
        }
        return out;
    }

    private Object escapeIfString(Object object) {
        if(object instanceof String) {
            return XssProtectUtils.escapeHtml((String) object);
        } else {
            return object;
        }
    }

    private Object unescapeIfString(Object o) {
        if(o instanceof String) {
            return XssProtectUtils.unescapeHtml((String) o);
        } else {
            return o;
        }
    }
}
