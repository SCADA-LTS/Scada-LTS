package org.scada_lts.permissions.migration;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.scada_lts.permissions.migration.MigrationPermissionsUtils.generateDataPointAccess;

public class GenerateDataPointPermissionTest {

    @Test
    public void when_dataPoint_with_id_then_access_with_this_id() {

        //given:
        int dataPointIdExpected = 123;
        DataPointVO dataPoint = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint.setId(dataPointIdExpected);
        ShareUser shareUser = new ShareUser(1234, ShareUser.ACCESS_SET);

        //when:
        DataPointAccess result = generateDataPointAccess(shareUser, dataPoint);

        //then:
        assertEquals(dataPointIdExpected, result.getDataPointId());
    }

    @Test
    public void when_dataPoint_is_settable_and_shareUser_with_set_access_then_set_permission() {

        //given:
        boolean settable = true;
        int permissionExpected = ShareUser.ACCESS_SET;
        int setAccess = ShareUser.ACCESS_SET;

        DataPointVO dataPoint = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint.setId(123);
        dataPoint.setSettable(settable);
        ShareUser shareUser = new ShareUser(1234, setAccess);

        //when:
        DataPointAccess result = generateDataPointAccess(shareUser, dataPoint);

        //then:
        assertEquals(permissionExpected, result.getPermission());
    }

    @Test
    public void when_dataPoint_is_settable_and_shareUser_with_read_access_then_read_permission() {
        //given:
        boolean settable = true;
        int permissionExpected = ShareUser.ACCESS_READ;
        int readAccess = ShareUser.ACCESS_READ;

        DataPointVO dataPoint = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint.setId(123);
        dataPoint.setSettable(settable);
        ShareUser shareUser = new ShareUser(1234, readAccess);

        //when:
        DataPointAccess result = generateDataPointAccess(shareUser, dataPoint);

        //then:
        assertEquals(permissionExpected, result.getPermission());
    }

    @Test
    public void when_dataPoint_is_not_settable_and_shareUser_with_set_access_then_read_permission() {
        //given:
        boolean settable = false;
        int permissionExpected = ShareUser.ACCESS_READ;
        int setAccess = ShareUser.ACCESS_SET;

        DataPointVO dataPoint = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint.setId(123);
        dataPoint.setSettable(settable);
        ShareUser shareUser = new ShareUser(1234, setAccess);

        //when:
        DataPointAccess result = generateDataPointAccess(shareUser, dataPoint);

        //then:
        assertEquals(permissionExpected, result.getPermission());
    }

    @Test
    public void when_dataPoint_is_not_settable_and_shareUser_with_read_access_then_read_permission() {
        //given:
        boolean settable = false;
        int permissionExpected = ShareUser.ACCESS_READ;
        int readAccess = ShareUser.ACCESS_READ;

        DataPointVO dataPoint = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint.setId(123);
        dataPoint.setSettable(settable);
        ShareUser shareUser = new ShareUser(1234, readAccess);

        //when:
        DataPointAccess result = generateDataPointAccess(shareUser, dataPoint);

        //then:
        assertEquals(permissionExpected, result.getPermission());
    }
}
