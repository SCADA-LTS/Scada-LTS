package org.scada_lts.ds.polling.protocol.opcua.client.impl;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.maint.work.ClosingWorkItem;
import com.serotonin.mango.rt.maint.work.WorkItem;
import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.DataTypeTreeBuilder;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.sdk.core.DataTypeTree;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.scada_lts.ds.polling.exception.PollingServiceException;
import org.scada_lts.ds.polling.protocol.opcua.client.IOpcUaService;
import org.scada_lts.ds.polling.service.DataPointReadResponse;
import org.scada_lts.ds.polling.protocol.opcua.vo.*;
import org.scada_lts.recursive.SearchOpcUaNodesAction;

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.*;

import static org.scada_lts.ds.polling.protocol.opcua.client.impl.OpcUaUtils.createLocator;
import static org.scada_lts.ds.polling.protocol.opcua.client.impl.OpcUaUtils.sendReadServerStateAndTime;

public class OpcUaService implements IOpcUaService {

    private static final Logger LOG = LogManager.getLogger(OpcUaService.class);

    private final OpcUaDataSourceVO dataSource;
    private UaClient client;
    private DataTypeTree dataTypeTree;

    public OpcUaService(OpcUaDataSourceVO dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void initialize() throws PollingServiceException {
        this.terminate();
        try {
            OpcUaClient opcUaClient = OpcUaClientFactory.createClient(dataSource);
            this.client = opcUaClient.connect().get(dataSource.getDefaultTimeout(), TimeUnit.MILLISECONDS);
            this.dataTypeTree = DataTypeTreeBuilder.build(opcUaClient);
        } catch (Exception ex) {
            LOG.warn(LoggingUtils.exceptionInfo(ex), ex);
            this.terminate();
            throw new PollingServiceException(ex.getMessage(), ex);
        }
    }

    @Override
    public DataPointReadResponse read(List<DataPointVO> dataPoints, long time) throws PollingServiceException {
        DataPointReadResponse response = new DataPointReadResponse();

        List<NodeId> nodeIds = new ArrayList<>();
        List<DataPointVO> dataPointReadable = new ArrayList<>();
        for(DataPointVO dataPointVO: dataPoints) {
            OpcUaPointLocatorVO pointLocatorVO = dataPointVO.getPointLocator();
            try {
                NodeId nodeId = NodeId.parse(pointLocatorVO.getNodeId());
                nodeIds.add(nodeId);
                dataPointReadable.add(dataPointVO);
            } catch (Throwable throwable) {
                response.add(dataPointVO.getXid(), throwable);
            }
        }
        if(nodeIds.size() != dataPointReadable.size()) {
            for(DataPointVO dataPointVO: dataPointReadable) {
                response.add(dataPointVO.getXid(), new PollingServiceException(getMessage("Read", dataPointVO.getPointLocator(), "null", "The node ids number is different than the number of points for which we want to retrieve the value.")));
            }
            return response;
        }
        ReadResponse readResponse = null;
        try {
            UaClient client = getClient();
            readResponse = OpcUaUtils.sendRead(client, nodeIds);
        } catch (Exception ex) {
            LOG.warn(LoggingUtils.exceptionInfo(ex), ex);
            throw new PollingServiceException(ex.getMessage(), ex);
        } finally {}

        if(readResponse.getResults().length != dataPointReadable.size()) {
            for(DataPointVO dataPointVO: dataPointReadable) {
                response.add(dataPointVO.getXid(), new PollingServiceException(getMessage("Read", dataPointVO.getPointLocator(), "null", "The number of results returned is different from the number of points for which a value was retrieved.")));
            }
            return response;
        }

        for(int i = 0; i < readResponse.getResults().length; i++) {
            DataValue dataValue = readResponse.getResults()[i];
            DataPointVO dataPointVO = dataPointReadable.get(i);
            OpcUaPointLocatorVO pointLocator = dataPointVO.getPointLocator();

            StatusCode statusCode = dataValue.getStatusCode();
            String dataPointXid = dataPointVO.getXid();

            if (statusCode == null) {
                Variant variant = dataValue.getValue();
                response.add(dataPointXid, new PollingServiceException(getMessage("Read", pointLocator, variant.getValue(), "statusCode is null")));
            } else if(!statusCode.isGood()) {
                Variant variant = dataValue.getValue();
                response.add(dataPointXid, new PollingServiceException(getMessage("Read", pointLocator, variant.getValue(), statusCode.toString())));
            } else {
                try {
                    PointValueTime pointValueTime = convertToPointValueTime(time, pointLocator, dataValue);
                    response.add(dataPointXid, pointValueTime);
                } catch (Throwable throwable) {
                    response.add(dataPointXid, throwable);
                }
            }
        }

        return response;
    }

    @Override
    public void write(DataPointVO dataPoint, Object value) throws PollingServiceException {
        OpcUaPointLocatorVO pointLocator = dataPoint.getPointLocator();
        if(value == null) {
            throw new PollingServiceException(getMessage("Write", pointLocator, null, "Value is null!"));
        }
        if(value instanceof ImageValue) {
            throw new PollingServiceException(getMessage("Write",pointLocator, value, value.getClass().getName() + " is not supported!"));
        }
        if(!pointLocator.getOpcDataType().validate(value)) {
            throw new PollingServiceException(getMessage("Write",pointLocator, value, value + " value is invalid!"));
        }
        Object valueToSend = null;
        try {
            valueToSend = pointLocator.getOpcDataType().convertToWrite(value);
        } catch (Exception e) {
            throw new PollingServiceException(e.getMessage(), e);
        }
        try {
            UaClient client = getClient();
            NodeId nodeId = NodeId.parse(pointLocator.getNodeId());
            WriteResponse writeResponse = OpcUaUtils.sendWrite(client, nodeId, valueToSend);
            StatusCode responseCode = writeResponse.getResponseHeader().getServiceResult();

            if (!responseCode.isGood()) {
                throw new PollingServiceException(getMessage("Write",pointLocator, valueToSend, responseCode.toString()));
            }

            if(writeResponse.getResults() != null && writeResponse.getResults().length > 0) {
                StatusCode statusCode = writeResponse.getResults()[0];
                if (!statusCode.isGood()) {
                    throw new PollingServiceException(getMessage("Write",pointLocator, valueToSend, statusCode.toString()));
                }
            }

        } catch (Exception ex) {
            LOG.warn(LoggingUtils.exceptionInfo(ex), ex);
            throw new PollingServiceException(ex.getMessage(), ex);
        } finally {
        }
    }

    @Override
    public void ping() throws PollingServiceException {
        try {
            UaClient client = getClient();
            if (client == null) {
                throw new IllegalStateException("No connected!");
            }
            List<DataValue> result = sendReadServerStateAndTime(client);
        } catch (Exception ex) {
            LOG.warn(LoggingUtils.exceptionInfo(ex), ex);
            throw new PollingServiceException(ex.getMessage(), ex);
        } finally {
        }
    }

    @Override
    public List<OpcUaPointLocatorVO> browse(OpcUaPointLocatorVO pointLocator, int searchDepth, Comparator<OpcUaPointLocatorVO> comparator) {
        String identifier = pointLocator.getIdentifier();
        OpcUaIdentifierType identifierType = pointLocator.getIdentifierType();
        OpcUaDataType dataType = pointLocator.getOpcDataType();
        int namespaceIndex = pointLocator.getNamespaceIndex();
        if(StringUtils.isEmpty(identifier) || identifierType == OpcUaIdentifierType.ALL || namespaceIndex == -1) {
            try {
                if (client == null) {
                    throw new IllegalStateException("No connected!");
                }
                Set<OpcUaPointLocatorVO> result = new CopyOnWriteArraySet<>();

                SearchOpcUaNodesAction searchNodes = new SearchOpcUaNodesAction(Identifiers.RootFolder,
                        result,
                        searchDepth == 0 ? 3 : searchDepth,
                        item -> false,
                        item -> (identifierType == OpcUaIdentifierType.ALL || identifierType == item.getIdentifierType())
                                && (StringUtils.isEmpty(identifier) || identifier.equalsIgnoreCase(item.getIdentifier()))
                                && (dataType == OpcUaDataType.allType() || dataType == item.getOpcDataType())
                                && (namespaceIndex == -1 || item.getNamespaceIndex() == namespaceIndex),
                        client, dataTypeTree);

                searchNodes.call();

                List<OpcUaPointLocatorVO> items = new ArrayList<>(result);
                items.sort(comparator);
                return items;
            } catch (Exception ex) {
                LOG.warn(LoggingUtils.exceptionInfo(ex), ex);
                return Collections.emptyList();
            } finally {
            }
        } else {
            OpcUaPointLocatorVO result = createLocator(namespaceIndex, identifier, identifierType, client, dataTypeTree);
            return List.of(result);
        }
    }

    @Override
    public void terminate() throws PollingServiceException {
        try {
            this.doClose(client);
        } catch (Throwable e) {
            throw new PollingServiceException(e.getMessage(), e);
        }
    }

    @Override
    public boolean validate(OpcUaPointLocatorVO pointLocator) {
        try {
            if(pointLocator.getOpcDataType() == OpcUaDataType.unknownType()) {
                return false;
            }
            NodeId nodeId = NodeId.parse(pointLocator.getNodeId());
            ReadResponse readResponse = OpcUaUtils.sendRead(client, nodeId);
            ResponseHeader handler = readResponse.getResponseHeader();
            if(!handler.getServiceResult().isGood())
                return false;
            if(readResponse.getResults()[0].getStatusCode() == null)
                return false;
            return readResponse.getResults()[0].getStatusCode().isGood();
        } catch (Exception ex) {
            LOG.warn(LoggingUtils.exceptionInfo(ex), ex);
            return false;
        } finally {
        }
    }

    @Override
    public String getName() {
        return "[OPC UA] ";
    }

    private void doClose(UaClient client) throws PollingServiceException {
        if(client != null) {
            UaClient client1 = this.client;
            WorkItem workItem = new ClosingWorkItem(new AutoCloseable() {
                @Override
                public void close() throws Exception {
                    client1.disconnect().get(dataSource.getDefaultTimeout(), TimeUnit.MILLISECONDS);
                }
            }, "[OPC UA] Closed connection for: " + LoggingUtils.dataSourceInfo(dataSource));
            Common.ctx.getBackgroundProcessing().addWorkItem(workItem);
        }
    }

    private UaClient getClient() throws Exception {
        if(client == null) {
            throw new IllegalStateException("No init!");
        }
        return client;
    }

    private static String getMessage(String operation, OpcUaPointLocatorVO pointLocator, Object value, String message) {
        return MessageFormat.format("Failed {0} nodeId: {1}, data type: {2}, name: {3}, value: {4}, message: {5}", operation, pointLocator.getNodeId(), pointLocator.getOpcDataType(), pointLocator.getNodeName(), value, message);
    }

    private static String getMessage(String operation, String message) {
        return MessageFormat.format("Failed {0} message: {1}", operation, message);
    }

    private static PointValueTime convertToPointValueTime(long time, OpcUaPointLocatorVO pointLocator, DataValue dataValue) {

        MangoValue mangoValue = null;
        try {
            Variant variant = dataValue.getValue();
            Object valueRaw = variant.getValue();
            if(valueRaw.getClass().isArray()) {
                Object value = Array.get(valueRaw, 0);
                mangoValue = pointLocator.getOpcDataType().convertToRead(value);
            } else {
                mangoValue = pointLocator.getOpcDataType().convertToRead(valueRaw);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new PointValueTime(mangoValue, time);
    }
}
