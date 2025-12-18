package org.scada_lts.scripting;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.ClassShutter;
import org.scada_lts.utils.SystemSettingsUtils;

import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SandboxClassShutter implements ClassShutter {

    private static final Log LOG = LogFactory.getLog(SandboxContextFactory.class);

    private static final Pattern[] SECURITY_JS_ACCESS_DENIED_CLASS_REGEXES = Stream.of(SystemSettingsUtils.getSecurityJsAccessDeniedClassRegexes())
            .map(Pattern::compile)
            .collect(Collectors.toList())
            .toArray(new Pattern[]{});

    private static final Pattern[] SECURITY_JS_ACCESS_GRANTED_CLASS_REGEXES = Stream.of(SystemSettingsUtils.getSecurityJsAccessGrantedClassRegexes())
            .map(Pattern::compile)
            .collect(Collectors.toList())
            .toArray(new Pattern[]{});

    private final Pattern[] securityJsAccessDeniedClassRegexes;
    private final Pattern[] securityJsAccessGrantedClassRegexes;

    public SandboxClassShutter() {
        this.securityJsAccessDeniedClassRegexes = SECURITY_JS_ACCESS_DENIED_CLASS_REGEXES;
        this.securityJsAccessGrantedClassRegexes = SECURITY_JS_ACCESS_GRANTED_CLASS_REGEXES;
    }

    @Override
    public boolean visibleToScripts(String className) {
        if(!SystemSettingsUtils.isSecurityJsFilterEnabled())
            return true;
        for(Pattern pattern: securityJsAccessDeniedClassRegexes) {
            if (pattern.matcher(className).matches()) {
                if(LOG.isWarnEnabled())
                    LOG.warn("access denied for class: " + className);
                return false;
            }
        }
        for(Pattern pattern: securityJsAccessGrantedClassRegexes) {
            if (pattern.matcher(className).matches()) {
                return true;
            }
        }
        if(LOG.isWarnEnabled())
            LOG.warn("access denied for class: " + className);
        return false;
    }
}
