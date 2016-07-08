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
package com.serotonin.mango.vo.dataSource.pop3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.pop3.Pop3DataSourceRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.util.SerializationHelper;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
@JsonRemoteEntity
public class Pop3DataSourceVO extends DataSourceVO<Pop3DataSourceVO> {
    public static final Type TYPE = Type.POP3;

    @Override
    protected void addEventTypes(List<EventTypeVO> ets) {
        ets.add(createEventType(Pop3DataSourceRT.INBOX_EXCEPTION_EVENT, new LocalizableMessage("event.ds.emailInbox")));
        ets.add(createEventType(Pop3DataSourceRT.MESSAGE_READ_EXCEPTION_EVENT, new LocalizableMessage(
                "event.ds.emailRead")));
        ets.add(createEventType(Pop3DataSourceRT.PARSE_EXCEPTION_EVENT, new LocalizableMessage("event.ds.emailParse")));
    }

    private static final ExportCodes EVENT_CODES = new ExportCodes();
    static {
        EVENT_CODES.addElement(Pop3DataSourceRT.INBOX_EXCEPTION_EVENT, "INBOX_EXCEPTION");
        EVENT_CODES.addElement(Pop3DataSourceRT.MESSAGE_READ_EXCEPTION_EVENT, "MESSAGE_READ_EXCEPTION");
        EVENT_CODES.addElement(Pop3DataSourceRT.PARSE_EXCEPTION_EVENT, "PARSE_EXCEPTION");
    }

    @Override
    public ExportCodes getEventCodes() {
        return EVENT_CODES;
    }

    @Override
    public LocalizableMessage getConnectionDescription() {
        return new LocalizableMessage("common.default", username);
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public DataSourceRT createDataSourceRT() {
        return new Pop3DataSourceRT(this);
    }

    @Override
    public Pop3PointLocatorVO createPointLocator() {
        return new Pop3PointLocatorVO();
    }

    @JsonRemoteProperty
    private String pop3Server;
    @JsonRemoteProperty
    private String username;
    @JsonRemoteProperty
    private String password;
    private int updatePeriodType = Common.TimePeriods.MINUTES;
    @JsonRemoteProperty
    private int updatePeriods = 5;

    public String getPop3Server() {
        return pop3Server;
    }

    public void setPop3Server(String pop3Server) {
        this.pop3Server = pop3Server;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUpdatePeriodType() {
        return updatePeriodType;
    }

    public void setUpdatePeriodType(int updatePeriodType) {
        this.updatePeriodType = updatePeriodType;
    }

    public int getUpdatePeriods() {
        return updatePeriods;
    }

    public void setUpdatePeriods(int updatePeriods) {
        this.updatePeriods = updatePeriods;
    }

    @Override
    public void validate(DwrResponseI18n response) {
        super.validate(response);
        if (StringUtils.isEmpty(pop3Server))
            response.addContextualMessage("pop3Server", "validate.required");
        if (StringUtils.isEmpty(username))
            response.addContextualMessage("username", "validate.required");
        if (StringUtils.isEmpty(password))
            response.addContextualMessage("password", "validate.required");
        if (!Common.TIME_PERIOD_CODES.isValidId(updatePeriodType))
            response.addContextualMessage("updatePeriodType", "validate.invalidValue");
        if (updatePeriods <= 0)
            response.addContextualMessage("updatePeriods", "validate.greaterThanZero");
    }

    @Override
    protected void addPropertiesImpl(List<LocalizableMessage> list) {
        AuditEventType.addPeriodMessage(list, "dsEdit.pop3.checkPeriod", updatePeriodType, updatePeriods);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.server", pop3Server);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.username", username);
        AuditEventType.addPropertyMessage(list, "dsEdit.pop3.password", password);
    }

    @Override
    protected void addPropertyChangesImpl(List<LocalizableMessage> list, Pop3DataSourceVO from) {
        AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.pop3.checkPeriod", from.updatePeriodType,
                from.updatePeriods, updatePeriodType, updatePeriods);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.server", from.pop3Server, pop3Server);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.username", from.username, username);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.pop3.password", from.password, password);
    }

    //
    // /
    // / Serialization
    // /
    //
    private static final long serialVersionUID = -1;
    private static final int version = 1;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        SerializationHelper.writeSafeUTF(out, pop3Server);
        SerializationHelper.writeSafeUTF(out, username);
        SerializationHelper.writeSafeUTF(out, password);
        out.writeInt(updatePeriodType);
        out.writeInt(updatePeriods);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            pop3Server = SerializationHelper.readSafeUTF(in);
            username = SerializationHelper.readSafeUTF(in);
            password = SerializationHelper.readSafeUTF(in);
            updatePeriodType = in.readInt();
            updatePeriods = in.readInt();
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        super.jsonDeserialize(reader, json);
        Integer value = deserializeUpdatePeriodType(json);
        if (value != null)
            updatePeriodType = value;
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        super.jsonSerialize(map);
        serializeUpdatePeriodType(map, updatePeriodType);
    }
}
