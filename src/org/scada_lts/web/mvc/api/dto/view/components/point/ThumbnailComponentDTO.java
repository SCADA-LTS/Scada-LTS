package org.scada_lts.web.mvc.api.dto.view.components.point;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.view.component.ThumbnailComponent;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class ThumbnailComponentDTO extends PointComponentDTO {
    private Integer scalePercent;

    public ThumbnailComponentDTO() {
    }

    public ThumbnailComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName, String dataPointXid, String nameOverride, Boolean settableOverride, String bkgdColorOverride, Boolean displayControls, Integer scalePercent) {
        super(index, idSuffix, x, y, z, typeName, dataPointXid, nameOverride, settableOverride, bkgdColorOverride, displayControls);
        this.scalePercent = scalePercent;
    }

    public Integer getScalePercent() {
        return scalePercent;
    }

    public void setScalePercent(Integer scalePercent) {
        this.scalePercent = scalePercent;
    }

    @Override
    public ThumbnailComponent createFromBody(User user) {
        ThumbnailComponent c = new ThumbnailComponent();
        c.setScalePercent(scalePercent);

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
