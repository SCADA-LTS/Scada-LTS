package org.scada_lts.utils.security;

import java.io.File;

import static org.scada_lts.utils.PathSecureUtils.validateFilename;
import static org.scada_lts.utils.PathSecureUtils.validatePath;
import static br.org.scadabr.vo.exporter.util.FileUtil.normalizePathSeparators;

public final class SafeZipFileUtils {

    private SafeZipFileUtils() {}

    public static boolean valid(String absolutePath) {
        if(absolutePath.contains("/") || absolutePath.contains("\\")) {
            absolutePath = normalizePathSeparators(absolutePath);
            int index = absolutePath.lastIndexOf(File.separator);
            String path = absolutePath.substring(0, index);
            String name = absolutePath.length() < index + 1 ? "" : absolutePath.substring(index + 1);
            return validatePath(absolutePath, a -> true)
                    && validatePath(path, a -> true)
                    && validateFilename(name);
        } else {
            return validateFilename(absolutePath);
        }
    }
}
