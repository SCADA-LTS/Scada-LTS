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
        ViewAccess shareUserRead1 = new ViewAccess(1, 1);
        ViewAccess shareUserRead2 = new ViewAccess(2, 1);
        ViewAccess shareUserRead3 = new ViewAccess(3, 1);
        ViewAccess shareUserSet1 = new ViewAccess(1, 2);
        ViewAccess shareUserSet2 = new ViewAccess(2, 2);
        ViewAccess shareUserSet3 = new ViewAccess(3, 2);

        Set<ViewAccess> shareUsers1 = new HashSet<>();
        shareUsers1.add(shareUserRead1);
        shareUsers1.add(shareUserSet2);
        shareUsers1.add(shareUserRead3);

        Set<ViewAccess> shareUsers2 = new HashSet<>();
        shareUsers2.add(shareUserSet1);
        shareUsers2.add(shareUserRead2);
        shareUsers2.add(shareUserSet3);

        Set<ViewAccess> shareUsersExpected = new HashSet<>();
        shareUsersExpected.add(shareUserSet1);
        shareUsersExpected.add(shareUserSet2);
        shareUsersExpected.add(shareUserSet3);

        //when:
        Set<ViewAccess> result = PermissionsUtils.merge(shareUsers1, shareUsers2);

        //then:
        Assert.assertEquals(shareUsersExpected, result);

        //and when:
        result = PermissionsUtils.merge(shareUsers2, shareUsers1);

        //then:
        Assert.assertEquals(shareUsersExpected, result);
    }

    @Test
    public void when_mergeDataPointAccesses_for_dataPointAccesses_set_and_read_then_dataPointAccesses_set() {

        //given:
        DataPointAccess shareUserRead1 = new DataPointAccess(1, 1);
        DataPointAccess shareUserRead2 = new DataPointAccess(2, 1);
        DataPointAccess shareUserRead3 = new DataPointAccess(3, 1);
        DataPointAccess shareUserSet1 = new DataPointAccess(1, 2);
        DataPointAccess shareUserSet2 = new DataPointAccess(2, 2);
        DataPointAccess shareUserSet3 = new DataPointAccess(3, 2);

        Set<DataPointAccess> shareUsers1 = new HashSet<>();
        shareUsers1.add(shareUserRead1);
        shareUsers1.add(shareUserSet2);
        shareUsers1.add(shareUserRead3);

        Set<DataPointAccess> shareUsers2 = new HashSet<>();
        shareUsers2.add(shareUserSet1);
        shareUsers2.add(shareUserRead2);
        shareUsers2.add(shareUserSet3);

        Set<DataPointAccess> shareUsersExpected = new HashSet<>();
        shareUsersExpected.add(shareUserSet1);
        shareUsersExpected.add(shareUserSet2);
        shareUsersExpected.add(shareUserSet3);

        //when:
        Set<DataPointAccess> result = PermissionsUtils.mergeDataPointAccesses(shareUsers1, shareUsers2);

        //then:
        Assert.assertEquals(shareUsersExpected, result);

        //and when:
        result = PermissionsUtils.mergeDataPointAccesses(shareUsers2, shareUsers1);

        //then:
        Assert.assertEquals(shareUsersExpected, result);
    }
}
