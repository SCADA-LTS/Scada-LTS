package org.scada_lts.utils.security;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SafeMultipartFile {

    private final MultipartFile multipartFile;

    private SafeMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public static SafeMultipartFile safe(MultipartFile file) throws FileNotSafeException {
        SafeMultipartFile securedMultipartFile = new SafeMultipartFile(file);
        if(securedMultipartFile.validate())
            return securedMultipartFile;
        throw new FileNotSafeException(file.getOriginalFilename());
    }

    public String getName() {
        Path path = Paths.get(multipartFile.getName()).normalize();
        return path.toString();
    }

    public String getOriginalFilename() {
        Path path = Paths.get(multipartFile.getOriginalFilename()).normalize();
        return path.toString();
    }

    public byte[] getBytes() throws IOException {
        return multipartFile.getBytes();
    }

    public InputStream getInputStream() throws IOException {
        return multipartFile.getInputStream();
    }

    private boolean validate() {
        return getName().equals(multipartFile.getName())
                && getOriginalFilename().equals(multipartFile.getOriginalFilename())
                && getOriginalFilename().split("\\.").length == 2;
    }
}
