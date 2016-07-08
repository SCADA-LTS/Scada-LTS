package br.org.scadabr.api.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.org.scadabr.api.constants.AlarmLevel;
import br.org.scadabr.api.constants.DataSourceType;
import br.org.scadabr.api.constants.ErrorCode;
import br.org.scadabr.api.constants.EventType;
import br.org.scadabr.api.constants.ModbusDataType;
import br.org.scadabr.api.constants.ModbusRegisterRange;
import br.org.scadabr.api.constants.QualityCode;
import br.org.scadabr.api.exception.ScadaBRAPIException;
import br.org.scadabr.api.utils.APIConstants;
import br.org.scadabr.api.utils.APIUtils;
import br.org.scadabr.api.vo.APIError;
import br.org.scadabr.api.vo.EventDefinition;
import br.org.scadabr.api.vo.EventMessage;
import br.org.scadabr.api.vo.EventNotification;
import br.org.scadabr.api.vo.ItemInfo;
import br.org.scadabr.api.vo.ItemStringValue;
import br.org.scadabr.api.vo.ItemValue;
import br.org.scadabr.api.vo.ModbusIPConfig;
import br.org.scadabr.api.vo.ModbusPointConfig;
import br.org.scadabr.api.vo.ModbusSerialConfig;

import com.serotonin.mango.Common;
import com.serotonin.mango.Common.TimePeriods;
import com.serotonin.mango.db.dao.CompoundEventDetectorDao;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.DataSourceDao;
import com.serotonin.mango.db.dao.EventDao;
import com.serotonin.mango.db.dao.PointValueDao;
import com.serotonin.mango.db.dao.ScheduledEventDao;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.DataSourceVO.Type;
import com.serotonin.mango.vo.dataSource.modbus.ModbusIpDataSourceVO;
import com.serotonin.mango.vo.dataSource.modbus.ModbusIpDataSourceVO.TransportType;
import com.serotonin.mango.vo.dataSource.modbus.ModbusPointLocatorVO;
import com.serotonin.mango.vo.dataSource.modbus.ModbusSerialDataSourceVO;
import com.serotonin.mango.vo.dataSource.modbus.ModbusSerialDataSourceVO.EncodingType;
import com.serotonin.mango.vo.event.CompoundEventDetectorVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.mango.vo.event.ScheduledEventVO;
import com.serotonin.mango.vo.hierarchy.PointHierarchy;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;

public class MangoDaoImpl implements ScadaBRAPIDao {
	private User user;

	public MangoDaoImpl(String username) {
		User us = new UserDao().getUser(username);
		this.user = us;
	}

	private void checkUser() throws ScadaBRAPIException {
		if (this.user == null)
			throw new ScadaBRAPIException(new APIError(ErrorCode.ACCESS_DENIED,
					"Invalid User!"));
	}

	private PointHierarchy pH;

	@Override
	public List<ItemValue> getItemValueList(String[] itemList,
			List<APIError> errors) throws ScadaBRAPIException {
		List<ItemValue> itemsList = new ArrayList<ItemValue>();
		List<DataPointVO> ldpvo = null;

		pH = new DataPointDao().getPointHierarchy();
		for (String itemQName : itemList) {
			ArrayList<ItemValue> itemValueList = new ArrayList<ItemValue>();
			try {
				checkUser();
				if (itemQName == null)
					itemQName = "";
				int dataPointId = Common.ctx.getDataPointByName(itemQName);
				DataPointVO dp = new DataPointDao().getDataPoint(dataPointId);
				if (dp != null) {
					if (isValidDataPoint(dp)) {
						ItemValue iv = toItemValue(dp);
						if (iv != null)
							itemValueList.add(iv);
					}
				} else {
					if (ldpvo == null)
						ldpvo = new DataPointDao().getDataPoints(null, false);
					for (DataPointVO dataPointVO : ldpvo) {
						String completeName = APIUtils.getCompletePath(
								dataPointVO.getPointFolderId(), pH)
								+ dataPointVO.getName();

						if (APIUtils.isInPath(completeName, itemQName)
								|| itemQName.equals(dataPointVO.getName())) {

							if (isValidDataPoint(dataPointVO)) {

								ItemValue iv = toItemValue(dataPointVO);
								if (iv != null)
									itemValueList.add(iv);

							}
						}
					}
				}

				if (itemValueList.size() == 0) {
					APIError error = new APIError();
					error.setCode(ErrorCode.INVALID_PARAMETER);
					error.setDescription(APIConstants.UNKNOW_TAG_NAME);
					errors.add(error);
				} else {
					itemsList.addAll(itemValueList);
					errors.add(APIConstants.ERROR_OK);
				}
			} catch (ScadaBRAPIException e) {
				errors.add(e.getError());
				return null;
			}
		}
		return itemsList;
	}

	private boolean isValidDataPoint(DataPointVO dataPointVO) {
		return (!((dataPointVO.isNew()) || (!dataPointVO.isEnabled() && !dataPointVO
				.isNew())) && Permissions.hasDataPointReadPermission(user,
				dataPointVO));
	}

	private ItemValue toItemValue(DataPointVO dataPointVO) {
		RuntimeManager rtm = Common.ctx.getRuntimeManager();
		PointValueTime pvt = rtm.getDataPoint(dataPointVO.getId())
				.getPointValue();

		if (pvt != null) {
			ItemValue iv = new ItemValue();
			iv.setValue(pvt.getValue().getObjectValue());
			String completeItemName = APIUtils.getCompletePath(
					dataPointVO.getPointFolderId(), pH)
					+ dataPointVO.getName();
			iv.setItemName(completeItemName);

			Calendar ts = Calendar.getInstance();
			ts.setTimeInMillis(pvt.getTime());
			iv.setTimestamp(ts);

			iv.setDataType(APIUtils.whatType(pvt.getValue().getObjectValue()));

			iv.setQuality(QualityCode.GOOD);
			return iv;
		}
		return null;

	}

