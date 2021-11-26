package org.scada_lts.permissions.service.util;

import br.org.scadabr.vo.permission.ViewAccess;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class MergePermissionsTest {


    @Test
    public void when_merge_shareUsers_set_and_read_accessType_then_shareUsers_set_accessType() {

        //given:
        ShareUser shareUserRead1 = new ShareUser(1, 1);
        ShareUser shareUserRead2 = new ShareUser(2, 1);
        ShareUser shareUserRead3 = new ShareUser(3, 1);
        ShareUser shareUserSet1 = new ShareUser(1, 2);
        ShareUser shareUserSet2 = new ShareUser(2, 2);
        ShareUser shareUserSet3 = new ShareUser(3, 2);

        List<ShareUser> shareUsers1 = new ArrayList<>();
        shareUsers1.add(shareUserRead1);
        shareUsers1.add(shareUserSet2);
        shareUsers1.add(shareUserRead3);

        List<ShareUser> shareUsers2 = new ArrayList<>();
        shareUsers2.add(shareUserSet1);
        shareUsers2.add(shareUserRead2);
        shareUsers2.add(shareUserSet3);

        List<ShareUser> shareUsersExpected = new ArrayList<>();
        shareUsersExpected.add(shareUserSet1);
        shareUsersExpected.add(shareUserSet2);
        shareUsersExpected.add(shareUserSet3);
        shareUsersExpected.sort(Comparator.comparingInt(ShareUser::getUserId));

        //when:
        List<ShareUser> result = PermissionsUtils.merge(shareUsers1, shareUsers2);
        result.sort(Comparator.comparingInt(ShareUser::getUserId));

        //then:
        Assert.assertEquals(shareUsersExpected, result);

        //and when:
        result = PermissionsUtils.merge(shareUsers2, shareUsers1);
        result.sort(Comparator.comparingInt(ShareUser::getUserId));

        //then:
        Assert.assertEquals(shareUsersExpected, result);
    }

    @Test
    public void when_merge_for_permissions_set_and_read_then_permissions_set() {

        //given:
        ViewAccess viewAccessRead1 = new ViewAccess(1, 1);
        ViewAccess viewAccessRead2 = new ViewAccess(2, 1);
        ViewAccess viewAccessRead3 = new ViewAccess(3, 1);
        ViewAccess viewAccessSet1 = new ViewAccess(1, 2);
        ViewAccess viewAccessSet2 = new ViewAccess(2, 2);
        ViewAccess viewAccessSet3 = new ViewAccess(3, 2);

        Set<ViewAccess> viewAccesses1 = new HashSet<>();
        viewAccesses1.add(viewAccessRead1);
        viewAccesses1.add(viewAccessSet2);
        viewAccesses1.add(viewAccessRead3);

        Set<ViewAccess> viewAccesses2 = new HashSet<>();
        viewAccesses2.add(viewAccessSet1);
        viewAccesses2.add(viewAccessRead2);
        viewAccesses2.add(viewAccessSet3);

        Set<ViewAccess> viewAccessesExpected = new HashSet<>();
        viewAccessesExpected.add(viewAccessSet1);
        viewAccessesExpected.add(viewAccessSet2);
        viewAccessesExpected.add(viewAccessSet3);

        //when:
        Set<ViewAccess> result = PermissionsUtils.merge(viewAccesses1, viewAccesses2);

        //then:
        Assert.assertEquals(viewAccessesExpected, result);

        //and when:
        result = PermissionsUtils.merge(viewAccesses2, viewAccesses1);

        //then:
        Assert.assertEquals(viewAccessesExpected, result);
    }

    @Test
    public void when_mergeDataPointAccesses_for_dataPointAccesses_set_and_read_then_dataPointAccesses_set() {

        //given:
        DataPointAccess dataPointAccessRead1 = new DataPointAccess(1, 1);
        DataPointAccess dataPointAccessRead2 = new DataPointAccess(2, 1);
        DataPointAccess dataPointAccessRead3 = new DataPointAccess(3, 1);
        DataPointAccess dataPointAccessSet1 = new DataPointAccess(1, 2);
        DataPointAccess dataPointAccessSet2 = new DataPointAccess(2, 2);
        DataPointAccess dataPointAccessSet3 = new DataPointAccess(3, 2);

        Set<DataPointAccess> dataPointAccesses1 = new HashSet<>();
        dataPointAccesses1.add(dataPointAccessRead1);
        dataPointAccesses1.add(dataPointAccessSet2);
        dataPointAccesses1.add(dataPointAccessRead3);

        Set<DataPointAccess> dataPointAccesses2 = new HashSet<>();
        dataPointAccesses2.add(dataPointAccessSet1);
        dataPointAccesses2.add(dataPointAccessRead2);
        dataPointAccesses2.add(dataPointAccessSet3);

        Set<DataPointAccess> dataPointAccessesExpected = new HashSet<>();
        dataPointAccessesExpected.add(dataPointAccessSet1);
        dataPointAccessesExpected.add(dataPointAccessSet2);
        dataPointAccessesExpected.add(dataPointAccessSet3);

        //when:
        Set<DataPointAccess> result = PermissionsUtils.mergeDataPointAccesses(dataPointAccesses1, dataPointAccesses2);

        //then:
        Assert.assertEquals(dataPointAccessesExpected, result);

        //and when:
        result = PermissionsUtils.mergeDataPointAccesses(dataPointAccesses2, dataPointAccesses1);

        //then:
        Assert.assertEquals(dataPointAccessesExpected, result);
    }
}
