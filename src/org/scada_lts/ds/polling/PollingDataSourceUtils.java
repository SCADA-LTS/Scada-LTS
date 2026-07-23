package org.scada_lts.ds.polling;

import org.scada_lts.ds.polling.protocol.opcua.client.impl.NettyTerminateUtils;

public final class PollingDataSourceUtils {

    private PollingDataSourceUtils() {}

    public static void terminateNetty() {
        NettyTerminateUtils.releaseSharedResources();
    }
}
