package org.scada_lts.web.mvc.api.components.cmp;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.service.MultiChangesHistoryService;
import org.scada_lts.service.model.MultiChangeHistoryComponentDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @author grzegorz.bylica@abilit.eu on 17.10.2019
 */
@Controller
public class GetHistoryChangesAPI {
    private static final Log LOG = LogFactory.getLog(GetHistoryChangesAPI.class);

    private MultiChangesHistoryService multiChangesHistoryService;

    public GetHistoryChangesAPI(MultiChangesHistoryService multiChangesHistoryService) {
        this.multiChangesHistoryService = multiChangesHistoryService;
    }

    @RequestMapping(value = "/api/cmp/history/{xIdViewAndIdCmp}", method = RequestMethod.GET)
    public ResponseEntity<MultiChangeHistoryComponentDTO> getHistory(@PathVariable(name = "xIdViewAndIdCmp") String xidViewAndIdCmp, HttpServletRequest request) {
        LOG.info("/api/cmp/hisotry xidViewAndIdCmp:" + xidViewAndIdCmp);

        try {
            User user = Common.getUser(request);

            if (user != null) {

                MultiChangeHistoryComponentDTO multiChangeHistoryComponentDTO = new MultiChangeHistoryComponentDTO(
                        xidViewAndIdCmp,
                        multiChangesHistoryService.getHistory(xidViewAndIdCmp)
                );

                return new ResponseEntity<>(multiChangeHistoryComponentDTO, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            if (LOG.isTraceEnabled()) {
                LOG.trace(e);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
