package org.scada_lts.web.ws.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;

public class WebSocketStatsMonitor {

    private final SubProtocolWebSocketHandler subProtocolWebSocketHandler;
    private final ThreadPoolExecutor clientOutboundChannelExecutor;
    private final SimpUserRegistry userRegistry;

    public WebSocketStatsMonitor(SubProtocolWebSocketHandler subProtocolWebSocketHandler,
                                 ThreadPoolTaskExecutor clientOutboundChannelExecutor,
                                 SimpUserRegistry userRegistry) {
        this.subProtocolWebSocketHandler = subProtocolWebSocketHandler;
        this.clientOutboundChannelExecutor  = clientOutboundChannelExecutor.getThreadPoolExecutor();
        this.userRegistry = userRegistry;
    }
 
    public int getCurrentSessions() {
        return userRegistry.getUsers().stream().mapToInt(a -> a.getSessions().size()).sum();
    }

    public String getStatsInfo() {
        return subProtocolWebSocketHandler.getStatsInfo();
    }

    public String getSendBufferSize() {
        return formatByteCount(subProtocolWebSocketHandler.getSendBufferSizeLimit());
    }
 
    public int getOutboundPoolSize() {
        return clientOutboundChannelExecutor.getPoolSize();
    }
 
    public int getOutboundLargestPoolSize() {
        return clientOutboundChannelExecutor.getLargestPoolSize();
    }
 
    public int getOutboundActiveThreads() {
        return clientOutboundChannelExecutor.getActiveCount();
    }
 
    public int getOutboundQueuedTaskCount() {
        return clientOutboundChannelExecutor.getQueue().size();
    }
 
    public long getOutboundCompletedTaskCount() {
        return clientOutboundChannelExecutor.getCompletedTaskCount();
    }

    public String toPrintUsers() {
        StringBuilder result = new StringBuilder();
        for( SimpUser u : userRegistry.getUsers() ) {
            result.append(u.getName()).append("\n");
            if( u.hasSessions() ) {
                for( SimpSession s : u.getSessions() ) {
                    result.append("\t").append(s.getId()).append("\n");
                    for( SimpSubscription sub : s.getSubscriptions()) {
                        result.append("\t\t").append(sub.getId()).append(": ").append(sub.getDestination()).append("\n");
                    }
                }
            }
        }
        return result.toString();
    }

    public String toPrintStats() {
        String result = "Websocket Stats: \n";
        result += getStatsInfo() + "\n";
        result += "\tCurrent sessions: " + getCurrentSessions() + "\n";
        result += "\tSendBufferSize: " + getSendBufferSize() + "\n";
        result += "\tOutboundPoolSize: " + getOutboundPoolSize() + "\n";
        result += "\tOutboundLargestPoolSize: " + getOutboundLargestPoolSize() + "\n";
        result += "\tOutboundActiveThreads: " + getOutboundActiveThreads() + "\n";
        result += "\tOutboundQueuedTaskCount: " + getOutboundQueuedTaskCount() + "\n";
        result += "\tOutboundCompletedTaskCount: " + getOutboundCompletedTaskCount() + "\n";
        return result;
    }
 
    private static String formatByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), "KMGTPE".charAt(exp - 1));
    }
}
