package org.scada_lts.web.mvc.api.dto.view.components.point.imageset;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.db.IntValuePair;
import com.serotonin.mango.view.component.MultistateGraphicComponent;
import com.serotonin.mango.view.component.deserializer.ImageStateListDeserializer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;

import java.util.List;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class MultistateGraphicComponentDTO extends ImageSetComponentDTO {

    @JsonDeserialize(using = ImageStateListDeserializer.class)
    private List<IntValuePair> imageStateList;
    private Integer defaultImage;

    public MultistateGraphicComponentDTO() {
    }

    public MultistateGraphicComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName, String dataPointXid, String nameOverride, Boolean settableOverride, String bkgdColorOverride, Boolean displayControls, String imageSetId, Boolean displayText, List<IntValuePair> imageStateList, Integer defaultImage) {
        super(index, idSuffix, x, y, z, typeName, dataPointXid, nameOverride, settableOverride, bkgdColorOverride, displayControls, imageSetId, displayText);
        this.imageStateList = imageStateList;
        this.defaultImage = defaultImage;
    }

    public Integer getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(Integer defaultImage) {
        this.defaultImage = defaultImage;
    }

    public List<IntValuePair> getImageStateList() {
        return imageStateList;
    }

    public void setImageStateList(List<IntValuePair> imageStateList) {
        this.imageStateList = imageStateList;
    }

    @Override
    public MultistateGraphicComponent createFromBody(User user) {
        MultistateGraphicComponent c = new MultistateGraphicComponent();
        c.setDefaultImage(defaultImage);
        c.setImageStateList(imageStateList);

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
