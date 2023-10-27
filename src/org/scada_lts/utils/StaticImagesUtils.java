package org.scada_lts.utils;

import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.web.mvc.api.exceptions.ScadaErrorMessage;
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

    public static ResponseEntity<String> getAndSendImage(String fileName, HttpServletRequest request, HttpServletResponse response) {
        String path = getImageSystemFilePath(fileName, request);
        if(StringUtils.isEmpty(path))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        try {
            sendFile(response, path);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    public static String getUploadSystemFilePath(String fileName) {
        for(String path : getUploadsSystemFilePaths()) {
            Path path1 = Paths.get(path + getSeparator() + fileName);
            if(Files.exists(path1)) {
                return path1.toString();
            }
        }
        return "";
    }

    public static String getGraphicSystemFilePath(String fileName) {
        for(String path : getGraphicsSystemFilePaths()) {
            Path path1 = Paths.get(path + getSeparator() + fileName);
            if(Files.exists(path1)) {
                return path1.toString();
            }
        }
        return "";
    }

    private static void sendFile(HttpServletResponse response, String path) throws IOException {
        int bufferSize = 2048;
        byte[] data = new byte[bufferSize];
        int len;
        try (InputStream inputStream = new FileInputStream(path);
             ServletOutputStream output = response.getOutputStream()) {
            while ((len = inputStream.read(data, 0, bufferSize)) != -1) {
                output.write(data, 0, len);
            }
        }
    }

    private static String getImageSystemFilePath(String fileName, HttpServletRequest request) {
        String url = request.getRequestURI().replace(request.getContextPath(), "");
        if(url.startsWith("/graphics")) {
            return getGraphicSystemFilePath(fileName);
        } else if(url.startsWith("/uploads")) {
            return getUploadSystemFilePath(fileName);
        } else if(url.startsWith("/assets/images")) {
            return getAssetsImagesSystemFilePath(fileName);
        } else if(url.startsWith("/assets/")) {
            return getAssetsSystemFilePath(fileName);
        } else if(url.startsWith("/images")) {
            return getImagesSystemFilePath(fileName);
        } else if(url.startsWith("/img")) {
            return getImgSystemFilePath(fileName);
        } else {
            return "";
        }
    }

    private static String getDetail(String fileName) {
        return "file not exists: " + fileName;
    }

    private static String getImagesSystemFilePath(String filmName) {
        return PathSecureUtils.getSystemFilePath(getSeparator() + "images") + getSeparator() + filmName;
    }

    private static String getImgSystemFilePath(String filmName) {
        return PathSecureUtils.getSystemFilePath(getSeparator() + "img") + getSeparator() + filmName;
    }

    private static String getAssetsImagesSystemFilePath(String fileName) {
        return PathSecureUtils.getSystemFilePath(getSeparator() + "assets" + getSeparator() + "images") + getSeparator() + fileName;
    }

    private static String getAssetsSystemFilePath(String filmName) {
        return PathSecureUtils.getSystemFilePath(getSeparator() + "assets") + getSeparator() + filmName;
    }

    private static String getSeparator() {
        return File.separator;
    }

}
