package org.scada_lts.utils;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.view.DynamicImage;
import com.serotonin.mango.view.ImageSet;
import com.serotonin.mango.view.ViewGraphic;
import com.serotonin.mango.view.ViewGraphicLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.utils.security.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.scada_lts.svg.SvgUtils.isSvg;
import static org.scada_lts.utils.PathSecureUtils.getAppContextSystemFilePath;
import static org.scada_lts.utils.ScadaMimeTypeUtils.*;
import static org.scada_lts.utils.xml.XmlUtils.isXml;

public final class UploadFileUtils {

    private static final Log LOG = LogFactory.getLog(UploadFileUtils.class);
    private static final String INFO_FILE_NAME = "info.txt";
    private static final String IGNORE_THUMBS = "Thumbs.db";

    private static final String GRAPHICS_PATH = File.separator + "graphics";
    private static final String UPLOADS_PATH =  File.separator + "uploads";

    private UploadFileUtils() {}

    public static List<ZipEntry> filteringGraphicsFiles(List<ZipEntry> zipEntries, ZipFile zipFile) {
        return filter(zipEntries, entry -> isToGraphics(zipFile, entry));
    }

    public static List<ZipEntry> filteringUploadFiles(List<ZipEntry> zipEntries, ZipFile zipFile) {
        return filter(zipEntries, entry -> isToUploads(zipFile, entry));
    }

    public static List<File> filteringGraphicsFiles(List<File> files) {
        return filter(files, UploadFileUtils::isToGraphics);
    }

    public static List<File> filteringUploadFiles(List<File> files) {
        return filter(files, UploadFileUtils::isToUploads);
    }

    public static boolean isZip(MultipartFile multipartFile) {
        try {
            SafeMultipartFile safeMultipartFile;
            try {
                safeMultipartFile = SafeMultipartFile.safe(multipartFile);
            } catch (FileNotSafeException ex) {
                LOG.warn(ex.getMessage());
                return false;
            }
            return isZipMimeType(Paths.get(safeMultipartFile.getOriginalFilename()))
                    && !isXml(safeMultipartFile) && !isImageBitmap(safeMultipartFile);
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
            return false;
        }
    }

    public static boolean isToUploads(MultipartFile file) {
        if(file == null)
            return false;
        SafeMultipartFile safeMultipartFile;
        try {
            safeMultipartFile = SafeMultipartFile.safe(file);
        } catch (FileNotSafeException ex) {
            LOG.warn(ex.getMessage());
            return false;
        }
        String fileName = safeMultipartFile.getOriginalFilename();
        if(fileName == null)
            return false;
        if(isThumbsFile(fileName))
            return false;
        return isImageMimeType(Paths.get(fileName)) && (isImageBitmap(safeMultipartFile)
                || (isSvgMimeType(Paths.get(fileName)) && isSvg(safeMultipartFile)));
    }

    public static boolean isToUploads(File file) {
        if(file == null)
            return false;
        SafeFile safeFile;
        try {
            safeFile = SafeFile.safe(file);
        } catch (FileNotSafeException ex) {
            LOG.warn(ex.getMessage());
            return false;
        }
        if(isThumbsFile(safeFile))
            return false;
        return isImageMimeType(safeFile.toPath()) && (isImageBitmap(safeFile)
                || (isSvgMimeType(safeFile.toPath()) && isSvg(safeFile)));
    }

    public static boolean isToGraphics(File file) {
        if(file == null)
            return false;
        SafeFile safeFile;
        try {
            safeFile = SafeFile.safe(file);
        } catch (FileNotSafeException ex) {
            LOG.warn(ex.getMessage());
            return false;
        }
        if(isThumbsFile(safeFile))
            return false;
        if(isInfoFile(safeFile) && !isXml(safeFile) && !isImageBitmap(safeFile))
            return true;
        return isImageMimeType(safeFile.toPath()) && isImageBitmap(safeFile);
    }

    public static boolean isThumbsFile(File file) {
        SafeFile safeFile;
        try {
            safeFile = SafeFile.safe(file);
        } catch (FileNotSafeException ex) {
            LOG.warn(ex.getMessage());
            return false;
        }
        return isThumbsFile(safeFile);
    }

