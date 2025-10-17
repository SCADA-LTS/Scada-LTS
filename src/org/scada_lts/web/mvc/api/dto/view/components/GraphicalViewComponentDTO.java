package org.scada_lts.web.mvc.api.dto.view.components;

import br.org.scadabr.view.component.*;
import com.serotonin.mango.view.ImplDefinition;
import com.serotonin.mango.view.component.*;
import com.serotonin.mango.vo.User;
import org.scada_lts.web.mvc.api.dto.view.components.compound.CompoundComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.html.HtmlBaseComponentDTO;
import org.scada_lts.web.mvc.api.dto.view.components.point.PointComponentDTO;

public class GraphicalViewComponentDTO implements ViewComponentCreate{
    private Integer index;
    private String idSuffix;
    private Integer x;
    private Integer y;
    private Integer z;
    private String defName;

    public GraphicalViewComponentDTO() {
    }

    public GraphicalViewComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName) {
        this.index = index;
        this.idSuffix = idSuffix;
        this.x = x;
        this.y = y;
        this.z = z;
        this.defName = typeName;
    }

    public static Class<? extends GraphicalViewComponentDTO> resolveClassForDeserializer2(ImplDefinition def) {
        if (isPointComponent(def))
            return PointComponentDTO.class;
        if (isHtmlComponent(def))
            return HtmlBaseComponentDTO.class;
        if (isCompoundComponent(def))
            return CompoundComponentDTO.class;
        if (def == AlarmListComponent.DEFINITION)
            return AlarmListComponentDTO.class;
        return null;
    }

    public static boolean isPointComponent(ImplDefinition def) {
        return isImageSetComponent(def) || def == ButtonComponent.DEFINITION ||
                def == DynamicGraphicComponent.DEFINITION || def == EnhancedPointComponent.DEFINITION ||
                def == ScriptComponent.DEFINITION || def == SimpleImageComponent.DEFINITION ||
                def == SimplePointComponent.DEFINITION || def == ThumbnailComponent.DEFINITION;
    }

    public static boolean isHtmlComponent(ImplDefinition def) {
        return def == HtmlComponent.DEFINITION || def == ChartComparatorComponent.DEFINITION ||
                def == FlexBuilderComponent.DEFINITION || def == LinkComponent.DEFINITION ||
                def == ScriptButtonComponent.DEFINITION;
    }

    public static boolean isCompoundComponent(ImplDefinition def) {
        return def == SimpleCompoundComponent.DEFINITION || def == EnhancedImageChartComponent.DEFINITION ||
                def == ImageChartComponent.DEFINITION || def == WirelessTempHumSensor.DEFINITION;
    }

    public static boolean isImageSetComponent(ImplDefinition def) {
        return def == AnalogGraphicComponent.DEFINITION || def == BinaryGraphicComponent.DEFINITION ||
                def == MultistateGraphicComponent.DEFINITION;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getIdSuffix() {
        return idSuffix;
    }

    public void setIdSuffix(String idSuffix) {
        this.idSuffix = idSuffix;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getZ() {
        return z;
    }

    public void setZ(Integer z) {
        this.z = z;
    }

    public String getDefName() {
        return defName;
    }

    public void setDefName(String defName) {
        this.defName = defName;
    }

    @Override
    public Object createFromBody(User user) {
        return null;
    }
}
