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
package com.serotonin.mango.rt.dataSource;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.BinaryValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.view.text.MultistateRenderer;
import com.serotonin.mango.view.text.MultistateValue;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.util.StringUtils;
import com.serotonin.web.i18n.LocalizableException;
import com.serotonin.web.i18n.LocalizableMessage;
import org.jfree.util.Log;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttPointLocatorVO;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.DataSourceService;
import org.scada_lts.utils.MqttUtils;

import static net.bull.javamelody.internal.common.I18N.getResourceBundle;

/**
 * @author Matthew Lohbihler
 */
public class DataSourceUtils {

	public static MangoValue getValue(Pattern valuePattern, String data,
			int dataTypeId, String binary0Value, TextRenderer textRenderer,
			DecimalFormat valueFormat, String pointName)
			throws LocalizableException {
		if (data == null)
			throw new LocalizableException(new LocalizableMessage(
					"event.valueParse.noData", pointName));
		Matcher matcher = valuePattern.matcher(data);
		if (matcher.find()) {
			String valueStr = matcher.group(1);
			if (valueStr == null)
				valueStr = "";

			return getValue(valueStr, dataTypeId, binary0Value, textRenderer,
					valueFormat, pointName);
		}

		throw new NoMatchException(new LocalizableMessage(
				"event.valueParse.noValue", pointName));
	}

	public static long getValueTime(long time, Pattern timePattern,
			String data, DateFormat timeFormat, String pointName)
			throws LocalizableException {
		if (data == null)
			throw new LocalizableException(new LocalizableMessage(
					"event.valueParse.noData", pointName));

		// Get the time.
		long valueTime = time;
		if (timePattern != null) {
			Matcher matcher = timePattern.matcher(data);
			if (matcher.find()) {
				String timeStr = matcher.group(1);
				try {
					valueTime = timeFormat.parse(timeStr).getTime();
				} catch (ParseException e) {
					if (pointName == null)
						throw new LocalizableException(new LocalizableMessage(
								"event.valueParse.timeParse", timeStr));
					throw new LocalizableException(new LocalizableMessage(
							"event.valueParse.timeParsePoint", timeStr,
							pointName));
				}
			} else
				throw new LocalizableException(new LocalizableMessage(
						"event.valueParse.noTime", pointName));
		}

		return valueTime;
	}

	public static MangoValue getValue(String valueStr, int dataTypeId,
			String binary0Value, TextRenderer textRenderer,
			DecimalFormat valueFormat, String pointName)
			throws LocalizableException {
		if (dataTypeId == DataTypes.ALPHANUMERIC)
			return new AlphanumericValue(valueStr);

		if (dataTypeId == DataTypes.BINARY)
			return new BinaryValue(!valueStr.equals(binary0Value));

		if (dataTypeId == DataTypes.MULTISTATE) {
			if (textRenderer instanceof MultistateRenderer) {
				List<MultistateValue> multistateValues = ((MultistateRenderer) textRenderer)
						.getMultistateValues();
				for (MultistateValue multistateValue : multistateValues) {
					if (multistateValue.getText().equalsIgnoreCase(valueStr))
						return new com.serotonin.mango.rt.dataImage.types.MultistateValue(
								multistateValue.getKey());
				}
			}

			try {
				return com.serotonin.mango.rt.dataImage.types.MultistateValue
						.parseMultistate(valueStr);
			} catch (NumberFormatException e) {
				if (pointName == null)
					throw new LocalizableException(new LocalizableMessage(
							"event.valueParse.textParse", valueStr));
				throw new LocalizableException(new LocalizableMessage(
						"event.valueParse.textParsePoint", valueStr, pointName));
			}
		}

		if (dataTypeId == DataTypes.NUMERIC) {
			try {
				if (valueFormat != null)
					return new NumericValue(valueFormat.parse(valueStr)
							.doubleValue());
				return NumericValue.parseNumeric(valueStr);
			} catch (NumberFormatException e) {
				if (pointName == null)
					throw new LocalizableException(new LocalizableMessage(
							"event.valueParse.numericParse", valueStr));
				throw new LocalizableException(new LocalizableMessage(
						"event.valueParse.numericParsePoint", valueStr,
						pointName));
			} catch (ParseException e) {
				if (pointName == null)
					throw new LocalizableException(new LocalizableMessage(
							"event.valueParse.generalParse", e.getMessage(),
							valueStr));
				throw new LocalizableException(new LocalizableMessage(
						"event.valueParse.generalParsePoint", e.getMessage(),
						valueStr, pointName));
			}
		}

		return null;
	}

	public static int copyDataPoint(final int dataSourceId, final int dataPointId){
		DataSourceService dataSourceService = new DataSourceService();
		DataSourceVO<?> dataSource = dataSourceService.getDataSource(dataSourceId);

		DataPointService dataPointService = new DataPointService();
		DataPointVO dataPoint = dataPointService.getDataPoint(dataPointId);
		return copyPoint(dataSource, dataPoint, new DataPointService());
	}

	public static void copyPoints(int dataSourceId, DataSourceVO<?> dataSourceCopy, DataPointService dataPointService) {
		for (DataPointVO dataPoint: dataPointService.getDataPoints(dataSourceId, null)) {
			copyPoint(dataSourceCopy, dataPoint, dataPointService);
		}
	}

	public static int copyPoint(DataSourceVO<?> dataSourceCopy, DataPointVO dataPoint, DataPointService dataPointService) {
		DataPointVO dataPointCopy = dataPoint.copy();
		dataPointCopy.setId(Common.NEW_ID);
		dataPointCopy.setXid(new DataPointService().generateUniqueXid());
		dataPointCopy.setName(getCopyName(getResourceBundle(), dataPoint.getName(), 250));
		dataPointCopy.setDataSourceId(dataSourceCopy.getId());
		dataPointCopy.setDataSourceName(dataSourceCopy.getName());
		dataPointCopy.setDeviceName(dataSourceCopy.getName());
		dataPointCopy.setEnabled(dataSourceCopy.isEnabled());
		dataPointCopy.getComments().clear();

		if(dataPointCopy.getPointLocator() instanceof MqttPointLocatorVO) {
			MqttPointLocatorVO pointLocator = dataPointCopy.getPointLocator();
			pointLocator.setClientId(MqttUtils.generateUniqueClientId());
		}

		//Copy event detectors
		for (PointEventDetectorVO pointEventDetector: dataPointCopy.getEventDetectors()) {
			pointEventDetector.setId(Common.NEW_ID);
			pointEventDetector.setXid(new DataPointService().generateEventDetectorUniqueXid(dataPointCopy.getId()));
			pointEventDetector.njbSetDataPoint(dataPointCopy);
		}
		dataPointService.saveDataPoint(dataPointCopy);
		//Copy permissions
		dataPointService.copyPermissions(dataPoint.getId(), dataPointCopy.getId());
		return dataPointCopy.getId();
	}
	public static String getCopyName(ResourceBundle bundle, String originalName, int lengthLimit) {
		return StringUtils.truncate(Common.getMessage("common.copyPrefix", originalName), lengthLimit);
	}
}
