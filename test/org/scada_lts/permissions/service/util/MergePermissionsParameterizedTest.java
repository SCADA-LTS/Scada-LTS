package org.scada_lts.permissions.service.util;

import br.org.scadabr.vo.permission.Permission;
import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(Parameterized.class)
public class MergePermissionsParameterizedTest {

    @Parameterized.Parameters(name= "{index}: permissions1: {0}, permissions2: {1}, expected: {2}")
    public static Object[][] primeNumbers() {
        return new Object[][] {
                new Object[] {
                        Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2), new ViewAccess(3, 2)).collect(Collectors.toList()),
                        Collections.emptyList(),
                        Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2), new ViewAccess(3, 2)).collect(Collectors.toList()),
                }, new Object[] {
                        Stream.of(new ViewAccess(1, 2), new ViewAccess(2, 2), new ViewAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new ViewAccess(4, 2)).collect(Collectors.toList()),
                        Stream.of(new ViewAccess(1, 2), new ViewAccess(2, 2), new ViewAccess(3, 2), new ViewAccess(4, 2)).collect(Collectors.toList()),
                }, new Object[] {
                        Stream.of(new ViewAccess(1, 1)).collect(Collectors.toList()),
                        Stream.of(new ViewAccess(1, 2), new ViewAccess(2, 2), new ViewAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new ViewAccess(1, 2), new ViewAccess(2, 2), new ViewAccess(3, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new ViewAccess(4, 1)).collect(Collectors.toList()),
                        Stream.of(new ViewAccess(1, 2), new ViewAccess(2, 2), new ViewAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new ViewAccess(1, 2), new ViewAccess(2, 2), new ViewAccess(3, 2), new ViewAccess(4, 1)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new ViewAccess(1, 1), new ViewAccess(1, 1), new ViewAccess(2, 2), new ViewAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new ViewAccess(1, 2)).collect(Collectors.toList()),
                        Stream.of(new ViewAccess(1, 2), new ViewAccess(2, 2), new ViewAccess(3, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2), new ViewAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new ViewAccess(1, 2), new ViewAccess(2, 2)).collect(Collectors.toList()),
                        Stream.of(new ViewAccess(1, 2), new ViewAccess(2, 2), new ViewAccess(3, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2)).collect(Collectors.toList()),
                        Stream.of(new ViewAccess(1, 2), new ViewAccess(2, 1)).collect(Collectors.toList()),
                        Stream.of(new ViewAccess(1, 2), new ViewAccess(2, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2)).collect(Collectors.toList()),
                        Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2)).collect(Collectors.toList()),
                        Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Collections.emptyList(),
                        Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2), new ViewAccess(2, 2)).collect(Collectors.toList()),
                        Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2), new ViewAccess(2, 1)).collect(Collectors.toList()),
                        Stream.of(new ViewAccess(1, 2), new ViewAccess(2, 1), new ViewAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new ViewAccess(1, 2), new ViewAccess(2, 2), new ViewAccess(3, 2)).collect(Collectors.toList()),
                }, new Object[] {
                        Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2), new WatchListAccess(3, 2)).collect(Collectors.toList()),
                        Collections.emptyList(),
                        Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2), new WatchListAccess(3, 2)).collect(Collectors.toList()),
                }, new Object[] {
                        Stream.of(new WatchListAccess(1, 2), new WatchListAccess(2, 2), new WatchListAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new WatchListAccess(4, 2)).collect(Collectors.toList()),
                        Stream.of(new WatchListAccess(1, 2), new WatchListAccess(2, 2), new WatchListAccess(3, 2), new WatchListAccess(4, 2)).collect(Collectors.toList()),
                }, new Object[] {
                        Stream.of(new WatchListAccess(1, 1)).collect(Collectors.toList()),
                        Stream.of(new WatchListAccess(1, 2), new WatchListAccess(2, 2), new WatchListAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new WatchListAccess(1, 2), new WatchListAccess(2, 2), new WatchListAccess(3, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new WatchListAccess(4, 1)).collect(Collectors.toList()),
                        Stream.of(new WatchListAccess(1, 2), new WatchListAccess(2, 2), new WatchListAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new WatchListAccess(1, 2), new WatchListAccess(2, 2), new WatchListAccess(3, 2), new WatchListAccess(4, 1)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new WatchListAccess(1, 1), new WatchListAccess(1, 1), new WatchListAccess(2, 2), new WatchListAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new WatchListAccess(1, 2)).collect(Collectors.toList()),
                        Stream.of(new WatchListAccess(1, 2), new WatchListAccess(2, 2), new WatchListAccess(3, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2), new WatchListAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new WatchListAccess(1, 2), new WatchListAccess(2, 2)).collect(Collectors.toList()),
                        Stream.of(new WatchListAccess(1, 2), new WatchListAccess(2, 2), new WatchListAccess(3, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2)).collect(Collectors.toList()),
                        Stream.of(new WatchListAccess(1, 2), new WatchListAccess(2, 1)).collect(Collectors.toList()),
                        Stream.of(new WatchListAccess(1, 2), new WatchListAccess(2, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2)).collect(Collectors.toList()),
                        Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2)).collect(Collectors.toList()),
                        Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Collections.emptyList(),
                        Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2), new WatchListAccess(2, 2)).collect(Collectors.toList()),
                        Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2), new WatchListAccess(2, 1)).collect(Collectors.toList()),
                        Stream.of(new WatchListAccess(1, 2), new WatchListAccess(2, 1), new WatchListAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new WatchListAccess(1, 2), new WatchListAccess(2, 2), new WatchListAccess(3, 2)).collect(Collectors.toList()),
                }
        };
    }

    private final Set<Permission> permissions1;
    private final Set<Permission> permissions2;
    private final List<Permission> permissionsExpected;

    public MergePermissionsParameterizedTest(Collection<Permission> permissions1,
                                             Collection<Permission> permissions2,
                                             Collection<Permission> permissionsExpected) {
        this.permissions1 = Collections.unmodifiableSet(new HashSet<>(permissions1));
        this.permissions2 = Collections.unmodifiableSet(new HashSet<>(permissions2));
        this.permissionsExpected = sort(permissionsExpected);
    }

    @Test
    public void when_merge() {

        //when:
        List<Permission> result = sort(PermissionsUtils.merge(permissions1, permissions2));

        //then:
        Assert.assertEquals(permissionsExpected, result);
    }

    @Test
    public void when_merge_reverse_then_identity() {

        //when:
        List<Permission> result = sort(PermissionsUtils.merge(permissions2, permissions1));

        //then:
        Assert.assertEquals(permissionsExpected, result);
    }

    private List<Permission> sort(Collection<Permission> permissions) {
        return permissions.stream().sorted(Comparator.comparing(Permission::getId)).collect(Collectors.toList());
    }
}
