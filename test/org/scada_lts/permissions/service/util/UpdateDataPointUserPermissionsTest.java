package org.scada_lts.permissions.service.util;

import br.org.scadabr.db.utils.TestUtils;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.permissions.service.PermissionsService;
import utils.PermissionsServiceUserTestImpl;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class UpdateDataPointUserPermissionsTest {

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


    private User user;
    private List<DataPointAccess> fromUser;
    private PermissionsService<DataPointAccess, User> permissionsService;

    public UpdateDataPointUserPermissionsTest(List<DataPointAccess> fromDatabase, List<DataPointAccess> fromUser) {
        this.fromUser = fromUser;
        user = TestUtils.newUser(123);
        user.setDataPointPermissions(fromUser);
        Map<Integer, List<DataPointAccess>> permissions = new HashMap<>();
        permissions.put(user.getId(), fromDatabase);
        permissionsService = new PermissionsServiceUserTestImpl<>(permissions);
    }

    @Test
    public void updatePermissions() {

        //when:
        PermissionsUtils.updateDataPointPermissions(user, permissionsService);
        List<DataPointAccess> result = permissionsService.getPermissions(user);

        //then:
        assertEquals(fromUser.size(), result.size());
        assertTrue("Expected fromUser: " + fromUser + " but was: " + result, result.containsAll(fromUser));
    }
}