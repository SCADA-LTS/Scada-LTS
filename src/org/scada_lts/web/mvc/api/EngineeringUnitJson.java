package org.scada_lts.web.mvc.api;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.serotonin.mango.vo.EngineeringUnit;

@JsonSerialize(as= EngineeringUnit.class)
public class EngineeringUnitJson implements EngineeringUnit {

    private final EngineeringUnit engineeringUnit;

    public EngineeringUnitJson(EngineeringUnit engineeringUnit) {
        this.engineeringUnit = engineeringUnit;
    }

    @Override
    public int getValue() {
        return engineeringUnit.getValue();
    }

    @Override
    public String getName() {
        return engineeringUnit.getName();
    }

    @Override
    public String getSuffix() {
        return engineeringUnit.getSuffix();
    }

    @Override
    public String getKey() {
        return engineeringUnit.getKey();
    }
}
