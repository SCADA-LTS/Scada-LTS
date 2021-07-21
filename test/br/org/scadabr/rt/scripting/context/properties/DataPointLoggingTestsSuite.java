package br.org.scadabr.rt.scripting.context.properties;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({

        DataPointLoggingTypePropertiesTest.class,
        DataPointLoggingTypeNativeObjectTest.class,
        DataPointLoggingIntervalUpdateDataPointTest.class,
        DataPointLoggingTypeUpdateDataPointTest.class,

        DataPointPurgeTypePropertiesTest.class,
        DataPointPurgeTypeNativeObjectTest.class,
        DataPointPurgeTypeUpdateDataPointTest.class,

        DataPointDiscardValuesPropertiesTest.class,
        DataPointDiscardValuesUpdateDataPointTest.class
})
public class DataPointLoggingTestsSuite {
}
