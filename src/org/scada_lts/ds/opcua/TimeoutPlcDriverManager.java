package org.scada_lts.ds.opcua;

import org.apache.plc4x.java.DefaultPlcDriverManager;
import org.apache.plc4x.java.api.PlcDriver;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

public class TimeoutPlcDriverManager extends DefaultPlcDriverManager {

    private final Map<String, PlcDriver> driverMap;

    public TimeoutPlcDriverManager() {
        driverMap = new HashMap<>();
        ServiceLoader<TimeoutOpcuaPlcDriver> plcDriverLoader = ServiceLoader.load(TimeoutOpcuaPlcDriver.class, classLoader);
        for (PlcDriver driver : plcDriverLoader) {
            if (driverMap.containsKey(driver.getProtocolCode())) {
                throw new IllegalStateException(
                        "Multiple driver implementations available for protocol code '" +
                                driver.getProtocolCode() + "'");
            }
            driverMap.put(driver.getProtocolCode(), driver);
        }
    }

    @Override
    public PlcDriver getDriver(String protocolCode) throws PlcConnectionException {
        PlcDriver driver = driverMap.get(protocolCode);
        if (driver == null) {
            throw new PlcConnectionException("Unable to find driver for protocol '" + protocolCode + "'");
        }
        return driver;
    }

    @Override
    public Set<String> getProtocolCodes() {
        return driverMap.keySet();
    }
}
