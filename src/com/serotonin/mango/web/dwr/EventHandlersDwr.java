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
package com.serotonin.mango.web.dwr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.serotonin.mango.dao_cache.DaoInstances;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContextFactory;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.SystemEventType;
import com.serotonin.mango.rt.maint.work.ProcessWorkItem;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.DataPointExtendedNameComparator;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.event.CompoundEventDetectorVO;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.mango.vo.event.MaintenanceEventVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.mango.vo.event.ScheduledEventVO;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.vo.publish.PublishedPointVO;
import com.serotonin.mango.vo.publish.PublisherVO;
import com.serotonin.mango.web.dwr.beans.DataPointBean;
import com.serotonin.mango.web.dwr.beans.EventSourceBean;
import com.serotonin.mango.web.dwr.beans.RecipientListEntryBean;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import org.scada_lts.mango.service.PublisherService;

public class EventHandlersDwr extends BaseDwr {
	private static final Log LOG = LogFactory.getLog(EventHandlersDwr.class);

	private final ResourceBundle setPointSnippetMap = ResourceBundle
			.getBundle("setPointSnippetMap");

	public Map<String, Object> getInitData() {
		User user = Common.getUser();
		Permissions.ensureDataSourcePermission(user);

		Map<String, Object> model = new HashMap<String, Object>();

		// Get the data points
		List<DataPointBean> allPoints = new ArrayList<DataPointBean>();
		List<EventSourceBean> dataPoints = new ArrayList<EventSourceBean>();
		List<DataPointVO> dps = DaoInstances.DataPointDao.getDataPoints(
				DataPointExtendedNameComparator.instance, true);
		for (DataPointVO dp : dps) {
			if (!Permissions
					.hasDataSourcePermission(user, dp.getDataSourceId()))
				continue;

			allPoints.add(new DataPointBean(dp));

			if (dp.getEventDetectors().size() > 0) {
				EventSourceBean source = new EventSourceBean();
				source.setId(dp.getId());
				source.setName(dp.getExtendedName());

				for (PointEventDetectorVO ped : dp.getEventDetectors()) {
					EventTypeVO dpet = ped.getEventType();
					dpet.setHandlers(DaoInstances.EventDao.getEventHandlers(dpet));
					source.getEventTypes().add(dpet);
				}

				dataPoints.add(source);
			}
		}

		// Get the scheduled events
		List<EventTypeVO> scheduledEvents = new ArrayList<EventTypeVO>();
		for (ScheduledEventVO se : DaoInstances.ScheduledEventDao.getScheduledEvents()) {
			EventTypeVO et = se.getEventType();
			et.setHandlers(DaoInstances.EventDao.getEventHandlers(et));
			scheduledEvents.add(et);
		}
		model.put("scheduledEvents", scheduledEvents);

		// Get the compound event detectors
		List<EventTypeVO> compoundEvents = new ArrayList<EventTypeVO>();
		for (CompoundEventDetectorVO ced : DaoInstances.CompoundEventDetectorDao.getCompoundEventDetectors()) {
			EventTypeVO et = ced.getEventType();
			et.setHandlers(DaoInstances.EventDao.getEventHandlers(et));
			compoundEvents.add(et);
		}
		model.put("compoundEvents", compoundEvents);

		// Get the data sources
		List<EventSourceBean> dataSources = new ArrayList<EventSourceBean>();
		for (DataSourceVO<?> ds : DaoInstances.DataSourceDao.getDataSources()) {
			if (!Permissions.hasDataSourcePermission(user, ds.getId()))
				continue;

			if (ds.getEventTypes().size() > 0) {
				EventSourceBean source = new EventSourceBean();
				source.setId(ds.getId());
				source.setName(ds.getName());

				for (EventTypeVO dset : ds.getEventTypes()) {
					dset.setHandlers(DaoInstances.EventDao.getEventHandlers(dset));
					source.getEventTypes().add(dset);
				}

				dataSources.add(source);
			}
		}

		if (Permissions.hasAdmin(user)) {
			// Get the publishers
			List<EventSourceBean> publishers = new ArrayList<EventSourceBean>();
			for (PublisherVO<? extends PublishedPointVO> p : DaoInstances.PublisherDao
					.getPublishers(new PublisherService.PublisherNameComparator())) {
				if (p.getEventTypes().size() > 0) {
					EventSourceBean source = new EventSourceBean();
					source.setId(p.getId());
					source.setName(p.getName());

					for (EventTypeVO pet : p.getEventTypes()) {
						pet.setHandlers(DaoInstances.EventDao.getEventHandlers(pet));
						source.getEventTypes().add(pet);
					}

					publishers.add(source);
				}
			}
			model.put("publishers", publishers);

			// Get the maintenance events
			List<EventTypeVO> maintenanceEvents = new ArrayList<EventTypeVO>();
			for (MaintenanceEventVO me : DaoInstances.MaintenanceEventDao.getMaintenanceEvents()) {
				EventTypeVO et = me.getEventType();
				et.setHandlers(DaoInstances.EventDao.getEventHandlers(et));
				maintenanceEvents.add(et);
			}
			model.put("maintenanceEvents", maintenanceEvents);

			// Get the system events
			List<EventTypeVO> systemEvents = new ArrayList<EventTypeVO>();
			for (EventTypeVO sets : SystemEventType.getSystemEventTypes()) {
				sets.setHandlers(DaoInstances.EventDao.getEventHandlers(sets));
				systemEvents.add(sets);
			}
			model.put("systemEvents", systemEvents);

			// Get the audit events
			List<EventTypeVO> auditEvents = new ArrayList<EventTypeVO>();
			for (EventTypeVO aets : AuditEventType.getAuditEventTypes()) {
				aets.setHandlers(DaoInstances.EventDao.getEventHandlers(aets));
				auditEvents.add(aets);
			}
			model.put("auditEvents", auditEvents);
		}

		// Get the mailing lists.
		model.put("mailingLists", DaoInstances.MailingListDao.getMailingLists());

		// Get the users.
		model.put("users", DaoInstances.UserDao.getUsers());

		model.put("allPoints", allPoints);
		model.put("dataPoints", dataPoints);
		model.put("dataSources", dataSources);

		return model;
	}

