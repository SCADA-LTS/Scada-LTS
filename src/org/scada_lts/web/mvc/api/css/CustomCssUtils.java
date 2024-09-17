package org.scada_lts.web.mvc.api.css;

import com.serotonin.mango.util.LoggingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.utils.PathSecureUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class CustomCssUtils {

    private static final Log LOG = LogFactory.getLog(CustomCssUtils.class);
    private static final String CSS_FILENAME = "/assets/user_styles.css";
    private static final String TAB = new String(new byte[]{-30, -128, -125}, StandardCharsets.UTF_8);

    public static boolean saveToFile(CssStyle cssStyle) {
        try {
            File customCssFile = getCustomCssFileFromPath();
            if (customCssFile != null) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(customCssFile, StandardCharsets.UTF_8))) {
                    writer.write(cssStyle.getContent());
                }
            }
            return true;
        } catch (Exception ex) {
            LOG.warn(LoggingUtils.exceptionInfo(ex));
            return false;
        }
    }

    private static File getCustomCssFileFromPath() {
        try {
            Path path = PathSecureUtils.getAppContextSystemFilePath(CSS_FILENAME);
            File cssFile = path.toFile();
            if(Files.exists(path) == Files.notExists(path)) {
                LOG.warn("Missing file permissions!: " + CSS_FILENAME);
                return null;
            }
            if(Files.notExists(path)) {
                boolean created = cssFile.createNewFile();
                if(created) {
                    LOG.info("Created custom CSS stylesheet file: " + CSS_FILENAME);
                }
            }
            return cssFile;
        } catch (IOException e) {
            LOG.warn("Could not create a custom CSS file: " + CSS_FILENAME);
        }
        return null;
    }

    public static String replaceToTab(String content) {
        return content.replace(TAB, "\t");
    }
}
