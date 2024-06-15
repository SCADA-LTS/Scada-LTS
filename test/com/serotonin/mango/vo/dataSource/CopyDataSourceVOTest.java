package com.serotonin.mango.vo.dataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.ds.messaging.protocol.amqp.AmqpDataSourceVO;
import org.scada_lts.ds.messaging.protocol.mqtt.MqttDataSourceVO;
import org.scada_lts.utils.ReflectionTestUtils;

import static org.junit.Assert.*;


@RunWith(Parameterized.class)
public class CopyDataSourceVOTest {

    @Parameterized.Parameters(name= "{index}: dataSource: {1}")
    public static Object[][] dataSource() {
        return new Object[][] {
                {
                    new AmqpDataSourceVO(), AmqpDataSourceVO.class.getName()
                },
                {
                    new MqttDataSourceVO(), MqttDataSourceVO.class.getName()
                }
        };
    }

    private final DataSourceVO<?> expected;

    public CopyDataSourceVOTest(DataSourceVO<?> dataSource, String dataSourceTypeName) {
        this.expected = (DataSourceVO<?>) ReflectionTestUtils.settingRandom(dataSource);
    }

    @Test
    public void when_copy_then_equals() {

        //when:
        DataSourceVO<?> result = expected.copy();

        //then:
        assertEquals(expected, result);
    }

    @Test
    public void when_copy_then_other_reference() {

        //when:
        DataSourceVO<?> result = expected.copy();
        //then:
        assertNotSame(expected, result);
    }

    @Test
    public void when_copy_then_same_class() {

        //when:
        DataSourceVO<?> result = expected.copy();

        //then:
        assertEquals(expected.getClass(), result.getClass());
    }

    @Test
    public void when_copy_and_getConnectionDescription_then_other_reference() {

        //when:
        DataSourceVO<?> result = expected.copy();

        //then:
        assertNotSame(expected.getConnectionDescription(), result.getConnectionDescription());
    }

    @Test
    public void when_copy_and_getConnectionDescription_then_not_null() {

        //when:
        DataSourceVO<?> result = expected.copy();

        //then:
        assertNotNull(result.getConnectionDescription());
    }

    @Test
    public void when_copy_and_getEventTypes_then_other_reference() {

        //when:
        DataSourceVO<?> result = expected.copy();

        //then:
        assertNotSame(expected.getEventTypes(), result.getEventTypes());
    }

    @Test
    public void when_copy_and_getEventTypes_then_not_null() {

        //when:
        DataSourceVO<?> result = expected.copy();

        //then:
        assertNotNull(result.getEventTypes());
    }

    @Test
    public void when_copy_and_getState_then_other_reference() {

        //when:
        DataSourceVO<?> result = expected.copy();

        //then:
        assertNotSame(expected.getState(), result.getState());
    }

    @Test
    public void when_copy_and_getState_then_not_null() {

        //when:
        DataSourceVO<?> result = expected.copy();

        //then:
        assertNotNull(result.getState());
    }
}