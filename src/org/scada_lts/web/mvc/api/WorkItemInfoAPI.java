package org.scada_lts.web.mvc.api;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.maint.work.WorkItems;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.mvc.api.json.WorkItemInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/work-items")
public class WorkItemInfoAPI {

    private static final Log LOG = LogFactory.getLog(WorkItemInfoAPI.class);

    @GetMapping(value = "/")
    public ResponseEntity<List<WorkItemInfo>> getWorkItems(HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByNotExecuted();
            List<WorkItemInfo> response = getWorkItemInfos(workItems);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/executed")
    public ResponseEntity<List<WorkItemInfo>> getExecutedWorkItems(HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByExecuted();
            List<WorkItemInfo> response = getWorkItemInfos(workItems);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/success")
    public ResponseEntity<List<WorkItemInfo>> getExecutedSuccessWorkItems(HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getBySuccess();
            List<WorkItemInfo> response = getWorkItemInfos(workItems);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/fail")
    public ResponseEntity<List<WorkItemInfo>> getExecutedFailWorkItems(HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByFail();
            List<WorkItemInfo> response = getWorkItemInfos(workItems);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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

    @GetMapping(value = "/longer/{executedMs}")
    public ResponseEntity<List<WorkItemInfo>> getExecutedLongerWorkItems(HttpServletRequest request,
                                                                         @PathVariable("executedMs") int executedMs) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByExecutedLongerThan(executedMs);
            List<WorkItemInfo> response = getWorkItemInfos(workItems, byExecuteMsComparator());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/less/{executedMs}")
    public ResponseEntity<List<WorkItemInfo>> getExecutedLessWorkItems(HttpServletRequest request,
                                                                       @PathVariable("executedMs") int executedMs) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByExecutedLessThan(executedMs);
            List<WorkItemInfo> response = getWorkItemInfos(workItems, byExecuteMsComparator());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static Comparator<WorkItems.Execute> byExecuteMsComparator() {
        return (a, b) -> b.getWorkItem().getExecutedMs() - a.getWorkItem().getExecutedMs();
    }

    @GetMapping(value = "/group-by/")
    public ResponseEntity<Map<String, Long>> getWorkItemsGroupBy(HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByNotExecuted();
            Map<String, Long> response = groupByClassName(workItems);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/executed")
    public ResponseEntity<Map<String, Long>> getExecutedWorkItemsGroupBy(HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByExecuted();
            Map<String, Long> response = groupByClassName(workItems);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/success")
    public ResponseEntity<Map<String, Long>> getExecutedSuccessWorkItemsGroupBy(HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getBySuccess();
            Map<String, Long> response = groupByClassName(workItems);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/fail")
    public ResponseEntity<Map<String, Long>> getExecutedFailWorkItemsGroupBy(HttpServletRequest request) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByFail();
            Map<String, Long> response = groupByClassName(workItems);
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

    @GetMapping(value = "/group-by/longer/{executedMs}")
    public ResponseEntity<Map<String, Long>> getNotExecutedLongerWorkItemsGroupBy(HttpServletRequest request,
                                                                                  @PathVariable("executedMs") int executedMs) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByExecutedLongerThan(executedMs);
            Map<String, Long> response = groupByClassName(workItems, byExecuteMsComparator());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/group-by/less/{executedMs}")
    public ResponseEntity<Map<String, Long>> getNotExecutedLessWorkItemsGroupBy(HttpServletRequest request,
                                                                                @PathVariable("executedMs") int executedMs) {
        User user = Common.getUser(request);
        if(user == null || !user.isAdmin()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            List<WorkItems.Execute> workItems = Common.ctx.getBackgroundProcessing().getWorkItems().getByExecutedLessThan(executedMs);
            Map<String, Long> response = groupByClassName(workItems, byExecuteMsComparator());
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
