package org.scada_lts;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        InitStaticFieldTest.class,
        CreateObjectTest.class
})
public class InitObjectTestsSuite {
}
