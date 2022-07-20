package org.scada_lts.web.mvc.api.dto.view.components.compound;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.serotonin.mango.view.component.WirelessTempHumSensor;
import com.serotonin.mango.vo.User;

@JsonDeserialize(using = JsonDeserializer.None.class)
public class WirelessTempHumSensorDTO extends CompoundComponentDTO{

    public WirelessTempHumSensorDTO() {
    }

    @Override
    public WirelessTempHumSensor createFromBody(User user) {
        WirelessTempHumSensor c = new WirelessTempHumSensor();

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
