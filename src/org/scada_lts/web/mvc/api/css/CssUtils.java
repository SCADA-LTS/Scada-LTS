package org.scada_lts.web.mvc.api.css;

import com.serotonin.mango.Common;
import com.serotonin.mango.util.LoggingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.mvc.api.CustomCssAPI;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CssUtils {

    private static final Log LOG = LogFactory.getLog(CssUtils.class);
    private static final String CSS_FILENAME = "/assets/user_styles.css";

    public static boolean saveToFile(CssStyle cssStyle) {
        try {
            File customCssFile = getCustomCssFileFromPath();
            if (customCssFile != null) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(customCssFile.getAbsolutePath()))) {
                    writer.write(cssStyle.getContent());
                }
            }
            return true;
        } catch (Exception ex) {
            LOG.warn(LoggingUtils.exceptionInfo(ex));
            return false;
        }
    }

    public static File getCustomCssFileFromPath() {
        try {
            File cssFile = new File(Common.ctx.getCtx().getRealPath(CSS_FILENAME));
            if(!cssFile.exists()) {
                boolean created = cssFile.createNewFile();
                if(created) {
                    LOG.info("Created custom CSS stylesheet file: " + CSS_FILENAME);
                }
            }
            return cssFile;
        } catch (IOException e) {
            LOG.error("Could not create a custom CSS file: " + CSS_FILENAME);
        }
        return null;
    }
}
