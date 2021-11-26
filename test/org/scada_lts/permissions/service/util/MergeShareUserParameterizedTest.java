package org.scada_lts.permissions.service.util;

import com.serotonin.mango.view.ShareUser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(Parameterized.class)
public class MergeShareUserParameterizedTest {

    @Parameterized.Parameters(name= "{index}: permissions1: {0}, permissions2: {1}, expected: {2}")
    public static Object[][] primeNumbers() {
        return new Object[][] {
                new Object[] {
                    Stream.of(new ShareUser(1, 1), new ShareUser(2, 2), new ShareUser(3, 2)).collect(Collectors.toList()),
                    Collections.emptyList(),
                    Stream.of(new ShareUser(1, 1), new ShareUser(2, 2), new ShareUser(3, 2)).collect(Collectors.toList()),
                }, new Object[] {
                        Stream.of(new ShareUser(1, 2), new ShareUser(2, 2), new ShareUser(3, 2)).collect(Collectors.toList()),
                        Stream.of(new ShareUser(4, 2)).collect(Collectors.toList()),
                        Stream.of(new ShareUser(1, 2), new ShareUser(2, 2), new ShareUser(3, 2), new ShareUser(4, 2)).collect(Collectors.toList()),
                }, new Object[] {
                        Stream.of(new ShareUser(1, 1)).collect(Collectors.toList()),
                        Stream.of(new ShareUser(1, 2), new ShareUser(2, 2), new ShareUser(3, 2)).collect(Collectors.toList()),
                        Stream.of(new ShareUser(1, 2), new ShareUser(2, 2), new ShareUser(3, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new ShareUser(4, 1)).collect(Collectors.toList()),
                        Stream.of(new ShareUser(1, 2), new ShareUser(2, 2), new ShareUser(3, 2)).collect(Collectors.toList()),
                        Stream.of(new ShareUser(1, 2), new ShareUser(2, 2), new ShareUser(3, 2), new ShareUser(4, 1)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new ShareUser(1, 1), new ShareUser(1, 1), new ShareUser(2, 2), new ShareUser(3, 2)).collect(Collectors.toList()),
                        Stream.of(new ShareUser(1, 2)).collect(Collectors.toList()),
                        Stream.of(new ShareUser(1, 2), new ShareUser(2, 2), new ShareUser(3, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new ShareUser(1, 1), new ShareUser(2, 2), new ShareUser(3, 2)).collect(Collectors.toList()),
                        Stream.of(new ShareUser(1, 2), new ShareUser(2, 2)).collect(Collectors.toList()),
                        Stream.of(new ShareUser(1, 2), new ShareUser(2, 2), new ShareUser(3, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new ShareUser(1, 1), new ShareUser(2, 2)).collect(Collectors.toList()),
                        Stream.of(new ShareUser(1, 2), new ShareUser(2, 1)).collect(Collectors.toList()),
                        Stream.of(new ShareUser(1, 2), new ShareUser(2, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new ShareUser(1, 1), new ShareUser(2, 2)).collect(Collectors.toList()),
                        Stream.of(new ShareUser(1, 1), new ShareUser(2, 2)).collect(Collectors.toList()),
                        Stream.of(new ShareUser(1, 1), new ShareUser(2, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Collections.emptyList(),
                        Stream.of(new ShareUser(1, 1), new ShareUser(2, 2), new ShareUser(2, 2)).collect(Collectors.toList()),
                        Stream.of(new ShareUser(1, 1), new ShareUser(2, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new ShareUser(1, 1), new ShareUser(2, 2), new ShareUser(2, 1)).collect(Collectors.toList()),
                        Stream.of(new ShareUser(1, 2), new ShareUser(2, 1), new ShareUser(3, 2)).collect(Collectors.toList()),
                        Stream.of(new ShareUser(1, 2), new ShareUser(2, 2), new ShareUser(3, 2)).collect(Collectors.toList()),
                }
        };
    }

    private final List<ShareUser> permissions1;
    private final List<ShareUser> permissions2;
    private final List<ShareUser> permissionsExpected;

    public MergeShareUserParameterizedTest(List<ShareUser> permissions1,
                                           List<ShareUser> permissions2,
                                           List<ShareUser> permissionsExpected) {
        this.permissions1 = Collections.unmodifiableList(permissions1);
        this.permissions2 = Collections.unmodifiableList(permissions2);
        this.permissionsExpected = sort(permissionsExpected);
    }

    @Test
    public void when_merge() {

        //when:
        List<ShareUser> result = sort(PermissionsUtils.merge(permissions1, permissions2));

        //then:
        Assert.assertEquals(permissionsExpected, result);
    }

    @Test
    public void when_merge_reverse_then_identity() {

        //when:
        List<ShareUser> result = sort(PermissionsUtils.merge(permissions2, permissions1));

        //then:
        Assert.assertEquals(permissionsExpected, result);
    }


    private List<ShareUser> sort(Collection<ShareUser> permissions) {
        return permissions.stream().sorted(Comparator.comparing(ShareUser::getUserId)).collect(Collectors.toList());
    }
}
