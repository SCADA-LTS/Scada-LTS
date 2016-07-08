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
package com.serotonin.mango.vo.dataSource.snmp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import org.snmp4j.mp.SnmpConstants;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.snmp.SnmpDataSourceRT;
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
 * 
 */
@JsonRemoteEntity
public class SnmpDataSourceVO extends DataSourceVO<SnmpDataSourceVO> {
    public static final Type TYPE = Type.SNMP;

    public interface AuthProtocols {
        String NONE = "";
        String MD5 = "MD5";
        String SHA = "SHA";
    }

    public interface PrivProtocols {
        String NONE = "";
        String DES = "DES";
        String AES128 = "AES128";
        String AES192 = "AES192";
        String AES256 = "AES256";
    }

    @Override
    protected void addEventTypes(List<EventTypeVO> ets) {
        ets.add(createEventType(SnmpDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, new LocalizableMessage(
                "event.ds.dataSource")));
        ets.add(createEventType(SnmpDataSourceRT.PDU_EXCEPTION_EVENT, new LocalizableMessage("event.ds.pdu")));
    }

    private static final ExportCodes EVENT_CODES = new ExportCodes();
    static {
        EVENT_CODES.addElement(SnmpDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, "DATA_SOURCE_EXCEPTION");
        EVENT_CODES.addElement(SnmpDataSourceRT.PDU_EXCEPTION_EVENT, "PDU_EXCEPTION");
    }

    @Override
    public ExportCodes getEventCodes() {
        return EVENT_CODES;
    }

    @Override
    public LocalizableMessage getConnectionDescription() {
        return new LocalizableMessage("common.default", host);
    }

    @Override
    public Type getType() {
        return TYPE;
    }

    @Override
    public DataSourceRT createDataSourceRT() {
        return new SnmpDataSourceRT(this);
    }

    @Override
    public SnmpPointLocatorVO createPointLocator() {
        return new SnmpPointLocatorVO();
    }

    @JsonRemoteProperty
    private String host;
    @JsonRemoteProperty
    private int port = SnmpConstants.DEFAULT_COMMAND_RESPONDER_PORT;
    @JsonRemoteProperty
    private int snmpVersion;
    @JsonRemoteProperty
    private String community;
    @JsonRemoteProperty
    private String engineId;
    @JsonRemoteProperty
    private String contextEngineId;
    @JsonRemoteProperty
    private String contextName;
    @JsonRemoteProperty
    private String securityName;
    @JsonRemoteProperty
    private String authProtocol;
    @JsonRemoteProperty
    private String authPassphrase;
    @JsonRemoteProperty
    private String privProtocol;
    @JsonRemoteProperty
    private String privPassphrase;
    @JsonRemoteProperty
    private int retries = 2;
    @JsonRemoteProperty
    private int timeout = 1000;
    private int updatePeriodType = Common.TimePeriods.MINUTES;
    @JsonRemoteProperty
    private int updatePeriods = 5;
    @JsonRemoteProperty
    private int trapPort = SnmpConstants.DEFAULT_NOTIFICATION_RECEIVER_PORT;
    @JsonRemoteProperty
    private String localAddress;

    public String getAuthPassphrase() {
        return authPassphrase;
    }

    public void setAuthPassphrase(String authPassphrase) {
        this.authPassphrase = authPassphrase;
    }

    public String getAuthProtocol() {
        return authProtocol;
    }

