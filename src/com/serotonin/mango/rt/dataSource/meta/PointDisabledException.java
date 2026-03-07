package com.serotonin.mango.rt.dataSource.meta;

import com.serotonin.web.i18n.LocalizableMessage;

public class PointDisabledException extends DataPointStateException {

    public PointDisabledException(int dataPointId, LocalizableMessage message) {
        super(dataPointId, message);
    }

    public PointDisabledException(int dataPointId, String dataPointXid, String dataPointName, LocalizableMessage message) {
        super(dataPointId, dataPointXid, dataPointName, message);
    }
}