    public static boolean isInfoFile(File file) {
        SafeFile safeFile;
        try {
            safeFile = SafeFile.safe(file);
        } catch (FileNotSafeException ex) {
            LOG.warn(ex.getMessage());
            return false;
        }
        return isInfoFile(safeFile);
    }

    public static boolean isImageBitmap(File file) {
        SafeFile safeFile;
        try {
            safeFile = SafeFile.safe(file);
        } catch (FileNotSafeException ex) {
            LOG.warn(ex.getMessage());
            return false;
        }
        return isImageBitmap(safeFile);
    }

    public static void loadGraphics(ViewGraphicLoader loader, List<ImageSet> imageSets, List<DynamicImage> dynamicImages) {
        for(Path path: getGraphicsSystemFilePaths()) {
            loadGraphics(loader, imageSets, dynamicImages, path);
        }
    }

    public static List<Path> getUploadsSystemFilePaths() {
        return getImageSystemFilePaths(SystemSettingsUtils::getWebResourceUploadsPath, UPLOADS_PATH);
    }

    public static List<Path> getGraphicsSystemFilePaths() {
        return getImageSystemFilePaths(SystemSettingsUtils::getWebResourceGraphicsPath, GRAPHICS_PATH);
    }

    public static Path getUploadsSystemFileToWritePath() {
        return getImageSystemFileToWritePath(SystemSettingsUtils::getWebResourceUploadsPath, UPLOADS_PATH);
    }

    public static Path getGraphicsSystemFileToWritePath() {
        return getImageSystemFileToWritePath(SystemSettingsUtils::getWebResourceGraphicsPath, GRAPHICS_PATH);
    }

    public static Path getGraphicsBaseSystemFilePath(Path path) {
        if (path.toString().startsWith(GRAPHICS_PATH) || path.toString().endsWith(GRAPHICS_PATH)) {
            return Paths.get(path.toString().replace(GRAPHICS_PATH, ""));
        }
        return path;
    }

    public static Path getUploadsBaseSystemFilePath(Path path) {
        if (path.toString().startsWith(UPLOADS_PATH) || path.toString().endsWith(UPLOADS_PATH)) {
            return Paths.get(path.toString().replace(UPLOADS_PATH, ""));
        }
        return path;
    }

    private static boolean isThumbsFile(SafeFile file) {
        return isThumbsFile(file.getName());
    }

    private static boolean isInfoFile(SafeFile file) {
        return isInfoFile(file.toPath());
    }

    private static boolean isImageBitmap(SafeFile file) {
        try {
            return ImageIO.read(file.toFile()) != null;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
            return false;
        }
    }

    private static boolean isToGraphics(ZipFile zipFile, ZipEntry entry) {
        if(zipFile == null || entry == null)
            return true;
        SafeZipFile safeZipFile;
        try {
            safeZipFile = SafeZipFile.safe(zipFile);
        } catch (FileNotSafeException ex) {
            LOG.warn(ex.getMessage());
            return false;
        }
        SafeZipEntry safeZipEntry;
        try {
            safeZipEntry = SafeZipEntry.safe(entry);
        } catch (FileNotSafeException ex) {
            LOG.warn(ex.getMessage());
            return false;
        }
        return isToGraphics(safeZipFile, safeZipEntry);
    }

    private static boolean isToGraphics(SafeZipFile safeZipFile, SafeZipEntry safeZipEntry) {
        if(isThumbsFile(safeZipEntry.getName()))
            return false;
        if(isInfoFile(Paths.get(safeZipEntry.getName())) && !isXml(safeZipFile, safeZipEntry)
                && !isImageBitmap(safeZipFile, safeZipEntry))
            return true;
        return isImageMimeType(Paths.get(safeZipEntry.getName())) && isImageBitmap(safeZipFile, safeZipEntry);
    }

