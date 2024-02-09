package org.scada_lts.ds.messaging.service;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.ds.messaging.channel.InitMessagingChannels;
import org.scada_lts.ds.messaging.exception.MessagingServiceException;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.serotonin.mango.util.LoggingUtils.*;


class MessagingServiceImpl implements MessagingService {

    private static final Log LOG = LogFactory.getLog(MessagingServiceImpl.class);

    private final DataSourceVO<?> vo;
    private final InitMessagingChannels channels;

    private volatile boolean blocked;

    MessagingServiceImpl(DataSourceVO<?> vo, InitMessagingChannels channels) {
        this.vo = vo;
        this.channels = channels;
        this.blocked = false;
    }

    @Override
    public void publish(DataPointRT dataPoint, String message) throws MessagingServiceException {
        if(blocked) {
            throw new MessagingServiceException("Stop Publish: " + dataSourceInfo(vo) + ", Service of shutting down:  "  + dataSourceInfo(vo) + ", Value: " + message);
        }
        try {
            channels.publish(dataPoint, message);
        } catch (Exception e) {
            throw new MessagingServiceException("Error Publish: " + dataSourceInfo(vo) + ", Value: " + message + ", " + exceptionInfo(e), e);
        }
    }

    @Override
    public void initReceiver(DataPointRT dataPoint, Consumer<Exception> exceptionHandler, Supplier<Void> returnToNormal) throws MessagingServiceException {
        if(blocked) {
            LOG.warn("Stop Init Receiver: " + dataSourceInfo(vo) + ", Service of shutting down: "  + dataSourceInfo(vo));
            return;
        }
        if(isOpen(dataPoint)) {
            return;
        }
        try {
            channels.initChannel(dataPoint, exceptionHandler, returnToNormal);
        } catch (Exception e) {
            throw new MessagingServiceException("Error Init Receiver: " + dataSourceInfo(vo) + ", " + exceptionInfo(e), e);
        }
    }

    @Override
    public void removeReceiver(DataPointRT dataPoint) throws MessagingServiceException {
        if(blocked) {
            LOG.warn("Stop Remove Receiver: " + dataSourceInfo(vo) + ", Service of shutting down: "  + dataSourceInfo(vo));
            return;
        }
        try {
            channels.removeChannel(dataPoint);
        } catch (Exception e) {
            throw new MessagingServiceException("Error Remove Receiver: " + dataSourceInfo(vo) + ", " + exceptionInfo(e), e);
        }
    }

    @Override
    public boolean isOpen() {
        return channels.isOpenConnection();
    }

    @Override
    public boolean isOpen(DataPointRT dataPoint) {
        return isOpen() && channels.isOpenChannel(dataPoint);
    }

    @Override
    public void open() throws MessagingServiceException {
        if(blocked) {
            throw new MessagingServiceException("Stop Open Connection: Service of shutting down:  "  + dataSourceInfo(vo));
        }
        if(channels.isOpenConnection()) {
            LOG.warn("Stop Open Connection: Connection is opened:  "  + dataSourceInfo(vo));
            return;
        }
        try {
            channels.openConnection();
        } catch (Exception e) {
            throw new MessagingServiceException("Error Open Connection: " + dataSourceInfo(vo) + ", " + exceptionInfo(e), e);
        }
        blocked = false;
    }

    @Override
    public void close() throws MessagingServiceException {
        blocked = true;
        try {
            channels.closeChannels();
        } catch (Exception ex) {
            LOG.warn("Error Close Receivers: " + dataSourceInfo(vo) + ", " + exceptionInfo(ex), ex);
        }
    }
}
