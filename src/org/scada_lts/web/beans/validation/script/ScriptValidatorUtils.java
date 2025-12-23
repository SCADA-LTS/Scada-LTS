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

    /**
 * Prevents instantiation of this utility class.
 */
private ScriptValidatorUtils() {}

    private static final Log LOG = LogFactory.getLog(ScriptValidatorUtils.class);

    private static final Pattern[] SECURITY_JS_ACCESS_DENIED_CLASS_REGEXES = Stream.of(SystemSettingsUtils.getSecurityJsAccessDeniedClassRegexes())
            .map(pattern -> pattern.startsWith("^(") ? pattern.substring(1): pattern)
            .map(pattern -> "^([\\s\\S]*" + pattern + "[\\s\\S]*)")
            .map(Pattern::compile)
            .collect(Collectors.toList())
            .toArray(new Pattern[]{});

    /**
     * Determines whether a script is allowed by the configured security deny patterns.
     *
     * If the security JS validator is disabled the script is treated as allowed. A null
     * script is treated as disallowed. If the script matches any configured deny regex
     * it is treated as disallowed (a warning may be logged).
     *
     * @param script the script source to validate; may be null
     * @return `true` if the script is allowed by the security validator, `false` otherwise
     */
    public static boolean validate(String script) {
        if(!SystemSettingsUtils.isSecurityJsValidatorEnabled()) {
            return true;
        }
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

    /**
     * Ensures the provided script is allowed by the security validator.
     *
     * @param script the script source to validate
     * @throws ScriptLocalizableException if the script is disallowed by the configured security rules
     */
    public static void validateScript(String script) throws ScriptException {
        if(!validate(script))
            throw new ScriptLocalizableException(new LocalizableMessage("scadalts.security.js.validator.error"));
    }
}