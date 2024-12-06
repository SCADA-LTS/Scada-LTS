package br.org.scadabr;

import br.org.scadabr.vo.dataSource.opcua.OpcUaDataSourceVO;
import br.org.scadabr.vo.dataSource.opcua.OpcUaPointLocatorVO;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.maint.work.PlcConnectionClosingWorkItem;
import com.serotonin.mango.rt.maint.work.WorkItem;
import com.serotonin.mango.util.LoggingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcDriverManager;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.scada_lts.ds.opcua.TimeoutPlcDriverManager;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;

public class OpcUaMaster {

    private static final Log LOG = LogFactory.getLog(OpcUaMaster.class);

    private final OpcUaDataSourceVO<?> dataSource;
    private PlcConnection plcConnection;
    private PlcDriverManager driverManager;

    public OpcUaMaster(OpcUaDataSourceVO<?> dataSource) {
        this.dataSource = dataSource;
        this.driverManager = new TimeoutPlcDriverManager();
    };

    public void init() throws Exception {
        terminate();
        this.plcConnection = driverManager.getConnectionManager().getConnection(dataSource.getConnectionString());
    }

    public String getDataSourceXid() {
        return dataSource.getXid();
    }

    public void terminate() throws Exception {
        doClose(plcConnection);
    }

    public Object read(OpcUaPointLocatorVO pointLocator) throws Exception {
        PlcConnection plcConnection = getConnetion();
        PlcReadResponse readResponse = OpcUaUtils.sendRead(plcConnection, pointLocator.getTag(), pointLocator.getNodeId());
        PlcResponseCode responseCode = readResponse.getResponseCode(pointLocator.getTag());
        if (responseCode != PlcResponseCode.OK) {
            throw new IllegalStateException(MessageFormat.format("Failed read nodeId: {0}, tag: {1}, value: {2}, code: {3}", pointLocator.getNodeId(), pointLocator.getTag(), readResponse.getObject(pointLocator.getTag()), responseCode.name()));
        }
        return readResponse.getObject(pointLocator.getTag());
    }

    public void write(OpcUaPointLocatorVO pointLocator, MangoValue value) throws Exception {
        if(value == null) {
            throw new IllegalArgumentException(MessageFormat.format("Failed write nodeId: {0}, tag: {1}, msg: {2}", pointLocator.getNodeId(), pointLocator.getTag(), "Value is null!"));
        }
        if(value instanceof ImageValue) {
            throw new IllegalArgumentException(MessageFormat.format("Failed write nodeId: {0}, tag: {1}, msg: {2}", pointLocator.getNodeId(), pointLocator.getTag(), value.getClass().getName() + " is not supported!"));
        }
        Object valueToConvert = value.getObjectValue();
        Object valueToSend = pointLocator.getDataType().convert(valueToConvert);
        PlcConnection plcConnection = getConnetion();
        PlcWriteResponse writeResponse = OpcUaUtils.sendWrite(plcConnection, pointLocator.getTag(), pointLocator.getNodeId(), valueToSend);
        PlcResponseCode responseCode = writeResponse.getResponseCode(pointLocator.getTag());
        if (responseCode != PlcResponseCode.OK)
            throw new IllegalStateException(MessageFormat.format("Failed write nodeId: {0}, tag: {1}, valueToSend: {2}, code: {3}", pointLocator.getNodeId(), pointLocator.getTag(), valueToSend, responseCode.name()));
    }

    public int findNamespaceIndex(int numberOfAttempts, OpcUaPointLocatorVO pointLocator) {
        try {
            PlcConnection plcConnection = getConnetion();
            return OpcUaUtils.findNamespaceIndex(plcConnection, numberOfAttempts, pointLocator);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return -1;
        }
    }

    public boolean validateTag(OpcUaPointLocatorVO pointLocator, String tag) {
        try {
            PlcConnection plcConnection = getConnetion();
            return OpcUaUtils.validateTag(plcConnection, pointLocator, tag);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public void ping() throws Exception {
        PlcConnection plcConnection = getConnetion();
        if(!plcConnection.isConnected()) {
            throw new IllegalStateException("No connect!");
        }
    }

    private static void doClose(PlcConnection plcConnection) {
        if(plcConnection != null) {
            WorkItem workItem = new PlcConnectionClosingWorkItem(plcConnection);
            Common.ctx.getBackgroundProcessing().addWorkItem(workItem);
        }
    }

    private PlcConnection getConnetion() throws Exception {
        if(plcConnection == null) {
            throw new IllegalStateException("No init!");
        }
        return plcConnection;
    }
}
