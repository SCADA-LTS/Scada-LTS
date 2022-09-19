package com.serotonin.mango.util;

import com.serotonin.mango.Common;
import com.serotonin.mango.view.View;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.ViewService;
import org.scada_lts.utils.HttpParameterUtils;

import javax.servlet.http.HttpServletRequest;

public final class ViewControllerUtils {

    private ViewControllerUtils() {}

    private static final Log LOG = LogFactory.getLog(ViewControllerUtils.class);

    public static View getOrEmptyView(HttpServletRequest request, ViewService viewService) {
        View view = getViewCurrent(request, viewService);
        if(view == null) {
            view = HttpParameterUtils.getObject("emptyView", request, View.class).orElseGet(View::new);
        }
        return view;
    }

    public static View getViewCurrent(HttpServletRequest request, ViewService viewService) {
        int viewId = getViewId(request);
        if(viewId != Common.NEW_ID) {
            return viewService.getView(viewId);
        }
        String viewXid = getViewXid(request);
        if(viewXid.isBlank()) {
            return null;
        }
        return viewService.getViewByXid(viewXid);
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
