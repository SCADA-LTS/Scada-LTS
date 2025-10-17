package org.scada_lts.utils;

import com.serotonin.mango.Common;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.serorepl.utils.StringUtils;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.*;

import static org.scada_lts.utils.PathSecureUtils.FileSystemPaths.*;

public final class PathSecureUtils {

    private static final Log LOG = LogFactory.getLog(PathSecureUtils.class);

    private PathSecureUtils() {}


    public static String toPartialPath(File file) {
        return toSecurePath(file.toPath(), (originPath, base) -> java.nio.file.Paths.get(normalizeSeparator(originPath.toString().replace(base.toString(), ""))))
                .map(File::getPath)
                .orElse("");
    }

    public static Optional<File> toSecurePath(String path) {
        return toSecurePath(Paths.get(path));
    }

    public static Optional<File> toSecurePath(Path path) {
        return toSecurePath(path, (originPath, base) -> originPath);
    }

    public static Optional<File> toSecurePath(Path path, BinaryOperator<Path> reduce) {
        return normalizePath(path, reduce).map(Path::toFile);
    }

    public static String decodePath(String path) {
        return URLDecoder.decode(path, StandardCharsets.UTF_8);
    }

    public static String normalizeSeparator(String path) {
        return path.replace("/", File.separator).replace("\\", File.separator);
    }

    public static Path normalizePath(String path) {
        return Paths.get(normalizeSeparator(path)).toFile().getAbsoluteFile().toPath().normalize();
    }

    private static Optional<Path> normalizePath(Path path, BinaryOperator<Path> reduce) {

        if (path == null || path.toString().isEmpty()) {
            return Optional.empty();
        }

        Path normalizedPath = getAbsoluteResourcePath(path.toString());
        if (normalizedPath.toString().isEmpty()) {
            return Optional.empty();
        }

        Path appPath = getAppContextSystemFilePath();
        if (!appPath.toString().isEmpty() && normalizedPath.startsWith(appPath)) {
            return Optional.of(reduce.apply(normalizedPath, appPath));
        }

        Path catalinaHome = getCatalinaHomePath();
        if (!catalinaHome.toString().isEmpty() && normalizedPath.startsWith(catalinaHome)) {
            return Optional.of(reduce.apply(normalizedPath, catalinaHome));
        }

        for (Path uploadsPath : getUploadsSystemFilePaths()) {
            if (!uploadsPath.toString().isEmpty() && normalizedPath.startsWith(uploadsPath)) {
                return Optional.of(reduce.apply(normalizedPath, getUploadsBaseSystemFilePath(uploadsPath)));
            }
        }

        for (Path graphicsPath : getGraphicsSystemFilePaths()) {
            if (!graphicsPath.toString().isEmpty() && normalizedPath.startsWith(graphicsPath)) {
                return Optional.of(reduce.apply(normalizedPath, getGraphicsBaseSystemFilePath(graphicsPath)));
            }
        }

        LOG.warn("Path is invalid!");
        return Optional.empty();
    }

    private static void createPath(Path path, Consumer<Path> notExistsPath, Consumer<Path> existsPath) {
        if(existsPath(path) || path.toFile().mkdirs()) {
            existsPath.accept(path);
        } else {
            notExistsPath.accept(path);
        }
    }

    private static boolean existsPath(Path path) {
        if(!Files.exists(path) && Files.notExists(path)) {
            return false;
        } else if (!Files.exists(path) && !Files.notExists(path)) {
            return false;
        } else {
            return Files.exists(path);
        }
    }

