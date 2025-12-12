package com.serotonin.web.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.logging.log4j.core.util.JsonUtils;
import com.serotonin.web.i18n.I18NUtils;
import com.serotonin.web.i18n.LocalizableMessage;

public class DwrConvertTag extends TagSupport {

    private Object obj;

    public DwrConvertTag() {
        // no-op
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    @Override
    public int doStartTag() throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            String json = toJson(obj);
            out.write(json);
            return SKIP_BODY;
        } catch (IOException e) {
            throw new JspException("Error writing DwrConvertTag JSON content", e);
        }
    }

    @Override
    public void release() {
        super.release();
        this.obj = null;
    }

    private String toJson(Object value) {
        if (value == null) {
            return "null";
        }

        if (value instanceof Boolean) {
            return ((Boolean) value) ? "true" : "false";
        }

        if (value instanceof Number) {
            return value.toString();
        }

        if (value instanceof CharSequence) {
            return quoteString(value.toString());
        }

        if (value instanceof LocalizableMessage) {
            String localized = resolveLocalizableMessage((LocalizableMessage) value);
            return quoteString(localized);
        }

        if (value instanceof Map<?, ?>) {
            return mapToJson((Map<?, ?>) value);
        }

        if (value instanceof Collection<?>) {
            return collectionToJson((Collection<?>) value);
        }

        if (value.getClass().isArray()) {
            return arrayToJson(value);
        }

        return quoteString(String.valueOf(value));
    }

    private String resolveLocalizableMessage(LocalizableMessage lm) {
        try {
            if (pageContext == null) {
                return lm.getLocalizedMessage(null);
            }

            Object req = pageContext.getRequest();
            if (req instanceof HttpServletRequest) {
                HttpServletRequest httpReq = (HttpServletRequest) req;
                ResourceBundle bundle = I18NUtils.getBundle(httpReq);
                return lm.getLocalizedMessage(bundle);
            }

            return lm.getLocalizedMessage(null);
        } catch (Exception e) {
            String key = lm.getKey();
            return key != null ? key : lm.serialize();
        }
    }

    private String mapToJson(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        Iterator<? extends Map.Entry<?, ?>> it = map.entrySet().iterator();
        boolean first = true;
        while (it.hasNext()) {
            Map.Entry<?, ?> entry = it.next();
            if (!first) {
                sb.append(',');
            }
            first = false;

            String key = String.valueOf(entry.getKey());
            Object val = entry.getValue();

            sb.append(quoteString(key));
            sb.append(':');
            sb.append(toJson(val));
        }
        sb.append('}');
        return sb.toString();
    }

    private String collectionToJson(Collection<?> collection) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        Iterator<?> it = collection.iterator();
        boolean first = true;
        while (it.hasNext()) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            Object val = it.next();
            sb.append(toJson(val));
        }
        sb.append(']');
        return sb.toString();
    }

    private String arrayToJson(Object array) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            Object val = Array.get(array, i);
            sb.append(toJson(val));
        }
        sb.append(']');
        return sb.toString();
    }

    private String quoteString(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 16);
        sb.append('\"');
        JsonUtils.quoteAsString(s, sb);
        sb.append('\"');
        return sb.toString();
    }
}
