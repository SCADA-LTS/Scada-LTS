package utils;

import com.serotonin.mango.rt.event.AlarmLevels;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.DataPointEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.web.i18n.LocalizableMessage;
import org.joda.time.DateTime;

import java.util.Collections;

public class EventTestUtils {

    public static EventInstance createEventWithActiveTime(int id, DateTime activeTime, LocalizableMessage localizableMessage) {
        EventInstance event = new EventInstance(new DataPointEventType(1,3),
                activeTime.getMillis(), false, AlarmLevels.CRITICAL,
                localizableMessage, Collections.emptyMap());
        event.setId(id);
        return event;
    }

    public static EventInstance createEventWithActiveTime(int id, DateTime activeTime, EventType eventType) {
        EventInstance event = new EventInstance(eventType, activeTime.getMillis(), false, AlarmLevels.CRITICAL,
                new LocalizableMessage("com.test"), Collections.emptyMap());
        event.setId(id);
        return event;
    }

    public static EventHandlerVO createEventHandler(int id, int eventHandlerType) {
        EventHandlerVO emailEventHandler = new EventHandlerVO();
        emailEventHandler.setId(id);
        emailEventHandler.setHandlerType(eventHandlerType);
        return emailEventHandler;
    }
}
