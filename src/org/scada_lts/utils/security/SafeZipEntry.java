package org.scada_lts.utils.security;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;

public class SafeZipEntry {

    private final ZipEntry zipEntry;

    private SafeZipEntry(ZipEntry zipEntry) {
        this.zipEntry = zipEntry;
    }

    public static SafeZipEntry safe(ZipEntry file) throws FileNotSafeException {
        SafeZipEntry safeZipEntry = new SafeZipEntry(file);
        if(safeZipEntry.validate())
            return safeZipEntry;
        throw new FileNotSafeException(file.getName());
    }

    public String getName() {
        Path path = Paths.get(zipEntry.getName()).normalize();
        return path.toString();
    }

    public ZipEntry toZipEntry() {
        if(validate())
            return zipEntry;
        throw new SecurityException();
    }

    private boolean validate() {
        return getName().equals(zipEntry.getName())
                && getName().split("\\.").length == 2;
    }
}
