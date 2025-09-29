package com.serotonin.mango.web.dwr.security;

import org.directwebremoting.extend.Converter;
import org.directwebremoting.extend.ConverterManager;
import org.directwebremoting.extend.InboundContext;
import org.directwebremoting.extend.InboundVariable;
import org.directwebremoting.extend.MarshallException;
import org.directwebremoting.extend.OutboundContext;
import org.directwebremoting.extend.OutboundVariable;
import com.serotonin.web.i18n.I18NUtils;
import com.serotonin.web.i18n.LocalizableMessage;
import org.directwebremoting.WebContextFactory;

import java.util.IdentityHashMap;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;

/**
 * DWR Converter that escapes String properties in beans for XSS safety,
 * unless the property getter or field is annotated with @AllowHtml.
 *
 * Notes:
 * - Works as a drop-in next to the stock converters.
 * - We serialize the bean into a Map<String,Object>, sanitizing Strings on the way,
 *   then delegate to ConverterManager to produce the actual OutboundVariable.
 * - Inbound is intentionally not supported (fail-fast) to avoid silent trust of HTML.
 */
public class SelectiveXssBeanConverter implements Converter {
    private ConverterManager converterManager;
    private final Map<Class<?>, Map<String, Boolean>> allowCache = new WeakHashMap<>();
    private static final Object CYCLE_MARKER = new Object();

    @Override
    public void setConverterManager(ConverterManager converterManager) {
        this.converterManager = converterManager;
    }

    @Override
    public OutboundVariable convertOutbound(Object bean, OutboundContext outctx) throws MarshallException {
        if (bean == null)
            return converterManager.convertOutbound(null, outctx);
        try {
            Object sanitizedTop = sanitizeValue(bean, false, null, new IdentityHashMap<>());
            return converterManager.convertOutbound(sanitizedTop, outctx);
        } catch (Exception e) {
            throw new MarshallException(bean.getClass(), e);
        }
    }

    @Override
    public Object convertInbound(Class paramType, InboundVariable data, InboundContext inctx) throws MarshallException {
        throw new MarshallException(paramType, "Inbound not supported by SelectiveXssBeanConverter");
    }

    // ====== helpers ======

    private Object sanitizeValue(Object value,
                                 boolean parentAllowsHtml,
                                 Map<String, Boolean> propAllowCache,
                                 IdentityHashMap<Object, Object> seen) throws Exception {
        if (value == null) return null;

        // --- cycle guard for non-scalar objects ---
        if (!isSimpleScalar(value)) {
            if (seen.put(value, CYCLE_MARKER) != null) {
                return null; // break cycles
            }
        }

        // --- LocalizableMessage -> localized String, then maybe escape ---
        if (value instanceof LocalizableMessage) {
            String text = localize((LocalizableMessage) value);
            return parentAllowsHtml ? text : escapeHtml(text);
        }

        // --- plain String ---
        if (value instanceof CharSequence) {
            String s = value.toString();
            return parentAllowsHtml ? s : escapeHtml(s);
        }

        // --- simple scalars pass through ---
        if (isSimpleScalar(value)) return value;

        // --- arrays ---
        if (value.getClass().isArray()) {
            int len = java.lang.reflect.Array.getLength(value);
            Object copy = java.lang.reflect.Array.newInstance(value.getClass().getComponentType(), len);
            for (int i = 0; i < len; i++) {
                Object elem = java.lang.reflect.Array.get(value, i);
                java.lang.reflect.Array.set(copy, i, sanitizeValue(elem, false, null, seen));
            }
            return copy;
        }

        // --- collections ---
        if (value instanceof Collection) {
            Collection<?> src = (Collection<?>) value;
            java.util.List<Object> out = new java.util.ArrayList<>(src.size());
            for (Object o : src) out.add(sanitizeValue(o, false, null, seen));
            return out;
        }

        // --- maps ---
        if (value instanceof Map) {
            Map<?, ?> src = (Map<?, ?>) value;
            Map<Object, Object> out = new LinkedHashMap<>(src.size());
            for (Map.Entry<?, ?> e : src.entrySet()) {
                out.put(e.getKey(), sanitizeValue(e.getValue(), false, null, seen));
            }
            return out;
        }

        // --- bean -> Map<String,Object> with @AllowHtml support ---
        BeanInfo info = Introspector.getBeanInfo(value.getClass(), Object.class);
        Map<String, Boolean> classAllow = (propAllowCache != null)
                ? propAllowCache
                : allowCache.computeIfAbsent(value.getClass(), this::scanAllowedHtmlProps);

        Map<String, Object> out = new LinkedHashMap<>();
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            Method getter = pd.getReadMethod();
            if (getter == null) continue;
            Object v;
            try {
                v = getter.invoke(value);
            } catch (Exception ex) {
                continue;
            }
            boolean allowHtml = Boolean.TRUE.equals(classAllow.get(pd.getName()));
            out.put(pd.getName(), sanitizeValue(v, allowHtml, null, seen));
        }
        return out;
    }

    private Map<String, Boolean> scanAllowedHtmlProps(Class<?> type) {
        Map<String, Boolean> m = new HashMap<>();
        try {
            BeanInfo info = Introspector.getBeanInfo(type, Object.class);
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                String pn = pd.getName();
                boolean allow = false;

                if (!allow) {
                    Method getter = pd.getReadMethod();
                    if (getter != null && hasAllowHtml(getter)) {
                        allow = true;
                    } else {
                        try {
                            var field = type.getDeclaredField(pn);
                            if (hasAllowHtml(field)) allow = true;
                        } catch (NoSuchFieldException ignore) {
                        }
                    }
                }
                m.put(pn, allow);
            }
        } catch (Exception ignore) {
        }
        return m;
    }

    private boolean hasAllowHtml(AnnotatedElement e) {
        return e != null && e.isAnnotationPresent(AllowHtml.class);
    }

    private static String localize(LocalizableMessage lm) {
        return lm.getLocalizedMessage(
                I18NUtils.getBundle(WebContextFactory.get().getHttpServletRequest())
        );
    }

    private static boolean isSimpleScalar(Object v) {
        return v instanceof Number
                || v instanceof Boolean
                || v instanceof Character
                || v instanceof Enum<?>
                || v instanceof java.util.Date
                || v instanceof java.sql.Date
                || v instanceof java.sql.Time
                || v instanceof java.sql.Timestamp
                || v instanceof java.util.UUID;
    }

    private static String escapeHtml(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '&': sb.append("&amp;"); break;
                case '<': sb.append("&lt;");  break;
                case '>': sb.append("&gt;");  break;
                case '"': sb.append("&quot;");break;
                case '\'':sb.append("&#39;"); break;
                default:  sb.append(c);
            }
        }
        return sb.toString();
    }
}