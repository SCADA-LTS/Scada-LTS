package com.serotonin.mango.rt.dataSource.bacnet;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.web.dwr.DwrResponseI18n;

public final class BACnetUtils {

    private BACnetUtils() {}

    public static void checkFreePort(DwrResponseI18n response, int port) {
        LocalDevice localDevice = new LocalDevice(0, "255.255.255.255");
        localDevice.setPort(port);
        try{
            localDevice.initialize();
        } catch (Exception e) {
            response.addContextualMessage("port", "validation.invalidPort", e.getMessage());
        } finally {
            localDevice.terminate();
        }
    }
}
