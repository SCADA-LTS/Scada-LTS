package org.scada_lts.utils.security;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import static org.scada_lts.utils.PathSecureUtils.validateFilename;

public class SafeZipFile {
    private final ZipFile zipFile;

    private SafeZipFile(ZipFile zipFile) {
        this.zipFile = zipFile;
    }

    public static SafeZipFile safe(ZipFile file) throws FileNotSafeException {
        SafeZipFile safeZipFile = new SafeZipFile(file);
        if(safeZipFile.validate())
            return safeZipFile;
        throw new FileNotSafeException(file.getName());
    }

    public InputStream getInputStream(SafeZipEntry entry) throws IOException {
        return zipFile.getInputStream(entry.toZipEntry());
    }

    public String getName() {
        return zipFile.getName();
    }

    private boolean validate() {
        String name = zipFile.getName();
        return validateFilename(name);
    }
}
