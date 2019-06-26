package org.scada_lts.serialization;

import com.serotonin.util.SerializationHelper;
import org.junit.Test;
import org.scada_lts.serialization.component.AlarmListComponentV1;
import org.scada_lts.serialization.component.AlarmListComponentV2;

import static junit.framework.Assert.assertEquals;

public class SerializationTest {

    private static final int MAX_LIST_SIZE = 3;
    private static final int WIDTH = 300;
    private static final int MIN_ALARM_LEVEL = 1;

    @Test
    public void alarmListComponentSerializationTest() {
        AlarmListComponentV1 alarmListComponentV1 = new AlarmListComponentV1();
        alarmListComponentV1.setHideAckColumn(true);
        alarmListComponentV1.setHideAlarmLevelColumn(false);
        alarmListComponentV1.setMaxListSize(MAX_LIST_SIZE);
        alarmListComponentV1.setWidth(WIDTH);
        alarmListComponentV1.setMinAlarmLevel(MIN_ALARM_LEVEL);

        byte[] serialized = SerializationHelper.writeObjectToArray(alarmListComponentV1);

        serialized[65] = 50;

        AlarmListComponentV2 alarmListComponentV2 = (AlarmListComponentV2) SerializationHelper.readObjectFromArray(serialized);

        assertEquals(alarmListComponentV2.getMaxListSize(), MAX_LIST_SIZE);
        assertEquals(alarmListComponentV2.getWidth(), WIDTH);
        assertEquals(alarmListComponentV2.getMinAlarmLevel(), MIN_ALARM_LEVEL);
    }
}
