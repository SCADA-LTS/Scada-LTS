package org.scada_lts.web.ws.config;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;


public class WebSocketMessageBrokerStatsMonitor {
    private final Map<String, WebSocketSession> webSocketSessions;
    private final ThreadPoolExecutor outboundExecutor;
 
    @SuppressWarnings("unchecked")
    public WebSocketMessageBrokerStatsMonitor(SubProtocolWebSocketHandler webSocketHandler, ThreadPoolTaskExecutor outboundTaskExecutor) {
        this.webSocketSessions = (Map<String, WebSocketSession>) new DirectFieldAccessor(webSocketHandler).getPropertyValue("sessions");
        this.outboundExecutor  = outboundTaskExecutor.getThreadPoolExecutor();
    }
 
    public int getCurrentSessions() {
        return webSocketSessions.size();
    }
 
    public String getSendBufferSize() {
        int sendBufferSize = 0;
        for (WebSocketSession session : this.webSocketSessions.values()) {
            ConcurrentWebSocketSessionDecorator concurrentSession = (ConcurrentWebSocketSessionDecorator) session;
            sendBufferSize += concurrentSession.getBufferSize();
        }
        return formatByteCount(sendBufferSize);
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
    }
 
}
