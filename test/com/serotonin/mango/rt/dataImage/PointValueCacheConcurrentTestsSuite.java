package com.serotonin.mango.rt.dataImage;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        PointValueCacheGetMethodsConcurrentTest.class,
        SavePointValueMethodConcurrentTest.class
})
public class PointValueCacheConcurrentTestsSuite {
}
