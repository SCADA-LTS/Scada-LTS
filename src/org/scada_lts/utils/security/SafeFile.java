package org.scada_lts.utils.security;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.scada_lts.utils.PathSecureUtils.*;

public class SafeFile {

    private final File file;

    private SafeFile(File file) {
        this.file = file;
    }

    public static SafeFile safe(File file) throws FileNotSafeException {
        SafeFile safeFile = new SafeFile(new File(decodePath(file.getAbsolutePath())));
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
        if(validate())
            return file.toPath();
        return Path.of("");
    }

    private boolean validate() {
        String absolutePath = file.getAbsolutePath();
        String path = file.getPath();
        String name = file.getName();
        return validateFilename(name)
                && validatePath(path, Files::exists)
                && validatePath(absolutePath, Files::exists);
    }
}
