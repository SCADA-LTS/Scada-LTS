package org.scada_lts.ds.messaging;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.util.LoggingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class UpdatePointValueConsumer implements Consumer<byte[]> {

    private final DataPointRT dataPoint;
    private final Writable writable;
    private final String updateErrorKey;
    private final Consumer<Exception> exceptionHandler;

    public UpdatePointValueConsumer(DataPointRT dataPoint, Writable writable,
                                    String updateErrorKey, Consumer<Exception> exceptionHandler) {
        this.dataPoint = dataPoint;
        this.writable = writable;
        this.updateErrorKey = updateErrorKey;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public void accept(byte[] payload) {
        if(writable.isWritable()) {
            try {
                String message = new String(payload, StandardCharsets.UTF_8);
                dataPoint.updatePointValue(message);
                dataPoint.setAttribute(updateErrorKey, false);
            } catch (Exception ex) {
                exceptionHandler.accept(ex);
                dataPoint.setAttribute(updateErrorKey, true);
            }
        }
    }
}
