package org.scada_lts.web.mvc.api.dto.view.components.point;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.Common;
import com.serotonin.mango.view.DynamicImage;
import com.serotonin.mango.view.component.DynamicGraphicComponent;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class DynamicGraphicComponentDTO extends PointComponentDTO {
    private String dynamicImageId;
    private Boolean displayText;
    private Double min;
    private Double max;

    public DynamicGraphicComponentDTO() {
    }

    public DynamicGraphicComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName, String dataPointXid, String nameOverride, Boolean settableOverride, String bkgdColorOverride, Boolean displayControls, String dynamicImageId, Boolean displayText, Double min, Double max) {
        super(index, idSuffix, x, y, z, typeName, dataPointXid, nameOverride, settableOverride, bkgdColorOverride, displayControls);
        this.dynamicImageId = dynamicImageId;
        this.displayText = displayText;
        this.min = min;
        this.max = max;
    }

    public String getDynamicImageId() {
        return dynamicImageId;
    }

    public void setDynamicImageId(String dynamicImageId) {
        this.dynamicImageId = dynamicImageId;
    }

    public Boolean getDisplayText() {
        return displayText;
    }

    public void setDisplayText(Boolean displayText) {
        this.displayText = displayText;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    private DynamicImage getDynamicImage() {
        return Common.ctx.getDynamicImage(dynamicImageId);
    }

    @Override
    public DynamicGraphicComponent createFromBody(User user) {

        DynamicGraphicComponent c = new DynamicGraphicComponent();
        c.tsetDynamicImage(getDynamicImage());
        c.setDisplayText(displayText);
        c.setMax(max);
        c.setMin(min);

        c.setIndex(getIndex());
        c.setIdSuffix(getIdSuffix());
        c.setX(getX());
        c.setY(getY());
        c.setZ(getZ());

        DataPointVO dataPoint = getDataPoint();
        c.tsetDataPoint(dataPoint);
        c.setNameOverride(getNameOverride());
        c.setSettableOverride(getSettableOverride() && Permissions.hasDataPointSetPermission(user, dataPoint));
        c.setBkgdColorOverride(getBkgdColorOverride());
        c.setDisplayControls(getDisplayControls());
        c.validateDataPoint(user, false);
        resetPointComponent(c);
        return c;
    }
}
