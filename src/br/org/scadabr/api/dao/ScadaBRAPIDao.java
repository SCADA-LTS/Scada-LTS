package br.org.scadabr.api.dao;

import java.util.Calendar;
import java.util.List;

import br.org.scadabr.api.constants.AlarmLevel;
import br.org.scadabr.api.constants.DataSourceType;
import br.org.scadabr.api.constants.EventType;
import br.org.scadabr.api.exception.ScadaBRAPIException;
import br.org.scadabr.api.vo.APIError;
import br.org.scadabr.api.vo.EventDefinition;
import br.org.scadabr.api.vo.EventMessage;
import br.org.scadabr.api.vo.EventNotification;
import br.org.scadabr.api.vo.ItemInfo;
import br.org.scadabr.api.vo.ItemStringValue;
import br.org.scadabr.api.vo.ItemValue;

public interface ScadaBRAPIDao {

	public List<ItemValue> getItemValueList(String[] itemList,
			List<APIError> errors) throws ScadaBRAPIException;

	public List<ItemValue> getDataHistory(String itemQName,
			Calendar initialDate, Calendar finalDate, int maxReturn)
			throws ScadaBRAPIException;

	public ItemValue writeData(ItemValue itemValue) throws ScadaBRAPIException;

	public List<ItemInfo> browseTags(String itemPath)
			throws ScadaBRAPIException;

	public List<EventNotification> getEventNotifications(
			AlarmLevel minimumAlarmLevel) throws ScadaBRAPIException;

	public EventNotification ackEvent(int eventId) throws ScadaBRAPIException;

	public List<EventNotification> getEventsHistory(AlarmLevel alarmLevel,
			Calendar initialDate, Calendar finalDate, int maxReturn)
			throws ScadaBRAPIException;

	public EventMessage[] annotateEvent(int eventId, EventMessage message)
			throws ScadaBRAPIException;

	public List<EventDefinition> getEventDefinitions(EventType eventType)
			throws ScadaBRAPIException;

	public List<Object> getDataSources(DataSourceType dataSourceType)
			throws ScadaBRAPIException;

	public int configureDataSource(DataSourceType dataSourceType,
			Object dataSource) throws ScadaBRAPIException;

	public int configureDataPoint(int dataSourceId,
			DataSourceType dataSourceType, Object dataPoint)
			throws ScadaBRAPIException;

	public void removeDataSource(int id) throws ScadaBRAPIException;

	public List<Object> getDataPoints(int dataSourceid)
			throws ScadaBRAPIException;

	public void removeDataPoint(int id) throws ScadaBRAPIException;

	public ItemStringValue writeStringData(ItemStringValue itemValue)
			throws ScadaBRAPIException;

}
