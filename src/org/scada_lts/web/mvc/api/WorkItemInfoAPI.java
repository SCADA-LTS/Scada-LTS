package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.mvc.api.json.WorkItemInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            return new ResponseEntity<>(Common.ctx.getBackgroundProcessing().getWorkItems().stream()
                    .map(WorkItemInfo::new)
                    .collect(Collectors.toList()), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/")
    public ResponseEntity<Map<String, Long>> getGroupByWorkItemCount() {
        try {
            return new ResponseEntity<>(Common.ctx.getBackgroundProcessing().getWorkItems().stream()
                    .map(WorkItemInfo::new)
                    .collect(Collectors.groupingBy(WorkItemInfo::getClassName, Collectors.counting())), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
