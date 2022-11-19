package org.scada_lts.web.mvc.api.security;

import javax.servlet.http.HttpServletRequest;

public class Guard {

    private final WithIdentifierGuard withIdentifierGuard;
    private final GetIdentifierFromHttpParameterGuard getIdentifierFromHttpParameterGuard;

    public Guard(WithIdentifierGuard withIdentifierGuard,
                 GetIdentifierFromHttpParameterGuard getIdentifierFromHttpParameterGuard) {
        this.withIdentifierGuard = withIdentifierGuard;
        this.getIdentifierFromHttpParameterGuard = getIdentifierFromHttpParameterGuard;
    }

    public boolean hasUserOwnerPermission(HttpServletRequest request, int id) {
        return withIdentifierGuard.hasUserOwnerPermission(request, id);
    }

    public boolean hasWatchListOwnerPermission(HttpServletRequest request, String id, boolean isXid) {
        return withIdentifierGuard.hasWatchListOwnerPermission(request, id, isXid);
    }

    public boolean hasWatchListReadPermission(HttpServletRequest request, String id, boolean isXid) {
        return withIdentifierGuard.hasWatchListReadPermission(request, id, isXid);
    }

    public boolean hasWatchListSetPermission(HttpServletRequest request, String id, boolean isXid) {
        return withIdentifierGuard.hasWatchListSetPermission(request, id, isXid);
    }

    public boolean hasViewOwnerPermission(HttpServletRequest request, String id, boolean isXid) {
        return withIdentifierGuard.hasViewOwnerPermission(request, id, isXid);
    }

    public boolean hasViewReadPermission(HttpServletRequest request, String id, boolean isXid) {
        return withIdentifierGuard.hasViewReadPermission(request, id, isXid);
    }

    public boolean hasViewSetPermission(HttpServletRequest request, String id, boolean isXid) {
        return withIdentifierGuard.hasViewSetPermission(request, id, isXid);
    }

    public boolean hasDataPointReadPermission(HttpServletRequest request, String id, boolean isXid) {
        return withIdentifierGuard.hasDataPointReadPermission(request, id, isXid);
    }

    public boolean hasDataPointSetPermission(HttpServletRequest request, String id, boolean isXid) {
        return withIdentifierGuard.hasDataPointSetPermission(request, id, isXid);
    }

    public boolean hasDataSourceReadPermission(HttpServletRequest request, String id, boolean isXid) {
        return withIdentifierGuard.hasDataSourceReadPermission(request, id, isXid);
    }

    public boolean hasReportOwnerPermission(HttpServletRequest request, String id, boolean isXid) {
        return withIdentifierGuard.hasReportOwnerPermission(request, id, isXid);
    }

    public boolean hasReportReadPermission(HttpServletRequest request, String id, boolean isXid) {
        return withIdentifierGuard.hasReportReadPermission(request, id, isXid);
    }

    public boolean hasReportSetPermission(HttpServletRequest request, String id, boolean isXid) {
        return withIdentifierGuard.hasReportSetPermission(request, id, isXid);
    }

    public boolean hasWatchListOwnerPermission(HttpServletRequest request) {
        return getIdentifierFromHttpParameterGuard.hasWatchListOwnerPermission(request);
    }

    public boolean hasWatchListReadPermission(HttpServletRequest request) {
        return getIdentifierFromHttpParameterGuard.hasWatchListReadPermission(request);
    }

    public boolean hasWatchListSetPermission(HttpServletRequest request) {
        return getIdentifierFromHttpParameterGuard.hasWatchListSetPermission(request);
    }

    public boolean hasViewOwnerPermission(HttpServletRequest request) {
        return getIdentifierFromHttpParameterGuard.hasViewOwnerPermission(request);
    }

    public boolean hasViewReadPermission(HttpServletRequest request) {
        return getIdentifierFromHttpParameterGuard.hasViewReadPermission(request);
    }

    public boolean hasViewSetPermission(HttpServletRequest request) {
        return getIdentifierFromHttpParameterGuard.hasViewSetPermission(request);
    }

    public boolean hasDataPointReadPermission(HttpServletRequest request) {
        return getIdentifierFromHttpParameterGuard.hasDataPointReadPermission(request);
    }

