package org.scada_lts.ds.messaging;

import br.org.scadabr.db.utils.TestUtils;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.vo.DataPointVO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

@RunWith(Parameterized.class)
public class MessagingChannelsTest {

    @Parameterized.Parameters(name= "{index}: messagingChannels: {0}")
    public static MessagingChannels<?>[] messagingChannels() {
        return new MessagingChannels<?>[] {
                new SyncMessagingChannels<>(new ConcurrentHashMap<>()),
                new NonSyncMessagingChannels<>(new HashMap<>()),
                new NonSyncMessagingChannels<>(new ConcurrentHashMap<>()),
                new SyncMessagingChannels<>(new HashMap<>())
        };
    }

    private final MessagingChannels<Object> messagingChannels;

    public MessagingChannelsTest(MessagingChannels<Object> messagingChannels) {
        this.messagingChannels = messagingChannels;
    }

    private MessagingChannel<Object> messagingChannelOpenned1;
    private MessagingChannel<Object> messagingChannelClosed;
    private MessagingChannel<Object> messagingChannelOpenned2;
    private MessagingChannel<Object> messagingChannelOpenned3;

    private DataPointRT dataPointRT1;
    private DataPointRT dataPointRT2;
    private DataPointRT dataPointRT3;

    @Before
    public void config() {
        this.messagingChannelOpenned1 = new MessagingChannel<>() {

            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void close(int timeout) throws Exception {}

            @Override
            public Object toOrigin() {
                return null;
            }
        };
        this.messagingChannelClosed = new MessagingChannel<>() {
            @Override
            public boolean isOpen() {
                return false;
            }

            @Override
            public void close(int timeout) throws Exception {}

            @Override
            public Object toOrigin() {
                return null;
            }
        };

        this.messagingChannelOpenned2 = new MessagingChannel<>() {
            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void close(int timeout) throws Exception {}

            @Override
            public Object toOrigin() {
                return null;
            }
        };

        this.messagingChannelOpenned3 = new MessagingChannel<>() {
            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void close(int timeout) throws Exception {}

            @Override
            public Object toOrigin() {
                return null;
            }
        };

        DataPointVO dataPoint1 = TestUtils.newPointSettable(1, -1);
        dataPointRT1 = new DataPointRT(dataPoint1);
        DataPointVO dataPoint2 = TestUtils.newPointSettable(2, -1);
        dataPointRT2 = new DataPointRT(dataPoint2);
        DataPointVO dataPoint3 = TestUtils.newPointSettable(3, -1);
        dataPointRT3 = new DataPointRT(dataPoint3);

    }

    @Before
    public void reset() throws Exception {
        messagingChannels.closeChannels(100);
    }

    @Test
    public void when_removeChannel_then_channels_size_zero() throws Exception {

        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);

        //when:
        messagingChannels.removeChannel(dataPointRT1, 1000);

        //when:
        Assert.assertEquals(0, messagingChannels.size());

    }

    @Test
    public void when_removeChannel_then_isOpenChannel_false() throws Exception {

        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);

        //when:
        messagingChannels.removeChannel(dataPointRT1, 1000);

        //when:
        Assert.assertEquals(false, messagingChannels.isOpenChannel(dataPointRT1));

    }

    @Test
    public void when_removeChannel_one_form_two_then_one_isOpenChannel_true() throws Exception {

        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);
        messagingChannels.initChannel(dataPointRT2, () -> messagingChannelOpenned2);

        //when:
        messagingChannels.removeChannel(dataPointRT1, 1000);

        //when:
        Assert.assertEquals(true, messagingChannels.isOpenChannel(dataPointRT2));
    }

    @Test
    public void when_removeChannel_one_form_two_then_isOpenChannel_false() throws Exception {

        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);
        messagingChannels.initChannel(dataPointRT2, () -> messagingChannelOpenned2);

        //when:
        messagingChannels.removeChannel(dataPointRT1, 1000);

        //when:
        Assert.assertEquals(false, messagingChannels.isOpenChannel(dataPointRT1));
    }

    @Test
    public void when_removeChannel_one_form_two_then_one_size() throws Exception {

        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);
        messagingChannels.initChannel(dataPointRT2, () -> messagingChannelOpenned2);

        //when:
        messagingChannels.removeChannel(dataPointRT1, 1000);

        //when:
        Assert.assertEquals(1, messagingChannels.size());
    }

    @Test
    public void when_removeChannel_and_initChannel_on_other_dataPoints_then_one_size() throws Exception {

        //given:
        messagingChannels.initChannel(dataPointRT2, () -> messagingChannelOpenned2);

        //when:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);
        messagingChannels.removeChannel(dataPointRT1, 1000);

        //when:
        Assert.assertEquals(1, messagingChannels.size());

    }

    @Test
    public void when_removeChannel_and_initChannel_on_other_dataPoints_then_one_isOpenChannel_true() throws Exception {

        //given:
        messagingChannels.initChannel(dataPointRT2, () -> messagingChannelOpenned2);

        //when:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);
        messagingChannels.removeChannel(dataPointRT1, 1000);

        //when:
        Assert.assertEquals(true, messagingChannels.isOpenChannel(dataPointRT2));

    }

    @Test
    public void when_isOpenChannel_after_initChannel_then_true() {
        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);

        //when:
        boolean result = messagingChannels.isOpenChannel(dataPointRT1);

        //when:
        Assert.assertEquals(true, result);
    }

    @Test
    public void when_isOpenChannel_without_initChannel_then_false() {

        //when:
        boolean result = messagingChannels.isOpenChannel(dataPointRT1);

        //when:
        Assert.assertEquals(false, result);
    }

    @Test
    public void when_initChannel_with_three_dataPoints_then_size_three() {

        //when:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);
        messagingChannels.initChannel(dataPointRT2, () -> messagingChannelOpenned2);
        messagingChannels.initChannel(dataPointRT3, () -> messagingChannelOpenned3);

        //then:
        Assert.assertEquals(3, messagingChannels.size());
    }

    @Test
    public void when_initChannel_same_dataPoint_then_size_one() {

        //when:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);

        //then:
        Assert.assertEquals(1, messagingChannels.size());
    }

    @Test
    public void when_getChannel_after_initChannel_then_channel() {

        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);

        //when:
        MessagingChannel<?> result = messagingChannels.getChannel(dataPointRT1).orElse(null);

        //when:
        Assert.assertEquals(messagingChannelOpenned1, result);
    }

    @Test
    public void when_getChannel_without_initChannel_then_channel_null() {

        //when:
        MessagingChannel<?> result = messagingChannels.getChannel(dataPointRT1).orElse(null);

        //when:
        Assert.assertEquals(null, result);
    }

    @Test
    public void when_isOpen_after_initChannel_then_true() {
        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);

        //when:
        boolean result = messagingChannels.isOpen();

        //then:
        Assert.assertEquals(true, result);
    }

    @Test
    public void when_closeChannels_then_size_zero() throws Exception {
        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);
        messagingChannels.initChannel(dataPointRT2, () -> messagingChannelOpenned2);
        messagingChannels.initChannel(dataPointRT3, () -> messagingChannelOpenned3);

        //when:
        messagingChannels.closeChannels(1000);

        //when:
        Assert.assertEquals(0, messagingChannels.size());
    }
}