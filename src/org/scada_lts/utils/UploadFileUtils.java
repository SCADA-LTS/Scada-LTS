package org.scada_lts.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.utils.security.*;
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
        try {
            SafeMultipartFile securedMultipartFile = SafeMultipartFile.safe(multipartFile);
            return isZipMimeType(Paths.get(securedMultipartFile.getOriginalFilename()))
                    && !isXml(securedMultipartFile) && !isImageBitmap(securedMultipartFile);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
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
            LOG.error(ex.getMessage());
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
            LOG.error(ex.getMessage());
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
            LOG.error(ex.getMessage());
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
            LOG.error(ex.getMessage());
            return false;
        }
        return isThumbsFile(safeFile);
    }

    public static boolean isInfoFile(File file) {
        SafeFile safeFile;
        try {
            safeFile = SafeFile.safe(file);
        } catch (FileNotSafeException ex) {
            LOG.error(ex.getMessage());
            return false;
        }
        return isInfoFile(safeFile);
    }

    public static boolean isImageBitmap(File file) {
        SafeFile safeFile;
        try {
            safeFile = SafeFile.safe(file);
        } catch (FileNotSafeException ex) {
            LOG.error(ex.getMessage());
            return false;
        }
        return isImageBitmap(safeFile);
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
        SafeZipFile safeZipFile;
        try {
            safeZipFile = SafeZipFile.safe(zipFile);
        } catch (FileNotSafeException ex) {
            LOG.error(ex.getMessage());
            return false;
        }
        SafeZipEntry safeZipEntry;
        try {
            safeZipEntry = SafeZipEntry.safe(entry);
        } catch (FileNotSafeException ex) {
            LOG.error(ex.getMessage());
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
        } catch (FileNotFoundException ex) {
            LOG.error(ex.getMessage());
            return false;
        } catch (Exception ex) {
            LOG.warn(ex.getMessage());
            return false;
        }
    }

    private static boolean isImageBitmap(SafeZipFile zipFile, SafeZipEntry entry) {
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
