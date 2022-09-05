package org.scada_lts.ds.messaging;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.util.LoggingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class UpdatePointValueConsumer implements Consumer<byte[]> {

    private static final Log LOG = LogFactory.getLog(UpdatePointValueConsumer.class);

    private final DataPointRT dataPoint;
    private final Writable writable;
    private final String updateErrorKey;

    public UpdatePointValueConsumer(DataPointRT dataPoint, Writable writable, String updateErrorKey) {
        this.dataPoint = dataPoint;
        this.writable = writable;
        this.updateErrorKey = updateErrorKey;
    }

    @Override
    public void accept(byte[] payload) {
        if(writable.isWritable()) {
            try {
                String message = new String(payload, StandardCharsets.UTF_8);
                dataPoint.updatePointValue(message);
                dataPoint.setAttribute(updateErrorKey, false);
            } catch (Exception ex) {
                LOG.warn("Error Update Data Point: " + LoggingUtils.dataPointInfo(dataPoint.getVO()) + ", Message: " + ex.getMessage(), ex);
                dataPoint.setAttribute(updateErrorKey, true);
            }
        }
    }
}
