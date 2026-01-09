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

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.web.i18n.I18NUtils;
import com.serotonin.web.i18n.LocalizableMessage;

public class DwrConvertTag extends TagSupport {

    private static final ObjectMapper JSON_MAPPER = buildObjectMapper();

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
            return writeJsonValue(value);
        }

        if (value instanceof Number) {
            return numberToJson((Number) value);
        }

        if (value instanceof CharSequence) {
            return writeJsonValue(value.toString());
        }

        if (value instanceof LocalizableMessage) {
            String localized = resolveLocalizableMessage((LocalizableMessage) value);
            return writeJsonValue(localized);
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

        return writeJsonValue(value);
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

            sb.append(writeJsonValue(key));
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

    private String numberToJson(Number number) {
        if (number instanceof Double) {
            double value = (Double) number;
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                return "null";
            }
        } else if (number instanceof Float) {
            float value = (Float) number;
            if (Float.isNaN(value) || Float.isInfinite(value)) {
                return "null";
            }
        }
        return writeJsonValue(number);
    }

    private String writeJsonValue(Object value) {
        try {
            return JSON_MAPPER.writeValueAsString(value);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to serialize value for DwrConvertTag", e);
        }
    }

    private static ObjectMapper buildObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.getFactory().setCharacterEscapes(HtmlSafeCharacterEscapes.INSTANCE);
        return mapper;
    }

    private static final class HtmlSafeCharacterEscapes extends CharacterEscapes {
        private static final long serialVersionUID = 1L;
        private static final HtmlSafeCharacterEscapes INSTANCE = new HtmlSafeCharacterEscapes();
        private static final SerializableString ESC_LT = new SerializedString("\\u003c");
        private static final SerializableString ESC_GT = new SerializedString("\\u003e");
        private static final SerializableString ESC_AMP = new SerializedString("\\u0026");

        private final int[] escapeCodes;

        private HtmlSafeCharacterEscapes() {
            escapeCodes = CharacterEscapes.standardAsciiEscapesForJSON();
            escapeCodes['<'] = CharacterEscapes.ESCAPE_CUSTOM;
            escapeCodes['>'] = CharacterEscapes.ESCAPE_CUSTOM;
            escapeCodes['&'] = CharacterEscapes.ESCAPE_CUSTOM;
        }

        @Override
        public int[] getEscapeCodesForAscii() {
            return escapeCodes;
        }

        @Override
        public SerializableString getEscapeSequence(int ch) {
            if (ch == '<') {
                return ESC_LT;
            }
            if (ch == '>') {
                return ESC_GT;
            }
            if (ch == '&') {
                return ESC_AMP;
            }
            return null;
        }
    }
}
