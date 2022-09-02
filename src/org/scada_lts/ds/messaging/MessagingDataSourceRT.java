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
import org.scada_lts.ds.DataSourceUpdatable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class MessagingDataSourceRT extends PollingDataSource {

    public static final String ATTR_UNRELIABLE_KEY = "UNRELIABLE";

    public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
    public static final int DATA_POINT_PUBLISH_EXCEPTION_EVENT = 2;
    public static final int DATA_POINT_INIT_EXCEPTION_EVENT = 3;

    private static final Log LOG = LogFactory.getLog(MessagingDataSourceRT.class);

    private final DataSourceVO<?> vo;
    private final MessagingService messagingService;
    private final Map<Integer, AtomicInteger> updateAttemptsCounters;
    private final int updateAttempts;

    public MessagingDataSourceRT(DataSourceUpdatable<?> vo, MessagingService messagingService) {
        super(vo.toDataSource());
        this.vo = vo.toDataSource();
        this.messagingService = messagingService;
        setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
        this.updateAttemptsCounters = new ConcurrentHashMap<>();
        this.updateAttempts = vo.getUpdateAttempts();
    }

    @Override
    public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source) {
        DataPointVO dataPointVO = dataPoint.getVO();
        if (!messagingService.isOpen()) {
            LOG.warn(LoggingUtils.dataSourcePointValueTimeInfo(vo, dataPointVO, valueTime, source) + " - write failed.");
            raiseEvent(DATA_POINT_PUBLISH_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                    new LocalizableMessage("event.ds.publishFailed", dataPointVO.getName()));
            dataPoint.setAttribute(ATTR_UNRELIABLE_KEY, true);
            return;
        }
        String message = valueTime.getStringValue();
        try {
            messagingService.publish(dataPoint, message);
            returnToNormal(DATA_POINT_PUBLISH_EXCEPTION_EVENT, System.currentTimeMillis());
            dataPoint.setAttribute(ATTR_UNRELIABLE_KEY, false);
        } catch (Exception e) {
            LOG.error(e.getMessage() + " - " + LoggingUtils.dataPointInfo(dataPoint.getVO()), e);
            raiseEvent(DATA_POINT_PUBLISH_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                    new LocalizableMessage("event.ds.publishFailed", dataPointVO.getName()));
            dataPoint.setAttribute(ATTR_UNRELIABLE_KEY, true);
        }
    }

    @Override
    public void initialize() {
        try {
            messagingService.open();
            returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(),
                    true, DataSourceRT.getExceptionMessage(e));
        }
        super.initialize();
    }

    @Override
    public void terminate() {
        this.vo.setEnabled(false);
        super.terminate();
        try {
            messagingService.close();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(),
                    true, DataSourceRT.getExceptionMessage(e));
        }
        updateAttemptsCounters.clear();
    }

    @Override
    public void addDataPoint(DataPointRT dataPoint) {
        try {
            updateAttemptsCounters.putIfAbsent(dataPoint.getId(), new AtomicInteger());
            messagingService.initReceiver(dataPoint);
            returnToNormal(DATA_POINT_INIT_EXCEPTION_EVENT, System.currentTimeMillis());
            dataPoint.setAttribute(ATTR_UNRELIABLE_KEY, false);
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
            raiseEvent(DATA_POINT_INIT_EXCEPTION_EVENT, System.currentTimeMillis(),
                    true, DataSourceRT.getExceptionMessage(e));
            dataPoint.setAttribute(ATTR_UNRELIABLE_KEY, true);
        }
        super.addDataPoint(dataPoint);
    }

    @Override
    public void removeDataPoint(DataPointRT dataPoint) {
        try {
            updateAttemptsCounters.remove(dataPoint.getId());
            messagingService.removeReceiver(dataPoint);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            raiseEvent(DATA_POINT_INIT_EXCEPTION_EVENT, System.currentTimeMillis(),
                    true, DataSourceRT.getExceptionMessage(e));
        }
        super.removeDataPoint(dataPoint);
    }

    @Override
    protected void doPoll(long time) {
        for (DataPointRT dataPoint : dataPoints) {
            try {
                updateAttemptsCounters.putIfAbsent(dataPoint.getId(), new AtomicInteger());
                if(updateAttemptsCounters.get(dataPoint.getId()).get() < updateAttempts) {
                    messagingService.initReceiver(dataPoint);
                    updateAttemptsCounters.get(dataPoint.getId()).set(0);
                    returnToNormal(DATA_POINT_INIT_EXCEPTION_EVENT, time);
                    dataPoint.setAttribute(ATTR_UNRELIABLE_KEY, false);
                }
            } catch (Exception e) {
                updateAttemptsCounters.get(dataPoint.getId()).incrementAndGet();
                LOG.warn(e.getMessage(), e);
                raiseEvent(DATA_POINT_INIT_EXCEPTION_EVENT, System.currentTimeMillis(),
                        true, DataSourceRT.getExceptionMessage(e));
                dataPoint.setAttribute(ATTR_UNRELIABLE_KEY, true);
            }
        }
    }
}