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

    /**
     * Creates a SandboxClassShutter configured with the class-level security regex patterns.
     *
     * The instance's allow and deny pattern arrays are initialized from the class's
     * precompiled security regex arrays obtained from system settings.
     */
    public SandboxClassShutter() {
        this.securityJsAccessDeniedClassRegexes = SECURITY_JS_ACCESS_DENIED_CLASS_REGEXES;
        this.securityJsAccessGrantedClassRegexes = SECURITY_JS_ACCESS_GRANTED_CLASS_REGEXES;
    }

    /**
     * Determine whether a given class is visible to embedded scripts according to the security filter and configured allow/deny regex rules.
     *
     * @param className the fully-qualified name of the class to check
     * @return `true` if access to the class is allowed for scripts, `false` otherwise
     */
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