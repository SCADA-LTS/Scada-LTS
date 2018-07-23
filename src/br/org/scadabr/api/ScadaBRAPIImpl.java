/**
 * ScadaBRAPIImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package br.org.scadabr.api;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.org.scadabr.api.ae.AckEventsOptions;
import br.org.scadabr.api.ae.AckEventsParams;
import br.org.scadabr.api.ae.AckEventsResponse;
import br.org.scadabr.api.ae.ActiveEventsOptions;
import br.org.scadabr.api.ae.AnnotateEventParams;
import br.org.scadabr.api.ae.AnnotateEventResponse;
import br.org.scadabr.api.ae.BrowseEventsOptions;
import br.org.scadabr.api.ae.BrowseEventsParams;
import br.org.scadabr.api.ae.BrowseEventsResponse;
import br.org.scadabr.api.ae.EventsHistoryOptions;
import br.org.scadabr.api.ae.GetActiveEventsParams;
import br.org.scadabr.api.ae.GetActiveEventsResponse;
import br.org.scadabr.api.ae.GetEventsHistoryParams;
import br.org.scadabr.api.ae.GetEventsHistoryResponse;
import br.org.scadabr.api.config.BrowseDataPointsResponse;
import br.org.scadabr.api.config.BrowseDataSourcesResponse;
import br.org.scadabr.api.config.BrowseFlexProjectsResponse;
import br.org.scadabr.api.config.ConfigureDataPointResponse;
import br.org.scadabr.api.config.ConfigureDataSourceResponse;
import br.org.scadabr.api.config.GetFlexBuilderConfigResponse;
import br.org.scadabr.api.config.RemoveDataPointResponse;
import br.org.scadabr.api.config.RemoveDataSourceResponse;
import br.org.scadabr.api.config.RemoveFlexProjectResponse;
import br.org.scadabr.api.config.SetFlexBuilderConfigParams;
import br.org.scadabr.api.config.SetFlexBuilderConfigResponse;
import br.org.scadabr.api.constants.AlarmLevel;
import br.org.scadabr.api.constants.ErrorCode;
import br.org.scadabr.api.constants.EventType;
import br.org.scadabr.api.constants.ServerStateCode;
import br.org.scadabr.api.da.BrowseTagsOptions;
import br.org.scadabr.api.da.BrowseTagsParams;
import br.org.scadabr.api.da.BrowseTagsResponse;
import br.org.scadabr.api.da.GetStatusResponse;
import br.org.scadabr.api.da.ReadDataParams;
import br.org.scadabr.api.da.ReadDataResponse;
import br.org.scadabr.api.da.WriteDataOptions;
import br.org.scadabr.api.da.WriteDataParams;
import br.org.scadabr.api.da.WriteDataResponse;
import br.org.scadabr.api.da.WriteStringDataParams;
import br.org.scadabr.api.da.WriteStringDataResponse;
import br.org.scadabr.api.dao.MangoDaoImpl;
import br.org.scadabr.api.dao.ScadaBRAPIDao;
import br.org.scadabr.api.exception.ScadaBRAPIException;
import br.org.scadabr.api.hda.GetDataHistoryOptions;
import br.org.scadabr.api.hda.GetDataHistoryParams;
import br.org.scadabr.api.hda.GetDataHistoryResponse;
import br.org.scadabr.api.utils.APIConstants;
import br.org.scadabr.api.utils.APIUtils;
import br.org.scadabr.api.vo.APIError;
import br.org.scadabr.api.vo.EventDefinition;
import br.org.scadabr.api.vo.EventMessage;
import br.org.scadabr.api.vo.EventNotification;
import br.org.scadabr.api.vo.FlexProject;
import br.org.scadabr.api.vo.ItemInfo;
import br.org.scadabr.api.vo.ItemStringValue;
import br.org.scadabr.api.vo.ItemValue;
import br.org.scadabr.api.vo.ReplyBase;
import br.org.scadabr.api.vo.ServerStatus;
import br.org.scadabr.db.dao.FlexProjectDao;
import br.org.scadabr.rt.dataSource.ServerStateChecker;

import com.serotonin.mango.Common;

public class ScadaBRAPIImpl implements br.org.scadabr.api.ScadaBRAPI,
		APIConstants {
	private ScadaBRAPIDao dataDao = new MangoDaoImpl(
			AuthenticationHandler.getUsername());

	/**
	 * This method attempts to return a {@link GetStatusResponse} object
	 * containing basic information about the host server, such as software
	 * version, start up time, etc.
	 * 
	 * @since 0.2
	 * 
	 * @see GetStatusResponse
	 * @see ReplyBase
	 * @see ServerStatus
	 * @see ServerStateCode
	 * @return {@link GetStatusResponse}({@link ReplyBase}, {@link ServerStatus}
	 *         )
	 */
	public br.org.scadabr.api.da.GetStatusResponse getStatus()
			throws java.rmi.RemoteException {
		ReplyBase rb = new ReplyBase();
		rb.setRcvTime(Calendar.getInstance());
		GetStatusResponse response = new GetStatusResponse();
		ServerStatus ss = new ServerStatus();

		try {
			ss.setServerState(ServerStateChecker.getState());
			Calendar cl3 = APIUtils.getOnlineDate();
			ss.setStartTime(cl3);
			ss.setProductVersion(APIUtils.getVersion());
		} catch (Exception e) {
			ss.setServerState(ServerStateCode.FAILED);
		} finally {
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			response.setServerStatus(ss);
		}

		return response;

	}

	/**
	 * This method attempts to read the value of one or more registered TAGs. To
	 * read the value of one or more determinated TAGs just set the TAG's name
	 * on "itemsPath". As a return option, the maximum's return value
	 * "maxReturn" can be settable.
	 * 
	 * @since 0.2
	 * @param {@link ReadDataParams}(String[] itemPathList,
	 *        {@link RequestOptions} options)
	 * 
	 * @see ReadDataResponse
	 * @see ReplyBase
	 * @see ItemValue
	 * @see APIError
	 * @return {@link ReadDataResponse}({@link ReplyBase}, {@link ItemValue}[],
	 *         {@link APIError}[] )
	 */
	public ReadDataResponse readData(ReadDataParams parameters)
			throws java.rmi.RemoteException {

		ReplyBase rb = new ReplyBase();
		rb.setRcvTime(Calendar.getInstance());
		ReadDataResponse response = new ReadDataResponse();
		try {
			APIUtils.validateReadData(parameters);
		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}

		int maxReturn = MAX_READ_DATA_RETURN;

		if (parameters.getOptions() != null
				&& (parameters.getOptions().getMaxReturn() > 0)
				&& parameters.getOptions().getMaxReturn() < MAX_READ_DATA_RETURN)
			maxReturn = parameters.getOptions().getMaxReturn();

		String[] itemList = parameters.getItemPathList();
		List<ItemValue> aItemValue = new ArrayList<ItemValue>();
		List<APIError> errors = new ArrayList<APIError>();

		try {
			aItemValue = dataDao.getItemValueList(itemList, errors);
		} catch (ScadaBRAPIException e) {
			e.printStackTrace();
			errors.add(e.getError());
		}

		aItemValue = ((List<ItemValue>) limit(aItemValue, maxReturn));
		ItemValue[] ivArray = new ItemValue[aItemValue.size()];
		ivArray = aItemValue.toArray(ivArray);
		APIError[] aeArray = new APIError[errors.size()];
		aeArray = errors.toArray(aeArray);

		response.setErrors(aeArray);
		response.setItemsList(ivArray);
		rb.setReplyTime(Calendar.getInstance());
		response.setReplyBase(rb);
		return response;

	}

	/**
	 * This method attempts to write one or more values on registered TAGs. To
	 * write, just set the name and value of each ItemValue that will be wrote
	 * on each TAG. As a return option, the values wrote on each TAG can be
	 * settable to return or not "returnItemValues".
	 * 
	 * @since 0.2
	 * 
	 * @param {@link WriteDataParams}(ItemValue[] itemsList,
	 *        {@link WriteDataOptions} options)
	 * 
	 * 
	 * @see WriteDataResponse
	 * @see APIError
	 * @see ItemValue
	 * @see ReplyBase
	 * @return {@link WriteDataResponse}({@link ReplyBase}, {@link ItemValue} ,
	 *         {@link APIError} )
	 */
	public WriteDataResponse writeData(WriteDataParams parameters)
			throws java.rmi.RemoteException {
		// TODO: permissoes para escrita com problema
		ReplyBase rb = new ReplyBase();
		rb.setRcvTime(Calendar.getInstance());
		WriteDataResponse response = new WriteDataResponse();

		try {
			APIUtils.validateWriteData(parameters);
		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}

		if (parameters.getOptions() == null) {
			parameters.setOptions(new WriteDataOptions());
		}
		ArrayList<ItemValue> newItemValuesList = new ArrayList<ItemValue>();
		ArrayList<APIError> errors = new ArrayList<APIError>();

		for (ItemValue itemValue : parameters.getItemsList()) {
			try {
				ItemValue newItemValue = dataDao.writeData(itemValue);
				if (parameters.getOptions().isReturnItemValues())
					newItemValuesList.add(newItemValue);
				errors.add(APIConstants.ERROR_OK);
			} catch (ScadaBRAPIException e) {
				errors.add(e.getError());
			}
		}

		ItemValue[] ivArray = new ItemValue[newItemValuesList.size()];
		ivArray = newItemValuesList.toArray(ivArray);
		APIError[] aeArray = new APIError[errors.size()];
		aeArray = errors.toArray(aeArray);

		response.setItemsList(ivArray);
		response.setErrors(aeArray);

		rb.setReplyTime(Calendar.getInstance());
		response.setReplyBase(rb);

		return response;
	}

	@Override
	public WriteStringDataResponse writeStringData(
			WriteStringDataParams parameters) throws RemoteException {
		ReplyBase rb = new ReplyBase();
		rb.setRcvTime(Calendar.getInstance());
		WriteStringDataResponse response = new WriteStringDataResponse();

		try {
			APIUtils.validateStringWriteData(parameters);
		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}

		if (parameters.getOptions() == null) {
			parameters.setOptions(new WriteDataOptions());
		}
		ArrayList<ItemStringValue> newItemValuesList = new ArrayList<ItemStringValue>();
		ArrayList<APIError> errors = new ArrayList<APIError>();

		for (ItemStringValue itemValue : parameters.getItemsList()) {
			try {
				ItemStringValue newItemValue = dataDao
						.writeStringData(itemValue);
				if (parameters.getOptions().isReturnItemValues())
					newItemValuesList.add(newItemValue);
				errors.add(APIConstants.ERROR_OK);
			} catch (ScadaBRAPIException e) {
				errors.add(e.getError());
			}
		}

		ItemStringValue[] ivArray = new ItemStringValue[newItemValuesList
				.size()];
		ivArray = newItemValuesList.toArray(ivArray);
		APIError[] aeArray = new APIError[errors.size()];
		aeArray = errors.toArray(aeArray);

		response.setItemsList(ivArray);
		response.setErrors(aeArray);

		rb.setReplyTime(Calendar.getInstance());
		response.setReplyBase(rb);

		return response;
	}

	/**
	 * This method attempts to return all or just some registered TAGs specified
	 * in itemList. To return defined TAGs just set the itemList with the
	 * desired path. Oherwise, all TAGs will be returned. As a return option,
	 * the return's maximum value is settable.
	 * 
	 * @since 0.2
	 * @param {@link BrowseTagsParams}(String itemsPath,
	 *        {@link BrowseTagsOptions} options)
	 * 
	 * @see BrowseTagsResponse
	 * @see APIError
	 * @see ReplyBase
	 * @return {@link WriteDataResponse}({@link ReplyBase},{@link APIError} )
	 */
	public BrowseTagsResponse browseTags(BrowseTagsParams parameters)
			throws java.rmi.RemoteException {
		ReplyBase rb = new ReplyBase();
		rb.setRcvTime(Calendar.getInstance());
		BrowseTagsResponse response = new BrowseTagsResponse();

		if (parameters == null) {
			parameters = new BrowseTagsParams();
		}
		if (parameters.getOptions() == null) {
			parameters.setOptions(new BrowseTagsOptions());
		}
		int maxReturn = MAX_BROWSE_TAGS_RETURN;

		if ((parameters.getOptions().getMaxReturn() > 0)
				&& parameters.getOptions().getMaxReturn() < maxReturn) {
			maxReturn = parameters.getOptions().getMaxReturn();
		}

		List<ItemInfo> itemInfoList = new ArrayList<ItemInfo>();
		List<APIError> errors = new ArrayList<APIError>();

		try {
			itemInfoList = (ArrayList<ItemInfo>) dataDao.browseTags(parameters
					.getItemsPath());
			errors.add(APIConstants.ERROR_OK);
		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}

		itemInfoList = ((List<ItemInfo>) limit(itemInfoList, maxReturn));

		ItemInfo[] ivArray = new ItemInfo[itemInfoList.size()];
		ivArray = itemInfoList.toArray(ivArray);
		APIError[] aeArray = new APIError[errors.size()];
		aeArray = errors.toArray(aeArray);

		response.setItemsList(ivArray);
		response.setErrors(aeArray);
		rb.setReplyTime(Calendar.getInstance());
		response.setReplyBase(rb);

		return response;
	}

	/**
	 * This method attempts to collect data from a defined TAG. To collect data,
	 * is needed determinate a time interval, setting a initial date and a final
	 * date. A maximum value can be defined as a option.
	 * 
	 * @since 0.2
	 * 
	 * @param {@link GetDataHistoryParams}(String itemName,
	 *        {@link GetDataHistoryOptions} options)
	 * 
	 * @see GetDataHistoryResponse
	 * @see APIError
	 * @see ItemValue
	 * @see ReplyBase
	 * @return {@link GetDataHistoryResponse}({@link ReplyBase},
	 *         {@link ItemValue} , {@link APIError}, boolean moreValues )
	 */
	public GetDataHistoryResponse getDataHistory(GetDataHistoryParams parameters)
			throws java.rmi.RemoteException {
		ReplyBase rb = new ReplyBase();
		// received time
		rb.setRcvTime(Calendar.getInstance());
		GetDataHistoryResponse response = new GetDataHistoryResponse();

		try {
			APIUtils.validateGetDataHistoryParams(parameters);
		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}
		Calendar initialDate = parameters.getOptions().getInitialDate();
		Calendar finalDate = parameters.getOptions().getFinalDate();

		int maxReturn = MAX_DATA_HISTORY_RETURN;

		if (parameters.getOptions() != null
				&& (parameters.getOptions().getMaxReturn() > 0)
				&& parameters.getOptions().getMaxReturn() < MAX_DATA_HISTORY_RETURN)
			maxReturn = parameters.getOptions().getMaxReturn();

		List<ItemValue> itemValueList = new ArrayList<ItemValue>();
		ArrayList<APIError> errors = new ArrayList<APIError>();

		try {
			itemValueList = (ArrayList<ItemValue>) dataDao
					.getDataHistory(parameters.getItemName(), initialDate,
							finalDate, maxReturn);
			errors.add(APIConstants.ERROR_OK);
		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}

		ItemValue[] ivArray = new ItemValue[itemValueList.size()];
		ivArray = itemValueList.toArray(ivArray);
		APIError[] aeArray = new APIError[errors.size()];
		aeArray = errors.toArray(aeArray);

		response.setItemsList(ivArray);
		response.setErrors(aeArray);
		rb.setReplyTime(Calendar.getInstance());
		response.setReplyBase(rb);

		return response;
	}

	/**
	 * This method attempts to return system's active alarms . To define the
	 * desired alarm level, just set alarm level "alarmLevel" (the above levels
	 * are returned too), otherwise, all alarms will be returned. Another stuff
	 * that can be settable is the maximum's value return.
	 * 
	 * @since 0.2
	 * 
	 * @param {@link GetActiveEventsParams}(String eventsPath,
	 *        {@link ActiveEventsOptions} options)
	 * 
	 * @see GetActiveEventsResponse
	 * @see APIError
	 * @see EventNotification
	 * @see ReplyBase
	 * @return {@link GetActiveEventsResponse}({@link ReplyBase},
	 *         {@link EventNotification} , {@link APIError} )
	 */
	public GetActiveEventsResponse getActiveEvents(
			GetActiveEventsParams parameters) throws java.rmi.RemoteException {
		ReplyBase rb = new ReplyBase();
		rb.setRcvTime(Calendar.getInstance());
		GetActiveEventsResponse response = new GetActiveEventsResponse();

		if (parameters == null) {
			parameters = new GetActiveEventsParams();
		}
		int maxReturn = MAX_ACTIVE_EVENTS_RETURN;
		AlarmLevel alarmLevel = null;

		if (parameters.getOptions() != null) {
			if ((parameters.getOptions().getMaxReturn() > 0)
					&& parameters.getOptions().getMaxReturn() < MAX_ACTIVE_EVENTS_RETURN) {
				maxReturn = parameters.getOptions().getMaxReturn();
			}
			alarmLevel = parameters.getOptions().getAlarmLevel();
		}
		if (alarmLevel == null) {
			alarmLevel = AlarmLevel.NONE;
		}

		List<EventNotification> alarms = new ArrayList<EventNotification>();
		ArrayList<APIError> errors = new ArrayList<APIError>();

		try {
			alarms = dataDao.getEventNotifications(alarmLevel);
			errors.add(APIConstants.ERROR_OK);
		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}

		alarms = (List<EventNotification>) limit(alarms, maxReturn);

		EventNotification[] ivArray = new EventNotification[alarms.size()];
		ivArray = alarms.toArray(ivArray);
		APIError[] aeArray = new APIError[errors.size()];
		aeArray = errors.toArray(aeArray);

		response.setEventsList(ivArray);
		response.setErrors(aeArray);
		rb.setReplyTime(Calendar.getInstance());
		response.setReplyBase(rb);

		return response;

	}

	/**
	 * This method attempts to recognize the system's active alarms. To
	 * recognize a specified alarm is passed a event ID "eventsIds" that wants
	 * to be recognized. As another option, can be defined if the recognize's
	 * event details will be returned, "returnEventDetails".
	 * 
	 * @since 0.2
	 * 
	 * @param {@link AckEventsParams}(Integer[] eventsId,
	 *        {@link AckEventsOptions} options)
	 * 
	 * 
	 * @see AckEventsResponse
	 * @see APIError
	 * @see EventNotification
	 * @see ReplyBase
	 * @return {@link WriteDataResponse}({@link ReplyBase},
	 *         {@link EventNotification} , {@link APIError} )
	 */
	public AckEventsResponse ackEvents(AckEventsParams parameters)
			throws java.rmi.RemoteException {
		ReplyBase rb = new ReplyBase();
		rb.setRcvTime(Calendar.getInstance());
		AckEventsResponse response = new AckEventsResponse();

		try {
			APIUtils.validateAckEventsParams(parameters);
		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}

		boolean returnEventDetails = false;
		if (parameters.getOptions() != null) {
			returnEventDetails = parameters.getOptions().isReturnEventDetails();
		}
		Integer[] ids = parameters.getEventsId();
		ArrayList<APIError> errors = new ArrayList<APIError>();
		ArrayList<EventNotification> events = new ArrayList<EventNotification>();

		for (Integer eventId : ids) {
			try {
				EventNotification eventNotification = dataDao.ackEvent(eventId);
				if (returnEventDetails)
					events.add(eventNotification);
				errors.add(APIConstants.ERROR_OK);
			} catch (ScadaBRAPIException e) {
				errors.add(e.getError());
			}
		}

		EventNotification[] ivArray = new EventNotification[events.size()];
		ivArray = events.toArray(ivArray);
		APIError[] aeArray = new APIError[errors.size()];
		aeArray = errors.toArray(aeArray);

		response.setEvents(ivArray);
		response.setErrors(aeArray);
		rb.setReplyTime(Calendar.getInstance());
		response.setReplyBase(rb);
		return response;
	}

	/**
	 * This method attempts to return a alarm historian of notifications yet
	 * recongnized. To return that, is needed determinate a time interval,
	 * setting a initial date and a final date. To define the desired alarm
	 * level, just set alarm level "alarmLevel" (the above levels are returned
	 * too), otherwise, all alarms will be returned. Another stuff that can be
	 * settable is the maximum's value return.
	 * 
	 * @since 0.2
	 * 
	 * @param {@link GetEventsHistoryParams}(String eventsPath,
	 *        {@link EventsHistoryOptions} options)
	 * 
	 * 
	 * @see GetEventsHistoryResponse
	 * @see APIError
	 * @see EventNotification
	 * @see ReplyBase
	 * @return {@link WriteDataResponse}({@link ReplyBase},
	 *         {@link EventNotification} , {@link APIError} )
	 */
	public GetEventsHistoryResponse getEventsHistory(
			GetEventsHistoryParams parameters) throws java.rmi.RemoteException {

		ReplyBase rb = new ReplyBase();
		rb.setRcvTime(Calendar.getInstance());
		GetEventsHistoryResponse response = new GetEventsHistoryResponse();
		try {
			APIUtils.validateEventsHistoryParams(parameters);
		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}
		EventsHistoryOptions options = parameters.getOptions();
		Calendar initialDate = options.getInitialDate();
		Calendar finalDate = options.getFinalDate();

		int maxReturn = MAX_EVENTS_HISTORY_RETURN;

		if ((parameters.getOptions().getMaxReturn() > 0)
				&& parameters.getOptions().getMaxReturn() < MAX_EVENTS_HISTORY_RETURN)
			maxReturn = parameters.getOptions().getMaxReturn();

		List<EventNotification> events = new ArrayList<EventNotification>();
		List<APIError> errors = new ArrayList<APIError>();
		try {
			events = dataDao.getEventsHistory(options.getAlarmLevel(),
					initialDate, finalDate, maxReturn);
			errors.add(APIConstants.ERROR_OK);
		} catch (ScadaBRAPIException e) {
			errors.add(e.getError());
		}
		EventNotification[] alarmsArray = new EventNotification[events.size()];
		alarmsArray = events.toArray(alarmsArray);
		System.out.println("Número de eventos encontrados: " + events.size());
		APIError[] aeArray = new APIError[errors.size()];
		aeArray = errors.toArray(aeArray);

		response.setEventsList(alarmsArray);
		response.setErrors(aeArray);

		rb.setReplyTime(Calendar.getInstance());
		response.setReplyBase(rb);
		return response;
	}

	/**
	 * This method attempts to return system event's definitions. To return
	 * determinate type just set the event type "eventType" to the wished type.
	 * If not, will be returned all. As another option, to return specified
	 * options of each event just set "returnEventsConfig".
	 * 
	 * @since 0.2
	 * 
	 * @param {@link BrowseEventsParams}(String eventsPath,
	 *        {@link BrowseEventsOptions} options)
	 * 
	 * 
	 * @see BrowseEventsResponse
	 * @see APIError
	 * @see EventDefinition
	 * @see ReplyBase
	 * @return {@link BrowseEventsResponse}({@link ReplyBase},
	 *         {@link EventDefinition} , {@link APIError} )
	 */

	public BrowseEventsResponse browseEventsDefinitions(
			BrowseEventsParams parameters) throws java.rmi.RemoteException {
		ReplyBase replyBase = new ReplyBase();
		replyBase.setRcvTime(Calendar.getInstance());
		BrowseEventsResponse response = new BrowseEventsResponse();

		if (parameters == null) {
			parameters = new BrowseEventsParams();
		}

		EventType eventType = null;

		if (parameters.getOptions() != null) {
			eventType = parameters.getOptions().getEventType();
		}
		List<APIError> errors = new ArrayList<APIError>();
		List<EventDefinition> events = new ArrayList<EventDefinition>();

		try {
			events = dataDao.getEventDefinitions(eventType);
			errors.add(APIConstants.ERROR_OK);
		} catch (ScadaBRAPIException e) {
			errors.add(e.getError());
		}

		EventDefinition[] eventsArray = new EventDefinition[events.size()];
		eventsArray = events.toArray(eventsArray);
		response.setEventsList(eventsArray);
		APIError[] aeArray = new APIError[errors.size()];

		aeArray = errors.toArray(aeArray);
		response.setErrors(aeArray);
		replyBase.setReplyTime(Calendar.getInstance());
		response.setReplyBase(replyBase);
		return response;
	}

	/**
	 * This method attempts to write a message on a defined event. To annote a
	 * message on a event just pass the event's ID and a message to be attached
	 * on it.
	 * 
	 * @since 0.2
	 * 
	 * @param {@link AnnotateEventParams}(Integer eventId, {@link EventMessage}
	 *        message)
	 * 
	 * @see AnnotateEventResponse
	 * @see APIError
	 * @see EventMessage
	 * @see ReplyBase
	 * @return {@link AnnotateEventResponse}({@link ReplyBase},
	 *         {@link EventMessage} , {@link APIError} )
	 */
	public br.org.scadabr.api.ae.AnnotateEventResponse annotateEvent(
			br.org.scadabr.api.ae.AnnotateEventParams parameters)
			throws java.rmi.RemoteException {
		ReplyBase rb = new ReplyBase();
		rb.setRcvTime(Calendar.getInstance());
		AnnotateEventResponse response = new AnnotateEventResponse();

		try {
			APIUtils.validateAnnotateEvent(parameters);
		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}

		EventMessage[] messages = null;
		List<APIError> errors = new ArrayList<APIError>();
		try {
			messages = dataDao.annotateEvent(parameters.getEventId(),
					parameters.getMessage());
			errors.add(APIConstants.ERROR_OK);
		} catch (ScadaBRAPIException e) {
			errors.add(e.getError());
		}

		APIError[] aeArray = new APIError[errors.size()];
		aeArray = errors.toArray(aeArray);

		response.setEventMessagesList(messages);
		response.setErrors(aeArray);
		rb.setReplyTime(Calendar.getInstance());
		response.setReplyBase(rb);
		return response;
	}

	public br.org.scadabr.api.config.BrowseDataSourcesResponse browseDataSources(
			br.org.scadabr.api.config.BrowseDataSourcesParams parameters)
			throws java.rmi.RemoteException {
		ReplyBase rb = new ReplyBase();
		rb.setRcvTime(Calendar.getInstance());
		BrowseDataSourcesResponse response = new BrowseDataSourcesResponse();
		try {
			APIUtils.validateBrowseDataSourcesParams(parameters);
		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}
		List<Object> dataSources = null;
		try {
			dataSources = dataDao.getDataSources(parameters.getType());
			response.setErrors(new APIError[] { new APIError(ErrorCode.OK,
					"No Problem Found") });

		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}
		Object[] dsArray = new Object[dataSources.size()];
		dsArray = dataSources.toArray(dsArray);

		response.setType(parameters.getType());
		response.setDataSources(dsArray);
		rb.setReplyTime(Calendar.getInstance());
		response.setReplyBase(rb);
		return response;
	}

	public br.org.scadabr.api.config.ConfigureDataSourceResponse configureDataSource(
			br.org.scadabr.api.config.ConfigureDataSourceParams parameters)
			throws java.rmi.RemoteException {
		ReplyBase rb = new ReplyBase();
		rb.setRcvTime(Calendar.getInstance());
		ConfigureDataSourceResponse response = new ConfigureDataSourceResponse();

		try {
			APIUtils.validateConfigureDataSourceParams(parameters);
		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}
		int id = 0;
		try {
			id = dataDao.configureDataSource(parameters.getType(),
					parameters.getDataSource());
			response.setErrors(new APIError[] { new APIError(ErrorCode.OK,
					"No Problem Found") });

		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}
		response.setId(id);
		rb.setReplyTime(Calendar.getInstance());
		response.setReplyBase(rb);
		return response;
	}

	public br.org.scadabr.api.config.RemoveDataSourceResponse removeDataSource(
			br.org.scadabr.api.config.RemoveDataSourceParams parameters)
			throws java.rmi.RemoteException {
		ReplyBase rb = new ReplyBase();
		rb.setRcvTime(Calendar.getInstance());
		RemoveDataSourceResponse response = new RemoveDataSourceResponse();
		try {
			APIUtils.validateRemoveDataSourceParams(parameters);
		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}

		try {
			dataDao.removeDataSource(parameters.getId());
			response.setErrors(new APIError[] { new APIError(ErrorCode.OK,
					"No Problem Found") });
		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}

		rb.setReplyTime(Calendar.getInstance());
		response.setReplyBase(rb);
		return response;
	}

	public br.org.scadabr.api.config.BrowseDataPointsResponse browseDataPoints(
			br.org.scadabr.api.config.BrowseDataPointsParams parameters)
			throws java.rmi.RemoteException {
		ReplyBase rb = new ReplyBase();
		rb.setRcvTime(Calendar.getInstance());
		BrowseDataPointsResponse response = new BrowseDataPointsResponse();
		try {
			APIUtils.validateBrowseDataPointsParams(parameters);
		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}
		List<Object> dataPoints = null;
		try {
			dataPoints = dataDao.getDataPoints(parameters.getDataSourceId());
			response.setErrors(new APIError[] { new APIError(ErrorCode.OK,
					"No Problem Found") });

		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}
		Object[] dsArray = new Object[dataPoints.size()];
		dsArray = dataPoints.toArray(dsArray);

		response.setDataPoints(dsArray);
		rb.setReplyTime(Calendar.getInstance());
		response.setReplyBase(rb);
		return response;
	}

	public br.org.scadabr.api.config.ConfigureDataPointResponse configureDataPoint(
			br.org.scadabr.api.config.ConfigureDataPointParams parameters)
			throws java.rmi.RemoteException {
		ReplyBase rb = new ReplyBase();
		rb.setRcvTime(Calendar.getInstance());
		ConfigureDataPointResponse response = new ConfigureDataPointResponse();

		try {
			APIUtils.validateConfigureDataPointParams(parameters);
		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}

		int id = 0;
		try {
			id = dataDao.configureDataPoint(parameters.getDataSourceId(),
					parameters.getType(), parameters.getDataPoint());
			response.setErrors(new APIError[] { new APIError(ErrorCode.OK,
					"No Problem Found") });

		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}
		response.setId(id);
		rb.setReplyTime(Calendar.getInstance());
		response.setReplyBase(rb);
		return response;
	}

	public br.org.scadabr.api.config.RemoveDataPointResponse removeDataPoint(
			br.org.scadabr.api.config.RemoveDataPointParams parameters)
			throws java.rmi.RemoteException {
		ReplyBase rb = new ReplyBase();
		rb.setRcvTime(Calendar.getInstance());
		RemoveDataPointResponse response = new RemoveDataPointResponse();
		try {
			APIUtils.validateRemoveDataPointParams(parameters);
		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}

		try {
			dataDao.removeDataPoint(parameters.getId());
			response.setErrors(new APIError[] { new APIError(ErrorCode.OK,
					"No Problem Found") });
		} catch (ScadaBRAPIException e) {
			response.setErrors(new APIError[] { e.getError() });
			rb.setReplyTime(Calendar.getInstance());
			response.setReplyBase(rb);
			return response;
		}

		rb.setReplyTime(Calendar.getInstance());
		response.setReplyBase(rb);
		return response;
	}

	private List<?> limit(List<?> list, int limitSize) {
		if (limitSize > 0 && limitSize < list.size()) {
			list = list.subList(0, limitSize);
		}
		return list;
	}

	@Override
	public GetFlexBuilderConfigResponse getFlexBuilderConfig(int projectId)
			throws RemoteException {
		GetFlexBuilderConfigResponse response = new GetFlexBuilderConfigResponse();
		ReplyBase rb = new ReplyBase();
		rb.setRcvTime(Calendar.getInstance());

		FlexProjectDao dao = new FlexProjectDao();
		FlexProject project = dao.getFlexProject(projectId);

		if (project == null) {
			project = new FlexProject(Common.NEW_ID, "Novo Projeto",
					"Descrição do Projeto", "");
		}

		response.setProject(project);

		rb.setReplyTime(Calendar.getInstance());
		response.setReplyBase(rb);
		return response;
	}

	@Override
	public BrowseFlexProjectsResponse browseFlexProjects()
			throws RemoteException {
		BrowseFlexProjectsResponse response = new BrowseFlexProjectsResponse();
		ReplyBase rb = new ReplyBase();
		rb.setRcvTime(Calendar.getInstance());

		FlexProjectDao dao = new FlexProjectDao();
		List<FlexProject> list = dao.getFlexProjects();

		FlexProject[] projects = new FlexProject[list.size()];
		projects = list.toArray(projects);

		response.setProjects(projects);

		rb.setReplyTime(Calendar.getInstance());
		response.setReplyBase(rb);
		return response;
	}

	@Override
	public SetFlexBuilderConfigResponse setFlexBuilderConfig(
			SetFlexBuilderConfigParams params) throws RemoteException {
		SetFlexBuilderConfigResponse response = new SetFlexBuilderConfigResponse();
		ReplyBase rb = new ReplyBase();
		rb.setRcvTime(Calendar.getInstance());

		int projectId = params.getProject().getId();

		FlexProjectDao dao = new FlexProjectDao();
		FlexProject project = params.getProject();
		int id = dao.saveFlexProject(project.getId(), project.getName(),
				project.getDescription(), project.getXmlConfig());

		response.setProjectId(id);
		rb.setReplyTime(Calendar.getInstance());
		response.setReplyBase(rb);
		return response;
	}

	@Override
	public RemoveFlexProjectResponse removeFlexProject(int id)
			throws RemoteException {
		RemoveFlexProjectResponse response = new RemoveFlexProjectResponse();
		ReplyBase rb = new ReplyBase();
		rb.setRcvTime(Calendar.getInstance());

		FlexProjectDao dao = new FlexProjectDao();
		dao.deleteFlexProject(id);

		rb.setReplyTime(Calendar.getInstance());
		response.setReplyBase(rb);
		return response;
	}

}
