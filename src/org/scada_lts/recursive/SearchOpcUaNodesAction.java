package org.scada_lts.recursive;

import com.serotonin.mango.Common;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.sdk.core.DataTypeTree;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResult;
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;
import org.scada_lts.ds.polling.protocol.opcua.client.impl.OpcUaUtils;
import org.scada_lts.ds.polling.protocol.opcua.vo.OpcUaIdentifierType;
import org.scada_lts.ds.polling.protocol.opcua.vo.OpcUaPointLocatorVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

import static org.scada_lts.ds.polling.protocol.opcua.client.impl.OpcUaUtils.createLocator;

public class SearchOpcUaNodesAction implements Callable<Void> {

    private static final Logger LOG = LogManager.getLogger(SearchOpcUaNodesAction.class);

    private final NodeId nodeId;
    private final Set<OpcUaPointLocatorVO> result;
    private int searchDepth;
    private final Predicate<OpcUaPointLocatorVO> exclude;
    private final Predicate<OpcUaPointLocatorVO> include;
    private final UaClient client;
    private final DataTypeTree dataTypeTree;

    public SearchOpcUaNodesAction(NodeId nodeId, Set<OpcUaPointLocatorVO> result, int searchDepth, Predicate<OpcUaPointLocatorVO> exclude,
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
    public Void call() {

        if(searchDepth < 0)
            return null;

        BrowseResult browseResult = null;
        try {
            browseResult = OpcUaUtils.sendBrowse(client, nodeId);
        } catch (Exception e) {
            LOG.error("Failed browse nodeId: {}, message: {}", nodeId, e.getMessage());
            return null;
        }

        StatusCode statusCode = browseResult.getStatusCode();
        if(!statusCode.isGood()) {
            LOG.error("Failed browse nodeId: {}, code: {}", nodeId, statusCode);
            return null;
        }

        if(browseResult.getReferences() == null || browseResult.getReferences().length == 0) {
            LOG.info("End browse operation, nodeId: {}", nodeId);
            return null;
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

            tasks.add(new SearchOpcUaNodesAction(NodeId.parse(pointLocator.getNodeId()), result, depth, exclude, include, client, dataTypeTree));
        }

        if(!tasks.isEmpty())
            Common.ctx.getBackgroundProcessing().getCommonPool().invokeAll(tasks);

        return null;
    }
}
