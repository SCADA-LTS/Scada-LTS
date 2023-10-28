package org.scada_lts.utils;

import org.scada_lts.serorepl.utils.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.scada_lts.utils.UploadFileUtils.*;

public final class StaticImagesUtils {

    private StaticImagesUtils(){}

    public static ResponseEntity<String> getAndSendImage(HttpServletRequest request, HttpServletResponse response) {
        File file = getSystemFileByRequest(request);
        if(!UploadFileUtils.isToUploads(file))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        try {
            sendFile(response, file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    public static String getUploadSystemFilePath(String fileName) {
        for(String path : getUploadsSystemFilePaths()) {
            Path path1 = Paths.get(path + getSeparator() + getUploadsBaseSystemFilePath(fileName));
            if(Files.exists(path1)) {
                return path1.toString();
            }
        }
        return "";
    }

    public static String getGraphicSystemFilePath(String fileName) {
        for(String path : getGraphicsSystemFilePaths()) {
            Path path1 = Paths.get(path + getSeparator() + getGraphicsBaseSystemFilePath(fileName));
            if(Files.exists(path1)) {
                return path1.toString();
            }
        }
        return "";
    }

    private static void sendFile(HttpServletResponse response, File file) throws IOException {
        int bufferSize = 2048;
        byte[] data = new byte[bufferSize];
        int len;
        try (InputStream inputStream = new FileInputStream(file);
             ServletOutputStream output = response.getOutputStream()) {
            while ((len = inputStream.read(data, 0, bufferSize)) != -1) {
                output.write(data, 0, len);
            }
        }
    }

    private static File getSystemFileByRequest(HttpServletRequest request) {
        String url = request.getRequestURI().replace(request.getContextPath(), "");
        String path = null;
        if(url.startsWith("/graphics")) {
            path = getGraphicSystemFilePath(normalizeSeparator(url));
        } else if(url.startsWith("/uploads")) {
            path = getUploadSystemFilePath(normalizeSeparator(url));
        }
        if(StringUtils.isEmpty(path))
            path = PathSecureUtils.getSystemFilePath() + normalizeSeparator(url);
        return new File(path);
    }

    private static String getSeparator() {
        return File.separator;
    }

}
