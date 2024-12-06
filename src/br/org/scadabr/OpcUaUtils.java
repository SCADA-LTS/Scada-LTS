package br.org.scadabr;

import br.org.scadabr.vo.dataSource.opcua.OpcUaPointLocatorVO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.*;
import org.apache.plc4x.java.api.types.PlcResponseCode;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class OpcUaUtils {

    private static final Log LOG = LogFactory.getLog(OpcUaUtils.class);

    public OpcUaUtils() {
    }

    public static boolean validateTag(PlcConnection connection, OpcUaPointLocatorVO locator, String tag) throws ExecutionException, InterruptedException {
        String nodeId = locator.getNodeId(); // Replace with the actual node ID

        // Create a read request
        PlcReadRequest.Builder builder = connection.readRequestBuilder();
        builder.addTagAddress(tag, nodeId); // Add the node ID directly

        PlcReadRequest readRequest = builder.build();
        PlcReadResponse response = readRequest.execute().get();

        return response.getResponseCode(tag) == PlcResponseCode.OK;
    }

    public static int findNamespaceIndex(PlcConnection connection, int numberOfAttempts, OpcUaPointLocatorVO opcUaPointLocatorVO) {
        PlcReadResponse response;
        String tag = opcUaPointLocatorVO.getTag();
        OpcUaPointLocatorVO copied = opcUaPointLocatorVO.copy();
        for(int i = 0; i < numberOfAttempts; i++) {
            copied.setNamespaceIndex(i);
            String nodeId = copied.getNodeId();
            try {
                response = sendRead(connection, tag, nodeId);
                if(response != null && response.getResponseCode(tag) == PlcResponseCode.OK) {
                    Object value = read(response, tag);
                    LOG.info("tag: " + tag + ", nodeId: " + nodeId + ", value: " + value);
                    return i;
                }
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                break;
            }

        }
        return -1;
    }

    public static Object read(PlcReadResponse response, String varName) {
        if (response.getResponseCode(varName) == PlcResponseCode.OK) {
            return response.getObject(varName);
        } else {
            return null;
        }
    }

    public static PlcReadResponse sendRead(PlcConnection connection, String tag, String nodeId) throws InterruptedException, ExecutionException {
        PlcReadRequest.Builder builder = connection.readRequestBuilder();
        builder.addTagAddress(tag, nodeId); // Add the node ID directly
        PlcReadRequest readRequest = builder.build();
        return readRequest.execute().get();
    }

    public static PlcWriteResponse sendWrite(PlcConnection connection, String tag, String nodeId, Object value) throws InterruptedException, ExecutionException {
        PlcWriteRequest.Builder builder = connection.writeRequestBuilder();
        builder.addTagAddress(tag, nodeId, value);
        PlcWriteRequest readRequest = builder.build();
        return readRequest.execute().get();
    }

    public static PlcSubscriptionResponse sendSubscribe(PlcConnection connection, String tag, String nodeId) throws InterruptedException, ExecutionException {
        PlcSubscriptionRequest.Builder builder = connection.subscriptionRequestBuilder();
        builder.addChangeOfStateTagAddress(tag, nodeId);
        builder.addPreRegisteredConsumer(tag, new Consumer<PlcSubscriptionEvent>() {
            @Override
            public void accept(PlcSubscriptionEvent plcSubscriptionEvent) {
                System.out.println("sub value");
                Object value = plcSubscriptionEvent.getObject(tag);
                System.out.println("sub value: " + value);
            }
        });
        PlcSubscriptionRequest readRequest = builder.build();
        return readRequest.execute().get();
    }

    public static PlcReadResponse sendPing(PlcConnection connection, String tag, String nodeId) throws InterruptedException, ExecutionException, TimeoutException {
        return sendRead(connection, tag, nodeId);
    }

    private static PlcBrowseResponse sendBrowse(PlcConnection connection) throws InterruptedException, ExecutionException {
        PlcBrowseRequest.Builder builder = connection.browseRequestBuilder();
        PlcBrowseRequest readRequest = builder.build();
        return readRequest.execute().get();
    }
}
