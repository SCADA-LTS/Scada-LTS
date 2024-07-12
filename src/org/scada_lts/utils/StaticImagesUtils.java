package org.scada_lts.utils;

import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.web.mvc.api.dto.UploadImage;
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
        boolean exists = Files.exists(file.toPath());
        boolean notExists = Files.notExists(file.toPath());
        if(!exists && notExists)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        if(exists == notExists)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        if(!UploadFileUtils.isToUploads(file))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        try {
            sendFile(response, file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().build();
    }

    public static Path getUploadsSystemFilePath(Path fileName) {
        for(Path path : getUploadsSystemFilePaths()) {
            Path absolutePath = Paths.get(path + getSeparator() + getUploadsBaseSystemFilePath(fileName));
            if(Files.exists(absolutePath)) {
                return absolutePath;
            }
        }
        return Paths.get("");
    }

    public static Path getGraphicSystemFilePath(Path fileName) {
        for(Path path : getGraphicsSystemFilePaths()) {
            Path absolutePath = Paths.get(path + getSeparator() + getGraphicsBaseSystemFilePath(fileName));
            if(Files.exists(absolutePath)) {
                return absolutePath;
            }
        }
        return Paths.get("");
    }

    private static void sendFile(HttpServletResponse response, File file) throws IOException {
        int bufferSize = 2048;
        byte[] data = new byte[bufferSize];
        int len;
        String contentType = Files.probeContentType(file.toPath());
        response.setContentType(contentType);
        UploadImage image = UploadFileUtils.createUploadImage(file);
        response.addIntHeader("img-height", image.getHeight());
        response.addIntHeader("img-width", image.getWidth());
        try (InputStream inputStream = new FileInputStream(file);
             ServletOutputStream output = response.getOutputStream()) {
            while ((len = inputStream.read(data, 0, bufferSize)) != -1) {
                output.write(data, 0, len);
            }
        }
    }

    private static File getSystemFileByRequest(HttpServletRequest request) {
        String url = request.getRequestURI().replace(request.getContextPath(), "");
        Path path = Paths.get("");
        if(url.startsWith("/graphics")) {
            path = getGraphicSystemFilePath(Paths.get(url));
        } else if(url.startsWith("/uploads")) {
            path = getUploadsSystemFilePath(Paths.get(url));
        }
        if(StringUtils.isEmpty(path.toString()))
            path = PathSecureUtils.getAppContextSystemFilePath(url);
        return path.toFile();
    }

    private static String getSeparator() {
        return File.separator;
    }

}
