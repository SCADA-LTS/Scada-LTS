package org.scada_lts.utils.security;

import java.io.File;
import java.nio.file.Path;

import static org.scada_lts.utils.PathSecureUtils.validateFilename;
import static org.scada_lts.utils.PathSecureUtils.validatePath;

public class SafeFile {

    private final File file;

    private SafeFile(File file) {
        this.file = file;
    }

    public static SafeFile safe(File file) throws FileNotSafeException {
        SafeFile safeFile = new SafeFile(file);
        if(safeFile.validate())
            return safeFile;
        throw new FileNotSafeException(file.getName());
    }

    public String getName() {
        return file.getName();
    }

    public File toFile() {
        return file;
    }

    public Path toPath() {
        return file.toPath();
    }

    private boolean validate() {
        String absolutePath = file.getAbsolutePath();
        String path = file.getPath();
        String name = file.getName();
        return validatePath(absolutePath)
                && validatePath(path)
                && validateFilename(name);
    }
}
