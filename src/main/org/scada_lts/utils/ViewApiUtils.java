package org.scada_lts.utils;

import br.org.scadabr.view.component.*;
import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.view.DynamicImage;
import com.serotonin.mango.view.ImageSet;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.mango.view.View;
import com.serotonin.mango.view.component.*;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.ViewService;
import org.scada_lts.permissions.service.GetDataPointsWithAccess;
import org.scada_lts.serorepl.utils.StringUtils;
import org.scada_lts.web.mvc.api.dto.view.GraphicalViewDTO;
import org.scada_lts.web.mvc.api.dto.view.components.*;
import org.scada_lts.web.mvc.api.dto.view.components.compound.CompoundComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.compound.EnhancedImageChartComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.compound.ImageChartComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.html.ChartComparatorComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.html.FlexBuilderComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.html.LinkComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.html.ScriptButtonComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.point.DynamicGraphicComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.point.PointComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.point.ThumbnailComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.point.imageset.AnalogGraphicComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.point.imageset.BinaryGraphicComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.point.imageset.ImageSetComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.point.imageset.MultistateGraphicComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.point.script.ButtonComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.point.script.ScriptComponentDTO;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.serotonin.mango.view.component.ViewComponent.getImplementations;
import static org.scada_lts.serorepl.utils.StringUtils.isEmpty;
import static org.scada_lts.utils.UpdateValueUtils.setIf;
import static org.scada_lts.utils.ValidationUtils.*;
import static org.scada_lts.utils.ValidationUtils.msgIfNull;
import static org.scada_lts.web.mvc.api.dto.view.components.GraphicalViewComponentDTO.*;

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

    public static String validateGraphicalViewUpdate(GraphicalViewDTO body, User user) {
        String msg = msgIfNull("Correct id;", body.getId());
        msg += msgIfNonNullAndInvalid("Correct xid;", body.getXid(), StringUtils::isEmpty);
        msg += msgIfInvalid("Xid cannot be longer than 50;", body.getXid(), 50, StringUtils::isLengthGreaterThan);
        msg += msgIfNonNullAndInvalid("Correct name;", body.getName(), StringUtils::isEmpty);
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
                        if (isImageSetComponent(def)) {
                            ImageSetComponentDTO imageSetComponentDTO = (ImageSetComponentDTO) pointComponentDTO;
                            msg.append(validateImageSet(imageSetComponentDTO.getImageSetId()));
                            if (def == AnalogGraphicComponent.DEFINITION)
                                msg.append(validateAnalogGraphicComponent((AnalogGraphicComponentDTO) component));
                            if (def == BinaryGraphicComponent.DEFINITION)
                                msg.append(validateBinaryGraphicComponent((BinaryGraphicComponentDTO) component));
                            if (def == MultistateGraphicComponent.DEFINITION)
                                msg.append(validateMultiGraphicComponent((MultistateGraphicComponentDTO) component));
                        }
                        if (def == DynamicGraphicComponent.DEFINITION)
                            msg.append(validateDynamicGraphicComponent((DynamicGraphicComponentDTO) component));
                        if (def == ScriptComponent.DEFINITION)
                            msg.append(validateScriptComponent((ScriptComponentDTO) component));
                        if (def == ButtonComponent.DEFINITION)
                            msg.append(validateButtonComponent((ButtonComponentDTO) component));
                        if (def == ThumbnailComponent.DEFINITION)
                            msg.append(validateThumbnailComponent((ThumbnailComponentDTO) component));
                    }
                    if (isCompoundComponent(def)) {
                        msg.append(validateCompoundComponent((CompoundComponentDTO) component));
                        if (def == ImageChartComponent.DEFINITION)
                            msg.append(validateImageChartComponent((ImageChartComponentDTO) component));
                        if (def == EnhancedImageChartComponent.DEFINITION)
                            msg.append(validateEnhancedImageChartComponent((EnhancedImageChartComponentDTO) component));
                    }
                    if (isHtmlComponent(def)) {
                        if (def == ChartComparatorComponent.DEFINITION)
                            msg.append(validateChartComparatorComponent((ChartComparatorComponentDTO) component));
                        if (def == LinkComponent.DEFINITION)
                            msg.append(validateLinkComponent((LinkComponentDTO) component));
                        if (def == FlexBuilderComponent.DEFINITION)
                            msg.append(validateFlexBuilderComponent((FlexBuilderComponentDTO) component));
                        if (def == ScriptButtonComponent.DEFINITION)
                            msg.append(validateScriptButtonComponent((ScriptButtonComponentDTO) component));
                    }
                    if (def == AlarmListComponent.DEFINITION)
                        msg.append(validateAlarmListComponent((AlarmListComponentDTO) component));
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
        if (dataPoint.isPresent() && !GetDataPointsWithAccess.hasDataPointReadPermission(user, dataPoint.get()))
            msg.append("No permission to read datapoint for current user;");
        return msg.toString();
    }

    private static String validateLinkComponent(LinkComponentDTO body) {
        String msg = msgIfNullOrInvalid("Correct text;", body.getText(), StringUtils::isEmpty);
        msg += msgIfNullOrInvalid("Correct link;", body.getLink(), StringUtils::isEmpty);
        return msg;
    }

    private static String validateChartComparatorComponent(ChartComparatorComponentDTO body) {
        String msg = msgIfNonNullAndInvalid("Invalid width;", body.getWidth(), a -> a < 1);
        msg += msgIfNonNullAndInvalid("Invalid height;", body.getHeight(), a -> a < 1);
        return msg;
    }

    private static String validateFlexBuilderComponent(FlexBuilderComponentDTO body) {
        String msg = msgIfNonNullAndInvalid("Invalid width;", body.getWidth(), a -> a < FlexBuilderComponent.MIN_WIDTH || a > FlexBuilderComponent.MAX_WIDTH);
        msg += msgIfNonNullAndInvalid("Invalid height;", body.getHeight(), a -> a < FlexBuilderComponent.MIN_HEIGHT || a > FlexBuilderComponent.MAX_HEIGHT);
        return msg;
    }

    private static String validateAlarmListComponent(AlarmListComponentDTO body) {
        String msg = msgIfNonNullAndInvalid("Invalid width;", body.getWidth(), a -> a < 0);
        msg += msgIfNonNullAndInvalid("Invalid maxListSize;", body.getMaxListSize(), a -> a < 1);
        return msg;
    }

    private static String validateScriptButtonComponent(ScriptButtonComponentDTO body) {
        String msg = msgIfNullOrInvalid("Correct text;", body.getText(), StringUtils::isEmpty);
        msg += msgIfNullOrInvalid("Correct scriptXid;", body.getScriptXid(), StringUtils::isEmpty);
        return msg;
    }

    private static String validateAnalogGraphicComponent(AnalogGraphicComponentDTO body) {
        return msgIfInvalid("Max cannot be smaller than min;", body.getMin(), body.getMax(), (min, max) -> min >= max);
    }

    private static String validateBinaryGraphicComponent(BinaryGraphicComponentDTO body) {
        String msg = msgIfNonNullAndInvalid("Missing zero image;", body.getZeroImage(), a -> a == -1);
        msg += msgIfNonNullAndInvalid("Missing one image;", body.getOneImage(), a -> a == -1);
        return msg;
    }

    private static String validateDynamicGraphicComponent(DynamicGraphicComponentDTO body) {
        String msg = msgIfInvalid("Max cannot be smaller than min;", body.getMin(), body.getMax(), (min, max) -> min >= max);
        msg += validateDynamicImage(body.getDynamicImageId());
        return msg;
    }

    private static String validateScriptComponent(ScriptComponentDTO body) {
        return msgIfNullOrInvalid("Correct script;", body.getScript(), StringUtils::isEmpty);
    }

    private static String validateButtonComponent(ButtonComponentDTO body) {
        StringBuilder msg = new StringBuilder();
        msg.append(msgIfNonNullAndInvalid("Invalid width;", body.getWidth(), a -> a < 0));
        msg.append(msgIfNonNullAndInvalid("Invalid height;", body.getHeight(), a -> a < 0));
        msg.append(msgIfNonNullAndInvalid("Correct whenOffLabel;", body.getWhenOffLabel(), StringUtils::isEmpty));
        msg.append(msgIfNonNullAndInvalid("Correct whenOnLabel;", body.getWhenOnLabel(), StringUtils::isEmpty));
        return msg.toString();
    }

    private static String validateThumbnailComponent(ThumbnailComponentDTO body) {
        return msgIfNonNullAndInvalid("Invalid scale percent;", body.getScalePercent(), a -> a < 1);
    }

    private static String validateCompoundComponent(CompoundComponentDTO body) {
        return msgIfNullOrInvalid("Correct name;", body.getName(), StringUtils::isEmpty);
    }

    private static String validateImageChartComponent(ImageChartComponentDTO body) {
        return imageChartComponentValidation(body.getWidth(), body.getHeight(), body.getDurationType(), body.getDurationPeriods());
    }

    private static String validateEnhancedImageChartComponent(EnhancedImageChartComponentDTO body) {
        return imageChartComponentValidation(body.getWidth(), body.getHeight(), body.getDurationType(), body.getDurationPeriods());
    }

    private static String validateImageSet(String imageSetId) {
        ImageSet imageSet = Common.ctx.getImageSet(imageSetId);
        StringBuilder msg = new StringBuilder();
        if (!imageSet.isAvailable())
            msg.append("ImageSet not available;");
        return msg.toString();
    }

    private static String validateDynamicImage(String dynamicImageId) {
        DynamicImage dynamicImage = Common.ctx.getDynamicImage(dynamicImageId);
        StringBuilder msg = new StringBuilder();
        if (!dynamicImage.isAvailable())
            msg.append("DynamicImage not available;");
        return msg.toString();
    }

    private static String imageChartComponentValidation(Integer width, Integer height, Integer durationType, Integer durationPeriods) {
        StringBuilder msg = new StringBuilder();
        msg.append(msgIfNonNullAndInvalid("Invalid width;", width, a -> a < 1));
        msg.append(msgIfNonNullAndInvalid("Invalid height;", height, a -> a < 1));
        msg.append(msgIfNonNullAndInvalid("Invalid duration type;", durationType, a -> invalidDurationType(durationType)));
        msg.append(msgIfNonNullAndInvalid("Invalid duration periods;", durationPeriods, a -> a <= 0));
        return msg.toString();
    }

    private static boolean invalidDurationType(int durationType) {
        return !Common.TIME_PERIOD_CODES.isValidId(durationType);
    }

    public static Optional<View> getGraphicalView(int id, ViewService viewService) {
        try {
            View view = viewService.getView(id);
            return Optional.ofNullable(view);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public static Optional<View> getGraphicalView(String xid, ViewService viewService) {
        try {
            View view = viewService.getViewByXid(xid);
            return Optional.ofNullable(view);
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    public static boolean isViewPresent(String xid, ViewService viewService){
        return getGraphicalView(xid, viewService).isPresent();
    }

    public static void updateValueGraphicalView(View toUpdate, GraphicalViewDTO source, User user) {
        setIf(source.getXid(), toUpdate::setXid, a -> !isEmpty(a));
        setIf(source.getId(), toUpdate::setId, Objects::nonNull);
        setIf(source.getName(), toUpdate::setName, Objects::nonNull);
        setIf(source.getBackgroundFilename(), toUpdate::setBackgroundFilename, Objects::nonNull);
        setIf(source.getWidth(), toUpdate::setWidth, Objects::nonNull);
        setIf(source.getHeight(), toUpdate::setHeight, Objects::nonNull);
        setIf(source.getResolution(), toUpdate::setResolution, Objects::nonNull);
        setIf(source.getModificationTime(), toUpdate::setModificationTime, Objects::nonNull);
        setIf(source.getUserId(), toUpdate::setUserId, Objects::nonNull);
        setIf(source.getAnonymousAccess(), toUpdate::setAnonymousAccess, Objects::nonNull);
        setViewComponents(toUpdate, source, user);
    }

    public static void setViewComponents(View toUpdate, GraphicalViewDTO source, User user) {
        View view = source.createViewFromBody(user);
        setIf(view.getViewComponents(), toUpdate::setViewComponents, Objects::nonNull);
    }

    private static String validateMultiGraphicComponent(MultistateGraphicComponentDTO body) {
        StringBuilder errors = new StringBuilder();
        for(IntValuePair state: body.getImageStateList()) {
            errors.append(msgIfNullOrInvalid("Invalid state for index: " + state.getKey() + ";",
                    state.getValue(), a -> !isInteger(a)));
        }
        return errors.toString();
    }

    private static boolean isInteger(String a) {
        try {
            Integer.parseInt(a);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