	public String createSetValueContent(int pointId, String valueStr,
			String idSuffix) {
		DataPointVO pointVO = DaoInstances.DataPointDao.getDataPoint(pointId);
		Permissions.ensureDataSourcePermission(Common.getUser(),
				pointVO.getDataSourceId());

		MangoValue value = MangoValue.stringToValue(valueStr, pointVO
				.getPointLocator().getDataTypeId());

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("point", pointVO);
		model.put("idSuffix", idSuffix);
		model.put("text",
				pointVO.getTextRenderer()
						.getText(value, TextRenderer.HINT_FULL));
		model.put("rawText",
				pointVO.getTextRenderer().getText(value, TextRenderer.HINT_RAW));

		String snippet = setPointSnippetMap.getString(pointVO.getTextRenderer()
				.getClass().getName());
		return generateContent(WebContextFactory.get().getHttpServletRequest(),
				snippet, model);
	}

	public DwrResponseI18n saveSetPointEventHandler(int eventSourceId,
			int eventTypeRef1, int eventTypeRef2, int handlerId, String xid,
			String alias, boolean disabled, int targetPointId,
			int activeAction, String activeValueToSet, int activePointId,
			int inactiveAction, String inactiveValueToSet, int inactivePointId) {
		EventHandlerVO handler = new EventHandlerVO();
		handler.setHandlerType(EventHandlerVO.TYPE_SET_POINT);
		handler.setTargetPointId(targetPointId);
		handler.setActiveAction(activeAction);
		handler.setActiveValueToSet(activeValueToSet);
		handler.setActivePointId(activePointId);
		handler.setInactiveAction(inactiveAction);
		handler.setInactiveValueToSet(inactiveValueToSet);
		handler.setInactivePointId(inactivePointId);
		return save(eventSourceId, eventTypeRef1, eventTypeRef2, handler,
				handlerId, xid, alias, disabled);
	}