	// @Override
	// public List<ItemValue> getItemValueList(String itemQName)
	// throws ScadaBRAPIException {
	// checkUser();
	// ArrayList<ItemValue> itemValueList = new ArrayList<ItemValue>();
	//
	// if (itemQName == null)
	// itemQName = "";
	//
	// List<DataPointVO> ldpvo = new DataPointDao().getDataPoints(null);
	// for (DataPointVO dataPointVO : ldpvo) {
	// String completeName = APIUtils.getCompletePath(dataPointVO
	// .getPointFolderId())
	// + dataPointVO.getName();
	//
	// if (APIUtils.isInPath(completeName, itemQName)
	// || itemQName.equals(dataPointVO.getName())) {
	// if (!((dataPointVO.isNew()) || (!dataPointVO.isEnabled() && !dataPointVO
	// .isNew()))) {
	//
	// if (Permissions.hasDataPointReadPermission(user,
	// dataPointVO)) {
	//
	// PointValueTime pvt = new PointValueDao()
	// .getLatestPointValue(dataPointVO.getId());
	// if (pvt != null) {
	// ItemValue iv = new ItemValue();
	// iv.setValue(pvt.getValue().getObjectValue());
	// String completeItemName = APIUtils
	// .getCompletePath(dataPointVO
	// .getPointFolderId())
	// + dataPointVO.getName();
	// iv.setItemName(completeItemName);
	//
	// Calendar ts = Calendar.getInstance();
	// ts.setTimeInMillis(pvt.getTime());
	// iv.setTimestamp(ts);
	//
	// iv.setDataType(APIUtils.whatType(pvt.getValue()
	// .getObjectValue()));
	//
	// iv.setQuality(QualityCode.GOOD);
	// itemValueList.add(iv);
	// }
	// }
	//
	// }
	// }
	// }
	//
	// if (itemValueList.size() == 0) {
	// APIError error = new APIError();
	// error.setCode(ErrorCode.INVALID_PARAMETER);
	// error.setDescription(APIConstants.UNKNOW_TAG_NAME);
	// throw new ScadaBRAPIException(error);
	// }
	// return itemValueList;
	//
	// }

	@Override
	public ItemValue writeData(ItemValue itemValue) throws ScadaBRAPIException {
		checkUser();

		ItemValue newItemValue = new ItemValue();
		boolean flag = false;

		if (itemValue.getItemName() == null || itemValue.getValue() == null) {
			APIError error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER);
			throw new ScadaBRAPIException(error);
		}
		PointHierarchy pH = new DataPointDao().getPointHierarchy();

		int dataPointId = Common.ctx
				.getDataPointByName(itemValue.getItemName());
		DataPointVO dp = new DataPointDao().getDataPoint(dataPointId);

		if (dp != null) {
			checkValidWriteCommand(dp, itemValue);
			checkValidWritableDataPoint(dp);
			MangoValue value = MangoValue.stringToValue(itemValue.getValue()
					.toString(), dp.getPointLocator().getDataTypeId());
			Common.ctx.getRuntimeManager().setDataPointValue(dp.getId(), value,
					null);
			flag = true;
			newItemValue.setValue(itemValue.getValue());
			newItemValue.setItemName(dp.getName());

		} else {
			List<DataPointVO> listDPVO = new DataPointDao().getDataPoints(null,
					false);
			for (DataPointVO dataPointVO : listDPVO) {
				String completeName = APIUtils.getCompletePath(
						dataPointVO.getPointFolderId(), pH)
						+ dataPointVO.getName();
				if (itemValue.getItemName().equals(completeName)) {

					checkValidWriteCommand(dataPointVO, itemValue);
					checkValidWritableDataPoint(dataPointVO);

					MangoValue value = MangoValue.stringToValue(itemValue
							.getValue().toString(), dataPointVO
							.getPointLocator().getDataTypeId());
					Common.ctx.getRuntimeManager().setDataPointValue(
							dataPointVO.getId(), value, null);

					flag = true;
					newItemValue.setValue(itemValue.getValue());
					newItemValue.setItemName(dataPointVO.getName());
				}
			}
		}

		if (!flag) {
			APIError error = new APIError();
			error.setCode(ErrorCode.INVALID_PARAMETER);
			error.setDescription(APIConstants.UNSPECIFIED_ERROR
					+ " (Tag Inexistent)");
			throw new ScadaBRAPIException(error);
		}

