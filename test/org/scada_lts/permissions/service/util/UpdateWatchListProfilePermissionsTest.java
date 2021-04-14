package org.scada_lts.permissions.service.util;

import br.org.scadabr.vo.permission.WatchListAccess;
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

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class UpdateWatchListProfilePermissionsTest {

    @Parameterized.Parameters(name= "{index}: fromDatabase: {0}, fromUser: {1}")
    public static Object[][] primeNumbers() {
        return new Object[][] {new Object[] {
                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2), new WatchListAccess(3, 2)).collect(Collectors.toList()),
                Collections.emptyList()
        }, new Object[] {
                Stream.of(new WatchListAccess(1, 2), new WatchListAccess(2, 2), new WatchListAccess(3, 2)).collect(Collectors.toList()),
                Stream.of(new WatchListAccess(4, 2)).collect(Collectors.toList())
        }, new Object[] {
                Stream.of(new WatchListAccess(1, 1)).collect(Collectors.toList()),
                Stream.of(new WatchListAccess(1, 2), new WatchListAccess(2, 2), new WatchListAccess(3, 2)).collect(Collectors.toList())
        }, new Object[] {
                Stream.of(new WatchListAccess(4, 1)).collect(Collectors.toList()),
                Stream.of(new WatchListAccess(1, 2), new WatchListAccess(2, 2), new WatchListAccess(3, 2)).collect(Collectors.toList())
        }, new Object[] {
                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2), new WatchListAccess(3, 2)).collect(Collectors.toList()),
                Stream.of(new WatchListAccess(1, 2)).collect(Collectors.toList())
        }, new Object[] {
                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2), new WatchListAccess(3, 2)).collect(Collectors.toList()),
                Stream.of(new WatchListAccess(1, 2), new WatchListAccess(2, 2)).collect(Collectors.toList())
        }, new Object[] {
                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2)).collect(Collectors.toList()),
                Stream.of(new WatchListAccess(1, 2), new WatchListAccess(2, 1)).collect(Collectors.toList())
        }, new Object[] {
                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2)).collect(Collectors.toList()),
                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2)).collect(Collectors.toList())
        }, new Object[] {
                new ArrayList<>(),
                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2), new WatchListAccess(2, 2)).collect(Collectors.toList())
        }
        };
    }


    private UsersProfileVO profile;
    private List<WatchListAccess> fromUser;
    private PermissionsService<WatchListAccess, UsersProfileVO> permissionsService;

    public UpdateWatchListProfilePermissionsTest(List<WatchListAccess> fromDatabase, List<WatchListAccess> fromUser) {
        this.fromUser = fromUser;
        profile = new UsersProfileVO();
        profile.setId(123);
        profile.setWatchlistPermissions(fromUser);
        Map<Integer, List<WatchListAccess>> permissions = new HashMap<>();
        permissions.put(profile.getId(), fromDatabase);
        permissionsService = new PermissionsServiceProfileTestImpl<>(permissions);
    }

    @Test
    public void updatePermissions() {

        //when:
        PermissionsUtils.updateWatchListPermissions(profile, permissionsService);
        List<WatchListAccess> result = permissionsService.getPermissions(profile);

        //then:
        assertEquals(fromUser.size(), result.size());
        assertTrue("Expected fromUser: " + fromUser + " but was: " + result, result.containsAll(fromUser));
    }
}