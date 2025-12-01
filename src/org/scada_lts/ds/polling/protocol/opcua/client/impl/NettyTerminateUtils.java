package org.scada_lts.ds.polling.protocol.opcua.client.impl;

public class NettyTerminateUtils {

    public static void releaseSharedResources() {
        org.eclipse.milo.opcua.stack.core.Stack.releaseSharedResources();
    }
}
