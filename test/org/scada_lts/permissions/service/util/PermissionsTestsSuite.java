package org.scada_lts.permissions.service.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UpdateDataPointUserPermissionsTest.class,
        UpdateDataSourceUserPermissionsTest.class,
        UpdateViewProfilePermissionsTest.class,
        UpdateWatchListProfilePermissionsTest.class,
        UpdateDataPointProfilePermissionsTest.class,
        UpdateDataSourceProfilePermissionsTest.class,
        MergePermissionsTest.class,
        MergeDataPointPermissionsParameterizedTest.class,
        MergePermissionsParameterizedTest.class,
        MergeShareUserParameterizedTest.class
})
public class PermissionsTestsSuite {
}
