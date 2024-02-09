package org.scada_lts.ds.messaging.channel;

import com.serotonin.mango.rt.dataImage.DataPointRT;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UpdatePointValueConsumer implements Consumer<byte[]> {

    private final DataPointRT dataPoint;
    private final Writable writable;
    private final Consumer<Exception> exceptionHandler;
    private final Supplier<Void> returnToNormal;

    public UpdatePointValueConsumer(DataPointRT dataPoint, Writable writable, Consumer<Exception> exceptionHandler, Supplier<Void> returnToNormal) {
        this.dataPoint = dataPoint;
        this.writable = writable;
        this.exceptionHandler = exceptionHandler;
        this.returnToNormal = returnToNormal;
    }

    @Override
    public void accept(byte[] payload) {
        if(writable.isWritable()) {
            try {
                String message = new String(payload, StandardCharsets.UTF_8);
                dataPoint.updatePointValue(message);
                returnToNormal.get();
            } catch (Exception ex) {
                exceptionHandler.accept(ex);
            }
        }
    }
}
