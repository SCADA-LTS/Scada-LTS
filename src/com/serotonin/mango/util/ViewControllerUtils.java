package com.serotonin.mango.util;

import com.serotonin.mango.Common;
import com.serotonin.mango.view.View;
import org.scada_lts.mango.service.ViewService;
import org.scada_lts.utils.HttpParameterUtils;

import javax.servlet.http.HttpServletRequest;

public final class ViewControllerUtils {

    private ViewControllerUtils() {}

    public static View getOrEmptyView(HttpServletRequest request, ViewService viewService, boolean forceFromDatabase) {
        View view = getViewCurrent(request, viewService, forceFromDatabase);
        if(view == null) {
            view = HttpParameterUtils.getObject("emptyView", request, View.class).orElseGet(View::new);
        }
        return view;
    }

    public static View getOrEmptyView(HttpServletRequest request, ViewService viewService) {
        return getOrEmptyView(request, viewService, false);
    }

    public static View getViewCurrent(HttpServletRequest request, ViewService viewService, boolean forceFromDatabase) {
        int viewId = getViewId(request);
        if(viewId != Common.NEW_ID) {
            return viewService.getView(viewId, forceFromDatabase);
        }
        String viewXid = getViewXid(request);
        if(!viewXid.isBlank()) {
            return viewService.getViewByXid(viewXid, forceFromDatabase);
        }
        return null;
    }

    public static View getViewCurrent(HttpServletRequest request, ViewService viewService) {
        return getViewCurrent(request, viewService, false);
    }

    public static View getView(int viewId, HttpServletRequest request, ViewService viewService) {
        View view;
        if(viewId != Common.NEW_ID) {
            view = viewService.getView(viewId);
        } else {
            view = getOrEmptyView(request, viewService);
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
