package org.scada_lts.utils.security;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;

import static org.scada_lts.utils.PathSecureUtils.validateFilename;
import static org.scada_lts.utils.PathSecureUtils.validatePath;

public class SafeMultipartFile {

    private final MultipartFile multipartFile;

    private SafeMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }

    public static SafeMultipartFile safe(MultipartFile file) throws FileNotSafeException {
        SafeMultipartFile securedMultipartFile = new SafeMultipartFile(file);
        if (securedMultipartFile.validate())
            return securedMultipartFile;
        throw new FileNotSafeException(file.getOriginalFilename());
    }

    public String getName() {
        return multipartFile.getName();
    }

    public String getOriginalFilename() {
        return multipartFile.getOriginalFilename();
    }

    public byte[] getBytes() throws IOException {
        return multipartFile.getBytes();
    }

    public InputStream getInputStream() throws IOException {
        return multipartFile.getInputStream();
    }

    private boolean validate() {
        String originalName = multipartFile.getOriginalFilename();
        return validateFilename(originalName);
    }
}


