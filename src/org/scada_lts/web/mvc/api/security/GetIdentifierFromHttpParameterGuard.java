package org.scada_lts.web.mvc.api.security;

import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.utils.HttpParameterUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.function.BiPredicate;

import static org.scada_lts.web.mvc.api.security.GuardUtils.ARG_IS_XID_IS_NOT_SUPPORTED;
import static org.scada_lts.web.mvc.api.security.GuardUtils.doHasPermission;


public class GetIdentifierFromHttpParameterGuard {

    private static final Log LOG = LogFactory.getLog(GetIdentifierFromHttpParameterGuard.class);
    private final HasPermissionOperations hasPermissionOperations;

    public GetIdentifierFromHttpParameterGuard(HasPermissionOperations hasPermissionOperations) {
        this.hasPermissionOperations = hasPermissionOperations;
    }

    public boolean hasWatchListOwnerPermission(HttpServletRequest request) {
        return hasPermission(request, hasPermissionOperations::hasWatchListOwnerPermission,
                hasPermissionOperations::hasWatchListOwnerPermission);
    }

    public boolean hasWatchListReadPermission(HttpServletRequest request) {
        return hasPermission(request, hasPermissionOperations::hasWatchListReadPermission,
                hasPermissionOperations::hasWatchListReadPermission);
    }

    public boolean hasWatchListSetPermission(HttpServletRequest request) {
        return hasPermission(request, hasPermissionOperations::hasWatchListSetPermission,
                hasPermissionOperations::hasWatchListSetPermission);
    }

    public boolean hasViewOwnerPermission(HttpServletRequest request) {
        return hasPermission(request, hasPermissionOperations::hasViewOwnerPermission,
                hasPermissionOperations::hasViewOwnerPermission);
    }

    public boolean hasViewReadPermission(HttpServletRequest request) {
        return hasPermission(request, hasPermissionOperations::hasViewReadPermission,
                hasPermissionOperations::hasViewReadPermission);
    }

    public boolean hasViewSetPermission(HttpServletRequest request) {
        return hasPermission(request, hasPermissionOperations::hasViewSetPermission,
                hasPermissionOperations::hasViewSetPermission);
    }

    public boolean hasDataPointReadPermission(HttpServletRequest request) {
        return hasPermission(request, hasPermissionOperations::hasDataPointReadPermission,
                hasPermissionOperations::hasDataPointReadPermission);
    }

    public boolean hasDataPointSetPermission(HttpServletRequest request) {
        return hasPermission(request,hasPermissionOperations::hasDataPointSetPermission,
                hasPermissionOperations::hasDataPointSetPermission);
    }

    public boolean hasDataSourceReadPermission(HttpServletRequest request) {
        return hasPermission(request, hasPermissionOperations::hasDataSourceReadPermission,
                hasPermissionOperations::hasDataSourceReadPermission);
    }

    public boolean hasReportOwnerPermission(HttpServletRequest request) {
        return hasPermission(request, hasPermissionOperations::hasReportOwnerPermission,
                hasPermissionOperations::hasReportOwnerPermission);
    }

    public boolean hasReportReadPermission(HttpServletRequest request) {
        return hasPermission(request, hasPermissionOperations::hasReportReadPermission,
                hasPermissionOperations::hasReportReadPermission);
    }

    public boolean hasReportSetPermission(HttpServletRequest request) {
        return hasPermission(request, hasPermissionOperations::hasReportSetPermission,
                hasPermissionOperations::hasReportSetPermission);
    }

    public boolean hasReportInstanceOwnerPermission(HttpServletRequest request) {
        return hasPermission(request, hasPermissionOperations::hasReportInstanceOwnerPermission,
                (a, b) -> false, false);
    }

    public boolean hasReportInstanceReadPermission(HttpServletRequest request) {
        return hasPermission(request, hasPermissionOperations::hasReportInstanceReadPermission,
                (a, b) -> false,  false);
    }

    public boolean hasReportInstanceSetPermission(HttpServletRequest request) {
        return hasPermission(request, hasPermissionOperations::hasReportInstanceSetPermission,
                (a, b) -> false,  false);
    }

