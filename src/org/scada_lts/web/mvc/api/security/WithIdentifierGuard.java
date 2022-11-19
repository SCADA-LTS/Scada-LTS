package org.scada_lts.web.mvc.api.security;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.User;
import org.scada_lts.serorepl.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.function.BiPredicate;

import static org.scada_lts.web.mvc.api.security.GuardUtils.doHasPermission;

public class WithIdentifierGuard {

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
        return hasPermission(request, hasPermissionOperations::hasWatchListOwnerPermission, hasPermissionOperations::hasWatchListOwnerPermission, id, isXid);
    }

    public boolean hasWatchListReadPermission(HttpServletRequest request, String id, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasWatchListReadPermission, hasPermissionOperations::hasWatchListReadPermission, id, isXid);
    }

    public boolean hasWatchListSetPermission(HttpServletRequest request, String id, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasWatchListSetPermission, hasPermissionOperations::hasWatchListSetPermission, id, isXid);
    }

    public boolean hasViewOwnerPermission(HttpServletRequest request, String id, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasViewOwnerPermission, hasPermissionOperations::hasViewOwnerPermission, id, isXid);
    }

    public boolean hasViewReadPermission(HttpServletRequest request, String id, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasViewReadPermission, hasPermissionOperations::hasViewReadPermission, id, isXid);
    }

    public boolean hasViewSetPermission(HttpServletRequest request, String id, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasViewSetPermission, hasPermissionOperations::hasViewSetPermission, id, isXid);
    }

    public boolean hasDataPointReadPermission(HttpServletRequest request, String id, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasDataPointReadPermission, hasPermissionOperations::hasDataPointReadPermission, id, isXid);
    }

    public boolean hasDataPointSetPermission(HttpServletRequest request, String id, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasDataPointSetPermission, hasPermissionOperations::hasDataPointSetPermission, id, isXid);
    }

    public boolean hasDataSourceReadPermission(HttpServletRequest request, String id, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasDataSourceReadPermission, hasPermissionOperations::hasDataSourceReadPermission, id, isXid);
    }

    public boolean hasReportOwnerPermission(HttpServletRequest request, String id, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasReportOwnerPermission, hasPermissionOperations::hasReportOwnerPermission, id, isXid);
    }

    public boolean hasReportReadPermission(HttpServletRequest request, String id, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasReportReadPermission, hasPermissionOperations::hasReportReadPermission, id, isXid);
    }

    public boolean hasReportSetPermission(HttpServletRequest request, String id, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasReportSetPermission, hasPermissionOperations::hasReportSetPermission, id, isXid);
    }

    public boolean hasReportInstanceOwnerPermission(HttpServletRequest request, String id, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasReportInstanceOwnerPermission, null, id, false);
    }

    public boolean hasReportInstanceReadPermission(HttpServletRequest request, String id, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasReportInstanceReadPermission, null, id, false);
    }

    public boolean hasReportInstanceSetPermission(HttpServletRequest request, String id, boolean isXid) {
        return hasPermission(request, hasPermissionOperations::hasReportInstanceSetPermission, null, id, false);
    }

    private static boolean hasPermission(HttpServletRequest request,
                                         BiPredicate<User, Integer> permissionById,
                                         BiPredicate<User, String> permissionByXid,
                                         String identifier,
                                         boolean isXid) {
        if(StringUtils.isEmpty(identifier))
            return false;
        if(identifier.contains(",")) {
            String[] identifiers = identifier.split(",");
            for(String id: identifiers) {
                if(!doHasPermission(request, permissionById, permissionByXid, id, isXid))
                    return false;
            }
            return true;
        }
        return doHasPermission(request, permissionById, permissionByXid, identifier, isXid);
    }
}