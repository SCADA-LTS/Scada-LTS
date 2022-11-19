package org.scada_lts.web.mvc.api.security;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;

import static org.scada_lts.web.mvc.api.security.GuardUtils.ARG_IS_XID_IS_NOT_SUPPORTED;
import static org.scada_lts.web.mvc.api.security.GuardUtils.doHasPermission;

public class WithIdentifierGuard {

    private static final Log LOG = LogFactory.getLog(WithIdentifierGuard.class);

    private final HasPermissionOperations hasPermissionOperations;

    public WithIdentifierGuard(HasPermissionOperations hasPermissionOperations) {
        this.hasPermissionOperations = hasPermissionOperations;
    }

    public boolean hasUserOwnerPermission(HttpServletRequest request, int id) {
        User user = Common.getUser(request);
        if(user == null)
            return false;
        return hasPermissionOperations.hasUserOwnerPermission(user, id);
    }

    public boolean hasWatchListOwnerPermission(HttpServletRequest request, String id, boolean isXid) {
        return doHasPermission(request, hasPermissionOperations::hasWatchListOwnerPermission, hasPermissionOperations::hasWatchListOwnerPermission, id, isXid);
    }

    public boolean hasWatchListReadPermission(HttpServletRequest request, String id, boolean isXid) {
        return doHasPermission(request, hasPermissionOperations::hasWatchListReadPermission, hasPermissionOperations::hasWatchListReadPermission, id, isXid);
    }

    public boolean hasWatchListSetPermission(HttpServletRequest request, String id, boolean isXid) {
        return doHasPermission(request, hasPermissionOperations::hasWatchListSetPermission, hasPermissionOperations::hasWatchListSetPermission, id, isXid);
    }

    public boolean hasViewOwnerPermission(HttpServletRequest request, String id, boolean isXid) {
        return doHasPermission(request, hasPermissionOperations::hasViewOwnerPermission, hasPermissionOperations::hasViewOwnerPermission, id, isXid);
    }

    public boolean hasViewReadPermission(HttpServletRequest request, String id, boolean isXid) {
        return doHasPermission(request, hasPermissionOperations::hasViewReadPermission, hasPermissionOperations::hasViewReadPermission, id, isXid);
    }

    public boolean hasViewSetPermission(HttpServletRequest request, String id, boolean isXid) {
        return doHasPermission(request, hasPermissionOperations::hasViewSetPermission, hasPermissionOperations::hasViewSetPermission, id, isXid);
    }

    public boolean hasDataPointReadPermission(HttpServletRequest request, String id, boolean isXid) {
        return doHasPermission(request, hasPermissionOperations::hasDataPointReadPermission, hasPermissionOperations::hasDataPointReadPermission, id, isXid);
    }

    public boolean hasDataPointSetPermission(HttpServletRequest request, String id, boolean isXid) {
        return doHasPermission(request, hasPermissionOperations::hasDataPointSetPermission, hasPermissionOperations::hasDataPointSetPermission, id, isXid);
    }

    public boolean hasDataSourceReadPermission(HttpServletRequest request, String id, boolean isXid) {
        return doHasPermission(request, hasPermissionOperations::hasDataSourceReadPermission, hasPermissionOperations::hasDataSourceReadPermission, id, isXid);
    }

    public boolean hasReportOwnerPermission(HttpServletRequest request, String id, boolean isXid) {
        return doHasPermission(request, hasPermissionOperations::hasReportOwnerPermission, hasPermissionOperations::hasReportOwnerPermission, id, isXid);
    }

    public boolean hasReportReadPermission(HttpServletRequest request, String id, boolean isXid) {
        return doHasPermission(request, hasPermissionOperations::hasReportReadPermission, hasPermissionOperations::hasReportReadPermission, id, isXid);
    }

    public boolean hasReportSetPermission(HttpServletRequest request, String id, boolean isXid) {
        return doHasPermission(request, hasPermissionOperations::hasReportSetPermission, hasPermissionOperations::hasReportSetPermission, id, isXid);
    }

    public boolean hasReportInstanceOwnerPermission(HttpServletRequest request, String id, boolean isXid) {
        if(isXid) {
            LOG.warn(ARG_IS_XID_IS_NOT_SUPPORTED);
            return false;
        }
        return doHasPermission(request, hasPermissionOperations::hasReportInstanceOwnerPermission, (a,b) -> false, id, false);
    }

    public boolean hasReportInstanceReadPermission(HttpServletRequest request, String id, boolean isXid) {
        if(isXid) {
            LOG.warn(ARG_IS_XID_IS_NOT_SUPPORTED);
            return false;
        }
        return doHasPermission(request, hasPermissionOperations::hasReportInstanceReadPermission, (a,b) -> false, id, false);
    }

    public boolean hasReportInstanceSetPermission(HttpServletRequest request, String id, boolean isXid) {
        if(isXid) {
            LOG.warn(ARG_IS_XID_IS_NOT_SUPPORTED);
            return false;
        }
        return doHasPermission(request, hasPermissionOperations::hasReportInstanceSetPermission, (a,b) -> false, id, false);
    }
}