package com.serotonin.mango.util;

import com.serotonin.mango.Common;
import com.serotonin.mango.view.View;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.mango.service.ViewService;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.utils.HttpParameterUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ResourceBundle;

public final class ViewControllerUtils {

    public static final String VIEW_PREFIX = "view_";

    private ViewControllerUtils() {}

    public static View getOrEmptyView(HttpServletRequest request, ViewService viewService, boolean edit) {
        View view = edit ? getViewCurrentEdit(request) : getViewCurrent(request, viewService, edit);
        if(view == null) {
            view = HttpParameterUtils.getObject("emptyView", request, View.class).orElseGet(View::new);
        }
        String viewName = HttpParameterUtils.getValue("view.name", request, a -> a).orElse("");
        if(!StringUtils.isEmpty(viewName)) {
            view.setName(viewName);
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


    public static View copyAndSaveView(View view, ViewService viewService) {
        View viewCopy = view.copy();
        viewCopy.setId(Common.NEW_ID);
        viewCopy.setXid(viewService.generateUniqueXid());
        viewCopy.setName(generateCopyName(Common.getBundle(), view.getName(), 250));
        /*viewCopy.setViewComponents(view.getViewComponents());
        viewCopy.setViewUsers(view.getViewUsers());
        viewCopy.setResolution(view.getResolution());
        viewCopy.setBackgroundFilename(view.getBackgroundFilename());
        viewCopy.setUserId(view.getUserId());
        viewCopy.setAnonymousAccess(view.getAnonymousAccess());*/

        viewService.saveView(viewCopy);

        return viewCopy;
    }

    public static String generateCopyName(ResourceBundle bundle, String name, int length) {
        return StringUtils.truncate(LocalizableMessage.getMessage(bundle, "common.copyPrefix", name), length);
    }
}