    private static String encodePath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    public final static class FileSystemPaths {

        private static final Log LOG = LogFactory.getLog(PathSecureUtils.FileSystemPaths.class);

        private static final String GRAPHICS_PATH = File.separator + "graphics";
        private static final String UPLOADS_PATH =  File.separator + "uploads";

        private FileSystemPaths() {}

        public static Path getGraphicsBaseSystemFilePath(Path path) {
            String decoded = decodePath(path.toString());
            if (decoded.startsWith(GRAPHICS_PATH) || decoded.endsWith(GRAPHICS_PATH)) {
                return Paths.get(decoded.replace(GRAPHICS_PATH, ""));
            }
            return Paths.get(decoded);
        }

        public static Path getUploadsBaseSystemFilePath(Path path) {
            String decoded = decodePath(path.toString());
            if (decoded.startsWith(UPLOADS_PATH) || decoded.endsWith(UPLOADS_PATH)) {
                return Paths.get(decoded.replace(UPLOADS_PATH, ""));
            }
            return Paths.get(decoded);
        }

        public static Path getGraphicsBaseSystemFilePath() {
            return getGraphicsBaseSystemFilePath(getGraphicsSystemFileToWritePath());
        }

        public static Path getUploadsBaseSystemFilePath() {
            return getUploadsBaseSystemFilePath(getUploadsSystemFileToWritePath());
        }

        public static List<Path> getUploadsSystemFilePaths() {
            SystemSettingsService systemSettingsService = new SystemSettingsService();
            return getImageSystemFilePaths(systemSettingsService::getWebResourceUploadsPath, UPLOADS_PATH);
        }

        public static List<Path> getGraphicsSystemFilePaths() {
            SystemSettingsService systemSettingsService = new SystemSettingsService();
            return getImageSystemFilePaths(systemSettingsService::getWebResourceGraphicsPath, GRAPHICS_PATH);
        }

        public static Path getUploadsSystemFileToWritePath() {
            SystemSettingsService systemSettingsService = new SystemSettingsService();
            return getImageSystemFileToWritePath(systemSettingsService::getWebResourceUploadsPath, UPLOADS_PATH);
        }

        public static Path getGraphicsSystemFileToWritePath() {
            SystemSettingsService systemSettingsService = new SystemSettingsService();
            return getImageSystemFileToWritePath(systemSettingsService::getWebResourceGraphicsPath, GRAPHICS_PATH);
        }

        public static Path getAppContextSystemFilePath(String folder) {
            if(folder == null)
                throw new NullPointerException();
            String realPath = Common.ctx.getServletContext().getRealPath(normalizeSeparator(decodePath(folder)));
            if(realPath == null)
                return Paths.get("");
            return Paths.get(realPath);
        }

        public static Path getAppContextSystemFilePath() {
            return getAppContextSystemFilePath(File.separator);
        }

        public static Path getAbsoluteResourcePath(String path) {
            Path normalizedPath = PathSecureUtils.normalizePath(path);
            if (!path.equals(normalizedPath.toString())) {
                Path basePath = getCatalinaHomePath();
                return Path.of(basePath + File.separator + normalizeSeparator(path));
            } else {
                return normalizedPath;
            }
        }

        public static Path getCatalinaHomePath() {
            String catalinaHome = Common.getHomeDir();
            return Paths.get(catalinaHome);
        }

        private static List<Path> getImageSystemFilePaths(Supplier<String> getLocalPath, String folder) {
            List<Path> paths = new ArrayList<>();
            String normalizeFolder = normalizeSeparator(decodePath(folder));
            String normalizePath = normalizeSeparator(decodePath(getLocalPath.get()));
            if (!StringUtils.isEmpty(normalizePath) && (normalizePath.endsWith(normalizeFolder)
                    || normalizePath.endsWith(normalizeFolder + File.separator))) {
                Path path = getAbsoluteResourcePath(normalizePath);
                createPath(path, notExistsPath(), paths::add);
            }
            Path path = getAppContextSystemFilePath(normalizeFolder);
            createPath(path, notExistsPath(), paths::add);
            if (paths.isEmpty()) {
                throw new IllegalStateException(Common.getMessage("Could not create paths!"));
            }
            return paths;
        }

        private static Path getImageSystemFileToWritePath(Supplier<String> getLocalPath, String folder) {
            Path path;
            String normalizedFolder = normalizeSeparator(decodePath(folder));
            String normalizedPath = normalizeSeparator(decodePath(getLocalPath.get()));
            if (!StringUtils.isEmpty(normalizedPath) && (normalizedPath.endsWith(normalizedFolder)
                    || normalizedPath.endsWith(normalizedFolder + File.separator))) {
                path = getAbsoluteResourcePath(normalizedPath);
                createPath(path, notExistsPath(), a -> {
                });
            } else {
                path = getAppContextSystemFilePath(normalizedFolder);
            }
            if (!existsPath(path)) {
                path = getAppContextSystemFilePath(normalizedFolder);
                createPath(path, a -> {
                    throw new IllegalStateException(Common.getMessage("Could not create paths! :" + a));
                }, a -> {
                });
            }
            return path;
        }

        private static Consumer<Path> notExistsPath() {
            return a -> {
                LOG.error("Could not create path! : " + a);
            };
        }
    }

    public final static class ValidationPaths {

        private ValidationPaths() {}

        public static boolean validateFilename(String name) {
            String decoded = decodePath(name);
            String ext = FilenameUtils.getExtension(decoded);
            if (ext.isEmpty())
                return false;
            String withoutExt = FilenameUtils.removeExtension(decoded);
            if (withoutExt.isEmpty())
                return false;
            if (decoded.equals(withoutExt))
                return false;
            if (decoded.contains("..") || decoded.contains("\\"))
                return false;
            if (decoded.contains("/"))
                return false;
            if (StringUtils.isEmpty(decoded))
                return false;
            if (!decoded.equals(name) && !validateDecoded(name))
                return false;
            if (decoded.length() > 255)
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
            if (StringUtils.isEmpty(decoded))
                return false;
            String baseName = FilenameUtils.getFullPath(decoded);
            if (StringUtils.isEmpty(baseName)) {
                if (StringUtils.isEmpty(FilenameUtils.getExtension(name))) {
                    baseName = name;
                } else {
                    return false;
                }
            }

            if (!decoded.equals(baseName) && !validateDecoded(baseName))
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

        private static boolean validateDecoded(String name) {
            String withoutWhitespace = name
                    .replaceAll("\\s", "")
                    .replace(File.separator, "")
                    .replace(":", "");
            String withoutWhitespaceEncoded = encodePath(withoutWhitespace);
            String withoutWhitespaceDecoded = decodePath(withoutWhitespaceEncoded);
            return !StringUtils.isEmpty(withoutWhitespaceDecoded) && withoutWhitespaceDecoded.equals(withoutWhitespace);
        }
    }
}
