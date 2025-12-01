package org.scada_lts.ds.polling.protocol.opcua.client;

public class NettyTerminateUtils {

    public static void releaseSharedResources() {
        org.eclipse.milo.opcua.stack.core.Stack.releaseSharedResources();
    }
}
