package com.serotonin.mango.rt.dataSource.meta;

import com.serotonin.web.i18n.LocalizableMessage;

public class PointUnavailableException extends DataPointStateException {

    public PointUnavailableException(int dataPointId, LocalizableMessage message) {
        super(dataPointId, message);
    }

    public PointUnavailableException(int dataPointId, String dataPointXid, String dataPointName, LocalizableMessage message) {
        super(dataPointId, dataPointXid, dataPointName, message);
    }
}
