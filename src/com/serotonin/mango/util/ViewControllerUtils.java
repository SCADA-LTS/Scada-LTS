package com.serotonin.mango.util;

import com.serotonin.mango.view.View;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.ViewService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ViewControllerUtils {

    private ViewControllerUtils() {}

    private static final Log LOG = LogFactory.getLog(ViewControllerUtils.class);

    public static View getViewCurrent(HttpServletRequest request, ViewService viewService) {
        String id = request.getParameter("viewId");
        Optional<View> result = Optional.empty();
        if(id != null) {
            result = getView(viewService::getView, () -> Integer.valueOf(id));
        }
        if(!result.isPresent()) {
            String xid = request.getParameter("viewXid");
            return getView(viewService::getViewByXid, () -> xid)
                    .orElse(null);
        }
        return result.get();
    }

    private static <A> Optional<View> getView(Function<A, View> identifier, Supplier<A> convert) {
        try {
            View currentView = identifier.apply(convert.get());
            return Optional.ofNullable(currentView);
        } catch (NumberFormatException e) {
            return Optional.empty();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return Optional.empty();
        }
    }
}
