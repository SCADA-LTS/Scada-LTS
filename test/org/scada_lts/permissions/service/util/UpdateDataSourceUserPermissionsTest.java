package org.scada_lts.permissions.service.util;

import br.org.scadabr.db.utils.TestUtils;
import com.serotonin.mango.vo.User;
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
public class UpdateDataSourceUserPermissionsTest {

    @Parameterized.Parameters(name= "{index}: fromDatabase: {0}, fromUser: {1}")
    public static Object[][] primeNumbers() {
        return new Object[][] {new Object[] {
                        Stream.of(1, 2, 3).collect(Collectors.toList()),
                        Collections.emptyList()
                }, new Object[] {
                        Stream.of(1, 2, 3).collect(Collectors.toList()),
                        Stream.of(4).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(1).collect(Collectors.toList()),
                        Stream.of(1, 2, 3).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(4).collect(Collectors.toList()),
                        Stream.of(1, 2, 3).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(1, 2, 3).collect(Collectors.toList()),
                        Stream.of(1).collect(Collectors.toList())
                }, new Object[] {
                        new ArrayList<>(),
                        Stream.of(1, 2, 3).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(1, 2, 3).collect(Collectors.toList()),
                        Stream.of(1, 2, 3).collect(Collectors.toList())
                }
        };
    }


    private User user;
    private List<Integer> fromUser;
    private PermissionsService<Integer, User> permissionsService;

    public UpdateDataSourceUserPermissionsTest(List<Integer> fromDatabase, List<Integer> fromUser) {
        this.fromUser = fromUser;
        user = TestUtils.newUser(123);
        user.setDataSourcePermissions(fromUser);
        Map<Integer, List<Integer>> permissions = new HashMap<>();
        permissions.put(user.getId(), fromDatabase);
        permissionsService = new PermissionsServiceUserTestImpl<>(permissions);
    }

    @Test
    public void updatePermissions() {

        //when:
        PermissionsUtils.updateDataSourcePermissions(user, permissionsService);
        List<Integer> result = permissionsService.getPermissions(user);

        //then:
        assertEquals(fromUser.size(), result.size());
        assertTrue("Expected fromUser: " + fromUser + " but was: " + result, result.containsAll(fromUser));
    }
}