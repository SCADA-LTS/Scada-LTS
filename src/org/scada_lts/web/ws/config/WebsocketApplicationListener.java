package org.scada_lts.web.ws.config;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.ws.beans.ScadaPrincipal;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;


@Component
public class WebsocketApplicationListener {
    private final Log LOG = LogFactory.getLog(WebsocketApplicationListener.class);
    
    List<WebsocketSessionListener> listeners = new ArrayList<WebsocketSessionListener>();
    
    
    @EventListener
    public void onSessionConnect(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        log("connect", event.getMessage().getHeaders());
    }

    @EventListener
    public void onSessionConnected(SessionConnectedEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        log("connected", sha);
        fireConnectedEvent(sha, (ScadaPrincipal) event.getUser());
    }
    
    @EventListener
    public void onSessionDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        log("disconnected", sha);
        fireDisconnectedEvent(sha, (ScadaPrincipal) event.getUser());
    }
    
    @EventListener
    public void onSessionSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        log("subscribe", event.getMessage().getHeaders());
        fireSubscribeEvent(sha, (ScadaPrincipal) event.getUser());
    }
    
    @EventListener
    public void onSessionUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        log("unsubscribe", sha);
        fireUnsubscribeEvent(sha, (ScadaPrincipal) event.getUser());
    }
    
    
    public void addListener(WebsocketSessionListener listener) {
    	listeners.add(listener);
    }
    
    public void removeListener(WebsocketSessionListener listener) {
    	listeners.remove(listener);
    }

    private void fireSubscribeEvent(StompHeaderAccessor sha, ScadaPrincipal user) {
    	for(WebsocketSessionListener wsl : listeners) {
    		wsl.onSubscribe(sha, user);
    	}
    }
    
    private void fireUnsubscribeEvent(StompHeaderAccessor sha, ScadaPrincipal user) {
    	for(WebsocketSessionListener wsl : listeners) {
    		wsl.onUnsubscribe(sha, user);
    	}
    }
    
    private void fireConnectedEvent(StompHeaderAccessor sha, ScadaPrincipal user) {
    	for(WebsocketSessionListener wsl : listeners) {
    		wsl.onConnected(sha, user);
    	}
    }
    
    private void fireDisconnectedEvent(StompHeaderAccessor sha, ScadaPrincipal user) {
    	for(WebsocketSessionListener wsl : listeners) {
    		wsl.onDisconnected(sha, user);
    	}
    }
    
    private void log(String event, StompHeaderAccessor sha) {
        String sessionId = sha.getSessionId();
        String destination = sha.getDestination();
        String user = sha.getUser() == null ? "<empty>" : sha.getUser().getName();
        sessionId = sessionId == null ? "<null>" : sessionId;
        destination = destination == null ? "<null>" : destination;
        user = user == null ? "<null>" : user;
        LOG.info(event + " - sessionId: " + sessionId + ", destination: " + destination + ", user: " + user);
    }

    private void log(String event, MessageHeaders headers) {
        String sessionId = SimpMessageHeaderAccessor.getSessionId(headers);
        String destination = SimpMessageHeaderAccessor.getDestination(headers);
        String user = SimpMessageHeaderAccessor.getUser(headers) == null ? "<empty>" : SimpMessageHeaderAccessor.getUser(headers).getName();
        sessionId = sessionId == null ? "<null>" : sessionId;
        destination = destination == null ? "<null>" : destination;
        user = user == null ? "<null>" : user;
        LOG.info(event + " - sessionId: " + sessionId + ", destination: " + destination + ", user: " + user);
    }
    
}
