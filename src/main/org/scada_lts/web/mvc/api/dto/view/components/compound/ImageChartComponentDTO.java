package org.scada_lts.web.mvc.api.dto.view.components.compound;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.view.component.ImageChartComponent;
import com.serotonin.mango.vo.User;

import java.util.Map;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class ImageChartComponentDTO extends CompoundComponentDTO{

    private Integer width;
    private Integer height;
    private Integer durationType;
    private Integer durationPeriods;

    public ImageChartComponentDTO() {
    }

    public ImageChartComponentDTO(Integer index, String idSuffix, Integer x, Integer y, Integer z, String typeName, String name, Map<String, String> compoundChildren, Integer width, Integer height, Integer durationType, Integer durationPeriods) {
        super(index, idSuffix, x, y, z, typeName, name, compoundChildren);
        this.width = width;
        this.height = height;
        this.durationType = durationType;
        this.durationPeriods = durationPeriods;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getDurationType() {
        return durationType;
    }

    public void setDurationType(Integer durationType) {
        this.durationType = durationType;
    }

    public Integer getDurationPeriods() {
        return durationPeriods;
    }

    public void setDurationPeriods(Integer durationPeriods) {
        this.durationPeriods = durationPeriods;
    }

    @Override
    public ImageChartComponent createFromBody(User user) {
        ImageChartComponent c = new ImageChartComponent();
        c.setDurationPeriods(durationPeriods);
        c.setDurationType(durationType);
        c.setWidth(width);
        c.setHeight(height);

        c.setName(getName());
        c.setChildren(getCompoundChildren());

        c.setIndex(getIndex());
        c.setIdSuffix(getIdSuffix());
        c.setX(getX());
        c.setY(getY());
        c.setZ(getZ());

        return c;
    }
}
