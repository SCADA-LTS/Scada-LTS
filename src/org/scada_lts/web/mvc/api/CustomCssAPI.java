package org.scada_lts.web.mvc.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.web.mvc.api.css.CssStyle;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import static org.scada_lts.web.mvc.api.css.CssUtils.getCustomCssFileFromPath;
import static org.scada_lts.web.mvc.api.css.CssUtils.saveToFile;

@RestController
@RequestMapping("/api/customcss")
public class CustomCssAPI {

    private static final Log LOG = LogFactory.getLog(CustomCssAPI.class);
    private static final String REQ_RESP_ERROR = "Couldn't create a *.css file.";

    private final SystemSettingsService systemSettingsService;

    public CustomCssAPI(SystemSettingsService systemSettingsService) {
        this.systemSettingsService = systemSettingsService;
    }

    @GetMapping("")
    public ResponseEntity<?> getCustomCss(HttpServletRequest request) {
        LOG.info("GET: /api/customcss");
        try {
            CssStyle customCss = systemSettingsService.getCustomCss();
            if (!StringUtils.isEmpty(customCss.getContent())) {
                return new ResponseEntity<>(customCss, HttpStatus.OK);
            }
            return new ResponseEntity<>(REQ_RESP_ERROR,HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("")
    public ResponseEntity<?> saveCustomCss(HttpServletRequest request, @Valid @RequestBody(required = true) CssStyle cssStyle) {
        LOG.info("POST: /api/customcss");
        try {
            if(!StringUtils.isEmpty(cssStyle.getContent())) {
                systemSettingsService.saveCustomCss(cssStyle);
                saveToFile(cssStyle);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
