package org.scada_lts.web.ws.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurationSupport;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;



@Configuration
//@EnableWebSocketMessageBroker  
//public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
public class WebSocketConfig extends WebSocketMessageBrokerConfigurationSupport  {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/ws-scada");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-scada/").setHandshakeHandler(handshakeHandler())
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS();

        registry.addEndpoint("/ws-scada/alarmLevel").setHandshakeHandler(handshakeHandler())
                .setAllowedOrigins("http://localhost:3000")
                .withSockJS()
                .setStreamBytesLimit(512 * 1024)
                .setHttpMessageCacheSize(1000) 
                .setDisconnectDelay(30 * 1000)
                .setInterceptors(new HttpSessionHandshakeInterceptor()) ;
    }
    
    
    @Override 
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) { 
        registration.setSendTimeLimit(15 * 1000)
                    .setSendBufferSizeLimit(512 * 1024) 
                    .setMessageSizeLimit(128 * 1024);
    } 
    
    @Bean
    public LoggingChannelInterceptor loggingChannelInterceptor() {
        return new LoggingChannelInterceptor();
    }

    @Bean
    public HandshakeHandler handshakeHandler() {
        return new ScadaHandshakeHandler();
    }
    
    @Bean
    public WebSocketMessageBrokerStatsMonitor statsMonitor() {
        return new WebSocketMessageBrokerStatsMonitor((SubProtocolWebSocketHandler) subProtocolWebSocketHandler(), clientOutboundChannelExecutor());
    }
    
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.setInterceptors(loggingChannelInterceptor());
    }
 
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(8);
        registration.setInterceptors(loggingChannelInterceptor());
    }

}
