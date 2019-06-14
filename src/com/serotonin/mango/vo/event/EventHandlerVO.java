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
package com.serotonin.mango.vo.event;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.json.JsonArray;
import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.json.JsonSerializable;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.EventDao;
import com.serotonin.mango.db.dao.MailingListDao;
import com.serotonin.mango.db.dao.UserDao;
import com.serotonin.mango.rt.event.handlers.EmailHandlerRT;
import com.serotonin.mango.rt.event.handlers.EventHandlerRT;
import com.serotonin.mango.rt.event.handlers.ProcessHandlerRT;
import com.serotonin.mango.rt.event.handlers.ScriptHandlerRT;
import com.serotonin.mango.rt.event.handlers.SetPointHandlerRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ChangeComparable;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.util.LocalizableJsonException;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.mailingList.EmailRecipient;
import com.serotonin.mango.web.dwr.beans.RecipientListEntryBean;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

@JsonRemoteEntity
public class EventHandlerVO implements Serializable,
		ChangeComparable<EventHandlerVO>, JsonSerializable {
	public static final String XID_PREFIX = "EH_";

	public static final int TYPE_SET_POINT = 1;
	public static final int TYPE_EMAIL = 2;
	public static final int TYPE_PROCESS = 3;
	public static final int TYPE_SCRIPT = 4;

	public static ExportCodes TYPE_CODES = new ExportCodes();
	static {
		TYPE_CODES.addElement(TYPE_SET_POINT, "SET_POINT",
				"eventHandlers.type.setPoint");
		TYPE_CODES.addElement(TYPE_EMAIL, "EMAIL", "eventHandlers.type.email");
		TYPE_CODES.addElement(TYPE_PROCESS, "PROCESS",
				"eventHandlers.type.process");
		TYPE_CODES.addElement(TYPE_SCRIPT, "SCRIPT",
				"eventHandlers.type.script");
	}

	public static final int RECIPIENT_TYPE_ACTIVE = 1;
	public static final int RECIPIENT_TYPE_ESCALATION = 2;
	public static final int RECIPIENT_TYPE_INACTIVE = 3;

	public static ExportCodes RECIPIENT_TYPE_CODES = new ExportCodes();
	static {
		RECIPIENT_TYPE_CODES.addElement(RECIPIENT_TYPE_ACTIVE, "ACTIVE",
				"eventHandlers.recipientType.active");
		RECIPIENT_TYPE_CODES.addElement(RECIPIENT_TYPE_ESCALATION,
				"ESCALATION", "eventHandlers.recipientType.escalation");
		RECIPIENT_TYPE_CODES.addElement(RECIPIENT_TYPE_INACTIVE, "INACTIVE",
				"eventHandlers.recipientType.inactive");
	}

	public static final int SET_ACTION_NONE = 0;
	public static final int SET_ACTION_POINT_VALUE = 1;
	public static final int SET_ACTION_STATIC_VALUE = 2;

	public static ExportCodes SET_ACTION_CODES = new ExportCodes();
	static {
		SET_ACTION_CODES.addElement(SET_ACTION_NONE, "NONE",
				"eventHandlers.action.none");
		SET_ACTION_CODES.addElement(SET_ACTION_POINT_VALUE, "POINT_VALUE",
				"eventHandlers.action.point");
		SET_ACTION_CODES.addElement(SET_ACTION_STATIC_VALUE, "STATIC_VALUE",
				"eventHandlers.action.static");
	}

	// Common fields
	private int id = Common.NEW_ID;
	private String xid;
	@JsonRemoteProperty
	private String alias;
	private int handlerType;
	@JsonRemoteProperty
	private boolean disabled;

	// Set point handler fields.
	private int targetPointId;
	private int activeAction;
	private String activeValueToSet;
	private int activePointId;
	private int inactiveAction;
	private String inactiveValueToSet;
	private int inactivePointId;

	// Email handler fields.
	private List<RecipientListEntryBean> activeRecipients;
	private boolean sendEscalation;
	private int escalationDelayType;
	private int escalationDelay;
	private List<RecipientListEntryBean> escalationRecipients;
	private boolean sendInactive;
	private boolean inactiveOverride;
	private List<RecipientListEntryBean> inactiveRecipients;

	// Process handler fields.
	private String activeProcessCommand;
	private String inactiveProcessCommand;

	// script fields
	private int activeScriptCommand;
	private int inactiveScriptCommand;

	public EventHandlerRT createRuntime() {
		switch (handlerType) {
		case TYPE_SET_POINT:
			return new SetPointHandlerRT(this);
		case TYPE_EMAIL:
			return new EmailHandlerRT(this);
		case TYPE_PROCESS:
			return new ProcessHandlerRT(this);
		case TYPE_SCRIPT:
			return new ScriptHandlerRT(this);
		}
		throw new ShouldNeverHappenException("Unknown handler type: "
				+ handlerType);
	}

	public LocalizableMessage getMessage() {
		if (!StringUtils.isEmpty(alias))
			return new LocalizableMessage("common.default", alias);
		return getTypeMessage(handlerType);
	}

	public static LocalizableMessage getSetActionMessage(int action) {
		switch (action) {
		case SET_ACTION_NONE:
			return new LocalizableMessage("eventHandlers.action.none");
		case SET_ACTION_POINT_VALUE:
			return new LocalizableMessage("eventHandlers.action.point");
		case SET_ACTION_STATIC_VALUE:
			return new LocalizableMessage("eventHandlers.action.static");
		}
		return new LocalizableMessage("common.unknown");
	}

	private static LocalizableMessage getTypeMessage(int handlerType) {
		switch (handlerType) {
		case TYPE_SET_POINT:
			return new LocalizableMessage("eventHandlers.type.setPoint");
		case TYPE_EMAIL:
			return new LocalizableMessage("eventHandlers.type.email");
		case TYPE_PROCESS:
			return new LocalizableMessage("eventHandlers.type.process");
		case TYPE_SCRIPT:
			return new LocalizableMessage("eventHandlers.type.script");
		}
		return new LocalizableMessage("common.unknown");
	}

	public int getTargetPointId() {
		return targetPointId;
	}

	public void setTargetPointId(int targetPointId) {
		this.targetPointId = targetPointId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getXid() {
		return xid;
	}

	public void setXid(String xid) {
		this.xid = xid;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public int getHandlerType() {
		return handlerType;
	}

	public void setHandlerType(int handlerType) {
		this.handlerType = handlerType;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public int getActiveAction() {
		return activeAction;
	}

	public void setActiveAction(int activeAction) {
		this.activeAction = activeAction;
	}

	public int getInactiveAction() {
		return inactiveAction;
	}

	public void setInactiveAction(int inactiveAction) {
		this.inactiveAction = inactiveAction;
	}

	public String getActiveValueToSet() {
		return activeValueToSet;
	}

	public void setActiveValueToSet(String activeValueToSet) {
		this.activeValueToSet = activeValueToSet;
	}

	public int getActivePointId() {
		return activePointId;
	}

	public void setActivePointId(int activePointId) {
		this.activePointId = activePointId;
	}

	public String getInactiveValueToSet() {
		return inactiveValueToSet;
	}

	public void setInactiveValueToSet(String inactiveValueToSet) {
		this.inactiveValueToSet = inactiveValueToSet;
	}

	public int getInactivePointId() {
		return inactivePointId;
	}

	public void setInactivePointId(int inactivePointId) {
		this.inactivePointId = inactivePointId;
	}

	public List<RecipientListEntryBean> getActiveRecipients() {
		return activeRecipients;
	}

	public void setActiveRecipients(
			List<RecipientListEntryBean> activeRecipients) {
		this.activeRecipients = activeRecipients;
	}

	public int getEscalationDelay() {
		return escalationDelay;
	}

	public void setEscalationDelay(int escalationDelay) {
		this.escalationDelay = escalationDelay;
	}

	public int getEscalationDelayType() {
		return escalationDelayType;
	}

	public void setEscalationDelayType(int escalationDelayType) {
		this.escalationDelayType = escalationDelayType;
	}

	public List<RecipientListEntryBean> getEscalationRecipients() {
		return escalationRecipients;
	}

	public void setEscalationRecipients(
			List<RecipientListEntryBean> escalationRecipients) {
		this.escalationRecipients = escalationRecipients;
	}

	public boolean isSendEscalation() {
		return sendEscalation;
	}

	public void setSendEscalation(boolean sendEscalation) {
		this.sendEscalation = sendEscalation;
	}

	public boolean isSendInactive() {
		return sendInactive;
	}

	public void setSendInactive(boolean sendInactive) {
		this.sendInactive = sendInactive;
	}

	public boolean isInactiveOverride() {
		return inactiveOverride;
	}

	public void setInactiveOverride(boolean inactiveOverride) {
		this.inactiveOverride = inactiveOverride;
	}

	public List<RecipientListEntryBean> getInactiveRecipients() {
		return inactiveRecipients;
	}

	public void setInactiveRecipients(
			List<RecipientListEntryBean> inactiveRecipients) {
		this.inactiveRecipients = inactiveRecipients;
	}

	public String getActiveProcessCommand() {
		return activeProcessCommand;
	}

	public void setActiveProcessCommand(String activeProcessCommand) {
		this.activeProcessCommand = activeProcessCommand;
	}

	public String getInactiveProcessCommand() {
		return inactiveProcessCommand;
	}

	public void setInactiveProcessCommand(String inactiveProcessCommand) {
		this.inactiveProcessCommand = inactiveProcessCommand;
	}

	public String getTypeKey() {
		return "event.audit.eventHandler";
	}

	public void validate(DwrResponseI18n response) {
		if (handlerType == TYPE_SET_POINT) {
			DataPointVO dp = new DataPointDao().getDataPoint(targetPointId);

			if (dp == null)
				response.addGenericMessage("eventHandlers.noTargetPoint");
			else {
				int dataType = dp.getPointLocator().getDataTypeId();

				if (activeAction == SET_ACTION_NONE
						&& inactiveAction == SET_ACTION_NONE)
					response
							.addGenericMessage("eventHandlers.noSetPointAction");

				// Active
				if (activeAction == SET_ACTION_STATIC_VALUE
						&& dataType == DataTypes.MULTISTATE) {
					try {
						Integer.parseInt(activeValueToSet);
					} catch (NumberFormatException e) {
						response
								.addGenericMessage("eventHandlers.invalidActiveValue");
					}
				}

				if (activeAction == SET_ACTION_STATIC_VALUE
						&& dataType == DataTypes.NUMERIC) {
					try {
						Double.parseDouble(activeValueToSet);
					} catch (NumberFormatException e) {
						response
								.addGenericMessage("eventHandlers.invalidActiveValue");
					}
				}

				if (activeAction == SET_ACTION_POINT_VALUE) {
					DataPointVO dpActive = new DataPointDao()
							.getDataPoint(activePointId);

					if (dpActive == null)
						response
								.addGenericMessage("eventHandlers.invalidActiveSource");
					else if (dataType != dpActive.getPointLocator()
							.getDataTypeId())
						response
								.addGenericMessage("eventHandlers.invalidActiveSourceType");
				}

				// Inactive
				if (inactiveAction == SET_ACTION_STATIC_VALUE
						&& dataType == DataTypes.MULTISTATE) {
					try {
						Integer.parseInt(inactiveValueToSet);
					} catch (NumberFormatException e) {
						response
								.addGenericMessage("eventHandlers.invalidInactiveValue");
					}
				}

				if (inactiveAction == SET_ACTION_STATIC_VALUE
						&& dataType == DataTypes.NUMERIC) {
					try {
						Double.parseDouble(inactiveValueToSet);
					} catch (NumberFormatException e) {
						response
								.addGenericMessage("eventHandlers.invalidInactiveValue");
					}
				}

				if (inactiveAction == SET_ACTION_POINT_VALUE) {
					DataPointVO dpInactive = new DataPointDao()
							.getDataPoint(inactivePointId);

					if (dpInactive == null)
						response
								.addGenericMessage("eventHandlers.invalidInactiveSource");
					else if (dataType != dpInactive.getPointLocator()
							.getDataTypeId())
						response
								.addGenericMessage("eventHandlers.invalidInactiveSourceType");
				}
			}
		} else if (handlerType == TYPE_EMAIL) {
			if (activeRecipients.isEmpty())
				response.addGenericMessage("eventHandlers.noEmailRecips");

			if (sendEscalation) {
				if (escalationDelay <= 0)
					response.addContextualMessage("escalationDelay",
							"eventHandlers.escalDelayError");
				if (escalationRecipients.isEmpty())
					response.addGenericMessage("eventHandlers.noEscalRecips");
			}

			if (sendInactive && inactiveOverride) {
				if (inactiveRecipients.isEmpty())
					response
							.addGenericMessage("eventHandlers.noInactiveRecips");
			}
		} else if (handlerType == TYPE_PROCESS) {
			if (StringUtils.isEmpty(activeProcessCommand)
					&& StringUtils.isEmpty(inactiveProcessCommand))
				response.addGenericMessage("eventHandlers.invalidCommands");
		} else if (handlerType == TYPE_SCRIPT) {
			if (activeScriptCommand < 1 && inactiveScriptCommand < 1)
				response.addGenericMessage("eventHandlers.invalidScripts");
		}
	}

	@Override
	public void addProperties(List<LocalizableMessage> list) {
		DataPointDao dataPointDao = new DataPointDao();
		AuditEventType.addPropertyMessage(list, "common.xid", xid);
		AuditEventType.addPropertyMessage(list, "eventHandlers.alias", alias);
		AuditEventType.addPropertyMessage(list, "eventHandlers.type",
				getTypeMessage(handlerType));
		AuditEventType.addPropertyMessage(list, "common.disabled", disabled);
		if (handlerType == TYPE_SET_POINT) {
			AuditEventType.addPropertyMessage(list, "eventHandlers.target",
					dataPointDao.getExtendedPointName(targetPointId));
			AuditEventType.addPropertyMessage(list,
					"eventHandlers.activeAction",
					getSetActionMessage(activeAction));
			if (activeAction == SET_ACTION_POINT_VALUE)
				AuditEventType.addPropertyMessage(list,
						"eventHandlers.action.point", dataPointDao
								.getExtendedPointName(activePointId));
			else if (activeAction == SET_ACTION_STATIC_VALUE)
				AuditEventType.addPropertyMessage(list,
						"eventHandlers.action.static", activeValueToSet);

			AuditEventType.addPropertyMessage(list,
					"eventHandlers.inactiveAction",
					getSetActionMessage(inactiveAction));
			if (inactiveAction == SET_ACTION_POINT_VALUE)
				AuditEventType.addPropertyMessage(list,
						"eventHandlers.action.point", dataPointDao
								.getExtendedPointName(inactivePointId));
			else if (inactiveAction == SET_ACTION_STATIC_VALUE)
				AuditEventType.addPropertyMessage(list,
						"eventHandlers.action.static", inactiveValueToSet);
		} else if (handlerType == TYPE_EMAIL) {
			AuditEventType.addPropertyMessage(list,
					"eventHandlers.emailRecipients",
					createRecipientMessage(activeRecipients));
			AuditEventType.addPropertyMessage(list, "eventHandlers.escal",
					sendEscalation);
			if (sendEscalation) {
				AuditEventType.addPeriodMessage(list,
						"eventHandlers.escalPeriod", escalationDelayType,
						escalationDelay);
				AuditEventType.addPropertyMessage(list,
						"eventHandlers.escalRecipients",
						createRecipientMessage(escalationRecipients));
			}
			AuditEventType.addPropertyMessage(list,
					"eventHandlers.inactiveNotif", sendInactive);
			if (sendInactive) {
				AuditEventType.addPropertyMessage(list,
						"eventHandlers.inactiveOverride", inactiveOverride);
				if (inactiveOverride)
					AuditEventType.addPropertyMessage(list,
							"eventHandlers.inactiveRecipients",
							createRecipientMessage(inactiveRecipients));
			}
		} else if (handlerType == TYPE_PROCESS) {
			AuditEventType.addPropertyMessage(list,
					"eventHandlers.activeCommand", activeProcessCommand);
			AuditEventType.addPropertyMessage(list,
					"eventHandlers.inactiveCommand", inactiveProcessCommand);
		} else if (handlerType == TYPE_SCRIPT) {
			AuditEventType.addPropertyMessage(list,
					"eventHandlers.activeCommand", activeScriptCommand);
			AuditEventType.addPropertyMessage(list,
					"eventHandlers.inactiveCommand", inactiveScriptCommand);
		}
	}

	@Override
	public void addPropertyChanges(List<LocalizableMessage> list,
			EventHandlerVO from) {
		DataPointDao dataPointDao = new DataPointDao();
		AuditEventType.maybeAddPropertyChangeMessage(list, "common.xid",
				from.xid, xid);
		AuditEventType.maybeAddPropertyChangeMessage(list,
				"eventHandlers.alias", from.alias, alias);
		AuditEventType.maybeAddPropertyChangeMessage(list, "common.disabled",
				from.disabled, disabled);
		if (handlerType == TYPE_SET_POINT) {
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"eventHandlers.target", dataPointDao
							.getExtendedPointName(from.targetPointId),
					dataPointDao.getExtendedPointName(targetPointId));
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"eventHandlers.activeAction",
					getSetActionMessage(from.activeAction),
					getSetActionMessage(activeAction));
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"eventHandlers.action.point", dataPointDao
							.getExtendedPointName(from.activePointId),
					dataPointDao.getExtendedPointName(activePointId));
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"eventHandlers.action.static", from.activeValueToSet,
					activeValueToSet);

			AuditEventType.maybeAddPropertyChangeMessage(list,
					"eventHandlers.inactiveAction",
					getSetActionMessage(from.inactiveAction),
					getSetActionMessage(inactiveAction));
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"eventHandlers.action.point", dataPointDao
							.getExtendedPointName(from.inactivePointId),
					dataPointDao.getExtendedPointName(inactivePointId));
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"eventHandlers.action.static", from.inactiveValueToSet,
					inactiveValueToSet);
		} else if (handlerType == TYPE_EMAIL) {
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"eventHandlers.emailRecipients",
					createRecipientMessage(from.activeRecipients),
					createRecipientMessage(activeRecipients));
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"eventHandlers.escal", from.sendEscalation, sendEscalation);
			AuditEventType.maybeAddPeriodChangeMessage(list,
					"eventHandlers.escalPeriod", from.escalationDelayType,
					from.escalationDelay, escalationDelayType, escalationDelay);
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"eventHandlers.escalRecipients",
					createRecipientMessage(from.escalationRecipients),
					createRecipientMessage(escalationRecipients));
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"eventHandlers.inactiveNotif", from.sendInactive,
					sendInactive);
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"eventHandlers.inactiveOverride", from.inactiveOverride,
					inactiveOverride);
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"eventHandlers.inactiveRecipients",
					createRecipientMessage(from.inactiveRecipients),
					createRecipientMessage(inactiveRecipients));
		} else if (handlerType == TYPE_PROCESS) {
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"eventHandlers.activeCommand", from.activeProcessCommand,
					activeProcessCommand);
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"eventHandlers.inactiveCommand",
					from.inactiveProcessCommand, inactiveProcessCommand);
		} else if (handlerType == TYPE_SCRIPT) {
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"eventHandlers.activeCommand", from.activeScriptCommand,
					activeScriptCommand);
			AuditEventType.maybeAddPropertyChangeMessage(list,
					"eventHandlers.inactiveCommand",
					from.inactiveScriptCommand, inactiveScriptCommand);
		}
	}

	private static LocalizableMessage createRecipientMessage(
			List<RecipientListEntryBean> recipients) {
		MailingListDao mailingListDao = new MailingListDao();
		UserDao userDao = new UserDao();
		ArrayList<LocalizableMessage> params = new ArrayList<LocalizableMessage>();
		for (RecipientListEntryBean recip : recipients) {
			LocalizableMessage msg;
			if (recip.getRecipientType() == EmailRecipient.TYPE_MAILING_LIST)
				msg = new LocalizableMessage("event.audit.recip.mailingList",
						mailingListDao.getMailingList(recip.getReferenceId())
								.getName());
			else if (recip.getRecipientType() == EmailRecipient.TYPE_USER)
				msg = new LocalizableMessage("event.audit.recip.user", userDao
						.getUser(recip.getReferenceId()).getUsername());
			else
				msg = new LocalizableMessage("event.audit.recip.address", recip
						.getReferenceAddress());
			params.add(msg);
		}

		return new LocalizableMessage(
				"event.audit.recip.list." + params.size(), params.toArray());
	}

	//
	// /
	// / Serialization
	// /
	//
	private static final long serialVersionUID = -1;
	private static final int version = 3;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(version);
		out.writeInt(handlerType);
		out.writeBoolean(disabled);
		if (handlerType == TYPE_SET_POINT) {
			out.writeInt(targetPointId);
			out.writeInt(activeAction);
			SerializationHelper.writeSafeUTF(out, activeValueToSet);
			out.writeInt(activePointId);
			out.writeInt(inactiveAction);
			SerializationHelper.writeSafeUTF(out, inactiveValueToSet);
			out.writeInt(inactivePointId);
		} else if (handlerType == TYPE_EMAIL) {
			out.writeObject(activeRecipients);
			out.writeBoolean(sendEscalation);
			out.writeInt(escalationDelayType);
			out.writeInt(escalationDelay);
			out.writeObject(escalationRecipients);
			out.writeBoolean(sendInactive);
			out.writeBoolean(inactiveOverride);
			out.writeObject(inactiveRecipients);
		} else if (handlerType == TYPE_PROCESS) {
			SerializationHelper.writeSafeUTF(out, activeProcessCommand);
			SerializationHelper.writeSafeUTF(out, inactiveProcessCommand);
		} else if (handlerType == TYPE_SCRIPT) {
			out.writeInt(activeScriptCommand);
			out.writeInt(inactiveScriptCommand);
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int ver = in.readInt();

		// Switch on the version of the class so that version changes can be
		// elegantly handled.
		if (ver == 1) {
			handlerType = in.readInt();
			disabled = false;
			if (handlerType == TYPE_SET_POINT) {
				targetPointId = in.readInt();
				activeAction = in.readInt();
				activeValueToSet = SerializationHelper.readSafeUTF(in);
				activePointId = in.readInt();
				inactiveAction = in.readInt();
				inactiveValueToSet = SerializationHelper.readSafeUTF(in);
				inactivePointId = in.readInt();
			} else if (handlerType == TYPE_EMAIL) {
				activeRecipients = (List<RecipientListEntryBean>) in
						.readObject();
				sendEscalation = in.readBoolean();
				escalationDelayType = in.readInt();
				escalationDelay = in.readInt();
				escalationRecipients = (List<RecipientListEntryBean>) in
						.readObject();
				sendInactive = in.readBoolean();
				inactiveOverride = false;
				inactiveRecipients = new ArrayList<RecipientListEntryBean>();
			} else if (handlerType == TYPE_PROCESS) {
				activeProcessCommand = SerializationHelper.readSafeUTF(in);
				inactiveProcessCommand = SerializationHelper.readSafeUTF(in);
			}
		} else if (ver == 2) {
			handlerType = in.readInt();
			disabled = false;
			if (handlerType == TYPE_SET_POINT) {
				targetPointId = in.readInt();
				activeAction = in.readInt();
				activeValueToSet = SerializationHelper.readSafeUTF(in);
				activePointId = in.readInt();
				inactiveAction = in.readInt();
				inactiveValueToSet = SerializationHelper.readSafeUTF(in);
				inactivePointId = in.readInt();
			} else if (handlerType == TYPE_EMAIL) {
				activeRecipients = (List<RecipientListEntryBean>) in
						.readObject();
				sendEscalation = in.readBoolean();
				escalationDelayType = in.readInt();
				escalationDelay = in.readInt();
				escalationRecipients = (List<RecipientListEntryBean>) in
						.readObject();
				sendInactive = in.readBoolean();
				inactiveOverride = in.readBoolean();
				inactiveRecipients = (List<RecipientListEntryBean>) in
						.readObject();
			} else if (handlerType == TYPE_PROCESS) {
				activeProcessCommand = SerializationHelper.readSafeUTF(in);
				inactiveProcessCommand = SerializationHelper.readSafeUTF(in);
			}
		} else if (ver == 3) {
			handlerType = in.readInt();
			disabled = in.readBoolean();
			if (handlerType == TYPE_SET_POINT) {
				targetPointId = in.readInt();
				activeAction = in.readInt();
				activeValueToSet = SerializationHelper.readSafeUTF(in);
				activePointId = in.readInt();
				inactiveAction = in.readInt();
				inactiveValueToSet = SerializationHelper.readSafeUTF(in);
				inactivePointId = in.readInt();
			} else if (handlerType == TYPE_EMAIL) {
				activeRecipients = (List<RecipientListEntryBean>) in
						.readObject();
				sendEscalation = in.readBoolean();
				escalationDelayType = in.readInt();
				escalationDelay = in.readInt();
				escalationRecipients = (List<RecipientListEntryBean>) in
						.readObject();
				sendInactive = in.readBoolean();
				inactiveOverride = in.readBoolean();
				inactiveRecipients = (List<RecipientListEntryBean>) in
						.readObject();
			} else if (handlerType == TYPE_PROCESS) {
				activeProcessCommand = SerializationHelper.readSafeUTF(in);
				inactiveProcessCommand = SerializationHelper.readSafeUTF(in);
			} else if (handlerType == TYPE_SCRIPT) {
				activeScriptCommand = in.readInt();
				inactiveScriptCommand = in.readInt();
			}
		}
	}

	public void jsonSerialize(Map<String, Object> map) {
		DataPointDao dataPointDao = new DataPointDao();
		map.put("eventType", new EventDao().getEventHandlerType(id));

		map.put("xid", xid);
		map.put("handlerType", TYPE_CODES.getCode(handlerType));

		if (handlerType == TYPE_SET_POINT) {
			DataPointVO dp = dataPointDao.getDataPoint(targetPointId);
			if (dp != null)
				map.put("targetPointId", dp.getXid());

			// Active
			map.put("activeAction", SET_ACTION_CODES.getCode(activeAction));
			if (activeAction == SET_ACTION_POINT_VALUE) {
				dp = dataPointDao.getDataPoint(activePointId);
				if (dp != null)
					map.put("activePointId", dp.getXid());
			} else if (activeAction == SET_ACTION_STATIC_VALUE)
				map.put("activeValueToSet", activeValueToSet);

			// Inactive
			map.put("inactiveAction", SET_ACTION_CODES.getCode(inactiveAction));
			if (inactiveAction == SET_ACTION_POINT_VALUE) {
				dp = dataPointDao.getDataPoint(inactivePointId);
				if (dp != null)
					map.put("inactivePointId", dp.getXid());
			} else if (inactiveAction == SET_ACTION_STATIC_VALUE)
				map.put("inactiveValueToSet", inactiveValueToSet);
		} else if (handlerType == TYPE_EMAIL) {
			map.put("activeRecipients", activeRecipients);
			map.put("sendEscalation", sendEscalation);
			if (sendEscalation) {
				map.put("escalationDelayType", Common.TIME_PERIOD_CODES
						.getCode(escalationDelayType));
				map.put("escalationDelay", escalationDelay);
				map.put("escalationRecipients", escalationRecipients);
			}
			map.put("sendInactive", sendInactive);
			if (sendInactive) {
				map.put("inactiveOverride", inactiveOverride);
				if (inactiveOverride)
					map.put("inactiveRecipients", inactiveRecipients);
			}
		} else if (handlerType == TYPE_PROCESS) {
			map.put("activeProcessCommand", activeProcessCommand);
			map.put("inactiveProcessCommand", inactiveProcessCommand);
		} else if (handlerType == TYPE_SCRIPT) {
			map.put("activeScriptCommand", activeScriptCommand);
			map.put("inactiveScriptCommand", inactiveScriptCommand);
		}
	}

	@SuppressWarnings("unchecked")
	public void jsonDeserialize(JsonReader reader, JsonObject json)
			throws JsonException {
		DataPointDao dataPointDao = new DataPointDao();

		String text = json.getString("handlerType");
		if (text != null) {
			handlerType = TYPE_CODES.getId(text);
			if (!TYPE_CODES.isValidId(handlerType))
				throw new LocalizableJsonException(
						"emport.error.eventHandler.invalid", "handlerType",
						text, TYPE_CODES.getCodeList());
		}

		if (handlerType == TYPE_SET_POINT) {
			String xid = json.getString("targetPointId");
			if (xid != null) {
				DataPointVO vo = dataPointDao.getDataPoint(xid);
				if (vo == null)
					throw new LocalizableJsonException(
							"emport.error.missingPoint", xid);
				targetPointId = vo.getId();
			}

			// Active
			text = json.getString("activeAction");
			if (text != null) {
				activeAction = SET_ACTION_CODES.getId(text);
				if (!SET_ACTION_CODES.isValidId(activeAction))
					throw new LocalizableJsonException(
							"emport.error.eventHandler.invalid",
							"activeAction", text, SET_ACTION_CODES
									.getCodeList());
			}

			if (activeAction == SET_ACTION_POINT_VALUE) {
				xid = json.getString("activePointId");
				if (xid != null) {
					DataPointVO vo = dataPointDao.getDataPoint(xid);
					if (vo == null)
						throw new LocalizableJsonException(
								"emport.error.missingPoint", xid);
					activePointId = vo.getId();
				}
			} else if (activeAction == SET_ACTION_STATIC_VALUE) {
				text = json.getString("activeValueToSet");
				if (text != null)
					activeValueToSet = text;
			}

			// Inactive
			text = json.getString("inactiveAction");
			if (text != null) {
				inactiveAction = SET_ACTION_CODES.getId(text);
				if (!SET_ACTION_CODES.isValidId(inactiveAction))
					throw new LocalizableJsonException(
							"emport.error.eventHandler.invalid",
							"inactiveAction", text, SET_ACTION_CODES
									.getCodeList());
			}

			if (inactiveAction == SET_ACTION_POINT_VALUE) {
				xid = json.getString("inactivePointId");
				if (xid != null) {
					DataPointVO vo = dataPointDao.getDataPoint(xid);
					if (vo == null)
						throw new LocalizableJsonException(
								"emport.error.missingPoint", xid);
					inactivePointId = vo.getId();
				}
			} else if (inactiveAction == SET_ACTION_STATIC_VALUE) {
				text = json.getString("inactiveValueToSet");
				if (text != null)
					inactiveValueToSet = text;
			}
		} else if (handlerType == TYPE_EMAIL) {
			JsonArray jsonActiveRecipients = json
					.getJsonArray("activeRecipients");
			if (jsonActiveRecipients != null)
				activeRecipients = reader.readPropertyValue(
						jsonActiveRecipients, List.class,
						RecipientListEntryBean.class);

			Boolean b = json.getBoolean("sendEscalation");
			if (b != null)
				sendEscalation = b;

			if (sendEscalation) {
				text = json.getString("escalationDelayType");
				if (text != null) {
					escalationDelayType = Common.TIME_PERIOD_CODES.getId(text);
					if (escalationDelayType == -1)
						throw new LocalizableJsonException(
								"emport.error.invalid", "escalationDelayType",
								text, Common.TIME_PERIOD_CODES.getCodeList());
				}

				Integer i = json.getInt("escalationDelay");
				if (i != null)
					escalationDelay = i;

				JsonArray jsonEscalationRecipients = json
						.getJsonArray("escalationRecipients");
				if (jsonEscalationRecipients != null)
					escalationRecipients = reader.readPropertyValue(
							jsonEscalationRecipients, List.class,
							RecipientListEntryBean.class);
			}

			b = json.getBoolean("sendInactive");
			if (b != null)
				sendInactive = b;

			if (sendInactive) {
				b = json.getBoolean("inactiveOverride");
				if (b != null)
					inactiveOverride = b;

				if (inactiveOverride) {
					JsonArray jsonInactiveRecipients = json
							.getJsonArray("inactiveRecipients");
					if (jsonInactiveRecipients != null)
						inactiveRecipients = reader.readPropertyValue(
								jsonInactiveRecipients, List.class,
								RecipientListEntryBean.class);
				}
			}
		} else if (handlerType == TYPE_PROCESS) {
			text = json.getString("activeProcessCommand");
			if (text != null)
				activeProcessCommand = text;

			text = json.getString("inactiveProcessCommand");
			if (text != null)
				inactiveProcessCommand = text;
		} else if (handlerType == TYPE_SCRIPT) {
			Integer script = json.getInt("activeScriptCommand");
			if (text != null)
				activeScriptCommand = script;

			script = json.getInt("inactiveScriptCommand");
			if (text != null)
				inactiveScriptCommand = script;
		}
	}

	public void setActiveScriptCommand(int activeScriptCommand) {
		this.activeScriptCommand = activeScriptCommand;
	}

	public int getActiveScriptCommand() {
		return activeScriptCommand;
	}

	public void setInactiveScriptCommand(int inactiveScriptCommand) {
		this.inactiveScriptCommand = inactiveScriptCommand;
	}

	public int getInactiveScriptCommand() {
		return inactiveScriptCommand;
	}
}
