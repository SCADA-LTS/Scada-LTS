package com.serotonin.mango.rt.dataSource.meta;

import com.serotonin.web.i18n.LocalizableMessage;

import java.util.ResourceBundle;

public class PointDisabledException extends DataPointStateException {

    public PointDisabledException(int dataPointId, LocalizableMessage message, ResourceBundle resourceBundle) {
        super(dataPointId, message, resourceBundle);
    }

    public PointDisabledException(int dataPointId, String dataPointXid, String dataPointName,
                                  LocalizableMessage message, ResourceBundle resourceBundle) {
        super(dataPointId, dataPointXid, dataPointName, message, resourceBundle);
    }
}
