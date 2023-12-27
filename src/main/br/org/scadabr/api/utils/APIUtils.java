package br.org.scadabr.api.utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import br.org.scadabr.api.ae.AckEventsParams;
import br.org.scadabr.api.ae.AnnotateEventParams;
import br.org.scadabr.api.ae.GetEventsHistoryParams;
import br.org.scadabr.api.config.BrowseDataPointsParams;
import br.org.scadabr.api.config.BrowseDataSourcesParams;
import br.org.scadabr.api.config.ConfigureDataPointParams;
import br.org.scadabr.api.config.ConfigureDataSourceParams;
import br.org.scadabr.api.config.RemoveDataPointParams;
import br.org.scadabr.api.config.RemoveDataSourceParams;
import br.org.scadabr.api.constants.AlarmLevel;
import br.org.scadabr.api.constants.DataSourceType;
import br.org.scadabr.api.constants.DataType;
import br.org.scadabr.api.constants.ErrorCode;
import br.org.scadabr.api.constants.EventType;
import br.org.scadabr.api.da.ReadDataParams;
import br.org.scadabr.api.da.WriteDataParams;
import br.org.scadabr.api.da.WriteStringDataParams;
import br.org.scadabr.api.exception.ScadaBRAPIException;
import br.org.scadabr.api.hda.GetDataHistoryParams;
import br.org.scadabr.api.vo.APIError;
import br.org.scadabr.api.vo.EventDefinition;
import br.org.scadabr.api.vo.EventMessage;
import br.org.scadabr.api.vo.EventNotification;
import br.org.scadabr.api.vo.ModbusIPConfig;
import br.org.scadabr.api.vo.ModbusPointConfig;
import br.org.scadabr.api.vo.ModbusSerialConfig;

import com.serotonin.mango.DataTypes;
import com.serotonin.mango.db.dao.CompoundEventDetectorDao;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.ScheduledEventDao;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.UserComment;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.mango.vo.event.ScheduledEventVO;
import com.serotonin.mango.vo.hierarchy.PointFolder;
import com.serotonin.mango.vo.hierarchy.PointHierarchy;

public final class APIUtils {

	private static Calendar date = Calendar.getInstance();
	private static APIError[] errors;
	private static APIError error;
	private static String path;
	private static HashMap<Integer, String> folderIdToPath;

	public static String getCompletePath(int folderId, PointHierarchy pH) {
		PointFolder folder = pH.getRoot();

		path = "";
		folderIdToPath = new HashMap<Integer, String>();
		folder.setName("");
		searchFolder(folder);

		String completePath = folderIdToPath.get(folderId);

		if (completePath == null || completePath.trim().equals("")) {
			completePath = "";
		} else if (!completePath.endsWith(".")) {
			completePath += ".";
		}

		return completePath;
	}

	public static boolean checkCompatibleDataTypes(DataType dataType,
			Object value) {
		if (value instanceof Boolean) {
			return (dataType == DataType.BOOLEAN);
		}

		if (value instanceof Integer) {
			return (dataType == DataType.INTEGER);
		}

		if (value instanceof Double) {
			return (dataType == DataType.INTEGER)
					|| (dataType == DataType.DOUBLE)
					|| (dataType == DataType.FLOAT);
		}

		if (value instanceof String) {
			return (dataType == DataType.STRING);
		}
		return false;

	}

	public static DataType whatType(Object value) {
		if (value instanceof Boolean) {
			return DataType.BOOLEAN;
		}

		if (value instanceof Integer) {
			return DataType.INTEGER;
		}

		if (value instanceof Double) {
			return DataType.DOUBLE;
		}

		if (value instanceof String) {
			return DataType.STRING;
		}
		return DataType.BYTE;
	}

