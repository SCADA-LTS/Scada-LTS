package org.scada_lts.web.mvc.api.dto;

/**
 * @author Arkadiusz Parafiniuk on behalf of Abil'I.T. (code owner) email: sdt@abilit.eu
 */
public class ViewBinaryGraphicComponentDTO extends ViewImageSetComponentDTO {
    private int zeroImage;
    private int oneImage;

    public ViewBinaryGraphicComponentDTO() {
    }

    public ViewBinaryGraphicComponentDTO(String id, int index, String defName, String idSuffix, String style, int x, int y, String dataPointXid, String nameOverride, boolean settableOverride, String bkgdColorOverride, boolean displayControls, String imageSet, boolean displayText, int zeroImage, int oneImage) {
        super(id, index, defName, idSuffix, style, x, y, dataPointXid, nameOverride, settableOverride, bkgdColorOverride, displayControls, imageSet, displayText);
        this.zeroImage = zeroImage;
        this.oneImage = oneImage;
    }

    public int getZeroImage() {
        return zeroImage;
    }

    public void setZeroImage(int zeroImage) {
        this.zeroImage = zeroImage;
    }

    public int getOneImage() {
        return oneImage;
    }

    public void setOneImage(int oneImage) {
        this.oneImage = oneImage;
    }
}
