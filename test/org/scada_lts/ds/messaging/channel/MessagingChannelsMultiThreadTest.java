package org.scada_lts.ds.messaging.channel;

import br.org.scadabr.db.utils.TestUtils;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.vo.DataPointVO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utils.TestConcurrentUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@RunWith(Parameterized.class)
public class MessagingChannelsMultiThreadTest {

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

    public MessagingChannelsMultiThreadTest(MessagingChannels messagingChannels) {
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

        DataPointVO dataPoint1 = TestUtils.newPointSettable(1, -1);
        dataPointRT1 = new DataPointRT(dataPoint1, null);
        DataPointVO dataPoint2 = TestUtils.newPointSettable(2, -1);
        dataPointRT2 = new DataPointRT(dataPoint2, null);
        DataPointVO dataPoint3 = TestUtils.newPointSettable(3, -1);
        dataPointRT3 = new DataPointRT(dataPoint3, null);

    }

    @Before
    public void reset() throws Exception {
        messagingChannels.closeChannels();
    }

    @Test
    public void when_removeChannel_then_channels_size_zero() throws Exception {

        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);

        //when:
        TestConcurrentUtils.biConsumer(10, (dp, timeout) -> {
            try {
                messagingChannels.removeChannel(dp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, dataPointRT1, 1000);


        //when:
        Assert.assertEquals(0, messagingChannels.size());

    }

    @Test
    public void when_removeChannel_then_isOpenChannel_false() throws Exception {

        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);

        //when:
        TestConcurrentUtils.biConsumer(10, (dp, timeout) -> {
            try {
                messagingChannels.removeChannel(dp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, dataPointRT1, 1000);


        //when:
        Assert.assertEquals(false, messagingChannels.isOpenChannel(dataPointRT1));

    }

    @Test
    public void when_removeChannel_one_form_two_then_one_isOpenChannel_true() throws Exception {

        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);
        messagingChannels.initChannel(dataPointRT2, () -> messagingChannelOpenned2);

        //when:
        TestConcurrentUtils.biConsumer(10, (dp, timeout) -> {
            try {
                messagingChannels.removeChannel(dp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, dataPointRT1, 1000);

        //when:
        Assert.assertEquals(true, messagingChannels.isOpenChannel(dataPointRT2));
    }

    @Test
    public void when_removeChannel_one_form_two_then_isOpenChannel_false() throws Exception {

        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);
        messagingChannels.initChannel(dataPointRT2, () -> messagingChannelOpenned2);

        //when:
        TestConcurrentUtils.biConsumer(10, (dp, timeout) -> {
            try {
                messagingChannels.removeChannel(dp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, dataPointRT1, 1000);

        //when:
        Assert.assertEquals(false, messagingChannels.isOpenChannel(dataPointRT1));
    }

    @Test
    public void when_removeChannel_one_form_two_then_one_size() throws Exception {

        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);
        messagingChannels.initChannel(dataPointRT2, () -> messagingChannelOpenned2);

        //when:
        TestConcurrentUtils.biConsumer(10, (dp, timeout) -> {
            try {
                messagingChannels.removeChannel(dp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, dataPointRT1, 1000);

        //when:
        Assert.assertEquals(1, messagingChannels.size());
    }

    @Test
    public void when_removeChannel_and_initChannel_on_other_dataPoints_then_one_size() throws Exception {

        //given:
        messagingChannels.initChannel(dataPointRT2, () -> messagingChannelOpenned1);

        TestConcurrentUtils.BiConsumerAction<DataPointRT, Integer> action1 =
                new TestConcurrentUtils.BiConsumerAction<>((dp, timeout) -> {
                    try {
                        messagingChannels.initChannel(dp, () -> messagingChannelOpenned1);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, dataPointRT1, 1000);
        TestConcurrentUtils.BiConsumerAction<DataPointRT, Integer> action2 =
                new TestConcurrentUtils.BiConsumerAction<>((dp, timeout) -> {
                    try {
                        messagingChannels.removeChannel(dp);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, dataPointRT2, 1000);

        //when:
        TestConcurrentUtils.biConsumer(10, Arrays.asList(action1, action2));

        //when:
        Assert.assertEquals(1, messagingChannels.size());

    }

    @Test
    public void when_removeChannel_and_initChannel_on_other_dataPoints_then_one_isOpenChannel_true() throws Exception {

        //given:
        messagingChannels.initChannel(dataPointRT2, () -> messagingChannelOpenned1);
        TestConcurrentUtils.BiConsumerAction<DataPointRT, Integer> action1 =
                new TestConcurrentUtils.BiConsumerAction<>((dp, timeout) -> {
                    try {
                        messagingChannels.initChannel(dp, () -> messagingChannelOpenned1);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, dataPointRT1, 1000);
        TestConcurrentUtils.BiConsumerAction<DataPointRT, Integer> action2 =
                new TestConcurrentUtils.BiConsumerAction<>((dp, timeout) -> {
                    try {
                        messagingChannels.removeChannel(dp);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, dataPointRT2, 1000);

        //when:
        TestConcurrentUtils.biConsumer(10, Arrays.asList(action1, action2));

        //when:
        Assert.assertEquals(true, messagingChannels.isOpenChannel(dataPointRT1));

    }

    @Test
    public void when_isOpenChannel_after_initChannel_then_true() throws Exception {
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
    public void when_initChannel_with_three_dataPoints_then_size_three() throws Exception {

        //given:
        TestConcurrentUtils.BiConsumerAction<DataPointRT, Supplier<MessagingChannel>> action1 =
                new TestConcurrentUtils.BiConsumerAction<>((a, b) -> {
                    try {
                        messagingChannels.initChannel(a, b);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, dataPointRT1, () -> messagingChannelOpenned1);
        TestConcurrentUtils.BiConsumerAction<DataPointRT, Supplier<MessagingChannel>> action2 =
                new TestConcurrentUtils.BiConsumerAction<>((a, b) -> {
                    try {
                        messagingChannels.initChannel(a, b);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, dataPointRT2, () -> messagingChannelOpenned2);
        TestConcurrentUtils.BiConsumerAction<DataPointRT, Supplier<MessagingChannel>> action3 =
                new TestConcurrentUtils.BiConsumerAction<>((a, b) -> {
                    try {
                        messagingChannels.initChannel(a, b);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, dataPointRT3, () -> messagingChannelOpenned3);

        //when:
        TestConcurrentUtils.biConsumer(10, Arrays.asList(action1, action2, action3));

        //then:
        Assert.assertEquals(3, messagingChannels.size());
    }

    @Test
    public void when_initChannel_same_dataPoint_then_size_one() throws Exception{

        //when:
        TestConcurrentUtils.biConsumer(10, (a, b) -> {
            try {
                messagingChannels.initChannel(a, b);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, dataPointRT1, (Supplier<MessagingChannel>)(() -> messagingChannelOpenned1));

        //then:
        Assert.assertEquals(1, messagingChannels.size());
    }

    @Test
    public void when_isOpen_after_initChannel_then_true() throws Exception{
        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);

        //when:
        List<Boolean> result = TestConcurrentUtils.supplierWithResult(10, messagingChannels::isOpenConnection);

        //then:
        Assert.assertEquals(true, result.get(0));
    }

    @Test
    public void when_closeChannels_then_size_zero() throws Exception {
        //given:
        messagingChannels.initChannel(dataPointRT1, () -> messagingChannelOpenned1);
        messagingChannels.initChannel(dataPointRT2, () -> messagingChannelOpenned2);
        messagingChannels.initChannel(dataPointRT3, () -> messagingChannelOpenned3);

        //when:
        TestConcurrentUtils.consumer(10, (timeout) -> {
            try {
                messagingChannels.closeChannels();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, 1000);

        //when:
        Assert.assertEquals(0, messagingChannels.size());
    }
}