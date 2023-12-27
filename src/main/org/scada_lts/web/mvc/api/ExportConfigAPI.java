package org.scada_lts.web.mvc.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.utils.ExportConfigUtils;
import org.scada_lts.web.mvc.api.json.ExportConfig;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(path = "/api/export/config")
public class ExportConfigAPI {

    private static final Log LOG = LogFactory.getLog(ExportConfigAPI.class);

    @RequestMapping(value = "/", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<String> export(@RequestParam(required = false) String projectName,
                                         @RequestParam(required = false) boolean includeGraphicalFolder,
                                         @RequestParam(required = false) boolean includeUploadFolder,
                                         @RequestParam(required = false) Integer pointValuesMax,
                                         @RequestParam(required = false) String projectDescription,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {

        LOG.info(request.getRequestURI());

        ExportConfig exportConfig = ExportConfig.config(projectName, includeGraphicalFolder, includeUploadFolder, pointValuesMax, projectDescription);
        return ExportConfigUtils.export(request, response, exportConfig);
    }
}