    public boolean hasDataPointSetPermission(HttpServletRequest request) {
        return getIdentifierFromHttpParameterGuard.hasDataPointSetPermission(request);
    }

    public boolean hasDataSourceReadPermission(HttpServletRequest request) {
        return getIdentifierFromHttpParameterGuard.hasDataSourceReadPermission(request);
    }

    public boolean hasReportOwnerPermission(HttpServletRequest request) {
        return getIdentifierFromHttpParameterGuard.hasReportOwnerPermission(request);
    }

    public boolean hasReportReadPermission(HttpServletRequest request) {
        return getIdentifierFromHttpParameterGuard.hasReportReadPermission(request);
    }

    public boolean hasReportSetPermission(HttpServletRequest request) {
        return getIdentifierFromHttpParameterGuard.hasReportSetPermission(request);
    }

    public boolean hasWatchListOwnerPermission(HttpServletRequest request, boolean isXid) {
        return getIdentifierFromHttpParameterGuard.hasWatchListOwnerPermission(request, isXid);
    }

    public boolean hasWatchListReadPermission(HttpServletRequest request, boolean isXid) {
        return getIdentifierFromHttpParameterGuard.hasWatchListReadPermission(request, isXid);
    }

    public boolean hasWatchListSetPermission(HttpServletRequest request, boolean isXid) {
        return getIdentifierFromHttpParameterGuard.hasWatchListSetPermission(request, isXid);
    }

    public boolean hasViewOwnerPermission(HttpServletRequest request, boolean isXid) {
        return getIdentifierFromHttpParameterGuard.hasViewOwnerPermission(request, isXid);
    }

    public boolean hasViewReadPermission(HttpServletRequest request, boolean isXid) {
        return getIdentifierFromHttpParameterGuard.hasViewReadPermission(request, isXid);
    }

    public boolean hasViewSetPermission(HttpServletRequest request, boolean isXid) {
        return getIdentifierFromHttpParameterGuard.hasViewSetPermission(request, isXid);
    }

    public boolean hasDataPointReadPermission(HttpServletRequest request, boolean isXid) {
        return getIdentifierFromHttpParameterGuard.hasDataPointReadPermission(request, isXid);
    }

    public boolean hasDataPointSetPermission(HttpServletRequest request, boolean isXid) {
        return getIdentifierFromHttpParameterGuard.hasDataPointSetPermission(request, isXid);
    }

    public boolean hasDataSourceReadPermission(HttpServletRequest request, boolean isXid) {
        return getIdentifierFromHttpParameterGuard.hasDataSourceReadPermission(request, isXid);
    }

    public boolean hasReportOwnerPermission(HttpServletRequest request, boolean isXid) {
        return getIdentifierFromHttpParameterGuard.hasReportOwnerPermission(request, isXid);
    }

    public boolean hasReportReadPermission(HttpServletRequest request, boolean isXid) {
        return getIdentifierFromHttpParameterGuard.hasReportReadPermission(request, isXid);
    }

    public boolean hasReportSetPermission(HttpServletRequest request, boolean isXid) {
        return getIdentifierFromHttpParameterGuard.hasReportSetPermission(request, isXid);
    }

    public boolean hasWatchListOwnerPermission(HttpServletRequest request, boolean isXid, String idName) {
        return getIdentifierFromHttpParameterGuard.hasWatchListOwnerPermission(request, isXid, idName);
    }

    public boolean hasWatchListReadPermission(HttpServletRequest request, boolean isXid, String idName) {
        return getIdentifierFromHttpParameterGuard.hasWatchListReadPermission(request, isXid, idName);
    }

    public boolean hasWatchListSetPermission(HttpServletRequest request, boolean isXid, String idName) {
        return getIdentifierFromHttpParameterGuard.hasWatchListSetPermission(request, isXid, idName);
    }

    public boolean hasViewOwnerPermission(HttpServletRequest request, boolean isXid, String idName) {
        return getIdentifierFromHttpParameterGuard.hasViewOwnerPermission(request, isXid, idName);
    }

    public boolean hasViewReadPermission(HttpServletRequest request, boolean isXid, String idName) {
        return getIdentifierFromHttpParameterGuard.hasViewReadPermission(request, isXid, idName);
    }

