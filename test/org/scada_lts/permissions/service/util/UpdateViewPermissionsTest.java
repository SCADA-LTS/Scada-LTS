package org.scada_lts.permissions.service.util;

import br.org.scadabr.db.utils.TestUtils;
import br.org.scadabr.vo.permission.ViewAccess;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.permissions.service.PermissionsService;
import utils.PermissionsServiceTestImpl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class UpdateViewPermissionsTest {

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


    private User user;
    private List<ViewAccess> fromUser;
    private PermissionsService<ViewAccess, View> permissionsService;

    public UpdateViewPermissionsTest(List<ViewAccess> fromDatabase, List<ViewAccess> fromUser) {
        this.fromUser = fromUser;
        user = TestUtils.newUser(123);
        user.setViewPermissions(fromUser);
        Map<Integer, List<ViewAccess>> permissions = new HashMap<>();
        permissions.put(user.getId(), fromDatabase);
        permissionsService = new PermissionsServiceTestImpl<>(permissions);
    }

    @Test
    public void updatePermissions() {

        //when:
        PermissionsUtils.updateViewPermissions(user, permissionsService);
        List<ViewAccess> result = permissionsService.getPermissions(user);

        //then:
        assertEquals(fromUser.size(), result.size());
        assertTrue("Expected fromUser: " + fromUser + " but was: " + result, result.containsAll(fromUser));
    }
}