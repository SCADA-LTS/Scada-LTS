package org.scada_lts.utils.security;

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
        return zipEntry.getName();
    }

    public ZipEntry toZipEntry() {
        return zipEntry;
    }

    private boolean validate() {
        String zipEntryName = zipEntry.getName();
        return SafeZipFileUtils.valid(zipEntryName);
    }
}
