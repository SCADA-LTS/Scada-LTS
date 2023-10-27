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
import java.util.function.BinaryOperator;
import java.util.function.Predicate;

import static org.scada_lts.utils.UploadFileUtils.getGraphicsBaseSystemFilePath;
import static org.scada_lts.utils.UploadFileUtils.getUploadsBaseSystemFilePath;

public final class PathSecureUtils {

    private PathSecureUtils() {}

    private static final Log LOG = LogFactory.getLog(PathSecureUtils.class);

    public static Optional<File> toSecurePath(Path path) {
        return toSecurePath(path, (originPath, base) -> originPath);
    }

    public static Optional<File> toSecurePath(Path path, BinaryOperator<String> reduce) {
        return normalizePath(path, reduce).map(Path::toFile);
    }

    public static String getSystemFilePath(String separator) {
        if(separator == null)
            throw new NullPointerException();
        return Common.ctx.getServletContext().getRealPath(separator);
    }

    public static String getSystemFilePath() {
        return getSystemFilePath(File.separator);
    }

    public static String getPartialPath(File file) {
        return toSecurePath(file.toPath(), (originPath, base) -> originPath.replace(base, ""))
                .map(File::getPath)
                .orElse("");
    }

    public static boolean validateFilename(String name) {
        String decoded = URLDecoder.decode(name, StandardCharsets.UTF_8);
        if(StringUtils.isEmpty(decoded))
            return false;
        if(!decoded.equals(name))
            return false;
        if(!validatePath(decoded, path -> true))
            return false;
        String ext = FilenameUtils.getExtension(name);
        if(ext.isEmpty())
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

    public static boolean validatePath(String name, Predicate<Path> exists) {
        String decoded = URLDecoder.decode(name, StandardCharsets.UTF_8);
        if(StringUtils.isEmpty(decoded))
            return false;
        if(!decoded.equals(name))
            return false;
        try {
            Path path = Paths.get(decoded).normalize();
            String normalizedPath = path.toString();
            return exists.test(path) && normalizedPath.equals(decoded);
        } catch (Exception ex) {
            LOG.error("Path is invalid! " + ex.getMessage());
            return false;
        }
    }

    private static Optional<Path> normalizePath(Path path, BinaryOperator<String> reduce) {
        if(path == null) {
            return Optional.empty();
        }
        String appPath = getSystemFilePath();
        if(appPath == null) {
            return Optional.empty();
        }
        Path normalizedPath = path.normalize();
        if(normalizedPath.startsWith(appPath)) {
            return Optional.of(Paths.get(reduce.apply(normalizedPath.toString(), appPath)));
        }
        for(String uploadsPath: UploadFileUtils.getUploadsSystemFilePaths()) {
            if (normalizedPath.startsWith(uploadsPath)) {
                return Optional.of(Paths.get(reduce.apply(normalizedPath.toString(), getUploadsBaseSystemFilePath(uploadsPath))));
            }
        }
        for(String graphicsPath: UploadFileUtils.getGraphicsSystemFilePaths()) {
            if (normalizedPath.startsWith(graphicsPath)) {
                return Optional.of(Paths.get(reduce.apply(normalizedPath.toString(), getGraphicsBaseSystemFilePath(graphicsPath))));
            }
        }
        LOG.warn("Path is invalid!");
        return Optional.empty();
    }
}
