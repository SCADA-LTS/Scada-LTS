package org.scada_lts.ds.messaging;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.ds.messaging.amqp.AmqpDataSourceVO;

import java.io.IOException;

public class MessagingDataSourceRT extends PollingDataSource {

    public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
    public static final int DATA_POINT_EXCEPTION_EVENT = 2;
    public static final int DATA_POINT_WRITE_EXCEPTION_EVENT = 3;
    public static final int DATA_POINT_READ_EXCEPTION_EVENT = 4;

    private static final Log LOG = LogFactory.getLog(MessagingDataSourceRT.class);

    private final AmqpDataSourceVO vo;
    private final MessagingService messagingService;

    public MessagingDataSourceRT(AmqpDataSourceVO vo) {
        super(vo);
        this.vo = vo;
        this.messagingService = MessagingServiceFactory.amqp(vo);
        setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
    }

    @Override
    public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source) {
        if(dataPoint == null || dataPoint.getVO() == null) {
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), false,
                    new LocalizableMessage("event.exception2", "", " - write failed: " + LoggingUtils.pointValueTimeInfo(valueTime, source)));
            LOG.error(LoggingUtils.dataSourceInfo(vo) + " - write failed: " + LoggingUtils.pointValueTimeInfo(valueTime, source));
            return;
        }
        DataPointVO dataPointVO = dataPoint.getVO();
        if (!messagingService.isOpened()) {
            raiseEvent(DATA_POINT_WRITE_EXCEPTION_EVENT, System.currentTimeMillis(), false,
                    new LocalizableMessage("event.ds.writeFailed", dataPointVO.getName()));
            LOG.warn(LoggingUtils.dataSourcePointValueTimeInfo(vo, dataPointVO, valueTime, source) + " - write failed.");
            return;
        }
        String message = valueTime.getStringValue();
        try {
            messagingService.publish(dataPoint, message);
        } catch (IOException e) {
            LOG.error(e.getMessage() + " - " + LoggingUtils.dataPointInfo(dataPoint.getVO()), e);
        }
    }

    // Enable DataSource //
    @Override
    public void initialize() {

        try {
            messagingService.open();
        } catch (Exception e) {
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(),
                    true, DataSourceRT.getExceptionMessage(e));
        }

        super.initialize();
    }

    // Disable DataSource //
    @Override
    public void terminate() {
        try {
            messagingService.close();
        } catch (Exception e) {
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                    new LocalizableMessage("event.exception2", e.getClass().getName(), e.getMessage()));
        }
        this.vo.setEnabled(false);
        super.terminate();
    }

    @Override
    protected void doPoll(long time) {

        //If not amqpBindEstablished initialize DataPoints
        for (DataPointRT dp : dataPoints) {
            try {
                messagingService.consume(dp);
                returnToNormal(DATA_POINT_EXCEPTION_EVENT, time);
            } catch (IOException e) {
                raiseEvent(DATA_POINT_EXCEPTION_EVENT, time, false,
                        new LocalizableMessage("event.amqp.bindError", dp.getVO().getXid()));
            }
        }
    }
}