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
import org.scada_lts.ds.polling.exception.MasterException;
import org.scada_lts.ds.polling.protocol.opcua.client.IOpcUaMaster;
import org.scada_lts.ds.polling.service.DataPointReadResponse;
import org.scada_lts.ds.polling.protocol.opcua.vo.*;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Predicate;

import static org.scada_lts.ds.polling.protocol.opcua.client.impl.OpcUaUtils.createLocator;
import static org.scada_lts.ds.polling.protocol.opcua.client.impl.OpcUaUtils.sendReadServerStateAndTime;

public class OpcUaMaster implements IOpcUaMaster {

    private static final Logger LOG = LogManager.getLogger(OpcUaMaster.class);

    private final OpcUaDataSourceVO dataSource;
    private UaClient client;
    private DataTypeTree dataTypeTree;

    public OpcUaMaster(OpcUaDataSourceVO dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void init() throws MasterException {
        this.terminate();
        try {
            OpcUaClient opcUaClient = OpcUaClientFactory.createClient(dataSource);
            this.client = opcUaClient.connect().get(dataSource.getDefaultTimeout(), TimeUnit.MILLISECONDS);
            this.dataTypeTree = DataTypeTreeBuilder.build(opcUaClient);
        } catch (Exception ex) {
            LOG.warn(LoggingUtils.exceptionInfo(ex), ex);
            this.terminate();
            throw new MasterException(ex.getMessage(), ex);
        }
    }

    @Override
    public DataPointReadResponse read(List<DataPointVO> dataPoints, long time) throws MasterException {
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
        ReadResponse readResponse = null;
        try {
            UaClient client = getClient();
            readResponse = OpcUaUtils.sendRead(client, nodeIds);
        } catch (Exception ex) {
            LOG.warn(LoggingUtils.exceptionInfo(ex), ex);
            throw new MasterException(ex.getMessage(), ex);
        } finally {}

        for(int i = 0; i < readResponse.getResults().length; i++) {
            DataValue dataValue = readResponse.getResults()[i];
            DataPointVO dataPointVO = dataPointReadable.get(i);
            OpcUaPointLocatorVO pointLocator = dataPointVO.getPointLocator();

            StatusCode statusCode = dataValue.getStatusCode();
            String dataPointXid = dataPointVO.getXid();

            if (statusCode == null) {
                Variant variant = dataValue.getValue();
                response.add(dataPointXid, new MasterException(getMessage("Read", pointLocator, variant.getValue(), "statusCode is null")));
            } else if(!statusCode.isGood()) {
                Variant variant = dataValue.getValue();
                response.add(dataPointXid, new MasterException(getMessage("Read", pointLocator, variant.getValue(), statusCode.toString())));
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
    public void write(DataPointVO dataPoint, Object value) throws MasterException {
        OpcUaPointLocatorVO pointLocator = dataPoint.getPointLocator();
        if(value == null) {
            throw new MasterException(getMessage("Write", pointLocator, null, "Value is null!"));
        }
        if(value instanceof ImageValue) {
            throw new MasterException(getMessage("Write",pointLocator, value, value.getClass().getName() + " is not supported!"));
        }
        if(!pointLocator.getOpcDataType().validate(value)) {
            throw new MasterException(getMessage("Write",pointLocator, value, value + " value is invalid!"));
        }
        Object valueToSend = null;
        try {
            valueToSend = pointLocator.getOpcDataType().convertToWrite(value);
        } catch (Exception e) {
            throw new MasterException(e.getMessage(), e);
        }
        try {
            UaClient client = getClient();
            NodeId nodeId = NodeId.parse(pointLocator.getNodeId());
            WriteResponse writeResponse = OpcUaUtils.sendWrite(client, nodeId, valueToSend);
            StatusCode responseCode = writeResponse.getResponseHeader().getServiceResult();

            if (!responseCode.isGood()) {
                throw new MasterException(getMessage("Write",pointLocator, valueToSend, responseCode.toString()));
            }

            if(writeResponse.getResults() != null && writeResponse.getResults().length > 0) {
                StatusCode statusCode = writeResponse.getResults()[0];
                if (!statusCode.isGood()) {
                    throw new MasterException(getMessage("Write",pointLocator, valueToSend, statusCode.toString()));
                }
            }

        } catch (Exception ex) {
            LOG.warn(LoggingUtils.exceptionInfo(ex), ex);
            throw new MasterException(ex.getMessage(), ex);
        } finally {
        }
    }

    @Override
    public void ping() throws MasterException {
        try {
            UaClient client = getClient();
            if (client == null) {
                throw new IllegalStateException("No connected!");
            }
            List<DataValue> result = sendReadServerStateAndTime(client);
        } catch (Exception ex) {
            LOG.warn(LoggingUtils.exceptionInfo(ex), ex);
            throw new MasterException(ex.getMessage(), ex);
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

                SearchNodesAction searchNodesAction = new SearchNodesAction(Identifiers.RootFolder,
                        result,
                        searchDepth == 0 ? 3 : searchDepth,
                        item -> false,
                        item -> (identifierType == OpcUaIdentifierType.ALL || identifierType == item.getIdentifierType())
                                && (StringUtils.isEmpty(identifier) || identifier.equalsIgnoreCase(item.getIdentifier()))
                                && (dataType == OpcUaDataType.allType() || dataType == item.getOpcDataType())
                                && (namespaceIndex == -1 || item.getNamespaceIndex() == namespaceIndex),
                        client, dataTypeTree);

                searchNodesAction.compute();

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
    public void terminate() throws MasterException {
        try {
            this.doClose(client);
        } catch (Throwable e) {
            throw new MasterException(e.getMessage(), e);
        }
    }

    @Override
    public void close() throws Exception {
        terminate();
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

    private void doClose(UaClient client) throws MasterException {
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

    private static PointValueTime convertToPointValueTime(long time, OpcUaPointLocatorVO pointLocator, DataValue value) {
        MangoValue mangoValue = null;
        try {
            mangoValue = pointLocator.getOpcDataType().convertToRead(value.getValue().getValue());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new PointValueTime(mangoValue, time);
    }
    
    static class SearchNodesAction extends RecursiveAction {

        private final NodeId nodeId;
        private final Set<OpcUaPointLocatorVO> result;
        private int searchDepth;
        private final Predicate<OpcUaPointLocatorVO> exclude;
        private final Predicate<OpcUaPointLocatorVO> include;
        private final UaClient client;
        private final DataTypeTree dataTypeTree;

        public SearchNodesAction(NodeId nodeId, Set<OpcUaPointLocatorVO> result, int searchDepth, Predicate<OpcUaPointLocatorVO> exclude,
                                 Predicate<OpcUaPointLocatorVO> include, UaClient client, DataTypeTree dataTypeTree) {
            this.exclude = exclude;
            this.include = include;
            this.nodeId = nodeId;
            this.result = result;
            this.searchDepth = searchDepth;
            this.client = client;
            this.dataTypeTree = dataTypeTree;
        }

        @Override
        protected void compute() {

            if(searchDepth < 0)
                return;

            BrowseResult browseResult = null;
            try {
                browseResult = OpcUaUtils.sendBrowse(client, nodeId);
            } catch (Exception e) {
                LOG.error("Failed browse nodeId: {}, message: {}", nodeId, e.getMessage());
                return;
            }

            StatusCode statusCode = browseResult.getStatusCode();
            if(!statusCode.isGood()) {
                LOG.error("Failed browse nodeId: {}, code: {}", nodeId, statusCode);
                return;
            }

            if(browseResult.getReferences() == null || browseResult.getReferences().length == 0) {
                LOG.info("End browse operation, nodeId: {}", nodeId);
                return;
            }

            int depth = --searchDepth;
            List<Callable<Void>> tasks = new ArrayList<>();
            for(ReferenceDescription referenceDescription: browseResult.getReferences()) {

                int namespaceIndex = referenceDescription.getNodeId().getNamespaceIndex().intValue();
                String identifier = String.valueOf(referenceDescription.getNodeId().getIdentifier());
                OpcUaIdentifierType identifierType = OpcUaIdentifierType.valueOf(referenceDescription.getNodeId().getType().getValue());
                OpcUaPointLocatorVO pointLocator = createLocator(namespaceIndex, identifier, identifierType, client, dataTypeTree);

                if(!exclude.test(pointLocator) && include.test(pointLocator)) {
                    result.add(pointLocator);
                }

                tasks.add(() -> new SearchNodesAction(NodeId.parse(pointLocator.getNodeId()), result, depth, exclude, include, client, dataTypeTree).invoke());
            }

            if(!tasks.isEmpty())
                Common.ctx.getBackgroundProcessing().getCommonPool().invokeAll(tasks);
        }
    }
}
