package org.scada_lts.ds.polling.protocol.opcua.client.impl;


import com.serotonin.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.nodes.UaObjectNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.core.DataTypeTree;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseResultMask;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.*;
import org.scada_lts.ds.polling.protocol.opcua.vo.OpcUaDataType;
import org.scada_lts.ds.polling.protocol.opcua.vo.OpcUaIdentifierType;
import org.scada_lts.ds.polling.protocol.opcua.vo.OpcUaPointLocatorVO;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

public final class OpcUaUtils {

    private static final Logger LOG = LogManager.getLogger(OpcUaUtils.class);

    private OpcUaUtils() { }

    public static ReadResponse sendRead(UaClient client, NodeId nodeId) throws InterruptedException, ExecutionException, TimeoutException {
        ReadValueId valueId = new ReadValueId(nodeId, AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);
        long requestTimeout = client.getConfig().getRequestTimeout().longValue();
        return client.read(0.0, TimestampsToReturn.Source, Arrays.asList(valueId)).get(requestTimeout, TimeUnit.MILLISECONDS);
    }

    public static ReadResponse sendRead(UaClient client, List<NodeId> nodeIds) throws InterruptedException, ExecutionException, TimeoutException {

        List<ReadValueId> reads = new ArrayList<>();
        for(NodeId nodeId: nodeIds) {
            ReadValueId valueId = new ReadValueId(nodeId, AttributeId.Value.uid(), null, QualifiedName.NULL_VALUE);
            reads.add(valueId);
        }
        long requestTimeout = client.getConfig().getRequestTimeout().longValue();
        return client.read(0.0, TimestampsToReturn.Source, reads).get(requestTimeout, TimeUnit.MILLISECONDS);
    }

    public static WriteResponse sendWrite(UaClient client, NodeId nodeId, Object value) throws InterruptedException, ExecutionException, TimeoutException {
        Variant valueToSave = toVariant(value);
        WriteValue readValueId = new WriteValue(nodeId, AttributeId.Value.uid(), null, new DataValue(valueToSave, null, null));
        long requestTimeout = client.getConfig().getRequestTimeout().longValue();
        WriteResponse response = client.write(Arrays.asList(readValueId)).get(requestTimeout, TimeUnit.SECONDS);
        return response;
    }

    public static BrowseResult sendBrowse(UaClient client, NodeId browseRoot) throws ExecutionException, InterruptedException, TimeoutException {
        BrowseDescription browse = new BrowseDescription(
                browseRoot,
                BrowseDirection.Forward,
                Identifiers.References,
                true,
                uint(NodeClass.Object.getValue() | NodeClass.Variable.getValue() | NodeClass.Unspecified.getValue()),
                uint(BrowseResultMask.All.getValue())
        );
        OpcUaClientConfig clientConfig = client.getConfig();
        long requestTimeout = clientConfig.getRequestTimeout().longValue();
        return client.browse(browse).get(requestTimeout, TimeUnit.MILLISECONDS);
    }

    public static List<DataValue> sendReadServerStateAndTime(UaClient client) throws ExecutionException, InterruptedException, TimeoutException {
        List<NodeId> nodeIds = List.of(
                Identifiers.Server_ServerStatus_State,
                Identifiers.Server_ServerStatus_CurrentTime);
        long requestTimeout = client.getConfig().getRequestTimeout().longValue();
        return client.readValues(0.0, TimestampsToReturn.Both, nodeIds).get(requestTimeout, TimeUnit.MILLISECONDS);
    }

    public static String getNodeId(int namespaceIndex, String identifier, OpcUaIdentifierType identifierType) {
        return getNodeId(namespaceIndex, identifier, identifierType, "");
    }

    public static String getNodeId(int namespaceIndex, String identifier, OpcUaIdentifierType identifierType, String attributes) {
        if (StringUtils.isEmpty(attributes)) {
            return MessageFormat.format("ns={0};{1}={2}", namespaceIndex, identifierType.getCode(), identifier);
        }
        return MessageFormat.format("ns={0};{1}={2};{3}", namespaceIndex, identifierType.getCode(), identifier, attributes);
    }

    public static OpcUaPointLocatorVO createLocator(int namespaceIndex,
                                                    String identifier,
                                                    OpcUaIdentifierType identifierType,
                                                    UaClient client,
                                                    DataTypeTree dataTypeTree) {

        String nodeId = OpcUaUtils.getNodeId(namespaceIndex, identifier, identifierType);
        NodeId node = NodeId.parse(nodeId);
        String nodeName = "Unknown";
        OpcUaDataType baseDataType = OpcUaDataType.unknownType();
        boolean settable = false;
        try {
            UaVariableNode variableNode = client.getAddressSpace().getVariableNode(node);
            nodeName = variableNode.getDisplayName().getText();

            NodeId dataType = variableNode.getDataType();
            Class<?> type = dataTypeTree.getBackingClass(dataType);
            baseDataType = OpcUaDataType.valueOf(type);

            UByte accessLevel = variableNode.getUserAccessLevel();
            settable = baseDataType.isPossibleSettable() && (accessLevel.intValue() == AccessLevel.CurrentWrite.getValue() || accessLevel.intValue() == (AccessLevel.CurrentWrite.getValue() + AccessLevel.CurrentRead.getValue()));
        } catch (Exception e) {
            try {
                UaObjectNode objectNode = client.getAddressSpace().getObjectNode(node);
                nodeName = objectNode.getDisplayName().getText();

                NodeId dataType = objectNode.getTypeDefinition().getNodeId();
                Class<?> type = dataTypeTree.getBackingClass(dataType);
                baseDataType = OpcUaDataType.valueOf(type);
                settable = false;
            } catch (Exception ex) {
                LOG.error("Failed browse nodeId: {}, message: {}", node, ex.getMessage());
            }
        }

        OpcUaPointLocatorVO pointLocator = new OpcUaPointLocatorVO();
        pointLocator.setNamespaceIndex(namespaceIndex);
        pointLocator.setIdentifier(identifier);
        pointLocator.setIdentifierType(identifierType);
        pointLocator.setNodeName(nodeName);
        pointLocator.setOpcDataType(baseDataType);
        pointLocator.setSettable(settable);
        return pointLocator;
    }

    private static Variant toVariant(Object value) {
        Variant toSave;
        if(value instanceof Variant) {
            toSave = (Variant) value;
        } else {
            toSave = new Variant(value);
        }
        return toSave;
    }
}
