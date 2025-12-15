package org.scada_lts.web.beans.validation.script;

import com.serotonin.mango.rt.dataSource.meta.ScriptLocalizableException;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.utils.SystemSettingsUtils;

import javax.script.ScriptException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ScriptValidatorUtils {

    private ScriptValidatorUtils() {}

    private static final Log LOG = LogFactory.getLog(ScriptValidatorUtils.class);

    private static final Pattern[] SECURITY_JS_ACCESS_DENIED_CLASS_REGEXES = Stream.of(SystemSettingsUtils.getSecurityJsAccessDeniedClassRegexes())
            .map(pattern -> pattern.startsWith("^(") ? pattern.substring(1): pattern)
            .map(pattern -> "^([\\s\\S]*" + pattern + "[\\s\\S]*)")
            .map(Pattern::compile)
            .collect(Collectors.toList())
            .toArray(new Pattern[]{});

    public static boolean validate(String script) {
        if(script == null) {
            return false;
        }
        for(Pattern pattern: SECURITY_JS_ACCESS_DENIED_CLASS_REGEXES) {
            if (pattern.matcher(script).matches()) {
                if(LOG.isWarnEnabled())
                    LOG.warn("access denied for class: " + script);
                return false;
            }
        }
        return true;
    }

    public static void validateScript(String script) throws ScriptException {
        if(!validate(script))
            throw new ScriptLocalizableException(new LocalizableMessage("scadalts.security.js.validator.error"));
    }
}
