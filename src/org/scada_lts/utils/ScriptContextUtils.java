package org.scada_lts.utils;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public final class ScriptContextUtils {

    private ScriptContextUtils() {}

    public static Scriptable initStandardObjects(Context cx) {
        Scriptable scope;
        if(SystemSettingsUtils.isSecurityJsExecutorJavaEnabled())
            scope = cx.initStandardObjects();
        else
            scope = cx.initSafeStandardObjects();
        return scope;
    }

}
