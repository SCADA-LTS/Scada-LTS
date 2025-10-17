package org.scada_lts.serial;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import utils.TestConcurrentUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class SerialPortServiceMultiThreadTest {

    private static final Log LOG = LogFactory.getLog(SerialPortServiceMultiThreadTest.class);

    private SerialPortService subject1;
    private SerialPortService subject2;

    @Before
    public void config() {
        SerialPortParameters serialPort1 = SerialPortParameters.newParameters("ownerName",
                "COM3", 0, 0, 1, 8, 1, 0, 500);
        SerialPortParameters serialPort2 = SerialPortParameters.newParameters("ownerName",
                "COM4", 0, 0, 1, 8, 1, 0, 500);

        subject1 = SerialPortService.newService(serialPort1);
        subject2 = SerialPortService.newService(serialPort2);
    }

    @After
    public void clean() {
        subject1.close();
        subject2.close();
    }

    @Test
    public void when_open_close_two_ports_numberOfLaunches_100_then_not_exception() {

        //given:
        int numberOfLaunches = 100;
        AtomicInteger counter = new AtomicInteger(0);

        //when:
        TestConcurrentUtils.biConsumer(numberOfLaunches, (serialPort1, serialPort2) -> {
            try {
                serialPort1.open();
                serialPort2.open();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                counter.incrementAndGet();
            } finally {
                serialPort1.close();
                serialPort2.close();
            }
        }, subject1, subject2);

        //then:
        Assert.assertEquals(0, counter.get());
    }

    @Test
    public void when_open_close_one_port_numberOfLaunches_100_then_not_exception() {

        //given:
        int numberOfLaunches = 100;
        AtomicInteger counter = new AtomicInteger(0);

        //when:
        TestConcurrentUtils.consumer(numberOfLaunches, (serialPort1) -> {
            try {
                serialPort1.open();
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                counter.incrementAndGet();
            } finally {
                serialPort1.close();
            }
        }, subject1);

        //then:
        Assert.assertEquals(0, counter.get());
    }
}