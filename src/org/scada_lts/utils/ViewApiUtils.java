package org.scada_lts.utils;

import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.ViewService;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.web.mvc.api.dto.view.GraphicalViewDTO;
import org.scada_lts.web.mvc.api.dto.view.components.GraphicalViewComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.PointComponentDTO;

import java.util.List;
import java.util.Optional;

import static com.serotonin.mango.view.component.ViewComponent.getImplementations;
import static org.scada_lts.utils.ValidationUtils.*;
import static org.scada_lts.utils.ValidationUtils.msgIfNull;
import static org.scada_lts.web.mvc.api.dto.view.components.GraphicalViewComponentDTO.isPointComponent;

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

    public static String validateGraphicalViewDTO(GraphicalViewDTO body, User user) {
        String msg = msgIfNullOrInvalid("Correct xid;", body.getXid(), StringUtils::isEmpty);
        msg += msgIfInvalid("Xid cannot be longer than 50;", body.getXid(), 50, StringUtils::isLengthGreaterThan);
        msg += msgIfNullOrInvalid("Correct name;", body.getName(), StringUtils::isEmpty);
        msg += msgIfInvalid("Name cannot be longer than 100;", body.getName(), 100, StringUtils::isLengthGreaterThan);
        msg += validateViewComponentsList(body.getViewComponents(), user);
        return msg;
    }

    private static String validateViewComponentsList(List<GraphicalViewComponentDTO> components, User user) {
        StringBuilder msg = new StringBuilder();
        if (components != null) {
            for (GraphicalViewComponentDTO component : components) {
                msg.append(msgIfNull("Correct view component;", component));
                if (component != null) {
                    msg.append(validateViewComponentBody(component));
                    ImplDefinition def = ImplDefinition.findByName(getImplementations(), component.getDefName());
                    if (isPointComponent(def)) {
                        PointComponentDTO pointComponentDTO = (PointComponentDTO) component;
                        msg.append(validateDataPoint(pointComponentDTO.getDataPointXid(), user));
                    }
                }
            }
        }
        return msg.toString();
    }

    private static String validateViewComponentBody(GraphicalViewComponentDTO component) {
        String msg = msgIfNonNullAndInvalid("Correct x, it must be >= 0, value {0};", component.getX(), a -> a < 0);
        msg += msgIfNonNullAndInvalid("Correct y, it must be >= 0, value {0};", component.getY(), a -> a < 0);
        msg += msgIfNonNullAndInvalid("Correct z, it must be >= 0, value {0};", component.getZ(), a -> a < 0);
        return msg;
    }

    private static String validateDataPoint(String xid, User user) {
        Optional<DataPointVO> dataPoint = Optional.ofNullable(new DataPointService().getDataPoint(xid));
        StringBuilder msg = new StringBuilder();
        if (dataPoint.isEmpty())
            msg.append("DataPoint doesn't exist;");
        if (dataPoint.isPresent() && !Permissions.hasDataPointReadPermission(user, dataPoint.get()))
            msg.append("No permission to read datapoint for current user;");
        return msg.toString();
    }
}