    public boolean hasWatchListOwnerPermission(HttpServletRequest request, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasWatchListOwnerPermission,
                hasPermissionOperations::hasWatchListOwnerPermission, isXid);
    }

    public boolean hasWatchListReadPermission(HttpServletRequest request, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasWatchListReadPermission,
                hasPermissionOperations::hasWatchListReadPermission, isXid);
    }

    public boolean hasWatchListSetPermission(HttpServletRequest request, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasWatchListSetPermission,
                hasPermissionOperations::hasWatchListSetPermission, isXid);
    }

    public boolean hasViewOwnerPermission(HttpServletRequest request, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasViewOwnerPermission,
                hasPermissionOperations::hasViewOwnerPermission, isXid);
    }

    public boolean hasViewReadPermission(HttpServletRequest request, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasViewReadPermission,
                hasPermissionOperations::hasViewReadPermission, isXid);
    }

    public boolean hasViewSetPermission(HttpServletRequest request, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasViewSetPermission,
                hasPermissionOperations::hasViewSetPermission, isXid);
    }

    public boolean hasDataPointReadPermission(HttpServletRequest request, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasDataPointReadPermission,
                hasPermissionOperations::hasDataPointReadPermission, isXid);
    }

    public boolean hasDataPointSetPermission(HttpServletRequest request, boolean isXid) {
        return hasPermission(request,hasPermissionOperations::hasDataPointSetPermission,
                hasPermissionOperations::hasDataPointSetPermission, isXid);
    }

    public boolean hasDataSourceReadPermission(HttpServletRequest request, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasDataSourceReadPermission,
                hasPermissionOperations::hasDataSourceReadPermission, isXid);
    }

    public boolean hasReportOwnerPermission(HttpServletRequest request, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasReportOwnerPermission,
                hasPermissionOperations::hasReportOwnerPermission, isXid);
    }

    public boolean hasReportReadPermission(HttpServletRequest request, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasReportReadPermission,
                hasPermissionOperations::hasReportReadPermission, isXid);
    }

    public boolean hasReportSetPermission(HttpServletRequest request, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasReportSetPermission,
                hasPermissionOperations::hasReportSetPermission, isXid);
    }

    public boolean hasReportInstanceOwnerPermission(HttpServletRequest request, boolean isXid) {
        if(isXid) {
            LOG.warn(ARG_IS_XID_IS_NOT_SUPPORTED);
            return false;
        }
        return hasPermission(request, hasPermissionOperations::hasReportInstanceOwnerPermission, (a, b) -> false, false);
    }

    public boolean hasReportInstanceReadPermission(HttpServletRequest request, boolean isXid) {
        if(isXid) {
            LOG.warn(ARG_IS_XID_IS_NOT_SUPPORTED);
            return false;
        }
        return hasPermission(request, hasPermissionOperations::hasReportInstanceReadPermission, (a, b) -> false, false);
    }

    public boolean hasReportInstanceSetPermission(HttpServletRequest request, boolean isXid) {
        if(isXid) {
            LOG.warn(ARG_IS_XID_IS_NOT_SUPPORTED);
            return false;
        }
        return hasPermission(request, hasPermissionOperations::hasReportInstanceSetPermission, (a, b) -> false, false);
    }

    public boolean hasWatchListOwnerPermission(HttpServletRequest request, boolean isXid, String idName) {
        return hasPermission(request, hasPermissionOperations::hasWatchListOwnerPermission,
                hasPermissionOperations::hasWatchListOwnerPermission, isXid, idName);
    }

    public boolean hasWatchListReadPermission(HttpServletRequest request, boolean isXid, String idName) {
        return hasPermission(request, hasPermissionOperations::hasWatchListReadPermission,
                hasPermissionOperations::hasWatchListReadPermission, isXid, idName);
    }

    public boolean hasWatchListSetPermission(HttpServletRequest request, boolean isXid, String idName) {
        return hasPermission(request, hasPermissionOperations::hasWatchListSetPermission,
                hasPermissionOperations::hasWatchListSetPermission, isXid, idName);
    }

    public boolean hasViewOwnerPermission(HttpServletRequest request, boolean isXid, String idName) {
        return hasPermission(request, hasPermissionOperations::hasViewOwnerPermission,
                hasPermissionOperations::hasViewOwnerPermission, isXid, idName);
    }

    public boolean hasViewReadPermission(HttpServletRequest request, boolean isXid, String idName) {
        return hasPermission(request, hasPermissionOperations::hasViewReadPermission,
                hasPermissionOperations::hasViewReadPermission, isXid, idName);
    }

    public boolean hasViewSetPermission(HttpServletRequest request, boolean isXid, String idName) {
        return hasPermission(request, hasPermissionOperations::hasViewSetPermission,
                hasPermissionOperations::hasViewSetPermission, isXid, idName);
    }

    public boolean hasDataPointReadPermission(HttpServletRequest request, boolean isXid, String idName) {
        return hasPermission(request, hasPermissionOperations::hasDataPointReadPermission,
                hasPermissionOperations::hasDataPointReadPermission, isXid, idName);
    }

    public boolean hasDataPointSetPermission(HttpServletRequest request, boolean isXid, String idName) {
        return hasPermission(request,hasPermissionOperations::hasDataPointSetPermission,
                hasPermissionOperations::hasDataPointSetPermission, isXid, idName);
    }

    public boolean hasDataSourceReadPermission(HttpServletRequest request, boolean isXid, String idName) {
        return hasPermission(request, hasPermissionOperations::hasDataSourceReadPermission,
                hasPermissionOperations::hasDataSourceReadPermission, isXid, idName);
    }

    public boolean hasReportOwnerPermission(HttpServletRequest request, boolean isXid, String idName) {
        return hasPermission(request, hasPermissionOperations::hasReportOwnerPermission,
                hasPermissionOperations::hasReportOwnerPermission, isXid, idName);
    }

    public boolean hasReportReadPermission(HttpServletRequest request, boolean isXid, String idName) {
        return hasPermission(request, hasPermissionOperations::hasReportReadPermission,
                hasPermissionOperations::hasReportReadPermission, isXid, idName);
    }

    public boolean hasReportSetPermission(HttpServletRequest request, boolean isXid, String idName) {
        return hasPermission(request, hasPermissionOperations::hasReportSetPermission,
                hasPermissionOperations::hasReportSetPermission, isXid, idName);
    }

    public boolean hasReportInstanceOwnerPermission(HttpServletRequest request, boolean isXid, String idName) {
        if(isXid) {
            LOG.warn(ARG_IS_XID_IS_NOT_SUPPORTED);
            return false;
        }
        return hasPermission(request, hasPermissionOperations::hasReportInstanceOwnerPermission, (a, b) -> false, false, idName);
    }

    public boolean hasReportInstanceReadPermission(HttpServletRequest request, boolean isXid, String idName) {
        if(isXid) {
            LOG.warn(ARG_IS_XID_IS_NOT_SUPPORTED);
            return false;
        }
        return hasPermission(request, hasPermissionOperations::hasReportInstanceReadPermission, (a, b) -> false,false, idName);
    }

    public boolean hasReportInstanceSetPermission(HttpServletRequest request, boolean isXid, String idName) {
        if(isXid) {
            LOG.warn(ARG_IS_XID_IS_NOT_SUPPORTED);
            return false;
        }
        return hasPermission(request, hasPermissionOperations::hasReportInstanceSetPermission, (a, b) -> false, false, idName);
    }

    private static boolean hasPermission(HttpServletRequest request,
                                         BiPredicate<User, Integer> permissionById,
                                         BiPredicate<User, String> permissionByXid,
                                         boolean isXid) {
        if(isXid) {
            return hasPermission(request, permissionById, permissionByXid, true, "xid");
        } else {
            return hasPermission(request, permissionById, permissionByXid, false, "id");
        }
    }

    private static boolean hasPermission(HttpServletRequest request,
                                         BiPredicate<User, Integer> permissionById,
                                         BiPredicate<User, String> permissionByXid) {
        return hasPermission(request, permissionById, permissionByXid, false) ||
                hasPermission(request, permissionById, permissionByXid, true);
    }

    private static boolean hasPermission(HttpServletRequest request,
                                         BiPredicate<User, Integer> permissionById,
                                         BiPredicate<User, String> permissionByXid,
                                         boolean isXid,
                                         String idName) {
        return HttpParameterUtils.getValueOnlyRequest(idName, request, identifier ->
                        doHasPermission(request, permissionById, permissionByXid, identifier, isXid))
                .orElse(false);
    }
}