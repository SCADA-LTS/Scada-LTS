package org.scada_lts.ds.polling;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import org.scada_lts.ds.DataSourceUpdatable;
import org.scada_lts.ds.polling.service.PollingService;
import org.scada_lts.ds.polling.service.DataPointReadResponse;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.web.i18n.LocalizableMessage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PollingDataSourceRT extends PollingDataSource {

	private final Log LOG = LogFactory.getLog(PollingDataSourceRT.class);
	public static final int POINT_READ_EXCEPTION_EVENT = 1;
	public static final int DATA_SOURCE_EXCEPTION_EVENT = 2;
	public static final int POINT_WRITE_EXCEPTION_EVENT = 3;
	public static final int POINT_UPDATE_EXCEPTION_EVENT = 4;
	public static final int POINT_READ_ALL_EXCEPTION_EVENT = 5;
	public static final int UPDATE_TIME_EXCEEDED_UPDATE_PERIOD_EXCEPTION_EVENT = 6;

	private final PollingService pollingService;
	private final DataSourceVO<?> vo;
	private int timeoutCount = 0;
	private volatile boolean reconnected = false;

	public PollingDataSourceRT(DataSourceUpdatable<?> vo, PollingService pollingService) {
		super(vo.toDataSource());
		this.vo = vo.toDataSource();
		this.pollingService = pollingService;
		setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(),
				vo.isQuantize());
	}

	@Override
	protected void doPoll(long time) {

		List<DataPointRT> dataPoints = new ArrayList<>(getDataPoints());

		if (timeoutCount >= 3) {
			LOG.warn(pollingService.getName() + "Trying to reconnect ! :" + LoggingUtils.dataSourceInfo(vo));
			timeoutCount = 0;
			initialize();
		} else {
			try {
				pollingService.ping();
				returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, time);
			} catch (Throwable e) {
				String message = pollingService.getName() + "Poll Failed ! :" + LoggingUtils.exceptionInfo(e) + " - "
						+ LoggingUtils.dataSourceInfo(vo);
				LOG.warn(message);
				raiseEvent(
						DATA_SOURCE_EXCEPTION_EVENT,
						time,
						true,
						new LocalizableMessage("event.exception2", vo.getName(), message));
				timeoutCount++;
				return;
			}
		}

		if (!reconnected || dataPoints.isEmpty()) {
			return;
		}

		List<DataPointVO> points = dataPoints.stream()
				.map(DataPointRT::getVO)
				.collect(Collectors.toList());

		DataPointReadResponse response;
		try {
			response = pollingService.read(points, time);
			returnToNormal(POINT_READ_ALL_EXCEPTION_EVENT, time);
		} catch (Throwable throwable) {
			String message = pollingService.getName() + "Read All Failed ! :" + LoggingUtils.info(throwable, this) + " - "
					+ LoggingUtils.dataSourceInfo(vo);
			LOG.warn(message);
			raiseEvent(POINT_READ_ALL_EXCEPTION_EVENT, time, true,
					new LocalizableMessage("event.exception2",
							vo.getName(), message));
			return;
		}

		for (DataPointRT dataPoint: dataPoints) {
			DataPointVO dataPointVO = dataPoint.getVO();
			String dataPointXid = dataPointVO.getXid();

			response.getError(dataPointXid).ifPresentOrElse(error -> {
				String message = pollingService.getName() + "Read Failed ! :" + LoggingUtils.info(error, this, dataPoint) + " - " + LoggingUtils.dataSourceInfo(vo);
				LOG.warn(message);
				raiseEvent(POINT_READ_EXCEPTION_EVENT, time, true,
						new LocalizableMessage("event.exception2", vo.getName(), message), dataPoint);

			}, () -> {
				returnToNormal(POINT_READ_EXCEPTION_EVENT, time, dataPoint);
				response.getValue(dataPointXid).ifPresentOrElse(valueTime -> {
					try {
						dataPoint.updatePointValue(valueTime);
						returnToNormal(POINT_UPDATE_EXCEPTION_EVENT, time, dataPoint);
					} catch (Throwable throwable) {
						String message = pollingService.getName() + "Update Failed ! :" + LoggingUtils.info(throwable, this, dataPoint) + " - "
								+ LoggingUtils.dataSourceInfo(vo);
						LOG.warn(message);
						raiseEvent(POINT_UPDATE_EXCEPTION_EVENT, time, true,
								new LocalizableMessage("event.exception2",
										vo.getName(), message), dataPoint);
					}
				}, () -> {
					String message = pollingService.getName() + "Read Failed ! :" + LoggingUtils.dataPointInfo(dataPoint) + " - "
							+ LoggingUtils.dataSourceInfo(vo);
					LOG.warn(message);
					raiseEvent(POINT_UPDATE_EXCEPTION_EVENT, time, true,
							new LocalizableMessage("event.exception2", vo.getName(), message), dataPoint);
				});
			});

		}
	}

	@Override
	public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime,
			SetPointSource source) {

		try {
			pollingService.write(dataPoint.getVO(), valueTime);
			dataPoint.setPointValue(valueTime, null);
			returnToNormal(POINT_WRITE_EXCEPTION_EVENT, System.currentTimeMillis(), dataPoint);
		} catch (Throwable e) {
			String message = pollingService.getName() + "Write Failed ! :" + LoggingUtils.info(e, vo, dataPoint.getVO());
			LOG.warn(message);
			raiseEvent(POINT_WRITE_EXCEPTION_EVENT,
					System.currentTimeMillis(),
					true,
					new LocalizableMessage("event.exception2", vo.getName(), message), dataPoint);
		}
	}

	@Override
	public void initialize() {
		this.reconnected = false;
		try {
			pollingService.terminate();
		} catch (Throwable e) {
			LOG.warn(LoggingUtils.info(e, this));
		}
		try {
			this.pollingService.initialize();
			returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis());
			this.reconnected = true;
		} catch (Throwable e) {
			String message = pollingService.getName() + "Initialize Failed ! : " + LoggingUtils.exceptionInfo(e) + " - "
					+ LoggingUtils.dataSourceInfo(vo);
			LOG.warn(message);
			raiseEvent(
					DATA_SOURCE_EXCEPTION_EVENT,
					System.currentTimeMillis(),
					true,
					new LocalizableMessage("event.exception2", vo.getName(), message));
			return;
		}
		super.initialize();
	}

	@Override
	public void terminate() {
		super.terminate();
		try {
			if(pollingService != null)
				pollingService.terminate();
			returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis());
		} catch (Throwable e) {
			String message = pollingService.getName() + "Terminate Failed ! : " + LoggingUtils.exceptionInfo(e) + " - "
					+ LoggingUtils.dataSourceInfo(vo);
			LOG.error(message);
			raiseEvent(DATA_SOURCE_EXCEPTION_EVENT,
					System.currentTimeMillis(),
					true,
					new LocalizableMessage("event.exception2", vo.getName(), message));
		}
	}

	@Override
	public int getUpdateTimeExceededUpdatePeriodEventId() {
		return UPDATE_TIME_EXCEEDED_UPDATE_PERIOD_EXCEPTION_EVENT;
	}
}
