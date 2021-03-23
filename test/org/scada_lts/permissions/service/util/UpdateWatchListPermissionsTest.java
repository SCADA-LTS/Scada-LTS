package org.scada_lts.permissions.service.util;

import br.org.scadabr.db.utils.TestUtils;
import br.org.scadabr.vo.permission.WatchListAccess;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.permissions.service.PermissionsService;
import utils.PermissionsServiceTestImpl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class UpdateWatchListPermissionsTest {

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


    private User user;
    private List<WatchListAccess> fromUser;
    private PermissionsService<WatchListAccess, WatchList> permissionsService;

    public UpdateWatchListPermissionsTest(List<WatchListAccess> fromDatabase, List<WatchListAccess> fromUser) {
        this.fromUser = fromUser;
        user = TestUtils.newUser(123);
        user.setWatchListPermissions(fromUser);
        Map<Integer, List<WatchListAccess>> permissions = new HashMap<>();
        permissions.put(user.getId(), fromDatabase);
        permissionsService = new PermissionsServiceTestImpl<>(permissions);
    }

    @Test
    public void updatePermissions() {

        //when:
        PermissionsUtils.updateWatchListPermissions(user, permissionsService);
        List<WatchListAccess> result = permissionsService.getPermissions(user);

        //then:
        assertEquals(fromUser.size(), result.size());
        assertTrue("Expected fromUser: " + fromUser + " but was: " + result, result.containsAll(fromUser));
    }
}