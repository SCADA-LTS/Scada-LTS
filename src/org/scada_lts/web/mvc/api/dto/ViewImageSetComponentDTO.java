package org.scada_lts.web.mvc.api.dto;

import org.scada_lts.web.beans.validation.xss.XssProtect;

/**
 * @Author Arkadiusz Parafiniuk
 * arkadiusz.parafiniuk@gmail.com
 */
public class ViewImageSetComponentDTO extends ViewComponentDTO {

    @XssProtect
    private String dataPointXid;
    @XssProtect
    private String nameOverride;
    private boolean settableOverride;
    @XssProtect
    private String bkgdColorOverride;
    private boolean displayControls;
    @XssProtect
    private String imageSet;
    private boolean displayText;

    public ViewImageSetComponentDTO() {
    }

    public ViewImageSetComponentDTO(String id, int index, String defName, String idSuffix, String style, int x, int y, String dataPointXid, String nameOverride, boolean settableOverride, String bkgdColorOverride, boolean displayControls, String imageSet, boolean displayText) {
        super(id, index, defName, idSuffix, style, x, y);
        this.dataPointXid = dataPointXid;
        this.nameOverride = nameOverride;
        this.settableOverride = settableOverride;
        this.bkgdColorOverride = bkgdColorOverride;
        this.displayControls = displayControls;
        this.imageSet = imageSet;
        this.displayText = displayText;
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

    public String getImageSet() {
        return imageSet;
    }

    public void setImageSet(String imageSet) {
        this.imageSet = imageSet;
    }

    public boolean isDisplayText() {
        return displayText;
    }

    public void setDisplayText(boolean displayText) {
        this.displayText = displayText;
    }
}
