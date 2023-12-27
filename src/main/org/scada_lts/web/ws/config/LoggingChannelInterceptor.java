package org.scada_lts.web.ws.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;


@Component
public class LoggingChannelInterceptor extends ChannelInterceptorAdapter  {
    private final Log LOG = LogFactory.getLog(LoggingChannelInterceptor.class);
    
    
    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);
        
        // skip non-STOMP messages 
        if(sha.getCommand() == null) {
            return;
        }
        
        String sessionId = sha.getSessionId();
        LOG.info("STOMP SEND " +  sha.getCommand().toString() + " [sessionId: " + sessionId + "] - " + (sent? "sent" : "fail"));
        for( String hdr : sha.getMessageHeaders().keySet() ) {
            LOG.debug("\t" + hdr + ": " + sha.getMessageHeaders().get(hdr).toString());
        }
    }
    
    @Override 
    public Message<?> postReceive(Message<?> message,  MessageChannel channel) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(message);
        
        if(sha.getCommand() == null) {
            return message;
        }
        
        String sessionId = sha.getSessionId();
        LOG.info("STOMP RECEIVE " +  sha.getCommand().toString() + " [sessionId: " + sessionId + "]");
        for( String hdr : sha.getMessageHeaders().keySet() ) {
            LOG.debug("\t" + hdr + ": " + sha.getMessageHeaders().get(hdr).toString());
        }
        return message;
    }

}


