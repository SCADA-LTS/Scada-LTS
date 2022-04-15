package utils;

import java.io.File;
import java.text.MessageFormat;

public class UploadFileTestUtils {

    private static final String SEPARATOR = File.separator;

    public static String getResourcesPath(String path, String fileName) {
        return MessageFormat.format("test-resources{0}{1}{0}{2}", SEPARATOR, path, fileName);
    }
}
