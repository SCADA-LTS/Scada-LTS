package org.scada_lts.web.mvc.api.dto;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Arkadiusz Parafiniuk
 * arkadiusz.parafiniuk@gmail.com
 */
public class ViewMultistateGraphicComponentDTO extends ViewImageSetComponentDTO {
    private List<Pair<Integer, String>> stateImageMap = new ArrayList<>();
    private int defaultImage;

    public ViewMultistateGraphicComponentDTO() {
    }

    public ViewMultistateGraphicComponentDTO(String id, int index, String defName, String idSuffix, String style, int x, int y, String dataPointXid, String nameOverride, boolean settableOverride, String bkgdColorOverride, boolean displayControls, String imageSet, boolean displayText, List<Pair<Integer, String>> stateImageMap, int defaultImage) {
        super(id, index, defName, idSuffix, style, x, y, dataPointXid, nameOverride, settableOverride, bkgdColorOverride, displayControls, imageSet, displayText);
        this.stateImageMap = stateImageMap;
        this.defaultImage = defaultImage;
    }

    public List<Pair<Integer, String>> getStateImageMap() {
        return stateImageMap;
    }

    public void setStateImageMap(List<Pair<Integer, String>> stateImageMap) {
        this.stateImageMap = stateImageMap;
    }

    public int getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(int defaultImage) {
        this.defaultImage = defaultImage;
    }
}
