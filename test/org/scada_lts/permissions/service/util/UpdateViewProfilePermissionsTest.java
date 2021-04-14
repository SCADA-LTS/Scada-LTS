package org.scada_lts.permissions.service.util;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
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
public class UpdateViewProfilePermissionsTest {

    @Parameterized.Parameters(name= "{index}: fromDatabase: {0}, fromUser: {1}")
    public static Object[][] primeNumbers() {
        return new Object[][] {new Object[] {
                            Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2), new ViewAccess(3, 2)).collect(Collectors.toList()),
                            Collections.emptyList()
                    }, new Object[] {
                            Stream.of(new ViewAccess(1, 2), new ViewAccess(2, 2), new ViewAccess(3, 2)).collect(Collectors.toList()),
                            Stream.of(new ViewAccess(4, 2)).collect(Collectors.toList())
                    }, new Object[] {
                            Stream.of(new ViewAccess(1, 1)).collect(Collectors.toList()),
                            Stream.of(new ViewAccess(1, 2), new ViewAccess(2, 2), new ViewAccess(3, 2)).collect(Collectors.toList())
                    }, new Object[] {
                            Stream.of(new ViewAccess(4, 1)).collect(Collectors.toList()),
                            Stream.of(new ViewAccess(1, 2), new ViewAccess(2, 2), new ViewAccess(3, 2)).collect(Collectors.toList())
                    }, new Object[] {
                            Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2), new ViewAccess(3, 2)).collect(Collectors.toList()),
                            Stream.of(new ViewAccess(1, 2)).collect(Collectors.toList())
                    }, new Object[] {
                            Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2), new ViewAccess(3, 2)).collect(Collectors.toList()),
                            Stream.of(new ViewAccess(1, 2), new ViewAccess(2, 2)).collect(Collectors.toList())
                    }, new Object[] {
                            Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2)).collect(Collectors.toList()),
                            Stream.of(new ViewAccess(1, 2), new ViewAccess(2, 1)).collect(Collectors.toList())
                    }, new Object[] {
                            Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2)).collect(Collectors.toList()),
                            Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2)).collect(Collectors.toList())
                    }, new Object[] {
                            new ArrayList<>(),
                            Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2), new ViewAccess(2, 2)).collect(Collectors.toList())
                    }
        };
    }


    private UsersProfileVO profile;
    private List<ViewAccess> fromUser;
    private PermissionsService<ViewAccess, UsersProfileVO> permissionsService;

    public UpdateViewProfilePermissionsTest(List<ViewAccess> fromDatabase, List<ViewAccess> fromUser) {
        this.fromUser = fromUser;
        this.profile = new UsersProfileVO();
        this.profile.setId(123);
        this.profile.setViewPermissions(fromUser);

        Map<Integer, List<ViewAccess>> permissions = new HashMap<>();
        permissions.put(profile.getId(), fromDatabase);
        permissionsService = new PermissionsServiceProfileTestImpl<>(permissions);
    }

    @Test
    public void updatePermissions() {

        //when:
        PermissionsUtils.updateViewPermissions(profile, permissionsService);
        List<ViewAccess> result = permissionsService.getPermissions(profile);

        //then:
        assertEquals(fromUser.size(), result.size());
        assertTrue("Expected fromUser: " + fromUser + " but was: " + result, result.containsAll(fromUser));
    }
}