package org.scada_lts.serial;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SerialPortServiceMultiThreadTest.class,
        SerialPortServiceTest.class,
})
public class SerialPortServiceTestsSuite {}