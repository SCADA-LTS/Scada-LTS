package org.scada_lts.utils;

import com.serotonin.mango.Common;
import com.serotonin.mango.view.View;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.ViewService;

import java.util.Optional;

public final class ViewApiUtils {

    private ViewApiUtils() {
    }

    private static final Log LOG = LogFactory.getLog(ViewApiUtils.class);
    public static Optional<View> getViewByIdOrXid(Integer id, String xid, ViewService viewService) {
        try {
            View view = null;
            if (id != null) {
                view = viewService.getView(id);
            } else if (xid != null) {
                view = viewService.getViewByXid(xid);
            }
            if (view != null) {
                view.setBackgroundFilename(getBackgroundImageAbsolutePath(view.getBackgroundFilename()));
            }
            return Optional.ofNullable(view);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public static void deleteViewByIdOrXid(Integer id, String xid, ViewService viewService) {
        try {
            View view = null;
            if (id != null) {
                view = viewService.getView(id);
            } else if (xid != null) {
                view = viewService.getViewByXid(xid);
            }
            if (view != null)
                viewService.removeView(view.getId());
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public static String validView(View view) {
        return "";
    }

    private static String getBackgroundImageAbsolutePath(String backgroundImage) {
        if (backgroundImage == null || backgroundImage.isEmpty())
            return backgroundImage;
        else
            return Common.ctx.getCtx().getContextPath() + "/" + backgroundImage;
    }
}
