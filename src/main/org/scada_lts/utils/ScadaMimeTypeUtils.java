package org.scada_lts.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

public final class ScadaMimeTypeUtils {

    private ScadaMimeTypeUtils() {}

    private static final Log LOG = LogFactory.getLog(ScadaMimeTypeUtils.class);

    public static boolean isImageMimeType(Path path) {
        return isMimeType(path, mimeType -> mimeType != null && mimeType.startsWith("image/"));
    }

    public static boolean isSvgMimeType(Path path) {
        return isMimeType(path, a -> "image/svg+xml".equalsIgnoreCase(a) || "image/svg-xml".equalsIgnoreCase(a));
    }

    public static boolean isTxtMimeType(Path path) {
        return isMimeType(path, "text/plain"::equalsIgnoreCase);
    }

    public static boolean isZipMimeType(Path path) {
        return isMimeType(path, mimeType -> mimeType != null && mimeType.startsWith("application/") && ("application/zip".equalsIgnoreCase(mimeType) || "application/x-zip-compressed".equalsIgnoreCase(mimeType)));
    }

    private static boolean isMimeType(Path path, Predicate<String> mimeType) {
        return mimeType.test(getMimetype(path));
    }

    private static String getMimetype(Path path) {
        try {
            return Files.probeContentType(path);
        } catch (IOException ex) {
            LOG.warn(ex.getMessage());
            return "";
        }
    }
}
