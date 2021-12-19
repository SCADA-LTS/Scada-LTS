package org.scada_lts.utils;

import br.org.scadabr.vo.exporter.ZIPProjectManager;
import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.mvc.api.json.ExportConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExportConfigUtils {

    private static final Log LOG = LogFactory.getLog(ExportConfigUtils.class);

    public static ResponseEntity<String> export(HttpServletRequest request, HttpServletResponse response, ExportConfig exportConfig) {
        try {
            User user = Common.getUser(request);
            if (user.isAdmin()) {
                ZIPProjectManager exporter = new ZIPProjectManager(exportConfig);
                exporter.exportProject(request, response);
                return ResponseEntity.ok().build();
            } else {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