    public void setAuthProtocol(String authProtocol) {
        this.authProtocol = authProtocol;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getContextEngineId() {
        return contextEngineId;
    }

    public void setContextEngineId(String contextEngineId) {
        this.contextEngineId = contextEngineId;
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getEngineId() {
        return engineId;
    }

    public void setEngineId(String engineId) {
        this.engineId = engineId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPrivPassphrase() {
        return privPassphrase;
    }

    public void setPrivPassphrase(String privPassphrase) {
        this.privPassphrase = privPassphrase;
    }

    public String getPrivProtocol() {
        return privProtocol;
    }

    public void setPrivProtocol(String privProtocol) {
        this.privProtocol = privProtocol;
    }

    public String getSecurityName() {
        return securityName;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public int getUpdatePeriods() {
        return updatePeriods;
    }

    public void setUpdatePeriods(int updatePeriods) {
        this.updatePeriods = updatePeriods;
    }

    public int getUpdatePeriodType() {
        return updatePeriodType;
    }

    public void setUpdatePeriodType(int updatePeriodType) {
        this.updatePeriodType = updatePeriodType;
    }

    public int getSnmpVersion() {
        return snmpVersion;
    }

    public void setSnmpVersion(int snmpVersion) {
        this.snmpVersion = snmpVersion;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTrapPort() {
        return trapPort;
    }

    public void setTrapPort(int trapPort) {
        this.trapPort = trapPort;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    @Override
    public void validate(DwrResponseI18n response) {
        super.validate(response);
        if (!Common.TIME_PERIOD_CODES.isValidId(updatePeriodType))
            response.addContextualMessage("updatePeriodType", "validate.invalidValue");
        if (updatePeriods <= 0)
            response.addContextualMessage("updatePeriods", "validate.greaterThanZero");

        if (port <= 0 || port > 65535)
            response.addContextualMessage("port", "validate.invalidValue");

        if (trapPort <= 0 || trapPort > 65535)
            response.addContextualMessage("trapPort", "validate.invalidValue");

        if (StringUtils.isEmpty(host))
            response.addContextualMessage("host", "validate.required");

        try {
            InetAddress.getByName(host);
        }
        catch (UnknownHostException e) {
            response.addContextualMessage("host", "validate.invalidValue");
        }

        if (snmpVersion != SnmpConstants.version1 && snmpVersion != SnmpConstants.version2c
                && snmpVersion != SnmpConstants.version3)
            response.addContextualMessage("snmpVersion", "validate.invalidValue");
        if (timeout <= 0)
            response.addContextualMessage("timeout", "validate.greaterThanZero");
        if (retries < 0)
            response.addContextualMessage("retries", "validate.cannotBeNegative");
    }

    @Override
    protected void addPropertiesImpl(List<LocalizableMessage> list) {
        AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", updatePeriodType, updatePeriods);
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.host", host);
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.port", port);
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.version", snmpVersion);
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.community", community);
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.securityName", securityName);
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.authProtocol", authProtocol);
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.authPassphrase", authPassphrase);
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.privProtocol", privProtocol);
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.privPassphrase", privPassphrase);
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.engineId", engineId);
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.contextEngine", contextEngineId);
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.contextName", contextName);
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.retries", retries);
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.timeout", timeout);
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.trapPort", trapPort);
        AuditEventType.addPropertyMessage(list, "dsEdit.snmp.localAddress", localAddress);
    }

    @Override
    protected void addPropertyChangesImpl(List<LocalizableMessage> list, SnmpDataSourceVO from) {
        AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType,
                from.updatePeriods, updatePeriodType, updatePeriods);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.host", from.host, host);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.port", from.port, port);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.version", from.snmpVersion, snmpVersion);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.community", from.community, community);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.securityName", from.securityName, securityName);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.authProtocol", from.authProtocol, authProtocol);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.authPassphrase", from.authPassphrase,
                authPassphrase);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.privProtocol", from.privProtocol, privProtocol);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.privPassphrase", from.privPassphrase,
                privPassphrase);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.engineId", from.engineId, engineId);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.contextEngine", from.contextEngineId,
                contextEngineId);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.contextName", from.contextName, contextName);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.retries", from.retries, retries);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.timeout", from.timeout, timeout);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.trapPort", from.trapPort, trapPort);
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.snmp.localAddress", from.localAddress, localAddress);
    }

    //
    // /
    // / Serialization
    // /
    //
    private static final long serialVersionUID = -1;
    private static final int version = 2;

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(version);
        SerializationHelper.writeSafeUTF(out, host);
        out.writeInt(port);
        out.writeInt(snmpVersion);
        SerializationHelper.writeSafeUTF(out, community);
        SerializationHelper.writeSafeUTF(out, engineId);
        SerializationHelper.writeSafeUTF(out, contextEngineId);
        SerializationHelper.writeSafeUTF(out, contextName);
        SerializationHelper.writeSafeUTF(out, securityName);
        SerializationHelper.writeSafeUTF(out, authProtocol);
        SerializationHelper.writeSafeUTF(out, authPassphrase);
        SerializationHelper.writeSafeUTF(out, privProtocol);
        SerializationHelper.writeSafeUTF(out, privPassphrase);
        out.writeInt(retries);
        out.writeInt(timeout);
        out.writeInt(updatePeriodType);
        out.writeInt(updatePeriods);
        out.writeInt(trapPort);
        SerializationHelper.writeSafeUTF(out, localAddress);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();

        // Switch on the version of the class so that version changes can be elegantly handled.
        if (ver == 1) {
            host = SerializationHelper.readSafeUTF(in);
            port = in.readInt();
            snmpVersion = in.readInt();
            community = SerializationHelper.readSafeUTF(in);
            engineId = SerializationHelper.readSafeUTF(in);
            contextEngineId = SerializationHelper.readSafeUTF(in);
            contextName = SerializationHelper.readSafeUTF(in);
            securityName = SerializationHelper.readSafeUTF(in);
            authProtocol = SerializationHelper.readSafeUTF(in);
            authPassphrase = SerializationHelper.readSafeUTF(in);
            privProtocol = SerializationHelper.readSafeUTF(in);
            privPassphrase = SerializationHelper.readSafeUTF(in);
            retries = in.readInt();
            timeout = in.readInt();
            updatePeriodType = in.readInt();
            updatePeriods = in.readInt();
            trapPort = in.readInt();
            localAddress = "";
        }
        else if (ver == 2) {
            host = SerializationHelper.readSafeUTF(in);
            port = in.readInt();
            snmpVersion = in.readInt();
            community = SerializationHelper.readSafeUTF(in);
            engineId = SerializationHelper.readSafeUTF(in);
            contextEngineId = SerializationHelper.readSafeUTF(in);
            contextName = SerializationHelper.readSafeUTF(in);
            securityName = SerializationHelper.readSafeUTF(in);
            authProtocol = SerializationHelper.readSafeUTF(in);
            authPassphrase = SerializationHelper.readSafeUTF(in);
            privProtocol = SerializationHelper.readSafeUTF(in);
            privPassphrase = SerializationHelper.readSafeUTF(in);
            retries = in.readInt();
            timeout = in.readInt();
            updatePeriodType = in.readInt();
            updatePeriods = in.readInt();
            trapPort = in.readInt();
            localAddress = SerializationHelper.readSafeUTF(in);
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
