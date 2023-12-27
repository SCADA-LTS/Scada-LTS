package com.serotonin.mango.util;

import com.serotonin.mango.Common;
import com.serotonin.mango.view.View;
import org.scada_lts.mango.service.ViewService;
import org.scada_lts.utils.HttpParameterUtils;

import javax.servlet.http.HttpServletRequest;

public final class ViewControllerUtils {

    public static final String VIEW_PREFIX = "view_";

    private ViewControllerUtils() {}

    public static View getOrEmptyView(HttpServletRequest request, ViewService viewService, boolean edit) {
        View view = edit ? getViewCurrentEdit(request) : getViewCurrent(request, viewService, edit);
        if(view == null) {
            view = HttpParameterUtils.getObject("emptyView", request, View.class).orElseGet(View::new);
        }
        return view;
    }

    public static View getView(int viewId, HttpServletRequest request, ViewService viewService, boolean edit) {
        View view = null;
        if(viewId != Common.NEW_ID) {
            if (!edit) {
                view = viewService.getView(viewId);
            } else {
                view = HttpParameterUtils.getObject(VIEW_PREFIX + viewId, request, View.class)
                        .orElse(null);
            }
        }
        if(view == null)
            view = getOrEmptyView(request, viewService, edit);
        return view;
    }

    public static View getViewCurrent(HttpServletRequest request, ViewService viewService) {
        return getViewCurrent(request, viewService, false);
    }

    public static View getViewCurrent(HttpServletRequest request, ViewService viewService, boolean edit) {
        View view = null;
        int viewId = getViewId(request);
        if(viewId != Common.NEW_ID) {
            view = viewService.getView(viewId);
        }
        if(view == null) {
            String viewXid = getViewXid(request);
            if (!viewXid.isBlank()) {
                view = viewService.getViewByXid(viewXid);
            }
        }
        if(view == null)
            return null;
        return edit ? view.copy() : view;
    }

    private static View getViewCurrentEdit(HttpServletRequest request) {
        View view = null;
        int viewId = getViewId(request);
        if(viewId != Common.NEW_ID) {
            view = HttpParameterUtils.getObject(VIEW_PREFIX + viewId, request, View.class)
                    .orElse(null);
        }
        if(view == null) {
            String viewXid = getViewXid(request);
            if (!viewXid.isBlank()) {
                view = HttpParameterUtils.getObject(VIEW_PREFIX + viewXid, request, View.class)
                        .orElse(null);
            }
        }
        return view;
    }

    private static int getViewId(HttpServletRequest request) {
        return HttpParameterUtils.getValueOnlyRequest("viewId", request, Integer::valueOf).orElse(Common.NEW_ID);
    }

    private static String getViewXid(HttpServletRequest request) {
        return HttpParameterUtils.getValueOnlyRequest("viewXid", request, a -> a).orElse("");
    }
}
