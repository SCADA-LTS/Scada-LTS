package com.serotonin.mango.web.dwr.util;

import com.serotonin.mango.Common;
import com.serotonin.mango.view.View;
import org.scada_lts.mango.service.ViewService;
import org.scada_lts.utils.HttpParameterUtils;

import javax.servlet.http.HttpServletRequest;

public final class HttpViewUtils {

    private HttpViewUtils() {}

    public static View getView(ViewService viewService, HttpServletRequest request) {
        int viewId = getViewId(request);
        if(viewId != Common.NEW_ID) {
            return viewService.getView(viewId);
        }
        String viewXid = getViewXid(request);
        return viewService.getViewByXid(viewXid);
    }

    private static int getViewId(HttpServletRequest request) {
        return HttpParameterUtils.getValue("viewId", request, Integer::valueOf).orElse(-1);
    }

    private static String getViewXid(HttpServletRequest request) {
        return HttpParameterUtils.getValue("viewXid", request, a -> a).orElse("");
    }
}
