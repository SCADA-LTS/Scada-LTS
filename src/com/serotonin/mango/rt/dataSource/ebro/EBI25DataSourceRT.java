/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.rt.dataSource.ebro;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.NotImplementedException;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.PollingDataSource;
import com.serotonin.mango.vo.dataSource.ebro.EBI25DataSourceVO;
import com.serotonin.mango.vo.dataSource.ebro.EBI25PointLocatorVO;
import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.sero.messaging.MessagingExceptionHandler;
import com.serotonin.modbus4j.sero.messaging.TimeoutException;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class EBI25DataSourceRT extends PollingDataSource implements
		MessagingExceptionHandler {
	private final Log LOG = LogFactory.getLog(EBI25DataSourceRT.class);

	// public static final int POINT_READ_EXCEPTION_EVENT = 1;
	// public static final int POINT_WRITE_EXCEPTION_EVENT = 2;
	public static final int DATA_SOURCE_EXCEPTION_EVENT = 3;

	private ModbusMaster modbusMaster;
	private final EBI25DataSourceVO vo;

	public EBI25DataSourceRT(EBI25DataSourceVO vo) {
		super(vo);
		this.vo = vo;
		setPollingPeriod(vo.getUpdatePeriodType(), vo.getUpdatePeriods(), false);
	}

	@Override
	protected void doPoll(long time) {
		if (modbusMaster == null)
			return;

		// Get a list of logger indices. The list of points does not include
		// disabled points, so completely disabled
		// loggers will not be in the index list.
		List<Integer> loggerIndices = new ArrayList<Integer>();
		for (DataPointRT dp : dataPoints) {
			int index = ((EBI25PointLocatorRT) dp.getPointLocator()).getVO()
					.getIndex();
			if (!loggerIndices.contains(index))
				loggerIndices.add(index);
		}

		try {
			for (Integer loggerIndex : loggerIndices) {
				DataPointRT valuePoint = getLoggerPoint(loggerIndex,
						EBI25PointLocatorVO.TYPE_VALUE);
				DataPointRT batteryPoint = getLoggerPoint(loggerIndex,
						EBI25PointLocatorVO.TYPE_BATTERY);
				DataPointRT signalPoint = getLoggerPoint(loggerIndex,
						EBI25PointLocatorVO.TYPE_SIGNAL);

				// Read the first batch of values.
				BatchRead<String> batch = new BatchRead<String>();
				EBI25Constants.addLocator(batch, "count", loggerIndex,
						EBI25Constants.OFFSET_MEASUREMENT_COUNT, false);
				EBI25Constants.addLocator(batch, "start", loggerIndex,
						EBI25Constants.OFFSET_MEASUREMENT_START, true);
				EBI25Constants.addLocator(batch, "rate", loggerIndex,
						EBI25Constants.OFFSET_SAMPLE_RATE, false);
				EBI25Constants.addLocator(batch, "pointer", loggerIndex,
						EBI25Constants.OFFSET_MEASUREMENT_POINTER, false);
				EBI25Constants.addLocator(batch, "battery", loggerIndex,
						EBI25Constants.OFFSET_BATTERY, false);
				EBI25Constants.addLocator(batch, "signal", loggerIndex,
						EBI25Constants.OFFSET_SIGNAL, false);

				BatchResults<String> results = modbusMaster.send(batch);

				int count = EBI25Constants.getIntResult(results, "count");
				if (valuePoint != null && count > 0) {
					// Value point is enabled.
					BatchRead<String> valueBatch = new BatchRead<String>();

					int pointer = EBI25Constants.getIntResult(results,
							"pointer");
					for (int i = 0; i < count; i++) {
						EBI25Constants.addLocator(valueBatch, "value" + i,
								loggerIndex,
								EBI25Constants.OFFSET_MEASUREMENT_FIFO_START
										+ pointer, false);
						pointer = (pointer + 1)
								% EBI25Constants.MEASUREMENT_FIFO_LENGTH;
					}

					// Run the batch.
					BatchResults<String> valueResults = modbusMaster
							.send(valueBatch);

					// Clear the number of measurements counter now instead of
					// waiting until we read the value results
					// just to minimize the possibility of a race condition.
					modbusMaster.setValue(EBI25Constants.createLocator(
							loggerIndex,
							EBI25Constants.OFFSET_MEASUREMENT_COUNT, false), 0);

					EBI25PointLocatorVO locator = ((EBI25PointLocatorRT) valuePoint
							.getPointLocator()).getVO();
					int sampleRateSeconds = EBI25Constants.getIntResult(
							results, "rate");
					long valueTime = EBI25Constants.getTimeResult(results,
							"start");
					for (int i = 0; i < count; i++) {
						// Read the value
						int value = EBI25Constants.getIntResult(valueResults,
								"value" + i);

						// Update the point
						double dvalue = locator.translateFromRawValue(value);
						valuePoint.updatePointValue(new PointValueTime(dvalue,
								valueTime));

						// Add the sample rate onto the value time.
						valueTime += sampleRateSeconds * 1000;
					}

					// Back out the sample rate for use with the battery and
					// signal points.
					valueTime += sampleRateSeconds * 1000;

					if (batteryPoint != null)
						// Battery point is enabled
						batteryPoint.updatePointValue(new PointValueTime(
								EBI25Constants.getDoubleResult(results,
										"battery"), valueTime));

					if (signalPoint != null)
						// Battery point is enabled
						signalPoint.updatePointValue(new PointValueTime(
								EBI25Constants.getDoubleResult(results,
										"signal"), valueTime));
				}
			}

			// Deactivate any existing event.
			returnToNormal(DATA_SOURCE_EXCEPTION_EVENT, time);
		} catch (Exception e) {
			raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, time, true,
					getLocalExceptionMessage(e));
		}
	}

	private DataPointRT getLoggerPoint(int index, int type) {
		for (DataPointRT dp : dataPoints) {
			EBI25PointLocatorRT locator = dp.getPointLocator();
			if (locator.getVO().getIndex() == index
					&& locator.getVO().getType() == type)
				return dp;
		}
		return null;
	}

	//
	// /
	// / Lifecycle
	// /
	//
	@Override
	public void initialize() {
		try {
			modbusMaster = EBI25Constants.initModbusMaster(vo.getHost(),
					vo.getPort(), vo.isKeepAlive(), vo.getTimeout(),
					vo.getRetries(), this);

			// Deactivate any existing event.
			returnToNormal(DATA_SOURCE_EXCEPTION_EVENT,
					System.currentTimeMillis());
		} catch (Exception e) {
			raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(),
					true, getLocalExceptionMessage(e));
			LOG.debug("Error while initializing data source", e);
			return;
		}

		super.initialize();
	}

	@Override
	public void terminate() {
		super.terminate();
		EBI25Constants.destroyModbusMaster(modbusMaster);
	}

	//
	//
	// /
	// / Data source interface
	// /
	//
	//
	@Override
	public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime,
			SetPointSource source) {
		throw new NotImplementedException();
	}

	protected LocalizableMessage getLocalExceptionMessage(Exception e) {
		if (e instanceof ExceptionResultException) {
			ExceptionResultException ere = (ExceptionResultException) e;
			return new LocalizableMessage("event.ebi25.readError",
					ere.getKey(), ere.getExceptionResult()
							.getExceptionMessage());
		}

		if (e instanceof ModbusTransportException) {
			Throwable cause = e.getCause();
			if (cause instanceof TimeoutException)
				return new LocalizableMessage("event.modbus.noResponse");
			if (cause instanceof ConnectException)
				return new LocalizableMessage("common.default", e.getMessage());
		}

		return DataSourceRT.getExceptionMessage(e);
	}

	//
	//
	// /
	// / MessagingConnectionListener interface
	// /
	//
	//
	public void receivedException(Exception e) {
		raiseEvent(DATA_SOURCE_EXCEPTION_EVENT, System.currentTimeMillis(),
				true,
				new LocalizableMessage("event.ebi25.master", e.getMessage()));
	}
}
