package org.scada_lts.web.mvc.api.dto.view.components.point.imageset;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.view.component.BinaryGraphicComponent;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class BinaryGraphicComponentDTO extends ImageSetComponentDTO {

    private Integer zeroImage;
    private Integer oneImage;

    public BinaryGraphicComponentDTO() {
    }

    public BinaryGraphicComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName, String dataPointXid, String nameOverride, Boolean settableOverride, String bkgdColorOverride, Boolean displayControls, String imageSetId, Boolean displayText, Integer zeroImage, Integer oneImage) {
        super(index, idSuffix, x, y, z, typeName, dataPointXid, nameOverride, settableOverride, bkgdColorOverride, displayControls, imageSetId, displayText);
        this.zeroImage = zeroImage;
        this.oneImage = oneImage;
    }

    public Integer getZeroImage() {
        return zeroImage;
    }

    public void setZeroImage(Integer zeroImage) {
        this.zeroImage = zeroImage;
    }

    public Integer getOneImage() {
        return oneImage;
    }

    public void setOneImage(Integer oneImage) {
        this.oneImage = oneImage;
    }

    @Override
    public BinaryGraphicComponent createFromBody(User user) {
        BinaryGraphicComponent c = new BinaryGraphicComponent();
        c.setOneImage(oneImage);
        c.setZeroImage(zeroImage);

        c.tsetImageSet(getImageSet());
        c.setDisplayText(getDisplayText());

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
