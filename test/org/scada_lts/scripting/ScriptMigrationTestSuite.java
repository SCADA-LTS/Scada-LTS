package org.scada_lts.scripting;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        RegexReplaceTest.class,
        ScriptMigrationTest.class
})
public class ScriptMigrationTestSuite {
}
