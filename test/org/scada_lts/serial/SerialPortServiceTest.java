package org.scada_lts.serial;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.InputStream;
import java.io.OutputStream;

@RunWith(Parameterized.class)
public class SerialPortServiceTest {

    @Parameterized.Parameters(name= "{index}: baudRate: {0}, flowControlIn: {1}, flowControlOut: {2}, parity: {3}")
    public static Object[][] data() {
        return new Object[][]{
                new Object[]{0, 0, 0, 0},
                new Object[]{110, 1, 1, 1},

                new Object[]{110, 4, 0, 0},
                new Object[]{110, 0, 4, 0},

                new Object[]{300, 1, 1, 0},
                new Object[]{1200, 1, 1, 0},
                new Object[]{2400, 4, 4, 0},
                new Object[]{4800, 1, 4, 0},
                new Object[]{9600, 4, 1, 0},
                new Object[]{19200, 1, 1, 1},
                new Object[]{38400, 4, 4, 2},
                new Object[]{57600, 1, 4, 3},
                new Object[]{115200, 4, 1, 4},

                new Object[]{230400, 0, 1, 0},
                new Object[]{460800, 4, 0, 0},
                new Object[]{921600, 0, 4, 0},
                new Object[]{9600, 4, 0, 0},
                new Object[]{9600, 0, 1, 1},
                new Object[]{9600, 4, 0, 2},
                new Object[]{9600, 0, 4, 3},
                new Object[]{9600, 4, 0, 4},

                new Object[]{110, 0, 0, 0},
                new Object[]{921600, 4, 4, 4},
        };
    }

    private final int baudRate;
    private final int flowControlIn;
    private final int flowControlOut;
    private final int parity;

    public SerialPortServiceTest(int baudRate, int flowControlIn, int flowControlOut, int parity) {
        this.baudRate = baudRate;
        this.flowControlIn = flowControlIn;
        this.flowControlOut = flowControlOut;
        this.parity = parity;

    }

    private SerialPortService subject1;
    private SerialPortService subject2;
    private SerialPortService subject1SamePort;
    private SerialPortService subject1BadPort;

    @Before
    public void config() {
        SerialPortParameters serialPort1 = SerialPortParameters.newParameters("ownerName",
                "COM3", baudRate, flowControlIn, flowControlOut, 8, 1, parity, 500);
        SerialPortParameters serialPort2 = SerialPortParameters.newParameters("ownerName",
                "COM4", baudRate, flowControlIn, flowControlOut,  8, 1, parity, 500);
        SerialPortParameters serialPortBad = SerialPortParameters.newParameters("ownerName",
                "xyz", baudRate, flowControlIn, flowControlOut, 8, 1, parity, 500);

        subject1 = SerialPortService.newService(serialPort1);
        subject2 = SerialPortService.newService(serialPort2);
        subject1SamePort = SerialPortService.newService(serialPort1);
        subject1BadPort = SerialPortService.newService(serialPortBad);
    }

    @After
    public void clean() {
        subject1.close();
        subject2.close();
        subject1SamePort.close();
        subject1BadPort.close();
    }

    @Test
    public void when_open_then_isOpen_true() throws SerialPortException {

        //when:
        subject1.open();

        //and:
        boolean result = subject1.isOpen();

        //then:
        Assert.assertEquals(true, result);
    }

    @Test
    public void when_invoke_two_open_same_port_then_isOpen_true() throws SerialPortException {

        //when:
        subject1.open();
        subject1.open();

        //and:
        boolean result = subject1.isOpen();

        //then:
        Assert.assertEquals(true, result);
    }

    @Test
    public void when_close_then_isOpen_false() throws SerialPortException {

        //given:
        subject1.open();

        //when:
        subject1.close();

        //and:
        boolean result = subject1.isOpen();

        //then:
        Assert.assertEquals(false, result);
    }

    @Test
    public void when_open_two_ports_then_isOpen_true() throws SerialPortException {

        //when:
        subject1.open();
        subject2.open();

        //and:
        boolean result = subject1.isOpen();
        boolean result2 = subject2.isOpen();

        //then:
        Assert.assertEquals(true, result);
        Assert.assertEquals(true, result2);
    }

    @Test
    public void when_close_two_ports_then_isOpen_false() throws SerialPortException {

        //given:
        subject1.open();
        subject2.open();

        //when:
        subject1.close();
        subject2.close();

        //and:
        boolean result = subject1.isOpen();
        boolean result2 = subject2.isOpen();

        //then:
        Assert.assertEquals(false, result);
        Assert.assertEquals(false, result2);
    }


    @Test
    public void when_first_port_is_close_and_second_port_is_open_then_first_isOpen_false_and_second_isOpen_true() throws SerialPortException {

        //given:
        subject1.open();
        subject2.open();

        //when:
        subject1.close();

        //and:
        boolean result = subject1.isOpen();
        boolean result2 = subject2.isOpen();
        subject2.close();

        //then:
        Assert.assertEquals(false, result);
        Assert.assertEquals(true, result2);
    }

    @Test(expected = SerialPortException.class)
    public void when_open_same_ports_then_SerialPortException() throws SerialPortException {

        //when:
        subject1.open();
        Assert.assertEquals(true, subject1.isOpen());

        //and:
        subject1SamePort.open();
    }

    @Test(expected = SerialPortException.class)
    public void when_open_bad_port_then_SerialPortException() throws SerialPortException {

        //when:
        subject1BadPort.open();
    }

    @Test
    public void when_getOutputStream_then_not_null() throws SerialPortException {

        //given:
        subject1.open();

        //when:
        OutputStream outputStream = subject1.getOutputStream();
        Assert.assertNotNull(outputStream);
    }

    @Test
    public void when_getInputStream_then_not_null() throws SerialPortException {

        //given:
        subject1.open();

        //when:
        InputStream inputStream = subject1.getInputStream();
        Assert.assertNotNull(inputStream);
    }

    @Test
    public void when_getOutputStream_if_not_open_then_null() {
        //when:
        OutputStream outputStream = subject1.getOutputStream();
        Assert.assertNull(outputStream);
    }

    @Test
    public void when_getInputStream_if_not_open_then_null() {
        //when:
        InputStream inputStream = subject1.getInputStream();
        Assert.assertNull(inputStream);
    }

    @Test
    public void when_open_close_open_then_isOpen_true() throws SerialPortException {

        //when:
        subject1.open();
        subject1.close();
        subject1.open();

        //and:
        boolean result = subject1.isOpen();

        //then:
        Assert.assertEquals(true, result);
    }

    @Test
    public void when_open_close_open_close_then_isOpen_false() throws SerialPortException {

        //when:
        subject1.open();
        subject1.close();
        subject1.open();
        subject1.close();

        //and:
        boolean result = subject1.isOpen();

        //then:
        Assert.assertEquals(false, result);
    }
}