		return newItemValue;
	}

	private void checkValidWriteCommand(DataPointVO dataPointVO,
			ItemValue itemValue) throws ScadaBRAPIException {
		PointValueTime pvt = new PointValueDao()
				.getLatestPointValue(dataPointVO.getId());

		if (!APIUtils.checkCompatibleDataTypes(itemValue.getDataType(), pvt
				.getValue().getObjectValue())) {

			APIError error = new APIError();
			error.setCode(ErrorCode.INVALID_PARAMETER);
			error.setDescription(APIConstants.INVALID_PARAMETER
					+ " (itemValue.dataType)");
			throw new ScadaBRAPIException(error);
		}
	}

	private void checkValidStringWriteCommand(DataPointVO dataPointVO,
			ItemStringValue itemValue) throws ScadaBRAPIException {

		if (!APIUtils.checkStringValue(itemValue.getValue(),
				itemValue.getDataType())) {
			APIError error = new APIError();
			error.setCode(ErrorCode.INVALID_PARAMETER);
			error.setDescription(APIConstants.INVALID_PARAMETER
					+ " (itemValue.dataType)");
			throw new ScadaBRAPIException(error);
		}
	}

	private void checkValidWritableDataPoint(DataPointVO dataPointVO)
			throws ScadaBRAPIException {

		if (!dataPointVO.getPointLocator().isSettable()) {
			APIError error = new APIError();
			error.setCode(ErrorCode.READ_ONLY);
			error.setDescription(APIConstants.UNSPECIFIED_ERROR
					+ " (DataPoint not configurable)");
			throw new ScadaBRAPIException(error);
		}

		if (!dataPointVO.isEnabled()
				|| !new DataSourceDao().getDataSource(
						dataPointVO.getDataSourceId()).isEnabled()) {
			APIError error = new APIError();
			error.setCode(ErrorCode.INVALID_PARAMETER);
			error.setDescription(APIConstants.UNSPECIFIED_ERROR
					+ " (Disabled DataSource/DataPoint)");
			throw new ScadaBRAPIException(error);
		}

		if (!Permissions.hasDataPointSetPermission(user, dataPointVO)) {
			APIError error = new APIError();
			error.setCode(ErrorCode.ACCESS_DENIED);
			error.setDescription(APIConstants.ACCESS_DENIED);
			throw new ScadaBRAPIException(error);
		}
	}

	@Override
	public ItemStringValue writeStringData(ItemStringValue itemValue)
			throws ScadaBRAPIException {
		checkUser();
		ItemStringValue newItemValue = new ItemStringValue();
		boolean flag = false;

		if (itemValue.getItemName() == null || itemValue.getValue() == null) {
			APIError error = new APIError();
			error.setCode(ErrorCode.INSUFFICIENT_PARAMETERS);
			error.setDescription(APIConstants.INSUFFICIENT_PARAMETER);
			throw new ScadaBRAPIException(error);
		}

		int dataPointId = Common.ctx
				.getDataPointByName(itemValue.getItemName());
		DataPointRT dpRT = Common.ctx.getRuntimeManager().getDataPoint(
				dataPointId);
		DataPointVO dp = null;
		if (dpRT != null)
			dp = dpRT.getVO();
		else {
			APIError error = new APIError();
			error.setCode(ErrorCode.INVALID_PARAMETER);
			error.setDescription(APIConstants.INVALID_PARAMETER
					+ " (Disabled DataSource/DataPoint)");
			throw new ScadaBRAPIException(error);
		}

		if (dp != null) {
			checkValidStringWriteCommand(dp, itemValue);
			checkValidWritableDataPoint(dp);

			MangoValue value = MangoValue.stringToValue(itemValue.getValue(),
					dp.getPointLocator().getDataTypeId());
			Common.ctx.getRuntimeManager().setDataPointValue(dp.getId(), value,
					null);

			flag = true;
			newItemValue.setValue(itemValue.getValue());
			newItemValue.setItemName(dp.getName());

		} else {
			List<DataPointVO> listDPVO = new DataPointDao().getDataPoints(null,
					false);
			PointHierarchy pH = new DataPointDao().getPointHierarchy();
			for (DataPointVO dataPointVO : listDPVO) {
				String completeName = APIUtils.getCompletePath(
						dataPointVO.getPointFolderId(), pH)
						+ dataPointVO.getName();
				if (itemValue.getItemName().equals(completeName)) {

					checkValidStringWriteCommand(dp, itemValue);
					checkValidWritableDataPoint(dp);

					MangoValue value = MangoValue.stringToValue(itemValue
							.getValue(), dataPointVO.getPointLocator()
							.getDataTypeId());
					Common.ctx.getRuntimeManager().setDataPointValue(
							dataPointVO.getId(), value, null);

					flag = true;
					newItemValue.setValue(itemValue.getValue());
					newItemValue.setItemName(dataPointVO.getName());
				}
			}
		}
		if (!flag) {
			APIError error = new APIError();
			error.setCode(ErrorCode.INVALID_PARAMETER);
			error.setDescription(APIConstants.UNSPECIFIED_ERROR
					+ " (Tag Inexistent)");
			throw new ScadaBRAPIException(error);
		}

		return newItemValue;
	}

	@Override
	public List<ItemInfo> browseTags(String itemPath)
			throws ScadaBRAPIException {
		checkUser();
		ArrayList<ItemInfo> itemInfoList = new ArrayList<ItemInfo>();

		List<DataPointVO> ldpvo = new DataPointDao().getDataPoints(null, false);
		PointHierarchy pH = new DataPointDao().getPointHierarchy();

		Map<String, Integer> mapping = new HashMap<String, Integer>();

		for (DataPointVO dataPointVO : ldpvo) {

			String completeName = APIUtils.getCompletePath(
					dataPointVO.getPointFolderId(), pH)
					+ dataPointVO.getName();
			mapping.put(completeName, dataPointVO.getId());

			if (APIUtils.isInPath(completeName, itemPath)
					|| itemPath.equals(dataPointVO.getName())) {
				if (dataPointVO.isEnabled()) {

					if (Permissions.hasDataPointReadPermission(user,
							dataPointVO)) {
						ItemInfo itemInfo = new ItemInfo();
						itemInfo.setItemName(completeName);

						itemInfo.setWritable(false);
						if (dataPointVO.getPointLocator().isSettable()) {
							if (Permissions.hasDataPointSetPermission(user,
									dataPointVO))
								itemInfo.setWritable(true);
						}

						itemInfo.setDataType(APIUtils.toDataType(dataPointVO
								.getPointLocator().getDataTypeId()));
						dataPointVO.getPointLocator().getDataTypeId();
						itemInfoList.add(itemInfo);
					}
				}

			}
		}

		Common.ctx.getServletContext().setAttribute(
				Common.ContextKeys.DATA_POINTS_NAME_ID_MAPPING, mapping);

		if (itemInfoList.size() == 0) {
			APIError error = new APIError();
			error.setCode(ErrorCode.INVALID_PARAMETER);
			error.setDescription(APIConstants.UNSPECIFIED_ERROR
					+ " (no tag found)");
			throw new ScadaBRAPIException(error);
		}
		return itemInfoList;
	}

	@Override
	public List<ItemValue> getDataHistory(String itemQName,
			Calendar initialDate, Calendar finalDate, int maxReturn)
			throws ScadaBRAPIException {
		checkUser();
		List<DataPointVO> listDPVO = new DataPointDao().getDataPoints(null,
				false);
		List<ItemValue> itemHistory = null;
		PointHierarchy pH = new DataPointDao().getPointHierarchy();
		for (DataPointVO dataPointVO : listDPVO) {
			String completeName = APIUtils.getCompletePath(
					dataPointVO.getPointFolderId(), pH)
					+ dataPointVO.getName();

			if (itemQName.equals(completeName)) {

				if (!Permissions.hasDataPointReadPermission(user, dataPointVO)) {
					APIError error = new APIError();
					error.setCode(ErrorCode.ACCESS_DENIED);
					error.setDescription(APIConstants.ACCESS_DENIED);
					throw new ScadaBRAPIException(error);
				}

				List<PointValueTime> lpvt = new PointValueDao()
						.getPointValuesBetween(dataPointVO.getId(),
								initialDate.getTimeInMillis(),
								finalDate.getTimeInMillis());

				if (lpvt.size() == 0) {
					APIError error = new APIError();
					error.setCode(ErrorCode.INVALID_PARAMETER);
					error.setDescription("No data in this period.");
					throw new ScadaBRAPIException(error);
				}

				if (lpvt.size() <= maxReturn) {
					maxReturn = lpvt.size();
				}

				itemHistory = new ArrayList<ItemValue>();
				for (int i = 0; i < maxReturn; i++) {
					PointValueTime pointValueTime = lpvt.get(i);

					ItemValue iv = new ItemValue();
					iv.setItemName(dataPointVO.getName());
					iv.setValue(pointValueTime.getValue().getObjectValue());

					Calendar cl1 = Calendar.getInstance();
					cl1.setTimeInMillis(pointValueTime.getTime());

					iv.setTimestamp(cl1);
					iv.setQuality(QualityCode.GOOD);

					itemHistory.add(iv);

				}
			}
		}

		if (itemHistory == null || itemHistory.size() == 0) {
			APIError error = new APIError();
			error.setCode(ErrorCode.INVALID_PARAMETER);
			error.setDescription("Tag not found");
			throw new ScadaBRAPIException(error);
		}

		return itemHistory;

	}

	@Override
	public List<EventNotification> getEventNotifications(
			AlarmLevel minimumAlarmLevel) throws ScadaBRAPIException {
		List<EventNotification> alarms = new ArrayList<EventNotification>();
		List<EventInstance> events = new EventDao().getPendingEvents(1);
		checkUser();
		if (events == null || events.size() == 0) {
			APIError error = new APIError();
			error.setCode(ErrorCode.OK);
			error.setDescription("No alarms at this moment");
			throw new ScadaBRAPIException(error);
		}

		for (int i = 0; i < events.size(); i++) {
			EventInstance eventInstance = events.get(i);
			if (APIUtils.isHigherLevel(
					APIUtils.toAlarmLevel(eventInstance.getAlarmLevel()),
					minimumAlarmLevel)) {
				alarms.add(APIUtils.toEventNotification(eventInstance));
			}
		}

		return alarms;
	}

	@Override
	public EventNotification ackEvent(int eventId) throws ScadaBRAPIException {
		checkUser();
		EventInstance eventInstance = getEvent(eventId);

		if (eventInstance != null) {
			// alternateAckSource? - REM
			new EventDao().ackEvent(eventId, 1, 0, 0);
			// new EventDao().ackEvent(eventId, 1);
			return APIUtils.toEventNotification(eventInstance);
		} else {
			APIError error = new APIError();
			error.setCode(ErrorCode.INVALID_ID);
			error.setDescription(APIConstants.INVALID_ID);
			throw new ScadaBRAPIException(error);
		}
	}

	private EventInstance getEvent(int eventId) {
		List<EventInstance> events = new EventDao().getPendingEvents(1);
		for (EventInstance eventInstance : events) {
			if (eventInstance.getId() == eventId)
				return eventInstance;
		}
		return null;
	}

	private List<EventInstance> getAcknowledgedEvents() {
		// Adicionados parametros novos (?)
		// return new EventDao().search(0, -1, null, -1, null, 20000, 1, null);
		return new EventDao().search(0, -1, null, -1, null, 1, null, 0, 5000,
				null);
	}

	@Override
	public List<EventNotification> getEventsHistory(AlarmLevel alarmLevel,
			Calendar initialDate, Calendar finalDate, int maxReturn)
			throws ScadaBRAPIException {
		checkUser();
		List<EventNotification> alarms = new ArrayList<EventNotification>();
		List<EventInstance> events = getAcknowledgedEvents();
		if (events.size() <= maxReturn) {
			maxReturn = events.size();
		}
		for (int i = 0; (i < events.size()) && (alarms.size() < maxReturn); i++) {
			EventInstance eventInstance = events.get(i);
			if ((alarmLevel == null)
					|| APIUtils.toAlarmLevel(eventInstance.getAlarmLevel()) == alarmLevel) {
				Calendar ts = Calendar.getInstance();
				ts.setTimeInMillis(eventInstance.getActiveTimestamp());

				if (ts.getTimeInMillis() >= initialDate.getTimeInMillis()
						&& ts.getTimeInMillis() <= finalDate.getTimeInMillis()) {
					alarms.add(APIUtils.toEventNotification(eventInstance));
				}
			}
		}

		if (alarms.size() == 0) {
			APIError error = new APIError();
			error.setCode(ErrorCode.OK);
			error.setDescription("No alarm found in this period");
			throw new ScadaBRAPIException(error);
		}

		return alarms;
	}

	@Override
	public EventMessage[] annotateEvent(int eventId, EventMessage message)
			throws ScadaBRAPIException {
		checkUser();
		EventInstance event = getEvent(eventId);
		if (event != null) {
			new EventDao().insertEventComment(eventId,
					APIUtils.toUserComment(message));

			event = getEvent(eventId);
			return APIUtils.toEventMessage(event.getEventComments());
		} else {
			APIError error = new APIError();
			error.setCode(ErrorCode.INVALID_ID);
			error.setDescription("event inexistent");
			throw new ScadaBRAPIException(error);
		}

	}

	@Override
	public List<EventDefinition> getEventDefinitions(EventType eventType)
			throws ScadaBRAPIException {
		checkUser();
		List<EventDefinition> events = new ArrayList<EventDefinition>();

		if ((eventType == null) || eventType == EventType.POINT_CONDITION_EVENT) {
			List<DataSourceVO<?>> dataSources = new DataSourceDao()
					.getDataSources();
			List<PointEventDetectorVO> pointEventDetectors = new ArrayList<PointEventDetectorVO>();

			for (DataSourceVO<?> dataSourceVO : dataSources) {
				List<DataPointVO> dataPoints = new DataPointDao()
						.getDataPoints(dataSourceVO.getId(), null);
				for (DataPointVO dataPointVO : dataPoints) {
					pointEventDetectors.addAll(dataPointVO.getEventDetectors());
				}
			}

			for (PointEventDetectorVO pointEvent : pointEventDetectors) {
				EventDefinition event = APIUtils.toEventDefinition(
						pointEvent.getAlias(), pointEvent.getAlarmLevel(),
						EventType.POINT_CONDITION_EVENT);
				String description = APIUtils.toCondition(pointEvent);
				event.setConfiguration(description);

				events.add(event);
			}

			List<CompoundEventDetectorVO> compoundEvents = new CompoundEventDetectorDao()
					.getCompoundEventDetectors();

			for (CompoundEventDetectorVO pointEvent : compoundEvents) {
				EventDefinition event = APIUtils.toEventDefinition(
						pointEvent.getName(), pointEvent.getAlarmLevel(),
						EventType.POINT_CONDITION_EVENT);
				String description = "[COMPOUND_DETECTOR - ";
				description += pointEvent.getCondition() + "]";

				event.setConfiguration(description);
				events.add(event);
			}
		}

		if ((eventType == null) || eventType == EventType.SCHEDULED_EVENT) {
			List<ScheduledEventVO> scheduledEvents = new ScheduledEventDao()
					.getScheduledEvents();
			for (ScheduledEventVO pointEvent : scheduledEvents) {
				EventDefinition event = APIUtils.toEventDefinition(
						pointEvent.getAlias(), pointEvent.getAlarmLevel(),
						EventType.SCHEDULED_EVENT);

				String description = APIUtils
						.toScheduledEventConfig(pointEvent);
				event.setConfiguration(description);

				events.add(event);
			}
		}
		if ((eventType == null) || eventType == EventType.SYSTEM_EVENT) {
			List<EventTypeVO> systemEvents = SystemEventType
					.getSystemEventTypes();

			for (EventTypeVO eventTypeVO : systemEvents) {
				EventDefinition event = APIUtils.toEventDefinition(eventTypeVO
						.getDescription().getKey(),
						eventTypeVO.getAlarmLevel(), EventType.SYSTEM_EVENT);

				event.setConfiguration("No configuration");
				events.add(event);
			}
			List<EventTypeVO> auditEvents = AuditEventType.getAuditEventTypes();

			for (EventTypeVO eventTypeVO : auditEvents) {
				EventDefinition event = APIUtils.toEventDefinition(eventTypeVO
						.getDescription().getKey(),
						eventTypeVO.getAlarmLevel(), EventType.SYSTEM_EVENT);

				event.setConfiguration("No configuration");
				events.add(event);
			}
		}
		return events;
	}

	@Override
	public List<Object> getDataSources(DataSourceType dataSourceType)
			throws ScadaBRAPIException {
		checkUser();
		Type dsType = getDataSourceType(dataSourceType);

		List<DataSourceVO<?>> allDataSources = new DataSourceDao()
				.getDataSources();

		List<Object> dataSources = new ArrayList<Object>();

		for (DataSourceVO<?> dataSourceVO : allDataSources) {
			if (dataSourceVO.getType() == dsType) {
				if (Permissions.hasDataSourcePermission(user,
						dataSourceVO.getId())) {
					dataSources.add(getDataSourceConfig(dataSourceVO));
				}
			}
		}

		if (dataSources.size() == 0) {
			APIError error = new APIError();
			error.setCode(ErrorCode.OK);
			error.setDescription("No Data Source Found.");
			throw new ScadaBRAPIException(error);
		}

		return dataSources;
	}

	private Object getDataSourceConfig(DataSourceVO<?> dataSourceVO)
			throws ScadaBRAPIException {
		if (dataSourceVO.getType() == Type.MODBUS_IP) {
			ModbusIpDataSourceVO modbusDS = (ModbusIpDataSourceVO) dataSourceVO;

			ModbusIPConfig dataSource = new ModbusIPConfig();
			dataSource.setId(modbusDS.getId());
			dataSource.setName(modbusDS.getName());
			dataSource.setEnabled(modbusDS.isEnabled());
			dataSource.setHost(modbusDS.getHost());
			dataSource.setPollingPeriod(toMillis(modbusDS.getUpdatePeriods(),
					modbusDS.getUpdatePeriodType()));
			dataSource.setPort(modbusDS.getPort());
			dataSource.setRetries(modbusDS.getRetries());
			dataSource.setContiguousBatches(modbusDS.isContiguousBatches());
			dataSource.setCreateSlaveMonitorPoints(modbusDS
					.isCreateSlaveMonitorPoints());
			dataSource.setTimeout(modbusDS.getTimeout());

			return dataSource;
		} else if (dataSourceVO.getType() == Type.MODBUS_SERIAL) {
			ModbusSerialDataSourceVO modbusDS = (ModbusSerialDataSourceVO) dataSourceVO;
			ModbusSerialConfig dataSource = new ModbusSerialConfig();
			dataSource.setId(modbusDS.getId());
			dataSource.setName(modbusDS.getName());
			dataSource.setEnabled(modbusDS.isEnabled());
			dataSource.setSerialPort(modbusDS.getCommPortId());
			dataSource.setBaudrate(modbusDS.getBaudRate());
			dataSource.setRetries(modbusDS.getRetries());
			dataSource.setContiguousBatches(modbusDS.isContiguousBatches());
			dataSource.setCreateSlaveMonitorPoints(modbusDS
					.isCreateSlaveMonitorPoints());
			dataSource.setTimeout(modbusDS.getTimeout());
			dataSource.setPollingPeriod(toMillis(modbusDS.getUpdatePeriods(),
					modbusDS.getUpdatePeriodType()));

			return dataSource;
		}
		throw new ScadaBRAPIException(new APIError(ErrorCode.INVALID_PARAMETER,
				"DS type not supported yet"));
	}

	private long toMillis(int updatePeriods, int updatePeriodType) {
		long periodInMillis = 0;
		switch (updatePeriodType) {
		case TimePeriods.MILLISECONDS:
			periodInMillis = updatePeriods;
			break;

		case TimePeriods.SECONDS:
			periodInMillis = (updatePeriods * 1000);
			break;

		case TimePeriods.MINUTES:
			periodInMillis = (updatePeriods * 1000 * 60);
			break;

		case TimePeriods.HOURS:
			periodInMillis = (updatePeriods * 60 * 60 * 1000);
			break;
		}

		return periodInMillis;
	}

	private Type getDataSourceType(DataSourceType dsType)
			throws ScadaBRAPIException {
		if (dsType == DataSourceType.MODBUS_IP)
			return Type.MODBUS_IP;
		else if (dsType == DataSourceType.MODBUS_SERIAL)
			return Type.MODBUS_SERIAL;
		throw new ScadaBRAPIException(new APIError(ErrorCode.INVALID_PARAMETER,
				"DS type not supported yet"));
	}

	@Override
	public int configureDataSource(DataSourceType dataSourceType,
			Object dataSource) throws ScadaBRAPIException {
		checkUser();
		if (dataSourceType == DataSourceType.MODBUS_IP) {
			ModbusIPConfig config = (ModbusIPConfig) dataSource;
			ModbusIpDataSourceVO ds;

			if (config.getId() == Common.NEW_ID) {
				ds = new ModbusIpDataSourceVO();
				ds.setXid(new DataSourceDao().generateUniqueXid());
			} else {
				checkValidDataSourceId(config.getId(), dataSourceType);
				ds = (ModbusIpDataSourceVO) new DataSourceDao()
						.getDataSource(config.getId());
			}
			if (!Permissions.hasDataSourcePermission(user, config.getId())) {
				APIError error = new APIError();
				error.setCode(ErrorCode.ACCESS_DENIED);
				error.setDescription(APIConstants.ACCESS_DENIED);
				throw new ScadaBRAPIException(error);
			}

			ds.setId(config.getId());
			ds.setName(config.getName());
			ds.setEnabled(config.isEnabled());
			ds.setHost(config.getHost());
			ds.setPort(config.getPort());
			ds.setUpdatePeriods((int) config.getPollingPeriod());
			ds.setUpdatePeriodType(TimePeriods.MILLISECONDS);
			ds.setContiguousBatches(config.isContiguousBatches());
			ds.setCreateSlaveMonitorPoints(config.isCreateSlaveMonitorPoints());
			ds.setRetries(config.getRetries());
			ds.setTimeout(config.getTimeout());
			ds.setTransportType(TransportType.TCP);

			DwrResponseI18n validate = new DwrResponseI18n();
			ds.validate(validate);
			if (validate.getHasMessages())
				throw new ScadaBRAPIException(new APIError(
						ErrorCode.INVALID_PARAMETER,
						"Check the configuration parameters."));

			new DataSourceDao().saveDataSource(ds);
			return ds.getId();
		} else if (dataSourceType == DataSourceType.MODBUS_SERIAL) {
			ModbusSerialConfig config = (ModbusSerialConfig) dataSource;
			ModbusSerialDataSourceVO ds;

			if (config.getId() == Common.NEW_ID) {
				ds = new ModbusSerialDataSourceVO();
				ds.setXid(new DataSourceDao().generateUniqueXid());

			} else {
				checkValidDataSourceId(config.getId(), dataSourceType);
				ds = (ModbusSerialDataSourceVO) new DataSourceDao()
						.getDataSource(config.getId());
			}

			if (!Permissions.hasDataSourcePermission(user, config.getId())) {
				APIError error = new APIError();
				error.setCode(ErrorCode.ACCESS_DENIED);
				error.setDescription(APIConstants.ACCESS_DENIED);
				throw new ScadaBRAPIException(error);
			}
			ds.setId(config.getId());
			ds.setName(config.getName());
			ds.setEnabled(config.isEnabled());
			ds.setCommPortId(config.getSerialPort());
			ds.setBaudRate(config.getBaudrate());
			ds.setUpdatePeriods((int) config.getPollingPeriod());
			ds.setUpdatePeriodType(TimePeriods.MILLISECONDS);
			ds.setContiguousBatches(config.isContiguousBatches());
			ds.setCreateSlaveMonitorPoints(config.isCreateSlaveMonitorPoints());
			ds.setRetries(config.getRetries());
			ds.setTimeout(config.getTimeout());
			ds.setEncoding(EncodingType.RTU);

			DwrResponseI18n validate = new DwrResponseI18n();
			ds.validate(validate);
			if (validate.getHasMessages())
				throw new ScadaBRAPIException(new APIError(
						ErrorCode.INVALID_PARAMETER,
						"Check the configuration parameters."));

			new DataSourceDao().saveDataSource(ds);
			return ds.getId();
		}
		throw new ScadaBRAPIException(new APIError(ErrorCode.UNSPECIFIED_ERROR,
				"Should Never Happen Exception"));

	}

	private void checkValidDataSourceId(int id, DataSourceType dataSourceType)
			throws ScadaBRAPIException {
		try {
			if (dataSourceType == DataSourceType.MODBUS_IP) {
				ModbusIpDataSourceVO ds = (ModbusIpDataSourceVO) new DataSourceDao()
						.getDataSource(id);
			} else if (dataSourceType == DataSourceType.MODBUS_SERIAL) {
				ModbusSerialDataSourceVO ds = (ModbusSerialDataSourceVO) new DataSourceDao()
						.getDataSource(id);
			}
		} catch (Exception e) {
			throw new ScadaBRAPIException(new APIError(ErrorCode.INVALID_ID,
					"Invalid ID provided."));
		}

	}

	@Override
	public void removeDataSource(int id) throws ScadaBRAPIException {
		DataSourceVO ds = new DataSourceDao().getDataSource(id);
		checkUser();
		if (ds == null)
			throw new ScadaBRAPIException(new APIError(ErrorCode.INVALID_ID,
					"Data Source inexistent"));
		else {
			if (!Permissions.hasDataSourcePermission(user, id)) {
				APIError error = new APIError();
				error.setCode(ErrorCode.ACCESS_DENIED);
				error.setDescription(APIConstants.ACCESS_DENIED);
				throw new ScadaBRAPIException(error);
			}

			Common.ctx.getRuntimeManager().deleteDataSource(id);
		}

	}

	@Override
	public List<Object> getDataPoints(int dataSourceId)
			throws ScadaBRAPIException {
		checkSupportedDataPoints(dataSourceId);
		checkUser();

		if (!Permissions.hasDataSourcePermission(user, dataSourceId)) {
			APIError error = new APIError();
			error.setCode(ErrorCode.ACCESS_DENIED);
			error.setDescription(APIConstants.ACCESS_DENIED);
			throw new ScadaBRAPIException(error);
		}

		List<DataPointVO> mangoPoints = new DataPointDao().getDataPoints(
				dataSourceId, null);
		List<Object> dataPoints = new ArrayList<Object>();

		for (DataPointVO dataPointVO : mangoPoints) {

			ModbusPointConfig point = new ModbusPointConfig();
			point.setId(dataPointVO.getId());
			point.setName(dataPointVO.getName());
			point.setEnabled(dataPointVO.isEnabled());
			ModbusPointLocatorVO pointLocator = dataPointVO.getPointLocator();
			point.setSettable(pointLocator.isSettable());
			point.setAditive((int) pointLocator.getAdditive());
			point.setMultiplier((int) pointLocator.getMultiplier());
			point.setRegisterRange(toRegisterRange(pointLocator.getRange()));
			point.setDataType(toModbusDataType(pointLocator.getModbusDataType()));
			point.setOffset(pointLocator.getOffset());
			point.setSlaveId(pointLocator.getSlaveId());

			dataPoints.add(point);
		}

		if (dataPoints.size() == 0) {
			APIError error = new APIError();
			error.setCode(ErrorCode.OK);
			error.setDescription("No Data Point Found.");
			throw new ScadaBRAPIException(error);
		}

		return dataPoints;
	}

	private int toMangoModbusDataType(ModbusDataType dataType) {
		if (dataType == ModbusDataType.BINARY)
			return 1;

		if (dataType == ModbusDataType.TWO_BYTE_UNSIGNED_INT)
			return 2;

		if (dataType == ModbusDataType.TWO_BYTE_SIGNED_INT)
			return 3;

		if (dataType == ModbusDataType.FOUR_BYTE_UNSIGNED_INT)
			return 4;

		if (dataType == ModbusDataType.FOUR_BYTE_SIGNED_INT)
			return 5;

		if (dataType == ModbusDataType.FOUR_BYTE_UNSIGNED_INT_SWAPPED)
			return 6;

		if (dataType == ModbusDataType.FOUR_BYTE_SIGNED_INT_SWAPPED)
			return 7;

		if (dataType == ModbusDataType.FOUR_BYTE_FLOAT)
			return 8;

		if (dataType == ModbusDataType.FOUR_BYTE_FLOAT_SWAPPED)
			return 9;

		if (dataType == ModbusDataType.FOUR_BYTE_FLOAT_SWAPPED_INVERTED)
			return 20;

		if (dataType == ModbusDataType.EIGHT_BYTE_UNSIGNED_INT)
			return 10;

		if (dataType == ModbusDataType.EIGHT_BYTE_SIGNED_INT)
			return 11;

		if (dataType == ModbusDataType.EIGHT_BYTE_UNSIGNED_INT_SWAPPED)
			return 12;

		if (dataType == ModbusDataType.EIGHT_BYTE_SIGNED_INT_SWAPPED)
			return 13;

		if (dataType == ModbusDataType.EIGHT_BYTE_FLOAT)
			return 14;

		if (dataType == ModbusDataType.EIGHT_BYTE_FLOAT_SWAPPED)
			return 15;

		if (dataType == ModbusDataType.TWO_BYTE_BCD)
			return 16;

		if (dataType == ModbusDataType.FOUR_BYTE_BCD)
			return 17;
		return 0;
	}

	private ModbusDataType toModbusDataType(int dataType) {

		switch (dataType) {
		case 1:
			return ModbusDataType.BINARY;
		case 2:
			return ModbusDataType.TWO_BYTE_UNSIGNED_INT;
		case 3:
			return ModbusDataType.TWO_BYTE_SIGNED_INT;
		case 4:
			return ModbusDataType.FOUR_BYTE_UNSIGNED_INT;
		case 5:
			return ModbusDataType.FOUR_BYTE_SIGNED_INT;
		case 6:
			return ModbusDataType.FOUR_BYTE_UNSIGNED_INT_SWAPPED;
		case 7:
			return ModbusDataType.FOUR_BYTE_SIGNED_INT_SWAPPED;
		case 8:
			return ModbusDataType.FOUR_BYTE_FLOAT;
		case 9:
			return ModbusDataType.FOUR_BYTE_FLOAT_SWAPPED;
		case 20:
			return ModbusDataType.FOUR_BYTE_FLOAT_SWAPPED_INVERTED;
		case 10:
			return ModbusDataType.EIGHT_BYTE_UNSIGNED_INT;
		case 11:
			return ModbusDataType.EIGHT_BYTE_SIGNED_INT;
		case 12:
			return ModbusDataType.EIGHT_BYTE_UNSIGNED_INT_SWAPPED;
		case 13:
			return ModbusDataType.EIGHT_BYTE_SIGNED_INT_SWAPPED;
		case 14:
			return ModbusDataType.EIGHT_BYTE_FLOAT;
		case 15:
			return ModbusDataType.EIGHT_BYTE_FLOAT_SWAPPED;
		case 16:
			return ModbusDataType.TWO_BYTE_BCD;
		case 17:
			return ModbusDataType.FOUR_BYTE_BCD;
		}

		switch (dataType) {
		case 1:
			return ModbusDataType.BINARY;
		case 2:
			return ModbusDataType.TWO_BYTE_UNSIGNED_INT;
		case 3:
			return ModbusDataType.TWO_BYTE_SIGNED_INT;
		case 4:
			return ModbusDataType.FOUR_BYTE_UNSIGNED_INT;
		case 5:
			return ModbusDataType.FOUR_BYTE_SIGNED_INT;
		case 6:
			return ModbusDataType.FOUR_BYTE_UNSIGNED_INT_SWAPPED;
		case 7:
			return ModbusDataType.FOUR_BYTE_SIGNED_INT_SWAPPED;
		case 8:
			return ModbusDataType.FOUR_BYTE_FLOAT;
		case 9:
			return ModbusDataType.FOUR_BYTE_FLOAT_SWAPPED;
		case 20:
			return ModbusDataType.FOUR_BYTE_FLOAT_SWAPPED_INVERTED;
		case 10:
			return ModbusDataType.EIGHT_BYTE_UNSIGNED_INT;
		case 11:
			return ModbusDataType.EIGHT_BYTE_SIGNED_INT;
		case 12:
			return ModbusDataType.EIGHT_BYTE_UNSIGNED_INT_SWAPPED;
		case 13:
			return ModbusDataType.EIGHT_BYTE_SIGNED_INT_SWAPPED;
		case 14:
			return ModbusDataType.EIGHT_BYTE_FLOAT;
		case 15:
			return ModbusDataType.EIGHT_BYTE_FLOAT_SWAPPED;
		case 16:
			return ModbusDataType.TWO_BYTE_BCD;
		case 17:
			return ModbusDataType.FOUR_BYTE_BCD;
		}
		return null;
	}

	private int toMangoRegisterRange(ModbusRegisterRange range) {
		if (range == ModbusRegisterRange.COIL_STATUS)
			return 0x01;
		if (range == ModbusRegisterRange.INPUT_STATUS)
			return 0x02;
		if (range == ModbusRegisterRange.HOLDING_REGISTER)
			return 0x03;
		if (range == ModbusRegisterRange.INPUT_REGISTER)
			return 0x04;
		return 0;
	}

	private ModbusRegisterRange toRegisterRange(int range) {
		if (range == 0x01)
			return ModbusRegisterRange.COIL_STATUS;
		if (range == 0x02)
			return ModbusRegisterRange.INPUT_STATUS;
		if (range == 0x03)
			return ModbusRegisterRange.HOLDING_REGISTER;
		if (range == 0x04)
			return ModbusRegisterRange.INPUT_REGISTER;
		return null;
	}

	private void checkSupportedDataPoints(int dataSourceId)
			throws ScadaBRAPIException {
		DataSourceVO ds = new DataSourceDao().getDataSource(dataSourceId);
		if (ds == null)
			throw new ScadaBRAPIException(new APIError(ErrorCode.INVALID_ID,
					"Data Source inexistent"));
		else if (ds.getType() != Type.MODBUS_IP
				&& ds.getType() != Type.MODBUS_SERIAL)
			throw new ScadaBRAPIException(new APIError(ErrorCode.INVALID_ID,
					"Data Points not supported!"));
	}

	@Override
	public int configureDataPoint(int dataSourceId,
			DataSourceType dataSourceType, Object dataPoint)
			throws ScadaBRAPIException {
		checkSupportedDataPoints(dataSourceId);
		checkUser();
		if (!Permissions.hasDataSourcePermission(user, dataSourceId)) {
			APIError error = new APIError();
			error.setCode(ErrorCode.ACCESS_DENIED);
			error.setDescription(APIConstants.ACCESS_DENIED);
			throw new ScadaBRAPIException(error);
		}

		ModbusPointConfig point = (ModbusPointConfig) dataPoint;
		DataPointVO mangoPoint = null;
		ModbusPointLocatorVO mangoLocator = null;
		if (point.getId() == Common.NEW_ID) {
			mangoPoint = new DataPointVO();
			mangoPoint.setXid(new DataPointDao().generateUniqueXid());
			mangoLocator = new ModbusPointLocatorVO();
			mangoPoint
					.setEventDetectors(new ArrayList<PointEventDetectorVO>(0));

		} else {
			checkValidDataPointId(point.getId());
			mangoPoint = new DataPointDao().getDataPoint(point.getId());
			mangoLocator = mangoPoint.getPointLocator();
		}

		mangoPoint.setName(point.getName());
		mangoPoint.setDataSourceId(dataSourceId);
		mangoPoint.setEnabled(point.isEnabled());
		mangoPoint.setSettable(point.isSettable());
		mangoLocator.setOffset(point.getOffset());
		mangoLocator.setAdditive(point.getAditive());
		mangoLocator.setMultiplier(point.getMultiplier());
		mangoLocator.setSlaveId(point.getSlaveId());
		mangoLocator.setSettableOverride(point.isSettable());
		mangoLocator.setModbusDataType(toMangoModbusDataType(point
				.getDataType()));
		mangoLocator.setRange(toMangoRegisterRange(point.getRegisterRange()));

		mangoPoint.setPointLocator(mangoLocator);
		DwrResponseI18n validate = new DwrResponseI18n();
		validate(mangoPoint, validate);
		mangoLocator.validate(validate);
		if (validate.getHasMessages()) {
			throw new ScadaBRAPIException(new APIError(
					ErrorCode.INVALID_PARAMETER,
					"Check the configuration parameters."));
		}
		Common.ctx.getRuntimeManager().saveDataPoint(mangoPoint);
		return mangoPoint.getId();
	}

	private void validate(DataPointVO dataPoint, DwrResponseI18n response) {
		if (StringUtils.isEmpty(dataPoint.getXid()))
			response.addContextualMessage("xid", "validate.required");
		else if (!new DataPointDao().isXidUnique(dataPoint.getXid(),
				dataPoint.getId()))
			response.addContextualMessage("xid", "validate.xidUsed");
		if (StringUtils.isEmpty(dataPoint.getName()))
			response.addContextualMessage("name", "validate.required");
	}

	private void checkValidDataPointId(int id) throws ScadaBRAPIException {
		DataPointVO mangoVO = new DataPointDao().getDataPoint(id);
		if (mangoVO == null)
			throw new ScadaBRAPIException(new APIError(ErrorCode.INVALID_ID,
					"Data Point inexistent!"));

	}

	@Override
	public void removeDataPoint(int id) throws ScadaBRAPIException {
		checkValidDataPointId(id);
		checkUser();
		DataPointVO dp = new DataPointDao().getDataPoint(id);
		if (!Permissions.hasDataSourcePermission(user, dp.getDataSourceId())) {
			APIError error = new APIError();
			error.setCode(ErrorCode.ACCESS_DENIED);
			error.setDescription(APIConstants.ACCESS_DENIED);
			throw new ScadaBRAPIException(error);
		}

		Common.ctx.getRuntimeManager().deleteDataPoint(dp);

	}

}
