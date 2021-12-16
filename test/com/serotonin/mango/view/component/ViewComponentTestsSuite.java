package com.serotonin.mango.view.component;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ButtonComponentTest.class,
        ScriptComponentTest.class,
        ViewGraphicTest.class,
        MultistateGraphicComponentTest.class,
        DynamicGraphicComponentTest.class,
        AnalogGraphicComponentTest.class,
        BinaryGraphicComponentTest.class
})
public class ViewComponentTestsSuite {
}
