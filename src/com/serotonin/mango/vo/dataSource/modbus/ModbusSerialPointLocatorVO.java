package com.serotonin.mango.vo.dataSource.modbus;

import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;

public class ModbusSerialPointLocatorVO extends ModbusPointLocatorVO {

    private static final long serialVersionUID = -1;
    private static final int version = 1;

    public ModbusSerialPointLocatorVO() {
    }

    public ModbusSerialPointLocatorVO(ModbusPointLocatorVO pointLocator) {
        super(pointLocator);
    }

    @Override
    public void validateSlaveId(DwrResponseI18n response) {
        if (!StringUtils.isBetweenInc(this.getSlaveId(), 1, 247) && !this.isSocketMonitor())
            response.addContextualMessage("slaveId", "validate.1to247");
    }
}