    private static boolean isToUploads(ZipFile zipFile, ZipEntry entry) {
        if(zipFile == null || entry == null)
            return true;
        SafeZipFile safeZipFile;
        try {
            safeZipFile = SafeZipFile.safe(zipFile);
        } catch (FileNotSafeException ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
        SafeZipEntry safeZipEntry;
        try {
            safeZipEntry = SafeZipEntry.safe(entry);
        } catch (FileNotSafeException ex) {
            LOG.error(ex.getMessage());
            return false;
        }
        return isToUploads(safeZipFile, safeZipEntry);
    }

    private static boolean isToUploads(SafeZipFile safeZipFile, SafeZipEntry safeZipEntry) {
        if(isThumbsFile(safeZipFile.getName()))
            return false;
        if(isThumbsFile(safeZipEntry.getName()))
            return false;
        return isImageMimeType(Paths.get(safeZipEntry.getName())) && (isImageBitmap(safeZipFile, safeZipEntry)
                || (isSvgMimeType(Paths.get(safeZipEntry.getName())) && isSvg(safeZipFile, safeZipEntry)));
    }

    private static boolean isImageBitmap(SafeMultipartFile file) {
        try(InputStream inputStream = new ByteArrayInputStream(file.getBytes())) {
            return ImageIO.read(inputStream) != null;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
            return false;
        }
    }

    private static boolean isImageBitmap(SafeZipFile zipFile, SafeZipEntry entry) {
        try(InputStream inputStream = zipFile.getInputStream(entry)) {
            return ImageIO.read(inputStream) != null;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
            return false;
        }
    }

    private static boolean isInfoFile(Path path) {
        return isTxtMimeType(path) && INFO_FILE_NAME.equalsIgnoreCase(path.toFile().getName());
    }

    private static boolean isThumbsFile(String fileName) {
        return IGNORE_THUMBS.equalsIgnoreCase(fileName);
    }

    private static <T> List<T> filter(List<T> files, Predicate<T> predicate) {
        return files.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    private static List<Path> getImageSystemFilePaths(Supplier<String> getLocalPath, String folder) {
        List<Path> paths = new ArrayList<>();
        String normalizeSeparator = normalizeSeparator(getLocalPath.get());
        if (!StringUtils.isEmpty(normalizeSeparator) && normalizeSeparator.endsWith(folder)) {
            Path path = getAbsoluteResourcePath(normalizeSeparator);
            createIfNotExists(path);
            paths.add(path);
        }
        Path path = getAppContextSystemFilePath(Paths.get(normalizeSeparator(folder)));
        createIfNotExists(path);
        paths.add(path);
        return paths;
    }

    private static Path getImageSystemFileToWritePath(Supplier<String> getLocalPath, String folder) {
        Path path;
        String normalizeSeparator = normalizeSeparator(getLocalPath.get());
        if (!StringUtils.isEmpty(normalizeSeparator) && normalizeSeparator.endsWith(folder)) {
            path = getAbsoluteResourcePath(normalizeSeparator(normalizeSeparator));
        } else {
            path = getAppContextSystemFilePath(Paths.get(normalizeSeparator(folder)));
        }
        createIfNotExists(path);
        return path;
    }

    private static void createIfNotExists(Path path) {
        if(!Files.exists(path)) {
            path.toFile().mkdirs();
        }
    }

    private static void loadGraphics(ViewGraphicLoader loader,
                                     List<ImageSet> imageSets,
                                     List<DynamicImage> dynamicImages,
                                     Path path) {
        for (ViewGraphic graphic : loader.loadViewGraphics(path)) {
            if (graphic.isImageSet())
                imageSets.add((ImageSet) graphic);
            else if (graphic.isDynamicImage())
                dynamicImages.add((DynamicImage) graphic);
            else
                throw new ShouldNeverHappenException(
                        "Unknown view graphic type");
        }
    }

    private static Path getAbsoluteResourcePath(String path) {
        Path normalizedPath = normalizePath(path);
        if (!path.equals(normalizedPath.toString())) {
            return Path.of(basePath() + File.separator + normalizeSeparator(path));
        } else {
            return normalizedPath;
        }
    }

    private static Path normalizePath(String path) {
        return Paths.get(path).toFile().getAbsoluteFile().toPath().normalize();
    }

    public static String normalizeSeparator(String path) {
        return path.replace("/", File.separator).replace("\\", File.separator);
    }

    private static Path basePath() {
        return new File("../").getAbsoluteFile().toPath().normalize();
    }
}
