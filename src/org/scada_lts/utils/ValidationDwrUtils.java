package org.scada_lts.utils;

public final class ValidationDwrUtils {

    private ValidationDwrUtils() {
    }

    public static boolean validateVarNameScript(String varName) {
        char ch = varName.charAt(0);
        if (!Character.isLetter(ch) && ch != '_')
            return false;
        for (int i = 1; i < varName.length(); i++) {
            ch = varName.charAt(i);
            if (!Character.isLetterOrDigit(ch) && ch != '_')
                return false;
        }
        return true;
    }
}
