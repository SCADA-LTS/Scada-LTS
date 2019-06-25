package org.scada_lts.serialization;

import com.serotonin.util.SerializationHelper;
import org.junit.Test;
import org.scada_lts.serialization.component.AlarmListComponentV1;
import org.scada_lts.serialization.component.AlarmListComponentV2;

import java.io.*;

import static junit.framework.Assert.assertTrue;

public class SerializationTest {

    @Test
    public void alarmListComponentSerializationTest() throws IOException, ClassNotFoundException {
        AlarmListComponentV1 alarmListComponentV1 = new AlarmListComponentV1();
        alarmListComponentV1.setHideAckColumn(true);
        alarmListComponentV1.setHideAlarmLevelColumn(false);
        alarmListComponentV1.setMaxListSize(3);
        alarmListComponentV1.setWidth(300);
        alarmListComponentV1.setMinAlarmLevel(1);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream dos = new ObjectOutputStream(baos);
        dos.writeObject(alarmListComponentV1);
        dos.close();
        byte[] serialized = baos.toByteArray();


        serialized[65] = 50;


        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serialized));
        AlarmListComponentV2 alarmListComponentV2 = (AlarmListComponentV2) ois.readObject();

        assertTrue(alarmListComponentV2.getMaxListSize() == 3);
    }
}
