package org.scada_lts.web.ws.config;

import org.scada_lts.web.ws.beans.ScadaPrincipal;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public interface WebsocketSessionListener {

	public void onSubscribe(StompHeaderAccessor sha, ScadaPrincipal principal);
	
	public void onUnsubscribe(StompHeaderAccessor sha, ScadaPrincipal principal);
	
	public void onConnected(StompHeaderAccessor sha, ScadaPrincipal principal);
	
	public void onDisconnected(StompHeaderAccessor sha, ScadaPrincipal principal);
}
