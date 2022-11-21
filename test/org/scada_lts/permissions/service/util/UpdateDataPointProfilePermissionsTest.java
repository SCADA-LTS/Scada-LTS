package org.scada_lts.permissions.service.util;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.permissions.service.PermissionsService;
import utils.PermissionsServiceProfileTestImpl;
import utils.PermissionsServiceUserTestImpl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class UpdateDataPointProfilePermissionsTest {

    @Parameterized.Parameters(name= "{index}: fromDatabase: {0}, fromUser: {1}")
    public static Object[][] primeNumbers() {
        return new Object[][] {new Object[] {
                        Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2), new DataPointAccess(3, 2)).collect(Collectors.toList()),
                        Collections.emptyList()
                }, new Object[] {
                        Stream.of(new DataPointAccess(1, 2), new DataPointAccess(2, 2), new DataPointAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(4, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new DataPointAccess(1, 1)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 2), new DataPointAccess(2, 2), new DataPointAccess(3, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new DataPointAccess(4, 1)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 2), new DataPointAccess(2, 2), new DataPointAccess(3, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2), new DataPointAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2), new DataPointAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 2), new DataPointAccess(2, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 2), new DataPointAccess(2, 1)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2)).collect(Collectors.toList())
                }, new Object[] {
                        new ArrayList<>(),
                        Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2), new DataPointAccess(2, 2)).collect(Collectors.toList())
                }
        };
    }


    private UsersProfileVO profile;
    private List<DataPointAccess> fromUser;
    private PermissionsService<DataPointAccess, UsersProfileVO> permissionsService;

    public UpdateDataPointProfilePermissionsTest(List<DataPointAccess> fromDatabase, List<DataPointAccess> fromUser) {
        this.fromUser = fromUser;
        profile = new UsersProfileVO();
        profile.setId(123);
        profile.setDataPointPermissions(fromUser);
        Map<Integer, List<DataPointAccess>> permissions = new HashMap<>();
        permissions.put(profile.getId(), fromDatabase);
        permissionsService = new PermissionsServiceProfileTestImpl<>(permissions);
    }

    @Test
    public void updatePermissions() {

        //when:
        PermissionsUtils.updateDataPointPermissions(profile, permissionsService);
        List<DataPointAccess> result = permissionsService.getPermissions(profile);

        //then:
        assertEquals(fromUser.size(), result.size());
        assertTrue("Expected fromUser: " + fromUser + " but was: " + result, result.containsAll(fromUser));
    }
}