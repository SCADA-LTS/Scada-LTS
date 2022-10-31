package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.mvc.api.json.WorkItemInfo;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/work-items")
public class WorkItemInfoAPI {

    private static final Log LOG = LogFactory.getLog(WorkItemInfoAPI.class);

    @GetMapping(value = "/")
    public ResponseEntity<List<WorkItemInfo>> getWorkItems() {
        try {
            return new ResponseEntity<>(Common.ctx.getBackgroundProcessing().getWorkItems().get().stream()
                    .map(WorkItemInfo::new)
                    .collect(Collectors.toList()), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/")
    public ResponseEntity<Map<String, Long>> getWorkItemsGroupBy() {
        try {
            return new ResponseEntity<>(Common.ctx.getBackgroundProcessing().getWorkItems().get().stream()
                    .map(WorkItemInfo::new)
                    .collect(Collectors.groupingBy(WorkItemInfo::getClassName, Collectors.counting())), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/limit/{limit}/")
    public ResponseEntity<List<WorkItemInfo>> setWorkItemsLimit(@PathVariable(value = "limit", required = true) Integer limit) {
        try {
            if(limit != null && limit > 0) {
                Common.ctx.getBackgroundProcessing().getWorkItems().setLimit(limit);
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