	public DwrResponseI18n saveEmailEventHandler(int eventSourceId,
			int eventTypeRef1, int eventTypeRef2, int handlerId, String xid,
			String alias, boolean disabled,
			List<RecipientListEntryBean> activeRecipients,
			boolean sendEscalation, int escalationDelayType,
			int escalationDelay,
			List<RecipientListEntryBean> escalationRecipients,
			boolean sendInactive, boolean inactiveOverride,
			List<RecipientListEntryBean> inactiveRecipients) {
		EventHandlerVO handler = new EventHandlerVO();
		handler.setHandlerType(EventHandlerVO.TYPE_EMAIL);
		handler.setActiveRecipients(activeRecipients);
		handler.setSendEscalation(sendEscalation);
		handler.setEscalationDelayType(escalationDelayType);
		handler.setEscalationDelay(escalationDelay);
		handler.setEscalationRecipients(escalationRecipients);
		handler.setSendInactive(sendInactive);
		handler.setInactiveOverride(inactiveOverride);
		handler.setInactiveRecipients(inactiveRecipients);
		return save(eventSourceId, eventTypeRef1, eventTypeRef2, handler,
				handlerId, xid, alias, disabled);
	}

	public DwrResponseI18n saveProcessEventHandler(int eventSourceId,
			int eventTypeRef1, int eventTypeRef2, int handlerId, String xid,
			String alias, boolean disabled, String activeProcessCommand,
			String inactiveProcessCommand) {
		EventHandlerVO handler = new EventHandlerVO();
		handler.setHandlerType(EventHandlerVO.TYPE_PROCESS);
		handler.setActiveProcessCommand(activeProcessCommand);
		handler.setInactiveProcessCommand(inactiveProcessCommand);
		return save(eventSourceId, eventTypeRef1, eventTypeRef2, handler,
				handlerId, xid, alias, disabled);
	}

	public DwrResponseI18n saveScriptEventHandler(int eventSourceId,
			int eventTypeRef1, int eventTypeRef2, int handlerId, String xid,
			String alias, boolean disabled, int activeScriptCommand,
			int inactiveScriptCommand) {
		EventHandlerVO handler = new EventHandlerVO();
		handler.setHandlerType(EventHandlerVO.TYPE_SCRIPT);
		handler.setActiveScriptCommand(activeScriptCommand);
		handler.setInactiveScriptCommand(inactiveScriptCommand);
		return save(eventSourceId, eventTypeRef1, eventTypeRef2, handler,
				handlerId, xid, alias, disabled);
	}

	private DwrResponseI18n save(int eventSourceId, int eventTypeRef1,
			int eventTypeRef2, EventHandlerVO vo, int handlerId, String xid,
			String alias, boolean disabled) {
		EventTypeVO type = new EventTypeVO(eventSourceId, eventTypeRef1,
				eventTypeRef2);
		Permissions.ensureEventTypePermission(Common.getUser(), type);

		vo.setId(handlerId);
		vo.setXid(StringUtils.isEmpty(xid) ? DaoInstances.EventDao.generateUniqueXid() : xid);
		vo.setAlias(alias);
		vo.setDisabled(disabled);

		DwrResponseI18n response = new DwrResponseI18n();
		vo.validate(response);

		if (!response.getHasMessages()) {
			DaoInstances.EventDao.saveEventHandler(type, vo);
			response.addData("handler", vo);
		}

		return response;
	}

	public void deleteEventHandler(int handlerId) {
		Permissions.ensureEventTypePermission(Common.getUser(),
				DaoInstances.EventDao.getEventHandlerType(handlerId));
		DaoInstances.EventDao.deleteEventHandler(handlerId);
	}

	public LocalizableMessage testProcessCommand(String command) {
		if (StringUtils.isEmpty(command))
			return null;

		try {
			ProcessWorkItem.executeProcessCommand(command);
			return new LocalizableMessage("eventHandlers.commandTest.result");
		} catch (IOException e) {
			LOG.warn("Process error", e);
			return new LocalizableMessage("common.default", e.getMessage());
		}
	}
}
