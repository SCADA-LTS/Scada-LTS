package org.scada_lts.utils.security;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipFile;

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
        Path path = Paths.get(zipFile.getName()).normalize();
        return path.toString();
    }

    private boolean validate() {
        return getName().equals(zipFile.getName())
                && getName().split("\\.").length == 2;
    }
}
