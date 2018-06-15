package org.scada_lts.web.mvc.api.dto;

import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.view.ImageSet;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Arkadiusz Parafiniuk
 * arkadiusz.parafiniuk@gmail.com
 */
public class ViewMultistateGraphicComponentDTO extends ViewImageSetComponentDTO {
    private Map<Integer, Integer> stateImageMap = new HashMap<Integer, Integer>();
    private int defaultImage;

    public ViewMultistateGraphicComponentDTO() {
    }

    public ViewMultistateGraphicComponentDTO(String id, int index, String defName, String idSuffix, String style, int x, int y, String dataPointXid, String nameOverride, boolean settableOverride, String bkgdColorOverride, boolean displayControls, ImageSet imageSet, boolean displayText, Map<Integer, Integer> stateImageMap, int defaultImage) {
        super(id, index, defName, idSuffix, style, x, y, dataPointXid, nameOverride, settableOverride, bkgdColorOverride, displayControls, imageSet, displayText);
        this.stateImageMap = stateImageMap;
        this.defaultImage = defaultImage;
    }

    public Map<Integer, Integer> getStateImageMap() {
        return stateImageMap;
    }

    public void setStateImageMap(Map<Integer, Integer> stateImageMap) {
        this.stateImageMap = stateImageMap;
    }

    public int getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(int defaultImage) {
        this.defaultImage = defaultImage;
    }
}
