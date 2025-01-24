package org.scada_lts.ds.polling;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import org.scada_lts.ds.DataSourceUpdatable;
import org.scada_lts.ds.polling.service.IMaster;
import org.scada_lts.ds.polling.service.DataPointReadResponse;
import org.scada_lts.utils.DataPointUnreliableUtils;
import com.serotonin.mango.DataTypes;
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


public class PollingDataSourceRT<D extends DataSourceUpdatable<?>> extends PollingDataSource {

	private final Log LOG = LogFactory.getLog(PollingDataSourceRT.class);
	public static final int POINT_READ_EXCEPTION_EVENT = 1;
	public static final int DATA_SOURCE_EXCEPTION_EVENT = 2;
	public static final int POINT_WRITE_EXCEPTION_EVENT = 3;
	public static final int POINT_UPDATE_EXCEPTION_EVENT = 4;
	public static final int POINT_READ_ALL_EXCEPTION_EVENT = 5;

	private final IMaster master;
	private final DataSourceVO<?> vo;
	private int timeoutCount = 0;
	private volatile boolean reconnected = false;

	public PollingDataSourceRT(D vo, IMaster master) {
		super(vo.toDataSource());
		this.vo = vo.toDataSource();
		this.master = master;
		setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(),
				vo.isQuantize());
	}

	@Override
	protected void doPoll(long time) {

		List<DataPointRT> dataPoints = new ArrayList<>(getDataPoints());

		if (timeoutCount >= 3) {
			LOG.warn(master.getName() + "Trying to reconnect ! :" + LoggingUtils.dataSourceInfo(vo));
			timeoutCount = 0;
			initialize();
		} else {
			try {
				master.ping();
				_returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, time);
			} catch (Throwable e) {
				DataPointUnreliableUtils.setUnreliableDataPoints(dataPoints);
				String message = master.getName() + "Poll Failed ! :" + LoggingUtils.exceptionInfo(e) + " - "
						+ LoggingUtils.dataSourceInfo(vo);
				LOG.warn(message);
				_raiseEvent(
						DATA_SOURCE_EXCEPTION_EVENT,
						time,
						true,
						new LocalizableMessage("event.exception2", vo.getName(), message));
				timeoutCount++;
				return;
			}
		}

		if(!reconnected || dataPoints.isEmpty()) {
			return;
		}

		List<DataPointVO> dataPointsVO = dataPoints.stream()
				.map(DataPointRT::getVO)
				.collect(Collectors.toList());

		DataPointReadResponse response;
		try {
			response = master.readAll(dataPointsVO, time);
			_returnToNormal(POINT_READ_ALL_EXCEPTION_EVENT, time);
		} catch (Throwable throwable) {
			DataPointUnreliableUtils.setUnreliableDataPoints(dataPoints);
			String message = master.getName() + "Read All Failed ! :" + LoggingUtils.info(throwable, this) + " - "
					+ LoggingUtils.dataSourceInfo(vo);
			LOG.warn(message);
			_raiseEvent(POINT_READ_ALL_EXCEPTION_EVENT, time, true,
					new LocalizableMessage("event.exception2",
							vo.getName(), message));
			return;
		}

		for(DataPointRT dataPoint: dataPoints) {
			DataPointVO dataPointVO = dataPoint.getVO();
			Throwable error = response.getError(dataPointVO.getXid());

			if(error == null) {
				_returnToNormal(POINT_READ_EXCEPTION_EVENT, time, dataPoint.getId());
			} else {
				DataPointUnreliableUtils.setUnreliableDataPoint(dataPoint);
				String message = master.getName() + "Read Failed ! :" + LoggingUtils.info(error, this, dataPoint) + " - "
						+ LoggingUtils.dataSourceInfo(vo);
				LOG.warn(message);
				_raiseEvent(POINT_READ_EXCEPTION_EVENT, time, true,
						new LocalizableMessage("event.exception2",
								vo.getName(), message), dataPoint.getVO());
				continue;
			}

			PointValueTime pointValueTime = response.getValue(dataPointVO.getXid());

			if(pointValueTime == null) {
				DataPointUnreliableUtils.setUnreliableDataPoint(dataPoint);
				String message = master.getName() + "Read Failed ! :" + LoggingUtils.dataPointInfo(dataPoint) + " - "
						+ LoggingUtils.dataSourceInfo(vo);
				LOG.warn(message);
				_raiseEvent(POINT_UPDATE_EXCEPTION_EVENT, time, true,
						new LocalizableMessage("event.exception2", vo.getName(), message), dataPoint.getVO());
			} else {

				try {
					dataPoint.updatePointValue(pointValueTime);
					_returnToNormal(POINT_UPDATE_EXCEPTION_EVENT, time, dataPoint.getId());
					DataPointUnreliableUtils.resetUnreliableDataPoint(dataPoint);
				} catch (Throwable throwable) {
					DataPointUnreliableUtils.setUnreliableDataPoint(dataPoint);
					String message = master.getName() + "Update Failed ! :" + LoggingUtils.info(throwable, this, dataPoint) + " - "
							+ LoggingUtils.dataSourceInfo(vo);
					LOG.warn(message);
					_raiseEvent(POINT_UPDATE_EXCEPTION_EVENT, time, true,
							new LocalizableMessage("event.exception2",
									vo.getName(), message), dataPoint.getVO());
					continue;
				}
			}
		}
	}

	@Override
	public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime,
			SetPointSource source) {
		Object value;
		if (dataPoint.getDataTypeId() == DataTypes.NUMERIC)
			value = valueTime.getDoubleValue();
		else if (dataPoint.getDataTypeId() == DataTypes.BINARY)
			value = valueTime.getBooleanValue();
		else if (dataPoint.getDataTypeId() == DataTypes.MULTISTATE)
			value = valueTime.getIntegerValue();
		else
			value = valueTime.getStringValue();

		try {
			master.write(dataPoint.getVO(), value);
			_returnToNormal(POINT_WRITE_EXCEPTION_EVENT, System.currentTimeMillis(), dataPoint.getId());
			DataPointUnreliableUtils.resetUnreliableDataPoint(dataPoint);
		} catch (Throwable e) {
			DataPointUnreliableUtils.setUnreliableDataPoint(dataPoint);
			String message = master.getName() + "Write Failed ! :" + LoggingUtils.info(e, vo, dataPoint.getVO());
			LOG.warn(message);
			_raiseEvent(POINT_WRITE_EXCEPTION_EVENT,
					System.currentTimeMillis(),
					true,
					new LocalizableMessage("event.exception2", vo.getName(), message), dataPoint.getVO());
		}
	}

	@Override
	public void initialize() {
		this.reconnected = false;
		try {
			master.terminate();
		} catch (Throwable e) {
			LOG.warn(LoggingUtils.info(e, this));
		}
		List<DataPointRT> dataPoints = new ArrayList<>(this.dataPoints);
		try {
			this.master.init();
			_returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis());
			this.reconnected = true;
			DataPointUnreliableUtils.resetUnreliableDataPoints(dataPoints);
		} catch (Throwable e) {
			DataPointUnreliableUtils.setUnreliableDataPoints(dataPoints);
			String message = master.getName() + "Initialize Failed ! : " + LoggingUtils.exceptionInfo(e) + " - "
					+ LoggingUtils.dataSourceInfo(vo);
			LOG.warn(message);
			_raiseEvent(
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
			if(master != null)
				master.terminate();
			returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis());
		} catch (Throwable e) {
			String message = master.getName() + "Terminate Failed ! : " + LoggingUtils.exceptionInfo(e) + " - "
					+ LoggingUtils.dataSourceInfo(vo);
			LOG.error(message);
			raiseEvent(DATA_SOURCE_EXCEPTION_EVENT,
					System.currentTimeMillis(),
					true,
					new LocalizableMessage("event.exception2", vo.getName(), message));
		}
	}
}
