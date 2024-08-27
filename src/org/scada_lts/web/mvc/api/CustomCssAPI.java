package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.SystemSettingsDAO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Controller
@RequestMapping("/api/customcss")
public class CustomCssAPI {

    private static final Log LOG = LogFactory.getLog(CustomCssAPI.class);
    private static final String CSS_FILENAME = "/assets/user_styles.css";

    private static final String REQ_RESP_ERROR = "Couldn't create a *.css file.";

    @GetMapping("/")
    public ResponseEntity<String> getCustomCssFile(HttpServletRequest request) {
        LOG.info("GET: /api/customcss");
        try {
            new SystemSettingsDAO();
            String customCssContent = SystemSettingsDAO.getValue(SystemSettingsDAO.CUSTOM_STYLESHEET);

            if (customCssContent != null && !customCssContent.isEmpty()) {
                return new ResponseEntity<>(customCssContent, HttpStatus.OK);
            } else {
                String fileContent = readCustomCssFileAsString();
                if (fileContent != null) {
                    return new ResponseEntity<>(fileContent, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/")
    public ResponseEntity<String> saveCustomCssFile(HttpServletRequest request, @RequestBody String fileContent) {
        LOG.info("POST: /api/customcss");
        try {
            File cssFile = getCustomCssFileFromPath();
            if(cssFile != null) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(cssFile.getAbsolutePath()));
                writer.write(fileContent);
                new SystemSettingsDAO().setValue(SystemSettingsDAO.CUSTOM_STYLESHEET, fileContent);
                writer.close();
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(REQ_RESP_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private File getCustomCssFileFromPath() {
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

    private String readCustomCssFileAsString() {
        try {
            File cssFile = new File(Common.ctx.getCtx().getRealPath(CSS_FILENAME));
            if (cssFile.exists()) {
                return new String(Files.readAllBytes(cssFile.toPath()), StandardCharsets.UTF_8);
            } else {
                LOG.warn("CSS file does not exist: " + CSS_FILENAME);
            }
        } catch (IOException e) {
            LOG.error("Could not read the custom CSS file: " + CSS_FILENAME, e);
        }
        return null;
    }
}
