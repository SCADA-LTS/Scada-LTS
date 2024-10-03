package org.scada_lts.web.mvc.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.SystemSettingsService;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.web.mvc.api.css.CssStyle;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import static org.scada_lts.web.mvc.api.css.CustomCssUtils.saveToFile;

@RestController
@RequestMapping("/api/customcss")
public class CustomCssAPI {

    private static final Log LOG = LogFactory.getLog(CustomCssAPI.class);

    private final SystemSettingsService systemSettingsService;

    public CustomCssAPI(SystemSettingsService systemSettingsService) {
        this.systemSettingsService = systemSettingsService;
    }

    @GetMapping(value = "", produces = {"application/json;charset=UTF-8"})
    public ResponseEntity<CssStyle> getCustomCss(HttpServletRequest request) {
        LOG.info("GET: /api/customcss");
        try {
            CssStyle customCss = systemSettingsService.getCustomCss();
            if (!StringUtils.isEmpty(customCss.getContent())) {
                return new ResponseEntity<>(customCss.clearedOfTabs(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "", consumes = {"application/json;charset=UTF-8"})
    public ResponseEntity<?> saveCustomCss(HttpServletRequest request, @Valid @RequestBody(required = true) CssStyle cssStyle, BindingResult bindingResult) {
        LOG.info("POST: /api/customcss");
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());

            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        try {
            if (!StringUtils.isEmpty(cssStyle.getContent())) {
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
