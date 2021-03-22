package org.scada_lts.permissions.service.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UpdateDataPointPermissionsTest.class,
        UpdateDataSourcePermissionsTest.class,
        UpdateViewPermissionsTest.class,
        UpdateWatchListPermissionsTest.class
})
public class PermissionsTestsSuite {
}
