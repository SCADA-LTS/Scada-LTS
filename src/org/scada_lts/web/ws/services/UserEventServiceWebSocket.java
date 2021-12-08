package org.scada_lts.web.ws.services;


import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.ws.model.AlarmLevelMessage;
import org.scada_lts.web.ws.model.WsEventMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserEventServiceWebSocket extends AbstractWebSocket {

    private static final Log log = LogFactory.getLog(UserEventServiceWebSocket.class);

    public UserEventServiceWebSocket(SimpMessagingTemplate simpTemplate) {
        super(simpTemplate);
    }

    public void sendEventUpdate(User user, WsEventMessage message) {
        try {
            sendWebSocketMessage("/event/update/" + user.getUsername(), message);
        } catch (Exception ex) {
            log.warn(ex.getMessage(), ex);
        }
    }

    public void sendAlarmLevel(User user, AlarmLevelMessage message) {
        try {
            sendWebSocketMessage("/alarmLevel/" + user.getUsername(), message);
        } catch (Exception ex) {
            log.warn(ex);
        }
    }

}
