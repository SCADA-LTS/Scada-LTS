package org.scada_lts.web.mvc.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.utils.ExportConfigUtils;
import org.scada_lts.web.mvc.api.json.ExportConfig;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(path = "/api/export/config/only-text")
public class ExportOnlyTextConfigAPI {

    private static final Log LOG = LogFactory.getLog(ExportOnlyTextConfigAPI.class);

    @RequestMapping(value = "/projectName/{projectName}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<String> exportProjectName(@PathVariable String projectName,
                                                        @RequestParam(required = false) Integer pointValuesMax,
                                                        @RequestParam(required = false) String projectDescription,
                                                        HttpServletRequest request,
                                                        HttpServletResponse response) {
        LOG.info(request.getRequestURI());

        ExportConfig exportConfig = ExportConfig.onlyTextConfig(projectName, pointValuesMax, projectDescription);
        return ExportConfigUtils.export(request, response, exportConfig);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<String> export(@RequestParam(required = false) String projectName,
                                         @RequestParam(required = false) Integer pointValuesMax,
                                         @RequestParam(required = false) String projectDescription,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        LOG.info(request.getRequestURI());

        ExportConfig exportConfig = ExportConfig.onlyTextConfig(projectName, pointValuesMax, projectDescription);
        return ExportConfigUtils.export(request, response, exportConfig);
    }

    @RequestMapping(value = "/pointValuesMax/{pointValuesMax}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<String> exportPointValuesMax(@RequestParam(required = false) String projectName,
                                                           @PathVariable Integer pointValuesMax,
                                                           @RequestParam(required = false) String projectDescription,
                                                           HttpServletRequest request,
                                                           HttpServletResponse response) {
        LOG.info(request.getRequestURI());

        ExportConfig exportConfig = ExportConfig.onlyTextConfig(projectName, pointValuesMax, projectDescription);
        return ExportConfigUtils.export(request, response, exportConfig);
    }
}
