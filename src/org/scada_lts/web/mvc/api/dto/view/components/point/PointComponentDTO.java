package org.scada_lts.web.mvc.api.dto.view.components.point;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.view.component.PointComponent;
import com.serotonin.mango.view.component.deserializer.PointComponentDeserializer;
import com.serotonin.mango.vo.DataPointVO;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.web.mvc.api.dto.view.components.GraphicalViewComponentDTO;

@JsonDeserialize(using = PointComponentDeserializer.class)
public class PointComponentDTO extends GraphicalViewComponentDTO {

    private String dataPointXid;
    private String nameOverride;
    private Boolean settableOverride;
    private String bkgdColorOverride;
    private Boolean displayControls;

    public PointComponentDTO() {
    }

    public PointComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName, String dataPointXid, String nameOverride, Boolean settableOverride, String bkgdColorOverride, Boolean displayControls) {
        super(index, idSuffix, x, y, z, typeName);
        this.dataPointXid = dataPointXid;
        this.nameOverride = nameOverride;
        this.settableOverride = settableOverride;
        this.bkgdColorOverride = bkgdColorOverride;
        this.displayControls = displayControls;
    }

    public String getDataPointXid() {
        return dataPointXid;
    }

    public void setDataPointXid(String dataPointXid) {
        this.dataPointXid = dataPointXid;
    }

    public String getNameOverride() {
        return nameOverride;
    }

    public void setNameOverride(String nameOverride) {
        this.nameOverride = nameOverride;
    }

    public Boolean getSettableOverride() {
        return settableOverride;
    }

    public void setSettableOverride(Boolean settableOverride) {
        this.settableOverride = settableOverride;
    }

    public String getBkgdColorOverride() {
        return bkgdColorOverride;
    }

    public void setBkgdColorOverride(String bkgdColorOverride) {
        this.bkgdColorOverride = bkgdColorOverride;
    }

    public Boolean getDisplayControls() {
        return displayControls;
    }

    public void setDisplayControls(Boolean displayControls) {
        this.displayControls = displayControls;
    }

    protected DataPointVO getDataPoint() {
        return new DataPointService().getDataPoint(dataPointXid);
    }

    protected void resetPointComponent(PointComponent c) {
        if (c.tgetDataPoint() != null)
            c.tgetDataPoint().resetLastValue();
    }
}
