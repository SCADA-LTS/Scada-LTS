package org.scada_lts.utils.security;


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

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
        Path path = Paths.get(file.getName()).normalize();
        return path.toString();
    }

    public String getAbsolutePath() {
        Path path = Paths.get(file.getAbsolutePath()).normalize();
        return path.toString();
    }

    public File toFile() {
        return file;
    }

    public Path toPath() {
        return file.toPath();
    }

    private boolean validate() {
        return getAbsolutePath().equals(file.getAbsolutePath())
                && getName().equals(file.getName())
                && getName().split("\\.").length == 2;
    }
}
