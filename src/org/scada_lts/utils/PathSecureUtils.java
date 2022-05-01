package org.scada_lts.utils;

import com.serotonin.mango.Common;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public final class PathSecureUtils {

    private PathSecureUtils() {}

    private static final Log LOG = LogFactory.getLog(PathSecureUtils.class);

    public static Optional<File> toSecurePath(Path path) {
        return normalizePath(path).map(Path::toFile);
    }

    public static String getRealPath(String separator) {
        if(separator == null)
            throw new NullPointerException();
        return Common.ctx.getServletContext().getRealPath(separator);
    }

    public static String getRealPath() {
        return Common.ctx.getServletContext().getRealPath(File.separator);
    }

    public static String getPartialPath(File file) {
        return file.getAbsolutePath().replace(getRealPath(), "");
    }

    private static Optional<Path> normalizePath(Path path) {
        if(path == null) {
            return Optional.empty();
        }
        String appPath = getRealPath();
        if(appPath == null) {
            return Optional.empty();
        }
        Path normalizedPath = path.normalize();
        if(normalizedPath.startsWith(appPath)) {
            return Optional.of(normalizedPath);
        }
        LOG.warn("Path is invalid!");
        return Optional.empty();
    }
}