	public static boolean checkStringValue(String value, DataType dataType) {
		try {
			if (DataType.BOOLEAN == dataType) {
				System.out.println("Checando " + value + " para binario!");
				MangoValue.stringToValue(value, DataTypes.BINARY);
			} else if (DataType.INTEGER == dataType) {
				MangoValue.stringToValue(value, DataTypes.NUMERIC);
			} else if (DataType.DOUBLE == dataType) {
				MangoValue.stringToValue(value, DataTypes.NUMERIC);
			} else if (DataType.STRING == dataType) {
				MangoValue.stringToValue(value, DataTypes.ALPHANUMERIC);
			} else if (DataType.DOUBLE == dataType) {
				MangoValue.stringToValue(value, DataTypes.NUMERIC);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private static void searchFolder(PointFolder folder) {
		List<PointFolder> children = folder.getSubfolders();
		String anterior = path;

		if (!folder.getName().equals("")) {
			path += folder.getName() + ".";
		}
		for (int i = 0; i < children.size(); i++) {
			if (i == 0) {
				folderIdToPath.put(folder.getId(), path);
			}
			if (children.get(i).getSubfolders().size() > 0) {
				searchFolder(children.get(i));
			} else {
				String x = path + children.get(i).getName();
				folderIdToPath.put(children.get(i).getId(), x);
			}
			if ((i == children.size() - 1)) {
				path = anterior;
			}
		}
	}

	public static void validateWriteData(WriteDataParams params)
			throws ScadaBRAPIException {
		APIError error;
		if (params == null) {
			error = new APIError(ErrorCode.INSUFFICIENT_PARAMETERS,
					APIConstants.INSUFFICIENT_PARAMETER + " (WriteDataParams)");

			throw new ScadaBRAPIException(error);
		}

		if (params.getItemsList() == null || params.getItemsList().length == 0) {
			APIUtils.setError(ErrorCode.INSUFFICIENT_PARAMETERS,
					APIConstants.INSUFFICIENT_PARAMETER + " (itemList)");
			error = new APIError(ErrorCode.INSUFFICIENT_PARAMETERS,
					APIConstants.INSUFFICIENT_PARAMETER + " (itemList)");

			throw new ScadaBRAPIException(error);
		}
	}

	public static void validateStringWriteData(WriteStringDataParams params)
			throws ScadaBRAPIException {
		APIError error;
		if (params == null) {
			error = new APIError(ErrorCode.INSUFFICIENT_PARAMETERS,
					APIConstants.INSUFFICIENT_PARAMETER + " (WriteDataParams)");

			throw new ScadaBRAPIException(error);
		}

		if (params.getItemsList() == null || params.getItemsList().length == 0) {
			APIUtils.setError(ErrorCode.INSUFFICIENT_PARAMETERS,
					APIConstants.INSUFFICIENT_PARAMETER + " (itemList)");
			error = new APIError(ErrorCode.INSUFFICIENT_PARAMETERS,
					APIConstants.INSUFFICIENT_PARAMETER + " (itemList)");

			throw new ScadaBRAPIException(error);
		}
	}

	public static void validateReadData(ReadDataParams params)
			throws ScadaBRAPIException {
		APIError error = null;
		if (params == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (ReadDataParams)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getItemPathList() == null
				|| params.getItemPathList().length == 0) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (itemPathList)");
			throw new ScadaBRAPIException(error);
		}

	}

	public static void validateAnnotateEvent(AnnotateEventParams params)
			throws ScadaBRAPIException {
		APIError error;
		if (params == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (AnnotateEventParams)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getEventId() < 1) {
			error = new APIError();
			error.setCode(ErrorCode.INVALID_PARAMETER);
			error.setDescription(APIConstants.INVALID_PARAMETER + " (eventId)");
			throw new ScadaBRAPIException(error);
		}

		if (params.getMessage() == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (EventMessage)");
			throw new ScadaBRAPIException(error);
		}

		if (isEmpty(params.getMessage().getUser())) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (EventMessage.user)");
			throw new ScadaBRAPIException(error);
		}
		if (isEmpty(params.getMessage().getMessage())) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (EventMessage.message)");
			throw new ScadaBRAPIException(error);
		}
	}

	private static boolean isEmpty(String x) {
		if (x == null)
			return true;
		if (x.trim().equals(""))
			return true;
		return false;
	}

