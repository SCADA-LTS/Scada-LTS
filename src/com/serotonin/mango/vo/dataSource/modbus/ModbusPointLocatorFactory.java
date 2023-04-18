package com.serotonin.mango.vo.dataSource.modbus;

import br.org.scadabr.api.constants.DataSourceType;

public final class ModbusPointLocatorFactory {

    public static ModbusPointLocatorVO locator(DataSourceType dataSourceType) {
        if(DataSourceType._MODBUS_IP.equals(dataSourceType.getValue())) {
            return new ModbusPointLocatorVO();
        }
        return new ModbusSerialPointLocatorVO();
    }

    public static ModbusPointLocatorVO locator(ModbusDataSourceVO<?> dataSource) {
        if(dataSource instanceof ModbusIpDataSourceVO) {
            return new ModbusPointLocatorVO();
        }
        return new ModbusSerialPointLocatorVO();
    }
}
