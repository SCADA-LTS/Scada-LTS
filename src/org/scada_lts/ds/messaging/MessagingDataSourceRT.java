package org.scada_lts.ds.messaging;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.ds.messaging.amqp.AmqpDataSourceVO;


public class MessagingDataSourceRT extends PollingDataSource {

    public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
    public static final int DATA_POINT_WRITE_EXCEPTION_EVENT = 2;
    public static final int DATA_POINT_READ_EXCEPTION_EVENT = 3;

    private static final Log LOG = LogFactory.getLog(MessagingDataSourceRT.class);

    private final DataSourceVO<?> vo;
    private final MessagingService messagingService;

    public MessagingDataSourceRT(AmqpDataSourceVO vo) {
        super(vo);
        this.vo = vo;
        this.messagingService = MessagingServiceFactory.newService(vo);
        setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
    }

    @Override
    public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source) {
        if(dataPoint == null || dataPoint.getVO() == null) {
            LOG.error(LoggingUtils.dataSourceInfo(vo) + " - write failed: " + LoggingUtils.pointValueTimeInfo(valueTime, source));
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), false,
                    new LocalizableMessage("event.exception2", "", " - write failed: " + LoggingUtils.pointValueTimeInfo(valueTime, source)));
            return;
        }
        DataPointVO dataPointVO = dataPoint.getVO();
        if (!messagingService.isOpen()) {
            LOG.warn(LoggingUtils.dataSourcePointValueTimeInfo(vo, dataPointVO, valueTime, source) + " - write failed.");
            raiseEvent(DATA_POINT_WRITE_EXCEPTION_EVENT, System.currentTimeMillis(), false,
                    new LocalizableMessage("event.ds.writeFailed", dataPointVO.getName()));
            return;
        }
        String message = valueTime.getStringValue();
        try {
            messagingService.publish(dataPoint, message);
        } catch (Exception e) {
            LOG.error(e.getMessage() + " - " + LoggingUtils.dataPointInfo(dataPoint.getVO()), e);
            raiseEvent(DATA_POINT_WRITE_EXCEPTION_EVENT, System.currentTimeMillis(), false,
                    new LocalizableMessage("event.ds.writeFailed", dataPointVO.getName()));
        }
    }

    @Override
    public void initialize() {
        try {
            messagingService.open();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(),
                    true, DataSourceRT.getExceptionMessage(e));
        }
        super.initialize();
    }

    @Override
    public void terminate() {
        if(vo instanceof AmqpDataSourceVO) {
            if(((AmqpDataSourceVO)vo).isResetBrokerConfig()) {
                messagingService.resetBrokerConfig();
            }
        }
        try {
            messagingService.close();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                    new LocalizableMessage("event.exception2", e.getClass().getName(), e.getMessage()));
        }
        this.vo.setEnabled(false);
        super.terminate();
    }

    @Override
    protected void doPoll(long time) {
        for (DataPointRT dp : dataPoints) {
            try {
                messagingService.initReceiver(dp);
                returnToNormal(DATA_POINT_READ_EXCEPTION_EVENT, time);
            } catch (Exception e) {
                LOG.warn(e.getMessage(), e);
                raiseEvent(DATA_POINT_READ_EXCEPTION_EVENT, time, false,
                        new LocalizableMessage("event.amqp.bindError", dp.getVO().getXid()));
            }
        }
    }
}