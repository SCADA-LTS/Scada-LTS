package org.scada_lts.web.mvc.api.dto;

import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.vo.DataPointVO;

/**
 * @Author Arkadiusz Parafiniuk
 * arkadiusz.parafiniuk@gmail.com
 */
public class ViewSimplePointComponentDTO extends ViewComponentDTO {
    private String dataPointXid;
    private String nameOverride;
    private boolean settableOverride;
    private String bkgdColorOverride;
    private boolean displayControls;
    private boolean setDisplatText;
    private boolean displayPointName;
    private String styleAttribute;

    public ViewSimplePointComponentDTO() {
    }

    public ViewSimplePointComponentDTO(String id, int index, String defName, String idSuffix, String style, int x, int y, String dataPointXid, String nameOverride, boolean settableOverride, String bkgdColorOverride, boolean displayControls, boolean setDisplatText, boolean displayPointName, String styleAttribute) {
        super(id, index, defName, idSuffix, style, x, y);
        this.dataPointXid = dataPointXid;
        this.nameOverride = nameOverride;
        this.settableOverride = settableOverride;
        this.bkgdColorOverride = bkgdColorOverride;
        this.displayControls = displayControls;
        this.setDisplatText = setDisplatText;
        this.displayPointName = displayPointName;
        this.styleAttribute = styleAttribute;
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

    public boolean isSettableOverride() {
        return settableOverride;
    }

    public void setSettableOverride(boolean settableOverride) {
        this.settableOverride = settableOverride;
    }

    public String getBkgdColorOverride() {
        return bkgdColorOverride;
    }

    public void setBkgdColorOverride(String bkgdColorOverride) {
        this.bkgdColorOverride = bkgdColorOverride;
    }

    public boolean isDisplayControls() {
        return displayControls;
    }

    public void setDisplayControls(boolean displayControls) {
        this.displayControls = displayControls;
    }

    public boolean isSetDisplatText() {
        return setDisplatText;
    }

    public void setSetDisplatText(boolean setDisplatText) {
        this.setDisplatText = setDisplatText;
    }

    public boolean isDisplayPointName() {
        return displayPointName;
    }

    public void setDisplayPointName(boolean displayPointName) {
        this.displayPointName = displayPointName;
    }

    public String getStyleAttribute() {
        return styleAttribute;
    }

    public void setStyleAttribute(String styleAttribute) {
        this.styleAttribute = styleAttribute;
    }
}