    public boolean hasViewSetPermission(HttpServletRequest request, boolean isXid, String idName) {
        return getIdentifierFromHttpParameterGuard.hasViewSetPermission(request, isXid, idName);
    }

    public boolean hasDataPointReadPermission(HttpServletRequest request, boolean isXid, String idName) {
        return getIdentifierFromHttpParameterGuard.hasDataPointReadPermission(request, isXid, idName);
    }

    public boolean hasDataPointSetPermission(HttpServletRequest request, boolean isXid, String idName) {
        return getIdentifierFromHttpParameterGuard.hasDataPointSetPermission(request, isXid, idName);
    }

    public boolean hasDataSourceReadPermission(HttpServletRequest request, boolean isXid, String idName) {
        return getIdentifierFromHttpParameterGuard.hasDataSourceReadPermission(request, isXid, idName);
    }

    public boolean hasReportOwnerPermission(HttpServletRequest request, boolean isXid, String idName) {
        return getIdentifierFromHttpParameterGuard.hasReportOwnerPermission(request, isXid, idName);
    }

    public boolean hasReportReadPermission(HttpServletRequest request, boolean isXid, String idName) {
        return getIdentifierFromHttpParameterGuard.hasReportReadPermission(request, isXid, idName);
    }

    public boolean hasReportSetPermission(HttpServletRequest request, boolean isXid, String idName) {
        return getIdentifierFromHttpParameterGuard.hasReportSetPermission(request, isXid, idName);
    }

    public boolean hasReportInstanceOwnerPermission(HttpServletRequest request, String id, boolean isXid) {
        return withIdentifierGuard.hasReportInstanceOwnerPermission(request, id, isXid);
    }

    public boolean hasReportInstanceReadPermission(HttpServletRequest request, String id, boolean isXid) {
        return withIdentifierGuard.hasReportInstanceReadPermission(request, id, isXid);
    }

    public boolean hasReportInstanceSetPermission(HttpServletRequest request, String id, boolean isXid) {
        return withIdentifierGuard.hasReportInstanceSetPermission(request, id, isXid);
    }

    public boolean hasReportInstanceOwnerPermission(HttpServletRequest request) {
        return getIdentifierFromHttpParameterGuard.hasReportInstanceOwnerPermission(request);
    }

    public boolean hasReportInstanceReadPermission(HttpServletRequest request) {
        return getIdentifierFromHttpParameterGuard.hasReportInstanceReadPermission(request);
    }

    public boolean hasReportInstanceSetPermission(HttpServletRequest request) {
        return getIdentifierFromHttpParameterGuard.hasReportInstanceSetPermission(request);
    }

    public boolean hasReportInstanceOwnerPermission(HttpServletRequest request, boolean isXid) {
        return getIdentifierFromHttpParameterGuard.hasReportInstanceOwnerPermission(request, isXid);
    }

    public boolean hasReportInstanceReadPermission(HttpServletRequest request, boolean isXid) {
        return getIdentifierFromHttpParameterGuard.hasReportInstanceReadPermission(request, isXid);
    }

    public boolean hasReportInstanceSetPermission(HttpServletRequest request, boolean isXid) {
        return getIdentifierFromHttpParameterGuard.hasReportInstanceSetPermission(request, isXid);
    }

    public boolean hasReportInstanceOwnerPermission(HttpServletRequest request, boolean isXid, String idName) {
        return getIdentifierFromHttpParameterGuard.hasReportInstanceOwnerPermission(request, isXid, idName);
    }

    public boolean hasReportInstanceReadPermission(HttpServletRequest request, boolean isXid, String idName) {
        return getIdentifierFromHttpParameterGuard.hasReportInstanceReadPermission(request, isXid, idName);
    }

    public boolean hasReportInstanceSetPermission(HttpServletRequest request, boolean isXid, String idName) {
        return getIdentifierFromHttpParameterGuard.hasReportInstanceSetPermission(request, isXid, idName);
    }

    public String viewIdentifier(String value) {
        if(value.contains("-")) {
            String[] values = value.split("-");
            if(values.length == 2) {
                return values[0];
            }
        }
        return value;
    }
}
