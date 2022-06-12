package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.maint.work.WorkItems;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.mvc.api.json.WorkItemInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/work-items")
public class WorkItemInfoAPI {

    private static final Log LOG = LogFactory.getLog(WorkItemInfoAPI.class);

    @GetMapping(value = "/all")
    public ResponseEntity<List<WorkItemInfo>> getNotExecutedWorkItems(HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().get();
            List<WorkItemInfo> response = getWorkItemInfos(workItems);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/all")
    public ResponseEntity<Map<String, Long>> getNotExecutedWorkItemsGroupBy(HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().get();
            Map<String, Long> response = groupByClassName(workItems);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static Map<String, Long> groupByClassName(List<WorkItems.Execute> workItems, Comparator<WorkItems.Execute> comparator) {
        return workItems.stream()
                .sorted(comparator)
                .map(WorkItemInfo::new)
                .collect(Collectors.groupingBy(a -> a.getWorkItemExecute().getClassName(), Collectors.counting()));
    }

    private static Map<String, Long> groupByClassName(List<WorkItems.Execute> workItems) {
        return groupByClassName(workItems, Comparator.reverseOrder());
    }

    private static List<WorkItemInfo> getWorkItemInfos(List<WorkItems.Execute> workItems, Comparator<WorkItems.Execute> comparator) {
        return workItems.stream()
                .sorted(comparator)
                .map(WorkItemInfo::new)
                .collect(Collectors.toList());
    }

    private static List<WorkItemInfo> getWorkItemInfos(List<WorkItems.Execute> workItems) {
        return getWorkItemInfos(workItems, Comparator.reverseOrder());
    }
}
