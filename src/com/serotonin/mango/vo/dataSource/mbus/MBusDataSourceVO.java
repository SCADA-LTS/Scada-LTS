/*
 *   Mango - Open Source M2M - http://mango.serotoninsoftware.com
 *   Copyright (C) 2010 Arne Pl\u00f6se
 *   @author Arne Pl\u00f6se
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.vo.dataSource.mbus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.json.JsonRemoteEntity;
import com.serotonin.json.JsonRemoteProperty;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataSource.DataSourceRT;
import com.serotonin.mango.rt.dataSource.mbus.MBusDataSourceRT;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.util.ExportCodes;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;
import java.util.LinkedHashMap;
import net.sf.mbus4j.Connection;
import net.sf.mbus4j.AtModemConnection;
import net.sf.mbus4j.SerialPortConnection;
import net.sf.mbus4j.TcpIpConnection;
import net.sf.mbus4j.dataframes.MBusMedium;

@JsonRemoteEntity
public class MBusDataSourceVO extends DataSourceVO<MBusDataSourceVO> {
    
    private final static Log LOG = LogFactory.getLog(MBusDataSourceVO.class);

    public static MBusDataSourceVO createNewDataSource() {
        MBusDataSourceVO result = new MBusDataSourceVO();
        result.setConnection(new TcpIpConnection("91.135.13.66", 65031, Connection.DEFAULT_BAUDRATE, TcpIpConnection.DEFAULT_RESPONSE_TIMEOUT_OFFSET));
        LOG.fatal("TCP CONN");
        return result;
    }
    private static final ExportCodes EVENT_CODES = new ExportCodes();
//TODO more events???
    static {
        EVENT_CODES.addElement(MBusDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, "DATA_SOURCE_EXCEPTION");
        EVENT_CODES.addElement(MBusDataSourceRT.POINT_READ_EXCEPTION_EVENT, "POINT_READ_EXCEPTION");
        EVENT_CODES.addElement(MBusDataSourceRT.POINT_WRITE_EXCEPTION_EVENT, "POINT_WRITE_EXCEPTION");
    }
    @JsonRemoteProperty
    private int updatePeriodType = Common.TimePeriods.DAYS;
    @JsonRemoteProperty
    private int updatePeriods = 1;
    // TODO JSON manually
    private Connection connection;

    @Override
    public Type getType() {
        return Type.M_BUS;
    }

    @Override
    protected void addEventTypes(List<EventTypeVO> eventTypes) {
        eventTypes.add(createEventType(MBusDataSourceRT.DATA_SOURCE_EXCEPTION_EVENT, new LocalizableMessage(
                "event.ds.dataSource")));
        eventTypes.add(createEventType(MBusDataSourceRT.POINT_READ_EXCEPTION_EVENT, new LocalizableMessage(
                "event.ds.pointRead")));
        eventTypes.add(createEventType(MBusDataSourceRT.POINT_WRITE_EXCEPTION_EVENT, new LocalizableMessage(
                "event.ds.pointWrite")));
    }

    @Override
    public LocalizableMessage getConnectionDescription() {
        return new LocalizableMessage("common.default", connection.getClass().getSimpleName());
    }

    @Override
    public PointLocatorVO createPointLocator() {
        return new MBusPointLocatorVO();
    }

    @Override
    public DataSourceRT createDataSourceRT() {
        return new MBusDataSourceRT(this);
    }

    @Override
    public ExportCodes getEventCodes() {
        return EVENT_CODES;
    }

    @Override
    protected void addPropertiesImpl(List<LocalizableMessage> list) {
        AuditEventType.addPropertyMessage(list, "dsEdit.mbus.connection", connection);
        AuditEventType.addPeriodMessage(list, "dsEdit.updatePeriod", updatePeriodType, updatePeriods);
    }

    @Override
    protected void addPropertyChangesImpl(List<LocalizableMessage> list, MBusDataSourceVO from) {
        AuditEventType.maybeAddPropertyChangeMessage(list, "dsEdit.mbus.connection", from.connection, connection);
        AuditEventType.maybeAddPeriodChangeMessage(list, "dsEdit.updatePeriod", from.updatePeriodType,
                from.updatePeriods, updatePeriodType, updatePeriods);
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

        if (connection == null) {
            response.addContextualMessage("connection", "validate.required");
        }
        if (!Common.TIME_PERIOD_CODES.isValidId(updatePeriodType)) {
            response.addContextualMessage("updatePeriodType", "validate.invalidValue");
        }
        if (updatePeriods <= 0) {
            response.addContextualMessage("updatePeriods", "validate.greaterThanZero");
        }
    }
    //
    // /
    // / Serialization
    // /
    //
    private static final long serialVersionUID = -1;
    private static final int SERIAL_VERSION = 1;

    // Serialization for saveDataSource
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(SERIAL_VERSION);
        out.writeObject(connection);
        out.writeInt(updatePeriodType);
        out.writeInt(updatePeriods);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        int ver = in.readInt();
        try {
            switch ( ver) {
                case 1:
                    readObjectVer1(in);
                    break;
            }
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        super.jsonDeserialize(reader, json);
        JsonObject jsonConnection = null;
        if (!json.isNull("tcpConnection")) {
            LOG.fatal("TCP FROM JSON");
            jsonConnection = json.getJsonObject("tcpConnection");
            TcpIpConnection tcpConnection = new TcpIpConnection();
            tcpConnection.setHost(jsonConnection.getString("host"));
            tcpConnection.setPort(jsonConnection.getInt("port"));
            connection = tcpConnection;
        } else {
              LOG.fatal("NO TCP FROM JSON");
        }
        //TODO serial stuff
        connection.setBitPerSecond(jsonConnection.getInt("bitPerSecond"));
        connection.setResponseTimeOutOffset(jsonConnection.getInt("responseTimeOutOffset"));
    }

    @Override
    public void jsonSerialize(Map<String, Object> map) {
        super.jsonSerialize(map);
        Map<String, Object> connectionMap = new LinkedHashMap<String, Object>();
        connectionMap.put("bitPerSecond", connection.getBitPerSecond());
        connectionMap.put("responseTimeOutOffset", connection.getResponseTimeOutOffset());
        if (connection instanceof TcpIpConnection) {
            TcpIpConnection tcpConnection = (TcpIpConnection) connection;
            connectionMap.put("host", tcpConnection.getHost());
            connectionMap.put("port", tcpConnection.getPort());
            map.put("tcpConnection", connectionMap);
        }
        //TODO serial stuff
    }

    /**
     * Helper for JSP
     * 
     * @return
     */
    public boolean isSerialDirect() {
        return SerialPortConnection.class.equals(connection.getClass());
    }

    /**
     * Helper for JSP
     * 
     * @return
     */
    public boolean isSerialAtModem() {
        return AtModemConnection.class.equals(connection.getClass());
    }

    /**
     * Helper for JSP
     *
     * @return
     */
    public boolean isTcpIp() {
        return TcpIpConnection.class.equals(connection.getClass());
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    private void readObjectVer1(ObjectInputStream in) throws IOException, ClassNotFoundException {
        connection = (Connection) in.readObject();
        updatePeriodType = in.readInt();
        updatePeriods = in.readInt();
    }

    public String[] getLabels() {
        MBusMedium[] val = MBusMedium.values();
        String[] result = new String[val.length];
        for (int i = 0; i < val.length; i++) {
            result[i] = val[i].getLabel();
        }
        System.out.println("MBUSVALUEs: " + result.length);
        return result;
    }

}
