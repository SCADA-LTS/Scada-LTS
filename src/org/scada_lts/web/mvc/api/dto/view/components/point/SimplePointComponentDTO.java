package org.scada_lts.web.mvc.api.dto.view.components.point;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.view.component.SimplePointComponent;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class SimplePointComponentDTO extends PointComponentDTO {

    private Boolean displayPointName;
    private String styleAttribute;

    public SimplePointComponentDTO() {
    }

    public SimplePointComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName, String dataPointXid, String nameOverride, Boolean settableOverride, String bkgdColorOverride, Boolean displayControls, Boolean displayPointName, String styleAttribute) {
        super(index, idSuffix, x, y, z, typeName, dataPointXid, nameOverride, settableOverride, bkgdColorOverride, displayControls);
        this.displayPointName = displayPointName;
        this.styleAttribute = styleAttribute;
    }

    public Boolean getDisplayPointName() {
        return displayPointName;
    }

    public void setDisplayPointName(Boolean displayPointName) {
        this.displayPointName = displayPointName;
    }

    public String getStyleAttribute() {
        return styleAttribute;
    }

    public void setStyleAttribute(String styleAttribute) {
        this.styleAttribute = styleAttribute;
    }

    @Override
    public SimplePointComponent createFromBody(User user) {

        SimplePointComponent c = new SimplePointComponent();
        c.setDisplayPointName(displayPointName);
        c.setStyleAttribute(styleAttribute);

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
