package org.scada_lts.web.ws.services;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.ws.model.AlarmLevelMessage;
import org.scada_lts.web.ws.beans.ScadaPrincipal;
import org.scada_lts.web.ws.model.EventMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserEventServiceWebsocket extends AbstractWebSocket {

    private static final Log log = LogFactory.getLog(UserEventServiceWebsocket.class);


    public UserEventServiceWebsocket(SimpMessagingTemplate simpTemplate) {
        super(simpTemplate);
    }

    public void sendEventUpdate(ScadaPrincipal user, EventMessage message) {
        try {
            sendWebSocketMessage("/event/update/" + user.getName(), message);
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
    }

    public void sendEventUpdate(ScadaPrincipal user) {
        try {
            sendWebSocketMessage("/event/update/" + user.getName(), EventMessage.empty());
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
    }

    public void sendAlarmLevel(ScadaPrincipal user, AlarmLevelMessage message) {
        try {
            sendWebSocketMessage("/alarmLevel/" + user.getName(), message);
        } catch (Exception ex) {
            log.warn(ex);
        }
    }

}
