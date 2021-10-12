package org.scada_lts.permissions.migration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DataPointMigrationPermissionsCommandTest.class,
        DataSourceMigrationPermissionsCommandTest.class,
        MigrationPermissionsUtilsTest.class,
        GenerateDataPointPermissionTest.class,
        AccessesTest.class,
        MigrationUpdatePermissionsTest.class
})
public class MigrationPermissionsTestsSuite {
}
