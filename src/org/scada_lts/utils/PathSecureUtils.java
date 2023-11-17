package org.scada_lts.utils;

import com.serotonin.mango.Common;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.serorepl.utils.StringUtils;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
        String decoded = decodePath(name);
        String ext = FilenameUtils.getExtension(decoded);
        if(ext.isEmpty())
            return false;
        String withoutExt = FilenameUtils.removeExtension(decoded);
        if(withoutExt.isEmpty())
            return false;
        if(decoded.equals(withoutExt))
            return false;
        if(decoded.contains("..") || decoded.contains("\\"))
            return false;
        if(decoded.contains("/"))
            return false;
        if(StringUtils.isEmpty(decoded))
            return false;
        if(!decoded.equals(name) && !validateDecoded(name))
            return false;
        if(decoded.length() > 255)
            return false;
        try {
            Paths.get(decoded);
            return true;
        } catch (Exception ex) {
            LOG.warn("Filename is invalid! " + ex.getMessage());
            return false;
        }
    }

    public static boolean validatePath(String name, Predicate<Path> exists) {
        String decoded = decodePath(name);
        if(StringUtils.isEmpty(decoded))
            return false;
        String baseName = FilenameUtils.getFullPath(decoded);
        if(StringUtils.isEmpty(baseName)) {
            if(StringUtils.isEmpty(FilenameUtils.getExtension(name))) {
                baseName = name;
            } else {
                return false;
            }
        }

        if(!decoded.equals(baseName) && !validateDecoded(baseName))
            return false;
        try {
            Path path = Paths.get(baseName).normalize();
            String normalizedPath = path.toString();
            return exists.test(path) && (baseName.equals(normalizedPath) || baseName.equals(normalizedPath + File.separator));
        } catch (Exception ex) {
            LOG.warn("Path is invalid! " + ex.getMessage());
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

    private static boolean validateDecoded(String name) {
        String withoutWhitespace = name
                .replaceAll("\\s", "")
                .replace(File.separator, "")
                .replace(":", "");
        String withoutWhitespaceEncoded = URLEncoder.encode(withoutWhitespace, StandardCharsets.UTF_8);
        String withoutWhitespaceDecoded = URLDecoder.decode(withoutWhitespaceEncoded, StandardCharsets.UTF_8);
        return !StringUtils.isEmpty(withoutWhitespaceDecoded) && withoutWhitespaceDecoded.equals(withoutWhitespace);
    }

    public static String decodePath(String path) {
        return URLDecoder.decode(path, StandardCharsets.UTF_8);
    }
}
