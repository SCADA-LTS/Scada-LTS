package org.scada_lts.web.ws.config;

import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

@Component
public class WebSocketMessageBrokerStatsMonitor {
    /*private final SubProtocolWebSocketHandler webSocketSessions;
    private final ThreadPoolExecutor outboundExecutor;
 
    @SuppressWarnings("unchecked")
    public WebSocketMessageBrokerStatsMonitor(SubProtocolWebSocketHandler webSocketHandler, ThreadPoolTaskExecutor outboundTaskExecutor) {
        this.webSocketSessions = webSocketHandler;
        this.outboundExecutor  = outboundTaskExecutor.getThreadPoolExecutor();
    }
 
    public int getCurrentSessions() {
        return webSocketSessions.getProtocolHandlers().size();
    }
 
    public String getSendBufferSize() {
        return formatByteCount(webSocketSessions.getSendBufferSizeLimit());
    }
 
    public int getOutboundPoolSize() {
        return outboundExecutor.getPoolSize();
    }
 
    public int getOutboundLargestPoolSize() {
        return outboundExecutor.getLargestPoolSize();
    }
 
    public int getOutboundActiveThreads() {
        return outboundExecutor.getActiveCount();
    }
 
    public int getOutboundQueuedTaskCount() {
        return outboundExecutor.getQueue().size();
    }
 
    public long getOutboundCompletedTaskCount() {
        return outboundExecutor.getCompletedTaskCount();
    }
 
    private static String formatByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), "KMGTPE".charAt(exp - 1));
    }*/
 
}
