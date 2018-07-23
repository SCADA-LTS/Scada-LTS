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

import java.util.Date;
import java.util.List;

import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ExceptionResult;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.sero.messaging.MessagingExceptionHandler;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.BaseLocator;
import com.serotonin.modbus4j.locator.NumericLocator;

//import com.serotonin.modbus4j.messaging.MessagingExceptionHandler;
/**
 * @author Matthew Lohbihler
 */
public class EBI25Constants {
	public static final int SLAVE_NODE = 1;
	public static final int RANGE = RegisterRange.HOLDING_REGISTER;
	public static final int MAX_DATA_LOGGERS = 250;
	private static final int TWO_BYTE = DataType.TWO_BYTE_INT_SIGNED;
	public static final int FOUR_BYTE = DataType.FOUR_BYTE_INT_SIGNED_SWAPPED;

	// TODO Many of these values have been changed.
	public static final int OFFSET_SYS_TIME = 6;
	public static final int OFFSET_UNIT = 8;
	public static final int OFFSET_SAMPLE_RATE = 9;
	public static final int OFFSET_LOW_LIMIT = 10;
	public static final int OFFSET_HIGH_LIMIT = 11;
	public static final int OFFSET_SIGNAL = 12;
	public static final int OFFSET_BATTERY = 14;
	public static final int OFFSET_MEASUREMENT_COUNT = 16;
	public static final int OFFSET_MEASUREMENT_START = 20;
	public static final int OFFSET_MEASUREMENT_POINTER = 22;
	public static final int OFFSET_MEASUREMENT_FIFO_START = 23;

	public static final int MEASUREMENT_FIFO_LENGTH = 250;

	public static int calculateBaseAddress(int loggerIndex) {
		return loggerIndex * 300 + 1000;
	}

	public static final int UNIT_TYPE_TEMPERATURE = 1;
	public static final int UNIT_TYPE_PRESSURE = 2;
	public static final int UNIT_TYPE_HUMIDITY = 3;
	public static final int UNIT_TYPE_VOLTAGE = 4;
	public static final int UNIT_TYPE_CURRENT = 5;
	public static final int UNIT_TYPE_PH = 6;
	public static final int UNIT_TYPE_CONDUCTIVITY = 7;

	public static final ExportCodes UNIT_CODES = new ExportCodes();
	static {
		UNIT_CODES.addElement(UNIT_TYPE_TEMPERATURE, "TEMPERATURE");
		UNIT_CODES.addElement(UNIT_TYPE_PRESSURE, "PRESSURE");
		UNIT_CODES.addElement(UNIT_TYPE_HUMIDITY, "HUMIDITY");
		UNIT_CODES.addElement(UNIT_TYPE_VOLTAGE, "VOLTAGE");
		UNIT_CODES.addElement(UNIT_TYPE_CURRENT, "CURRENT");
		UNIT_CODES.addElement(UNIT_TYPE_PH, "PH");
		UNIT_CODES.addElement(UNIT_TYPE_CONDUCTIVITY, "CONDUCTIVITY");
	}

	public static String getStringResult(BatchResults<String> results,
			String key) throws ExceptionResultException {
		return getResult(results, key).toString();
	}

	public static String getDateResult(BatchResults<String> results, String key)
			throws ExceptionResultException {
		return new Date(getTimeResult(results, key)).toString();
	}

	public static long getTimeResult(BatchResults<String> results, String key)
			throws ExceptionResultException {
		return ((Number) getResult(results, key)).longValue() * 1000;
	}

	public static String getVersionResult(BatchResults<String> results,
			String key) throws ExceptionResultException {
		return new Double(
				((Number) getResult(results, key)).doubleValue() / 100)
				.toString();
	}

	public static int getIntResult(BatchResults<String> results, String key)
			throws ExceptionResultException {
		return ((Number) getResult(results, key)).intValue();
	}

	public static double getDoubleResult(BatchResults<String> results,
			String key) throws ExceptionResultException {
		return ((Number) getResult(results, key)).doubleValue();
	}

	private static Object getResult(BatchResults<String> results, String key)
			throws ExceptionResultException {
		Object o = results.getValue(key);
		if (o instanceof ExceptionResult)
			throw new ExceptionResultException(key, (ExceptionResult) o);
		return o;
	}

	public static BaseLocator<Number> createLocator(int offset, boolean fourByte) {
		return new NumericLocator(SLAVE_NODE, RANGE, offset,
				fourByte ? FOUR_BYTE : TWO_BYTE);
	}

	public static BaseLocator<Number> createLocator(int loggerIndex,
			int offset, boolean fourByte) {
		return new NumericLocator(SLAVE_NODE, RANGE,
				calculateBaseAddress(loggerIndex) + offset,
				fourByte ? FOUR_BYTE : TWO_BYTE);
	}

	public static void addLocator(BatchRead<String> batch, String key,
			int offset, boolean fourByte) {
		batch.addLocator(key, new NumericLocator(SLAVE_NODE, RANGE, offset,
				fourByte ? FOUR_BYTE : TWO_BYTE));
	}

	public static void addLocator(BatchRead<String> batch, String key,
			int loggerIndex, int offset, boolean fourByte) {
		batch.addLocator(key, new NumericLocator(SLAVE_NODE, RANGE,
				calculateBaseAddress(loggerIndex) + offset,
				fourByte ? FOUR_BYTE : TWO_BYTE));
	}

	public static ModbusMaster initModbusMaster(String host, int port,
			boolean keepAlive, int timeout, int retries,
			MessagingExceptionHandler exceptionListener)
			throws ModbusInitException {
		IpParameters params = new IpParameters();
		params.setHost(host);
		params.setPort(port);

		ModbusMaster modbusMaster = new ModbusFactory().createTcpMaster(params,
				keepAlive);
		modbusMaster.setTimeout(timeout);
		modbusMaster.setRetries(retries);
		//TODO write own exceptionListener implements MessagingExceptionHandler;
		if (exceptionListener != null)
			modbusMaster.setExceptionHandler((MessagingExceptionHandler) exceptionListener);

		modbusMaster.init();

		return modbusMaster;
	}

	public static void destroyModbusMaster(ModbusMaster modbusMaster) {
		if (modbusMaster != null)
			modbusMaster.destroy();
	}

	public static PointEventDetectorVO findDetector(
			List<PointEventDetectorVO> eventDetectors, boolean high) {
		for (PointEventDetectorVO ped : eventDetectors) {
			if (high
					&& ped.getDetectorType() == PointEventDetectorVO.TYPE_ANALOG_HIGH_LIMIT)
				return ped;
			if (!high
					&& ped.getDetectorType() == PointEventDetectorVO.TYPE_ANALOG_LOW_LIMIT)
				return ped;
		}
		return null;
	}
}
