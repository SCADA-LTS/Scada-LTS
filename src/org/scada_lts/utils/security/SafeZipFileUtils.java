package org.scada_lts.utils.security;

import static org.scada_lts.utils.PathSecureUtils.validateFilename;
import static org.scada_lts.utils.PathSecureUtils.validatePath;

public final class SafeZipFileUtils {

    private SafeZipFileUtils() {}

    public static boolean valid(String absolutePath) {
        if(absolutePath.contains("/") || absolutePath.contains("\\")) {
            int index = absolutePath.lastIndexOf("/");
            String path = absolutePath.substring(0, index);
            String name = absolutePath.length() < index + 1 ? "" : absolutePath.substring(index + 1);
            return validatePath(absolutePath)
                    && validatePath(path)
                    && validateFilename(name);
        } else {
            return validateFilename(absolutePath);
        }
    }
}
