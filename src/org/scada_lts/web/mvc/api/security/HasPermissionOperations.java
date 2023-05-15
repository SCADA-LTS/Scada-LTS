package org.scada_lts.web.mvc.api.security;

import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.*;
import org.scada_lts.permissions.service.*;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;

public class HasPermissionOperations {

    private static final Log LOG = LogFactory.getLog(HasPermissionOperations.class);

    private final DataPointService dataPointService;
    private final DataSourceService dataSourceService;
    private final ViewService viewService;
    private final WatchListService watchListService;
    private final ReportService reportService;

    public HasPermissionOperations() {
        this.dataPointService = new DataPointService();
        this.dataSourceService = new DataSourceService();
        this.viewService = new ViewService();
        this.watchListService = new WatchListService();
        this.reportService = new ReportService();
    }

    public HasPermissionOperations(DataPointService dataPointService, DataSourceService dataSourceService,
                                   ViewService viewService, WatchListService watchListService,
                                   ReportService reportService) {
        this.dataPointService = dataPointService;
        this.dataSourceService = dataSourceService;
        this.viewService = viewService;
        this.watchListService = watchListService;
        this.reportService = reportService;
    }

    public boolean hasUserOwnerPermission(User user, int id) {
        if(user == null)
            return false;
        return user.isAdmin() || user.getId() == id;
    }

    public boolean hasDataPointReadPermission(User user, int id) {
        return hasPermission(user, dataPointService::getDataPoint, id,
                GetDataPointsWithAccess::hasDataPointReadPermission);
    }

    public boolean hasDataPointReadPermission(User user, String xid) {
        return hasPermission(user, dataPointService::getDataPoint, xid,
                GetDataPointsWithAccess::hasDataPointReadPermission);
    }

    public boolean hasDataPointSetPermission(User user, int id) {
        return hasPermission(user, dataPointService::getDataPoint, id,
                GetDataPointsWithAccess::hasDataPointSetPermission);
    }

    public boolean hasDataPointSetPermission(User user, String xid) {
        return hasPermission(user, dataPointService::getDataPoint, xid,
                GetDataPointsWithAccess::hasDataPointSetPermission);
    }

    public boolean hasDataSourceReadPermission(User user, int id) {
        return hasPermission(user, dataSourceService::getDataSource, id,
                dataSourceService::hasDataSourceReadPermission);
    }

    public boolean hasDataSourceReadPermission(User user, String xid) {
        return hasPermission(user, dataSourceService::getDataSource, xid,
                dataSourceService::hasDataSourceReadPermission);
    }

    public boolean hasViewReadPermission(User user, int id) {
        return hasPermission(user, viewService::getView, id,
                GetViewsWithAccess::hasViewReadPermission);
    }

    public boolean hasViewReadPermission(User user, String xid) {
        return hasPermission(user, viewService::getViewByXid, xid,
                GetViewsWithAccess::hasViewReadPermission);
    }

    public boolean hasViewSetPermission(User user, int id) {
        return hasPermission(user, viewService::getView, id,
                GetViewsWithAccess::hasViewSetPermission);
    }

    public boolean hasViewSetPermission(User user, String xid) {
        return hasPermission(user, viewService::getViewByXid, xid,
                GetViewsWithAccess::hasViewSetPermission);
    }

    public boolean hasViewOwnerPermission(User user, int id) {
        return hasPermission(user, viewService::getView, id,
                GetViewsWithAccess::hasViewOwnerPermission);
    }

    public boolean hasViewOwnerPermission(User user, String xid) {
        return hasPermission(user, viewService::getViewByXid, xid,
                GetViewsWithAccess::hasViewOwnerPermission);
    }

    public boolean hasWatchListReadPermission(User user, int id) {
        return hasPermission(user, watchListService::getWatchList, id,
                GetWatchListsWithAccess::hasWatchListReadPermission, watchListService::populateWatchlistData);
    }

    public boolean hasWatchListReadPermission(User user, String xid) {
        return hasPermission(user, watchListService::getWatchList, xid,
                GetWatchListsWithAccess::hasWatchListReadPermission, watchListService::populateWatchlistData);
    }

