package com.serotonin.mango.web.dwr.beans;

import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.msg.ReadDeviceIdentificationRequest;
import com.serotonin.modbus4j.msg.ReadDeviceIdentificationResponse;
import com.serotonin.modbus4j.msg.ReadExceptionStatusRequest;
import com.serotonin.modbus4j.msg.ReadExceptionStatusResponse;
import com.serotonin.web.i18n.I18NUtils;
import com.serotonin.web.i18n.LocalizableMessage;

public class ModbusDeviceIdentificationListener implements TestingUtility, Runnable {
    private final ResourceBundle bundle;
    private final ModbusMaster modbusMaster;
    private final List<ModbusDeviceInfoBean> devicesFound = new LinkedList<>();
    private String message = "";
    private boolean finished = false;
    private boolean cancelled = false;
    private float progress = 0;

    public ModbusDeviceIdentificationListener(ResourceBundle bundle, ModbusMaster modbusMaster, boolean serial) {
        this.bundle = bundle;
        this.modbusMaster = modbusMaster;

        try {
            modbusMaster.init();
        } catch (ModbusInitException e) {
            if (serial)
                message = new LocalizableMessage("dsEdit.modbus.scannerSerial.startError", e.getMessage())
                        .getLocalizedMessage(bundle);
            else
                message = new LocalizableMessage("dsEdit.modbus.scannerIp.startError", e.getMessage())
                        .getLocalizedMessage(bundle);
            finished = true;
            return;
        }

        new Thread(this).start();
    }

    @Override
    public void run() {
        for (int i = 1; i <= 247; i++) {
            if (cancelled) {
                message = I18NUtils.getMessage(bundle, "dsEdit.modbus.scanner.cancelled");
                break;
            }

            progress = (float) i / 247;
            message = new LocalizableMessage("dsEdit.modbus.scanner.progress", Integer.toString((int) (progress * 100)))
                    .getLocalizedMessage(bundle);

            try {
                // Step 1: Send ReadExceptionStatusRequest (code 7) to see if a device is there.
                ReadExceptionStatusRequest request7 = new ReadExceptionStatusRequest(i);
                ReadExceptionStatusResponse response7 = (ReadExceptionStatusResponse) modbusMaster.send(request7);

                if (response7 != null) {
                    // Device found. Now try to get its identification.
                    ModbusDeviceInfoBean deviceInfo = new ModbusDeviceInfoBean(i);
                    devicesFound.add(deviceInfo);

                    try {
                        // Step 2: Send ReadDeviceIdentificationRequest (code 43)
                        ReadDeviceIdentificationRequest request43 = new ReadDeviceIdentificationRequest(i, 0, 0); // Object Id 0 for basic identification
                        ReadDeviceIdentificationResponse response43 = (ReadDeviceIdentificationResponse) modbusMaster.send(request43);

                        if(response43 != null && !response43.isException()) {
                            deviceInfo.setVendorName(response43.getIdentification().get(0));
                            deviceInfo.setProductCode(response43.getIdentification().get(1));
                            deviceInfo.setMajorMinorRevision(response43.getIdentification().get(2));
                        } else {
                            deviceInfo.setErrorMessage("Device identification not supported or returned an error.");
                        }

                    } catch (ModbusTransportException e) {
                        // This likely means the device does not support function 43, which is common.
                         deviceInfo.setErrorMessage("Device does not support identification function.");
                    }
                }
            } catch (ModbusTransportException e) {
                // This is the expected behavior for non-existent slaves, so we just ignore it.
            }
        }

        if (!cancelled) {
            message = I18NUtils.getMessage(bundle, "dsEdit.modbus.scanner.complete");
        }

        modbusMaster.destroy();
        finished = true;
    }

    public List<ModbusDeviceInfoBean> getDevicesFound() {
        return devicesFound;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void cancel() {
        cancelled = true;
    }
}
