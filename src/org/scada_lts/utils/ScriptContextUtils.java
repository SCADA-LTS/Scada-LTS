package org.scada_lts.utils;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public final class ScriptContextUtils {

    /**
 * Prevents instantiation of this utility class.
 */
private ScriptContextUtils() {}

    /**
     * Initialize and return a Rhino top-level scripting scope for the provided Context.
     *
     * @param cx the Rhino Context used to initialize the scope
     * @return the initialized top-level Scriptable scope; this will be the standard or a safe variant depending on system security settings
     */
    public static Scriptable initStandardObjects(Context cx) {
        Scriptable scope;
        if(SystemSettingsUtils.isSecurityJsExecutorJavaEnabled())
            scope = cx.initStandardObjects();
        else
            scope = cx.initSafeStandardObjects();
        return scope;
    }

}