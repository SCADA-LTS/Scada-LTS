package org.scada_lts.web.ws.auth;

import org.scada_lts.web.ws.config.ScadaHandshakeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.ExpiringSession;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.HandshakeHandler;

@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class WebSocketConfig<S extends ExpiringSession> extends AbstractSessionWebSocketMessageBrokerConfigurer<S> {

    @Override
    protected void configureStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-scada").withSockJS();
        registry.addEndpoint("/ws-scada/event").withSockJS();
        registry.addEndpoint("/ws-scada/alarmLevel").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue/", "/topic/");
        registry.setApplicationDestinationPrefixes("/app");
    }
/*
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptorAdapter(webSocketAuthenticatorService()));
        super.configureClientInboundChannel(registration);
    }*/

    @Bean
    public HandshakeHandler handshakeHandler() {
        return new ScadaHandshakeHandler();
    }
/*
    @Bean
    public LoggingChannelInterceptor loggingChannelInterceptor() {
        return new LoggingChannelInterceptor();
    }

    @Bean
    public WebSocketAuthenticatorService webSocketAuthenticatorService() {
        return new WebSocketAuthenticatorService();
    }

    @Bean
    public ChannelInterceptor authChannelInterceptorAdapter(WebSocketAuthenticatorService webSocketAuthenticatorService) {
        return new AuthChannelInterceptorAdapter(webSocketAuthenticatorService);
    }*/

}