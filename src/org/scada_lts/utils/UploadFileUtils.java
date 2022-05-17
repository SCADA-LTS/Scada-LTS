package org.scada_lts.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.scada_lts.svg.SvgUtils.isSvg;
import static org.scada_lts.utils.ScadaMimeTypeUtils.*;
import static org.scada_lts.utils.xml.XmlUtils.isXml;

public final class UploadFileUtils {

    private static final Log LOG = LogFactory.getLog(UploadFileUtils.class);
    private static final String INFO_FILE_NAME = "info.txt";
    private static final String IGNORE_THUMBS = "Thumbs.db";

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
        return isZipMimeType(Paths.get(multipartFile.getOriginalFilename()))
                && !isXml(multipartFile) && !isImageBitmap(multipartFile);
    }

    public static boolean isToUploads(MultipartFile file) {
        if(file == null)
            return false;
        String fileName = file.getOriginalFilename();
        if(fileName == null)
            return false;
        if(isThumbsFile(fileName))
            return false;
        return isImageMimeType(Paths.get(fileName)) && (isImageBitmap(file)
                || (isSvgMimeType(Paths.get(fileName)) && isSvg(file)));
    }

    public static boolean isToUploads(File file) {
        if(file == null)
            return false;
        if(isThumbsFile(file))
            return false;
        return isImageMimeType(file.toPath()) && (isImageBitmap(file) || (isSvgMimeType(file.toPath()) && isSvg(file)));
    }

    public static boolean isToGraphics(File file) {
        if(file == null)
            return false;
        if(isThumbsFile(file))
            return false;
        if(isInfoFile(file) && !isXml(file) && !isImageBitmap(file))
            return true;
        return isImageMimeType(file.toPath()) && isImageBitmap(file);
    }

    public static boolean isThumbsFile(File file) {
        return isThumbsFile(file.getName());
    }

    public static boolean isInfoFile(File file) {
        return isInfoFile(file.toPath());
    }

    public static boolean isImageBitmap(File file) {
        try {
            return ImageIO.read(file) != null;
        } catch (FileNotFoundException ex) {
            LOG.error(ex.getMessage());
            return false;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
            return false;
        }
    }

    private static boolean isToGraphics(ZipFile zipFile, ZipEntry entry) {
        if(zipFile == null || entry == null)
            return true;
        if(isThumbsFile(entry.getName()))
            return false;
        if(isInfoFile(Paths.get(entry.getName())) && !isXml(zipFile, entry)
                && !isImageBitmap(zipFile, entry))
            return true;
        return isImageMimeType(Paths.get(entry.getName())) && isImageBitmap(zipFile, entry);
    }

    private static boolean isToUploads(ZipFile zipFile, ZipEntry entry) {
        if(zipFile == null || entry == null)
            return true;
        if(isThumbsFile(entry.getName()))
            return false;
        return isImageMimeType(Paths.get(entry.getName())) && (isImageBitmap(zipFile, entry)
                || (isSvgMimeType(Paths.get(entry.getName())) && isSvg(zipFile, entry)));
    }

    private static boolean isImageBitmap(MultipartFile file) {
        try(InputStream inputStream = new ByteArrayInputStream(file.getBytes())) {
            return ImageIO.read(inputStream) != null;
        } catch (FileNotFoundException ex) {
            LOG.error(ex.getMessage());
            return false;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
            return false;
        }
    }

    private static boolean isImageBitmap(ZipFile zipFile, ZipEntry entry) {
        try(InputStream inputStream = zipFile.getInputStream(entry)) {
            return ImageIO.read(inputStream) != null;
        } catch (FileNotFoundException ex) {
            LOG.error(ex.getMessage());
            return false;
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
}
