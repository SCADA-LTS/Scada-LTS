package com.serotonin.mango.rt.dataSource.meta;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        JavaClassGrantedScriptExecutorTest.class,
        JavaClassDeniedScriptExecutorTest.class,
        JsScriptExecutorTest.class
})
public class ScriptExecutorTestsSuite {
}
