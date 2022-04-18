package org.scada_lts.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.scada_lts.utils.SvgUtils.*;

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
        return isZipFile(multipartFile.getOriginalFilename()) && !isSvg(multipartFile)
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
        return isImageByMimetype(fileName) && (isImageBitmap(file) || isSvg(file));
    }

    public static boolean isThumbsFile(File file) {
        return isThumbsFile(file.getName());
    }

    public static boolean isInfoFile(File file) {
        return isInfoFile(file.getName());
    }

    public static boolean isImageBitmap(File file) {
        try {
            return ImageIO.read(file) != null;
        } catch (FileNotFoundException ex) {
            LOG.error(ex.getMessage());
            return false;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            return false;
        }
    }

    public static boolean isToUploads(File file) {
        if(file == null)
            return false;
        if(isThumbsFile(file))
            return false;
        return isImageByMimetype(file) && (isImageBitmap(file) || isSvg(file));
    }

    public static boolean isToGraphics(File file) {
        if(file == null)
            return false;
        if(isThumbsFile(file))
            return false;
        if(isInfoFile(file) && !isSvg(file) && !isXml(file) && !isImageBitmap(file))
            return true;
        return isImageByMimetype(file) && isImageBitmap(file);
    }

    private static boolean isToGraphics(ZipFile zipFile, ZipEntry entry) {
        if(zipFile == null || entry == null)
            return true;
        if(isThumbsFile(entry.getName()))
            return false;
        if(isInfoFile(FilenameUtils.getName(entry.getName())) && !isSvg(zipFile, entry)
                && !isXml(zipFile, entry) && !isImageBitmap(zipFile, entry))
            return true;
        return isImageByMimetype(entry.getName()) && isImageBitmap(zipFile, entry);
    }

    private static boolean isToUploads(ZipFile zipFile, ZipEntry entry) {
        if(zipFile == null || entry == null)
            return true;
        if(isThumbsFile(entry.getName()))
            return false;
        return isImageByMimetype(entry.getName()) && (isImageBitmap(zipFile, entry) || isSvg(zipFile, entry));
    }

    private static boolean isImageBitmap(MultipartFile file) {
        try(InputStream inputStream = new ByteArrayInputStream(file.getBytes())) {
            return ImageIO.read(inputStream) != null;
        } catch (FileNotFoundException ex) {
            LOG.error(ex.getMessage());
            return false;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
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
            LOG.warn(ex.getMessage(), ex);
            return false;
        }
    }

    private static boolean isImageByMimetype(String fileName) {
        if(fileName == null)
            return false;
        return isImageByMimetype(new File(fileName));
    }

    private static boolean isImageByMimetype(File file) {
        String mimeType = getMimetype(file);
        return mimeType != null && mimeType.toLowerCase().contains("image");
    }

    private static boolean isInfoFile(String fileName) {
        return isTxtFile(fileName) && INFO_FILE_NAME.equalsIgnoreCase(fileName);
    }

    private static boolean isTxtFile(String fileName) {
        String mimeType = getMimetype(new File(fileName));
        return mimeType != null && mimeType.equalsIgnoreCase("text/plain");
    }

    private static boolean isZipFile(String fileName) {
        String mimeType = getMimetype(new File(fileName));
        return mimeType != null && mimeType.equalsIgnoreCase("application/zip");
    }


    private static boolean isThumbsFile(String fileName) {
        return IGNORE_THUMBS.equalsIgnoreCase(fileName);
    }

    private static <T> List<T> filter(List<T> files, Predicate<T> predicate) {
        return files.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    private static String getMimetype(File file) {
        try {
            return Files.probeContentType(file.toPath());
        } catch (IOException ex) {
            LOG.warn(ex.getMessage());
            return "";
        }
    }
}
