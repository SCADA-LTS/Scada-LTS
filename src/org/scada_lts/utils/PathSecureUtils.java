package org.scada_lts.utils;

import com.serotonin.mango.Common;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.serorepl.utils.StringUtils;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        return toSecurePath(file.toPath())
                .map(securePath -> securePath.getAbsolutePath().replace(getRealPath(), ""))
                .orElse("");
    }

    public static boolean validateFilename(String name) {
        String decoded = URLDecoder.decode(name, StandardCharsets.UTF_8);
        if(StringUtils.isEmpty(decoded))
            return false;
        if(!decoded.equals(name))
            return false;
        if(!validatePath(decoded))
            return false;
        String withoutExt = FilenameUtils.removeExtension(name);
        if(withoutExt.isEmpty())
            return false;
        if(name.equals(withoutExt))
            return false;
        if(name.contains("..") || name.contains("\\"))
            return false;
        if(name.contains("/"))
            return false;
        return name.length() < 256;
    }

    public static boolean validatePath(String name) {
        String decoded = URLDecoder.decode(name, StandardCharsets.UTF_8);
        if(StringUtils.isEmpty(decoded))
            return false;
        if(!decoded.equals(name))
            return false;
        try {
            Path path = Paths.get(decoded).normalize();
            String normalizedPath = path.toString();
            return normalizedPath.equals(decoded);
        } catch (Exception ex) {
            LOG.error("Path is invalid! " + ex.getMessage());
            return false;
        }
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