    public boolean hasWatchListSetPermission(User user, int id) {
        return hasPermission(user, watchListService::getWatchList, id,
                GetWatchListsWithAccess::hasWatchListSetPermission, watchListService::populateWatchlistData);
    }

    public boolean hasWatchListSetPermission(User user, String xid) {
        return hasPermission(user, watchListService::getWatchList, xid,
                GetWatchListsWithAccess::hasWatchListSetPermission, watchListService::populateWatchlistData);
    }

    public boolean hasWatchListOwnerPermission(User user, int id) {
        return hasPermission(user, watchListService::getWatchList, id,
                GetWatchListsWithAccess::hasWatchListOwnerPermission, watchListService::populateWatchlistData);
    }

    public boolean hasWatchListOwnerPermission(User user, String xid) {
        return hasPermission(user, watchListService::getWatchList, xid,
                GetWatchListsWithAccess::hasWatchListOwnerPermission, watchListService::populateWatchlistData);
    }

    public boolean hasReportReadPermission(User user, int id) {
        return hasPermission(user, reportService::getReport, id,
                GetReportsWithAccess::hasReportReadPermission);
    }

    public boolean hasReportReadPermission(User user, String xid) {
        return hasPermission(user, reportService::getReport, xid,
                GetReportsWithAccess::hasReportReadPermission);
    }

    public boolean hasReportSetPermission(User user, int id) {
        return hasPermission(user, reportService::getReport, id,
                GetReportsWithAccess::hasReportSetPermission);
    }

    public boolean hasReportSetPermission(User user, String xid) {
        return hasPermission(user, reportService::getReport, xid,
                GetReportsWithAccess::hasReportSetPermission);
    }

    public boolean hasReportOwnerPermission(User user, int id) {
        return hasPermission(user, reportService::getReport, id,
                GetReportsWithAccess::hasReportOwnerPermission);
    }

    public boolean hasReportOwnerPermission(User user, String xid) {
        return hasPermission(user, reportService::getReport, xid,
                GetReportsWithAccess::hasReportOwnerPermission);
    }

    public boolean hasReportInstanceReadPermission(User user, int id) {
        return hasPermission(user, reportService::getReportInstance, id,
                GetReportInstancesWithAccess::hasReportInstanceReadPermission);
    }

    public boolean hasReportInstanceSetPermission(User user, int id) {
        return hasPermission(user, reportService::getReportInstance, id,
                GetReportInstancesWithAccess::hasReportInstanceSetPermission);
    }

    public boolean hasReportInstanceOwnerPermission(User user, int id) {
        return hasPermission(user, reportService::getReportInstance, id,
                GetReportInstancesWithAccess::hasReportInstanceOwnerPermission);
    }

    private static <T, I> boolean hasPermission(User user, Function<I, T> getObjectBy, I id,
                                                BiPredicate<User, T> hasPermission,
                                                Consumer<T> populate) {
        if(user == null)
            return false;
        T object;
        try {
            object = getObjectBy.apply(id);
        } catch (EmptyResultDataAccessException ex) {
            LOG.warn("The object with the given id: " + id + " does not exist. If the object does not exist, then the authority cannot be established. Exception: " + ex.getMessage(), ex);
            return true;
        } catch (Exception ex) {
            LOG.warn("The object with the given id: " + id + ". Exception: " + ex.getMessage(), ex);
            return false;
        }
        if(object == null) {
            LOG.warn("The object with the given id: " + id + " is null. If the object does not exist, then the authority cannot be established.");
            return true;
        }
        try {
            populate.accept(object);
            return hasPermission.test(user, object);
        } catch (Exception ex) {
            LOG.warn(ex.getMessage(), ex);
            return false;
        }
    }

    private static <T, I> boolean hasPermission(User user, Function<I, T> getObjectBy, I id,
                                                BiPredicate<User, T> hasPermission) {
        return hasPermission(user, getObjectBy, id, hasPermission, a -> {});
    }

}
