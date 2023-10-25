package com.serotonin.mango.vo;

import com.serotonin.mango.rt.scripting.ScriptTest;
import com.serotonin.mango.rt.scripting.ScriptWithObjectContextEnableDisableDataPointTest;
import com.serotonin.mango.rt.scripting.ScriptWithObjectContextEnableDisableDataSourceTest;
import com.serotonin.mango.rt.scripting.ScriptWithObjectContextWriteDataPointTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoggedUsersTest.class,
        LoggedUsersMultiThreadTest.class
})
public class LoggedUserTestsSuite {
}