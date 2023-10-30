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

import static org.scada_lts.utils.UploadFileUtils.*;

public final class PathSecureUtils {

    private PathSecureUtils() {}

    private static final Log LOG = LogFactory.getLog(PathSecureUtils.class);

    public static Optional<File> toSecurePath(Path path) {
        return toSecurePath(path, (originPath, base) -> originPath);
    }

    public static Optional<File> toSecurePath(Path path, BinaryOperator<Path> reduce) {
        return normalizePath(path, reduce).map(Path::toFile);
    }

    public static Path getAppContextSystemFilePath(Path folder) {
        if(folder == null)
            throw new NullPointerException();
        String realPath = Common.ctx.getServletContext().getRealPath(normalizeSeparator(folder.toString()));
        if(realPath == null)
            return Paths.get("");
        return Paths.get(realPath);
    }

    public static Path getAppContextSystemFilePath() {
        return getAppContextSystemFilePath(Paths.get(File.separator));
    }

    public static String getPartialPath(File file) {
        return toSecurePath(file.toPath(), (originPath, base) -> Paths.get(normalizeSeparator(originPath.toString().replace(base.toString(), ""))))
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

    private static Optional<Path> normalizePath(Path path, BinaryOperator<Path> reduce) {
        if(path == null) {
            return Optional.empty();
        }
        Path appPath = getAppContextSystemFilePath();
        if(appPath.toString().isEmpty()) {
            return Optional.empty();
        }
        Path normalizedPath = path.normalize();
        if(normalizedPath.startsWith(appPath)) {
            return Optional.of(reduce.apply(normalizedPath, appPath));
        }
        for(Path uploadsPath: UploadFileUtils.getUploadsSystemFilePaths()) {
            if (normalizedPath.startsWith(uploadsPath)) {
                return Optional.of(reduce.apply(normalizedPath, getUploadsBaseSystemFilePath(uploadsPath)));
            }
        }
        for(Path graphicsPath: UploadFileUtils.getGraphicsSystemFilePaths()) {
            if (normalizedPath.startsWith(graphicsPath)) {
                return Optional.of(reduce.apply(normalizedPath, getGraphicsBaseSystemFilePath(graphicsPath)));
            }
        }
        LOG.warn("Path is invalid!");
        return Optional.empty();
    }
}
