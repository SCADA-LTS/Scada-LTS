package org.scada_lts.ds.messaging;

import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.ds.DataSourceUpdatable;
import org.scada_lts.ds.messaging.service.MessagingService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.serotonin.mango.util.LoggingUtils.*;


public class MessagingDataSourceRT extends PollingDataSource {


    private static final String ATTR_UPDATE_ERROR_KEY = "DP_UPDATE_ERROR";

    public static final int DATA_SOURCE_EXCEPTION_EVENT = 1;
    public static final int DATA_POINT_PUBLISH_EXCEPTION_EVENT = 2;
    public static final int DATA_POINT_INIT_EXCEPTION_EVENT = 3;
    public static final int DATA_POINT_UPDATE_EXCEPTION_EVENT = 4;

    private static final Log LOG = LogFactory.getLog(MessagingDataSourceRT.class);

    private final DataSourceVO<?> vo;
    private final MessagingService messagingService;
    private final Map<Integer, AtomicInteger> updateAttemptsCounters;
    private final int updateAttempts;

    public MessagingDataSourceRT(DataSourceUpdatable<?> vo, MessagingService messagingService) {
        super(vo.toDataSource());
        this.vo = vo.toDataSource();
        this.messagingService = messagingService;
        this.updateAttemptsCounters = new ConcurrentHashMap<>();
        this.updateAttempts = vo.getUpdateAttempts();
        setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
    }

    @Override
    public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source) {
        DataPointVO dataPointVO = dataPoint.getVO();
        if (!messagingService.isOpen(dataPoint)) {
            LOG.warn("Error Publish: " + dataSourcePointValueTimeInfo(vo, dataPointVO, valueTime, source));
            raiseEvent(DATA_POINT_PUBLISH_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                    getExceptionMessage(new RuntimeException("Error Publish: " + dataSourcePointValueTimeInfo(vo, dataPointVO, valueTime, source) + ", Message: Connection Closed. ")),
                    dataPoint);
            return;
        }
        String message = valueTime.getStringValue();
        try {
            messagingService.publish(dataPoint, message);
            returnToNormal(DATA_POINT_PUBLISH_EXCEPTION_EVENT, System.currentTimeMillis(), dataPoint);
        } catch (Exception e) {
            LOG.error(dataSourcePointValueTimeInfo(vo, dataPointVO, valueTime, source) + ", "
                    + exceptionInfo(e), e);
            raiseEvent(DATA_POINT_PUBLISH_EXCEPTION_EVENT, System.currentTimeMillis(), true,
                    new LocalizableMessage("event.ds.publishFailed", dataPointVO.getName()), dataPoint);
        }
    }

    @Override
    public void initialize() {
        try {
            messagingService.open();
            returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis());
        } catch (Exception e) {
            LOG.error(exceptionInfo(e), e);
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(),
                    true, getExceptionMessage(e));
        }
        super.initialize();
    }

    @Override
    public void terminate() {
        this.vo.setEnabled(false);
        super.terminate();
        try {
            messagingService.close();
            returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis());
        } catch (Exception e) {
            LOG.error(exceptionInfo(e), e);
            raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(),
                    true, getExceptionMessage(e));
        } finally {
            updateAttemptsCounters.clear();
        }
    }

    @Override
    public void addDataPoint(DataPointRT dataPoint) {
        try {
            updateAttemptsCounters.putIfAbsent(dataPoint.getId(), new AtomicInteger());
            messagingService.initReceiver(dataPoint, getPointUpdateExceptionHandler(dataPoint), getPointUpdateReturnToNormalHandler());
            returnToNormal(DATA_POINT_INIT_EXCEPTION_EVENT, System.currentTimeMillis(), dataPoint);
        } catch (Exception e) {
            LOG.error(exceptionInfo(e), e);
            raiseEvent(DATA_POINT_INIT_EXCEPTION_EVENT, System.currentTimeMillis(),
                    true, getExceptionMessage(e), dataPoint);
        }
        super.addDataPoint(dataPoint);
    }

    @Override
    public void removeDataPoint(DataPointRT dataPoint) {
        try {
            messagingService.removeReceiver(dataPoint);
            returnToNormal(DATA_POINT_INIT_EXCEPTION_EVENT, System.currentTimeMillis(), dataPoint);
        } catch (Exception e) {
            LOG.error(exceptionInfo(e), e);
            raiseEvent(DATA_POINT_INIT_EXCEPTION_EVENT, System.currentTimeMillis(),
                    true, getExceptionMessage(e), dataPoint);
        } finally {
            updateAttemptsCounters.remove(dataPoint.getId());
        }
        super.removeDataPoint(dataPoint);
    }

    @Override
    protected void doPoll(long time) {
        for (DataPointRT dataPoint : dataPoints) {
            try {
                updateAttemptsCounters.putIfAbsent(dataPoint.getId(), new AtomicInteger());
                if(updateAttemptsCounters.get(dataPoint.getId()).get() < updateAttempts) {
                    messagingService.initReceiver(dataPoint, getPointUpdateExceptionHandler(dataPoint), getPointUpdateReturnToNormalHandler());
                    updateAttemptsCounters.get(dataPoint.getId()).set(0);
                    returnToNormal(DATA_POINT_INIT_EXCEPTION_EVENT, System.currentTimeMillis(), dataPoint);
                }
            } catch (Exception e) {
                updateAttemptsCounters.get(dataPoint.getId()).incrementAndGet();
                LOG.warn(exceptionInfo(e), e);
                raiseEvent(DATA_POINT_INIT_EXCEPTION_EVENT, System.currentTimeMillis(),
                        true, getExceptionMessage(e), dataPoint);
            }
        }
    }

    private java.util.function.Consumer<Exception> getPointUpdateExceptionHandler(DataPointRT dataPoint) {
        return ex -> {
            LOG.warn("Error Update: " + dataPointInfo(dataPoint.getVO()) + ", " + dataSourceInfo(vo) + ", "
                    + exceptionInfo(ex), ex);
            raiseEvent(DATA_POINT_UPDATE_EXCEPTION_EVENT, System.currentTimeMillis(),
                    true, getExceptionMessage(new Exception("Error Update Data Point: " + dataPointInfo(dataPoint.getVO())
                            + ", " + exceptionInfo(ex), ex)
                    ), dataPoint);
        };
    }

    private java.util.function.Supplier<Void> getPointUpdateReturnToNormalHandler() {
        return () -> {
            returnToNormal(DATA_POINT_UPDATE_EXCEPTION_EVENT, System.currentTimeMillis());
            return null;
        };
    }
}