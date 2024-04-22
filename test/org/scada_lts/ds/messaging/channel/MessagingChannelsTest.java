package org.scada_lts.ds.messaging.channel;

import br.org.scadabr.db.utils.TestUtils;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.vo.DataPointVO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utils.mock.MockUtils;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class MessagingChannelsTest {

    @Parameterized.Parameters(name= "{index}: messagingChannels: {0}")
    public static MessagingChannels[] messagingChannels() {
        return new MessagingChannels[] {
                MessagingChannels.nonSync(new ConcurrentHashMap<>(), 1000),
                MessagingChannels.nonSync(new HashMap<>(), 1000),
                MessagingChannels.sync(new ConcurrentHashMap<>(), 1000),
                MessagingChannels.sync(new HashMap<>(), 1000),
        };
    }

    private final MessagingChannels messagingChannels;

    public MessagingChannelsTest(MessagingChannels messagingChannels) {
        this.messagingChannels = messagingChannels;
    }

    private MessagingChannel messagingChannelOpenned1;
    private MessagingChannel messagingChannelClosed;
    private MessagingChannel messagingChannelOpenned2;
    private MessagingChannel messagingChannelOpenned3;

    private DataPointRT dataPointRT1;
    private DataPointRT dataPointRT2;
    private DataPointRT dataPointRT3;

    @Before
    public void config() {
        this.messagingChannelOpenned1 = new MessagingChannel() {

            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void close(int timeout) {}

            @Override
            public void publish(String message) {}
        };
        this.messagingChannelClosed = new MessagingChannel() {
            @Override
            public boolean isOpen() {
                return false;
            }

            @Override
            public void close(int timeout) {}

            @Override
            public void publish(String message) {}
        };

        this.messagingChannelOpenned2 = new MessagingChannel() {
            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void close(int timeout) {}

            @Override
            public void publish(String message) {}
        };

        this.messagingChannelOpenned3 = new MessagingChannel() {
            @Override
            public boolean isOpen() {
                return true;
            }

            @Override
            public void close(int timeout) {}

            @Override
            public void publish(String message) {}
        };

        DataPointRT dataPointRT1 = mock(DataPointRT.class);
        when(dataPointRT1.getId()).thenReturn(1);
        this.dataPointRT1 = dataPointRT1;

        DataPointRT dataPointRT2 = mock(DataPointRT.class);
        when(dataPointRT2.getId()).thenReturn(2);
        this.dataPointRT2 = dataPointRT2;

        DataPointRT dataPointRT3 = mock(DataPointRT.class);
        when(dataPointRT3.getId()).thenReturn(3);
        this.dataPointRT3 = dataPointRT3;
    }

    @Before
    public void reset() throws Exception {
        messagingChannels.closeChannels();
    }

    @Test
    public void when_removeChannel_then_channels_size_zero() {

        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);

        //when:
        messagingChannels.removeChannel(dataPointRT1);

        //when:
        Assert.assertEquals(0, messagingChannels.size());

    }

    @Test
    public void when_removeChannel_then_isOpenChannel_false() {

        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);

        //when:
        messagingChannels.removeChannel(dataPointRT1);

        //when:
        Assert.assertEquals(false, messagingChannels.isOpenChannel(dataPointRT1));

    }

    @Test
    public void when_removeChannel_one_form_two_then_one_isOpenChannel_true() {

        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);
        messagingChannels.initChannel(dataPointRT2, () -> messagingChannelOpenned2);

        //when:
        messagingChannels.removeChannel(dataPointRT1);

        //when:
        Assert.assertEquals(true, messagingChannels.isOpenChannel(dataPointRT2));
    }

    @Test
    public void when_removeChannel_one_form_two_then_isOpenChannel_false() {

        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);
        messagingChannels.initChannel(dataPointRT2, () -> messagingChannelOpenned2);

        //when:
        messagingChannels.removeChannel(dataPointRT1);

        //when:
        Assert.assertEquals(false, messagingChannels.isOpenChannel(dataPointRT1));
    }

    @Test
    public void when_removeChannel_one_form_two_then_one_size() {

        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);
        messagingChannels.initChannel(dataPointRT2, () -> messagingChannelOpenned2);

        //when:
        messagingChannels.removeChannel(dataPointRT1);

        //when:
        Assert.assertEquals(1, messagingChannels.size());
    }

    @Test
    public void when_removeChannel_and_initChannel_on_other_dataPoints_then_one_size() {

        //given:
        messagingChannels.initChannel(dataPointRT2, () -> messagingChannelOpenned2);

        //when:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);
        messagingChannels.removeChannel(dataPointRT1);

        //when:
        Assert.assertEquals(1, messagingChannels.size());

    }

    @Test
    public void when_removeChannel_and_initChannel_on_other_dataPoints_then_one_isOpenChannel_true() {

        //given:
        messagingChannels.initChannel(dataPointRT2, () -> messagingChannelOpenned2);

        //when:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);
        messagingChannels.removeChannel(dataPointRT1);

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
    public void when_isOpen_after_initChannel_then_true() {
        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);

        //when:
        boolean result = messagingChannels.isOpenConnection();

        //then:
        Assert.assertEquals(true, result);
    }

    @Test
    public void when_closeChannels_then_size_zero() {
        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);
        messagingChannels.initChannel(dataPointRT2, () -> messagingChannelOpenned2);
        messagingChannels.initChannel(dataPointRT3, () -> messagingChannelOpenned3);

        //when:
        messagingChannels.closeChannels();

        //when:
        Assert.assertEquals(0, messagingChannels.size());
    }
}