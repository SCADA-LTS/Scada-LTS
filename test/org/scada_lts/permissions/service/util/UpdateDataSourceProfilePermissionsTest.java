package org.scada_lts.permissions.service.util;

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
public class UpdateDataSourceProfilePermissionsTest {

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


    private UsersProfileVO profile;
    private List<Integer> fromUser;
    private PermissionsService<Integer, UsersProfileVO> permissionsService;

    public UpdateDataSourceProfilePermissionsTest(List<Integer> fromDatabase, List<Integer> fromUser) {
        this.fromUser = fromUser;
        profile = new UsersProfileVO();
        profile.setId(123);
        profile.setDataSourcePermissions(fromUser);
        Map<Integer, List<Integer>> permissions = new HashMap<>();
        permissions.put(profile.getId(), fromDatabase);
        permissionsService = new PermissionsServiceProfileTestImpl<>(permissions);
    }

    @Test
    public void updatePermissions() {

        //when:
        PermissionsUtils.updateDataSourcePermissions(profile, permissionsService);
        List<Integer> result = permissionsService.getPermissions(profile);

        //then:
        assertEquals(fromUser.size(), result.size());
        assertTrue("Expected fromUser: " + fromUser + " but was: " + result, result.containsAll(fromUser));
    }
}