	public static void validateAckEventsParams(AckEventsParams params)
			throws ScadaBRAPIException {
		APIError error;
		if (params == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (AckEventsParams)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getEventsId() == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (eventsId)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getEventsId().length < 1) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (eventsId)");
			throw new ScadaBRAPIException(error);
		}
	}

	public static void validateGetDataHistoryParams(GetDataHistoryParams params)
			throws ScadaBRAPIException {
		APIError error;
		if (params == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (GetDataHistoryParams)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getItemName() == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (itemsPath)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getItemName().trim().equals("")) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (itemsPath)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getOptions() == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (GetDataHistoryOptions)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getOptions().getInitialDate() == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (GetDataHistoryOptions.initialDate)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getOptions().getFinalDate() == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (GetDataHistoryOptions.finalDate)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getOptions().getInitialDate().after(
				params.getOptions().getFinalDate())) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error
					.setDescription(APIConstants.INVALID_PARAMETER
							+ " (GetDataHistoryOptions.initialDate,GetDataHistoryOptions.finalDate)");
			throw new ScadaBRAPIException(error);
		}

	}

	public static void validateEventsHistoryParams(GetEventsHistoryParams params)
			throws ScadaBRAPIException {
		APIError error;
		if (params == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (GetEventsHistoryParams)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getOptions() == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (EventsHistoryOptions)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getOptions().getInitialDate() == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (EventsHistoryOptions.initialDate)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getOptions().getFinalDate() == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (EventsHistoryOptions.finalDate)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getOptions().getInitialDate().after(
				params.getOptions().getFinalDate())) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error
					.setDescription(APIConstants.INVALID_PARAMETER
							+ " (EventsHistoryOptions.initialDate,EventsHistoryOptions.finalDate)");
			throw new ScadaBRAPIException(error);
		}
	}

	public static void validateBrowseDataSourcesParams(
			BrowseDataSourcesParams params) throws ScadaBRAPIException {
		APIError error;
		if (params == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (BrowseDataSourcesParams)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getType() == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (dataSourceType)");
			throw new ScadaBRAPIException(error);
		}

	}

	public static void validateConfigureDataSourceParams(
			ConfigureDataSourceParams params) throws ScadaBRAPIException {
		APIError error;
		if (params == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (BrowseDataSourcesParams)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getType() == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (dataSourceType)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getDataSource() == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (dataSourceType)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getType() == DataSourceType.MODBUS_IP) {
			try {
				ModbusIPConfig config = (ModbusIPConfig) params.getDataSource();
			} catch (Exception e) {
				error = new APIError();
				error.setCode(ErrorCode.INVALID_PARAMETER);
				error.setDescription(APIConstants.INVALID_PARAMETER
						+ " (dataSourceType)");
				throw new ScadaBRAPIException(error);
			}
		}
		if (params.getType() == DataSourceType.MODBUS_SERIAL) {
			try {
				ModbusSerialConfig config = (ModbusSerialConfig) params
						.getDataSource();
			} catch (Exception e) {
				error = new APIError();
				error.setCode(ErrorCode.INVALID_PARAMETER);
				error.setDescription(APIConstants.INVALID_PARAMETER
						+ " (dataSourceType)");
				throw new ScadaBRAPIException(error);
			}
		}

	}

	public static void validateConfigureDataPointParams(
			ConfigureDataPointParams params) throws ScadaBRAPIException {
		APIError error;
		if (params == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (ConfigureDataPointParams)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getType() == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (dataSourceType)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getDataSourceId() < 1) {
			error = new APIError();
			error.setCode(ErrorCode.INVALID_ID);
			error.setDescription(APIConstants.INVALID_ID + " (dataSourceId)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getDataPoint() == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (dataPoint)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getType() == DataSourceType.MODBUS_IP
				|| params.getType() == DataSourceType.MODBUS_SERIAL) {
			try {
				ModbusPointConfig config = (ModbusPointConfig) params
						.getDataPoint();
			} catch (Exception e) {
				error = new APIError();
				error.setCode(ErrorCode.INVALID_PARAMETER);
				error.setDescription(APIConstants.INVALID_PARAMETER
						+ " (dataPoint)");
				throw new ScadaBRAPIException(error);
			}
		}

	}

	public static void validateRemoveDataSourceParams(
			RemoveDataSourceParams params) throws ScadaBRAPIException {
		APIError error;
		if (params == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (RemoveDataSourcesParams)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getId() < 1) {
			error = new APIError();
			error.setCode(ErrorCode.INVALID_ID);
			error.setDescription(APIConstants.INVALID_ID + " (id)");
			throw new ScadaBRAPIException(error);
		}

	}

	public static void validateRemoveDataPointParams(
			RemoveDataPointParams params) throws ScadaBRAPIException {
		APIError error;
		if (params == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (RemoveDataPointParams)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getId() < 1) {
			error = new APIError();
			error.setCode(ErrorCode.INVALID_ID);
			error.setDescription(APIConstants.INVALID_ID + " (id)");
			throw new ScadaBRAPIException(error);
		}

	}

	public static void validateBrowseDataPointsParams(
			BrowseDataPointsParams params) throws ScadaBRAPIException {
		APIError error;
		if (params == null) {
			error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER
					+ " (BrowseDataPointsParams)");
			throw new ScadaBRAPIException(error);
		}
		if (params.getDataSourceId() < 1) {
			error = new APIError();
			error.setCode(ErrorCode.INVALID_ID);
			error.setDescription(APIConstants.INVALID_ID + " (dataSourceId)");
			throw new ScadaBRAPIException(error);
		}

	}

	public static String toScheduledEventConfig(ScheduledEventVO sE) {
		String config = "";
		// TODO: outros tipos de agendados.
		switch (sE.getScheduleType()) {
		case 1:
			config += String.format(
					"[HOURLY - (active: %d:%d)(inactive: %d:%d)]", sE
							.getActiveMinute(), sE.getActiveSecond(), sE
							.getInactiveMinute(), sE.getInactiveSecond());
			break;
		case 2:
			config += String.format(
					"[DAILY - (active: %d:%d:%d)(inactive: %d:%d:%d)]", sE
							.getActiveHour(), sE.getActiveMinute(), sE
							.getActiveSecond(), sE.getInactiveHour(), sE
							.getInactiveMinute(), sE.getInactiveSecond());
			break;
		case 3: // weekly
			config += String
					.format(
							"[WEEKLY - (active: (weekday: %d - %d:%d:%d))(inactive:(weekday: %d - %d:%d:%d))]",
							sE.getActiveDay(), sE.getActiveHour(), sE
									.getActiveMinute(), sE.getActiveSecond(),
							sE.getInactiveDay(), sE.getInactiveHour(), sE
									.getInactiveMinute(), sE
									.getInactiveSecond());
			break;
		case 4: // monthly
			config += String
					.format(
							"[MONTHLY - (active: (day: %d - %d:%d:%d))(inactive:(day: %d - %d:%d:%d))]",
							sE.getActiveDay(), sE.getActiveHour(), sE
									.getActiveMinute(), sE.getActiveSecond(),
							sE.getInactiveDay(), sE.getInactiveHour(), sE
									.getInactiveMinute(), sE
									.getInactiveSecond());
			break;
		case 5: // yearly

			config += String
					.format(
							"[YEARLY- (active: (month: %d day: %d - %d:%d:%d))(inactive:(month: %d day: %d - %d:%d:%d))]",
							sE.getActiveMonth(), sE.getActiveDay(), sE
									.getActiveHour(), sE.getActiveMinute(), sE
									.getActiveSecond(), sE.getInactiveMonth(),
							sE.getInactiveDay(), sE.getInactiveHour(), sE
									.getInactiveMinute(), sE
									.getInactiveSecond());
			break;
		case 6: // once
			config += String
					.format(
							"[ONCE- (active: (year: %d month: %d day: %d - %d:%d:%d))(inactive:(year: %d month: %d day: %d - %d:%d:%d))]",
							sE.getActiveYear(), sE.getActiveMonth(), sE
									.getActiveDay(), sE.getActiveHour(), sE
									.getActiveMinute(), sE.getActiveSecond(),
							sE.getInactiveYear(), sE.getInactiveMonth(), sE
									.getInactiveDay(), sE.getInactiveHour(), sE
									.getInactiveMinute(), sE
									.getInactiveSecond());
			break;
		case 7: // cron
			config += String.format("[CRON - (active: %s)(inactive: %s)]", sE
					.getActiveCron(), sE.getInactiveCron());
			break;

		}
		return config;
	}

	public static String toCondition(PointEventDetectorVO pointEvent) {

		DataPointVO dp = new DataPointDao().getDataPoint(new DataPointDao()
				.getDataPointIdFromDetectorId(pointEvent.getId()));

		String config = "[" + dp.getName() + " - ";
		switch (pointEvent.getDetectorType()) {
		case 1:
			config += String.format("HIGH_LIMIT (limit: %f)(duration: %d)] ",
					pointEvent.getLimit(), pointEvent.getDuration());
			break;
		case 2:
			config += String.format("LOW_LIMIT (limit: %f)(duration: %d)]",
					pointEvent.getLimit(), pointEvent.getDuration());
			break;
		case 3: // state binario
			if (pointEvent.isBinaryState()) {
				config += String.format("STATE (state: %d)(duration: %d)]", 1,
						pointEvent.getDuration());
			} else {
				config += String.format(
						"BINARY_STATE (state: %d)(duration: %d)]", 0,
						pointEvent.getDuration());
			}
			break;
		case 4: // state multistate
			config += String.format("MULTISTATE (state: %d)(duration: %d)]",
					pointEvent.getMultistateState(), pointEvent.getDuration());

			break;
		case 5:
			config += String.format("CHANGE (duration: %d)]", pointEvent
					.getDuration());
			break;
		case 6:
			config += String.format(
					"STATE_CHANGE_COUNT (count: %d)(duration: %d)]", pointEvent
							.getChangeCount(), pointEvent.getDuration());
			break;
		case 7:
			config += String.format("NO_CHANGE (duration: %d)]", pointEvent
					.getDuration());
			break;
		case 8:
			config += String.format("NO_UPDATE (duration: %d)]", pointEvent
					.getDuration());
			break;
		case 9:
			config += String
					.format(
							"ALPHANUMERIC_STATE (state: \"%s\")(duration: %d)]",
							pointEvent.getAlphanumericState(), pointEvent
									.getDuration());
			break;
		case 10:
			config += String.format(
					"POSITIVE_CUSUM (pos limit: %f)(weight:%f)(duration: %d)]",
					pointEvent.getLimit(), pointEvent.getWeight(), pointEvent
							.getDuration());
			break;
		case 11:
			config += String.format(
					"NEGATIVE_CUSUM (neg limit: %f)(weight:%f)(duration: %d)]",
					pointEvent.getLimit(), pointEvent.getWeight(), pointEvent
							.getDuration());
			break;
		}

		return config;
	}

	private static void buildErrorArray(ErrorCode code, String description) {
		errors = new APIError[1];
		error = new APIError();
		APIUtils.error.setCode(code);
		APIUtils.error.setDescription(description);
		APIUtils.errors[0] = error;
	}

	public static APIError[] getError() {
		return APIUtils.errors;
	}

	public static void setError(ErrorCode code, String description) {
		buildErrorArray(code, description);
	}

	public static void setError(APIError[] error) {
		errors = new APIError[error.length];

		for (int i = 0; i < error.length; i++) {
			APIUtils.errors[i] = error[i];
		}
	}

	public static EventDefinition toEventDefinition(String name,
			int alarmLevel, EventType eventType) {
		EventDefinition event = new EventDefinition();
		event.setMessage(name);
		switch (alarmLevel) {
		case 0:
			event.setAlarmLevel(AlarmLevel.NONE);
			break;
		case 1:
			event.setAlarmLevel(AlarmLevel.INFORMATION);
			break;
		case 2:
			event.setAlarmLevel(AlarmLevel.URGENT);
			break;
		case 3:
			event.setAlarmLevel(AlarmLevel.CRITICAL);
			break;
		case 4:
			event.setAlarmLevel(AlarmLevel.LIFE_SAFETY);
			break;
		}
		event.setEventType(eventType);
		return event;
	}

	public static EventNotification toEventNotification(
			EventInstance eventInstance) {
		EventNotification event = new EventNotification();
		Calendar timestamp = Calendar.getInstance();
		timestamp.setTimeInMillis(eventInstance.getActiveTimestamp());
		Calendar ackTime = Calendar.getInstance();
		ackTime.setTimeInMillis(eventInstance.getAcknowledgedTimestamp());

		Calendar rtnTs = Calendar.getInstance();

		if (eventInstance.isRtnApplicable()) {
			rtnTs.setTimeInMillis(eventInstance.getRtnTimestamp());
		} else {
			rtnTs = null;
		}

		event.setId(eventInstance.getId());
		event.setTimestamp(timestamp);
		event.setRtnTime(rtnTs);
		event.setAlarmLevel(APIUtils
				.toAlarmLevel(eventInstance.getAlarmLevel()));
		event.setAckTime(ackTime);
		event.setMessage(APIUtils.toEventMessage(eventInstance
				.getEventComments()));

		if (eventInstance.getEventType().getDataPointId() != -1) {
			event.setEventType(EventType.POINT_CONDITION_EVENT);
			int dataPointId = eventInstance.getEventType().getDataPointId();
			int pointDetectorId = eventInstance.getEventType()
					.getReferenceId2();

			List<PointEventDetectorVO> peDetectors = new DataPointDao()
					.getDataPoint(dataPointId).getEventDetectors();

			for (PointEventDetectorVO pointEventDetectorVO : peDetectors) {
				if (pointEventDetectorVO.getId() == pointDetectorId) {

					event.setAlias(pointEventDetectorVO.getAlias());
				}
			}
		} else if (eventInstance.getEventType().getDataSourceId() != -1) {
			event.setEventType(EventType.SYSTEM_EVENT);
			event.setAlias("Data Source Event: "
					+ eventInstance.getEventType().getDataSourceId());

		} else if (eventInstance.getEventType().getCompoundEventDetectorId() != -1) {
			event.setEventType(EventType.POINT_CONDITION_EVENT);
			int compoundId = eventInstance.getEventType()
					.getCompoundEventDetectorId();
			event.setAlias(new CompoundEventDetectorDao()
					.getCompoundEventDetector(compoundId).getName());

		} else if (eventInstance.getEventType().getScheduleId() != -1) {
			event.setEventType(EventType.SCHEDULED_EVENT);
			int scheduleId = eventInstance.getEventType().getScheduleId();
			event.setAlias(new ScheduledEventDao()
					.getScheduledEvent(scheduleId).getAlias());
		} else {
			event.setEventType(EventType.SYSTEM_EVENT);
			event.setAlias(eventInstance.getMessage().getKey());
		}

		return event;
	}

	public static EventMessage[] toEventMessage(List<UserComment> list) {
		if (list != null) {
			EventMessage[] messages = new EventMessage[list.size()];

			for (int i = 0; i < list.size(); i++) {
				messages[i] = new EventMessage();
				messages[i].setMessage(list.get(i).getComment());
				Calendar ts = Calendar.getInstance();
				ts.setTimeInMillis(list.get(i).getTs());
				messages[i].setTimestamp(ts);
				messages[i].setUser(list.get(i).getUsername());
			}

			return messages;
		}
		return null;
	}

	public static DataType toDataType(int mangoDataType) {
		if (mangoDataType == DataTypes.ALPHANUMERIC)
			return DataType.STRING;
		else if (mangoDataType == DataTypes.BINARY)
			return DataType.BOOLEAN;
		else if (mangoDataType == DataTypes.NUMERIC)
			return DataType.DOUBLE;
		else if (mangoDataType == DataTypes.MULTISTATE)
			return DataType.INTEGER;
		return null;
	}

	public static UserComment toUserComment(EventMessage message) {
		UserComment comment = new UserComment();
		comment.setUserId(1);
		comment.setUsername(message.getUser());
		comment.setComment(message.getMessage());
		comment.setTs(Calendar.getInstance().getTimeInMillis());
		return comment;
	}

	public static boolean isHigherLevel(AlarmLevel level, AlarmLevel reference) {
		if (level == AlarmLevel.LIFE_SAFETY) {
			return true;
		}
		if ((level == AlarmLevel.CRITICAL)
				&& (reference != AlarmLevel.LIFE_SAFETY)) {
			return true;
		}
		if ((level == AlarmLevel.URGENT)
				&& (reference != AlarmLevel.LIFE_SAFETY)
				&& (reference != AlarmLevel.CRITICAL)) {
			return true;
		}
		if ((level == AlarmLevel.INFORMATION)
				&& (reference != AlarmLevel.LIFE_SAFETY)
				&& (reference != AlarmLevel.CRITICAL)
				&& (reference != AlarmLevel.URGENT)) {
			return true;
		}
		if ((level == AlarmLevel.NONE) && (reference == AlarmLevel.NONE)) {
			return true;
		}

		return false;
	}

	public static boolean isInPath(String item, String path) {
		if (item == null)
			item = "";
		if (path == null)
			path = "";

		String[] itens = item.split("\\.");
		String[] paths = path.split("\\.");

		if (path.trim().equals("")) {
			return true;
		}
		if (itens.length < paths.length) {
			return false;
		}

		for (int i = 0; i < paths.length; i++) {
			if (!itens[i].equals(paths[i])) {
				return false;
			}
		}

		return true;
	}

	public static void setOnLineDate() {

		Calendar cl2 = Calendar.getInstance();
		APIUtils.date = cl2;
	}

	public static Calendar getOnlineDate() {
		return APIUtils.date;
	}

	public static final String getVersion() {
		return "1.11.0";
	}

	public static AlarmLevel toAlarmLevel(int i) {
		switch (i) {
		case 0:
			return AlarmLevel.NONE;
		case 1:
			return AlarmLevel.INFORMATION;
		case 2:
			return AlarmLevel.URGENT;
		case 3:
			return AlarmLevel.CRITICAL;
		case 4:
			return AlarmLevel.LIFE_SAFETY;
		default:
			return AlarmLevel.NONE;
		}
	}

}
