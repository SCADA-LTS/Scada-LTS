package org.scada_lts.permissions.service.util;

import com.serotonin.mango.vo.permission.DataPointAccess;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(Parameterized.class)
public class MergeDataPointPermissionsParameterizedTest {

    @Parameterized.Parameters(name= "{index}: permissions1: {0}, permissions2: {1}, expected: {2}")
    public static Object[][] primeNumbers() {
        return new Object[][] {
                new Object[] {
                    Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2), new DataPointAccess(3, 2)).collect(Collectors.toList()),
                    Collections.emptyList(),
                    Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2), new DataPointAccess(3, 2)).collect(Collectors.toList()),
                }, new Object[] {
                        Stream.of(new DataPointAccess(1, 2), new DataPointAccess(2, 2), new DataPointAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(4, 2)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 2), new DataPointAccess(2, 2), new DataPointAccess(3, 2), new DataPointAccess(4, 2)).collect(Collectors.toList()),
                }, new Object[] {
                        Stream.of(new DataPointAccess(1, 1)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 2), new DataPointAccess(2, 2), new DataPointAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 2), new DataPointAccess(2, 2), new DataPointAccess(3, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new DataPointAccess(4, 1)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 2), new DataPointAccess(2, 2), new DataPointAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 2), new DataPointAccess(2, 2), new DataPointAccess(3, 2), new DataPointAccess(4, 1)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new DataPointAccess(1, 1), new DataPointAccess(1, 1), new DataPointAccess(2, 2), new DataPointAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 2)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 2), new DataPointAccess(2, 2), new DataPointAccess(3, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2), new DataPointAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 2), new DataPointAccess(2, 2)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 2), new DataPointAccess(2, 2), new DataPointAccess(3, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 2), new DataPointAccess(2, 1)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 2), new DataPointAccess(2, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Collections.emptyList(),
                        Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2), new DataPointAccess(2, 2)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2)).collect(Collectors.toList())
                }, new Object[] {
                        Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2), new DataPointAccess(2, 1)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 2), new DataPointAccess(2, 1), new DataPointAccess(3, 2)).collect(Collectors.toList()),
                        Stream.of(new DataPointAccess(1, 2), new DataPointAccess(2, 2), new DataPointAccess(3, 2)).collect(Collectors.toList()),
                }
        };
    }

    private final Collection<DataPointAccess> permissions1;
    private final Collection<DataPointAccess> permissions2;
    private final List<DataPointAccess> permissionsExpected;
    private final Predicate<DataPointAccess> byDataPointIdFilter = a -> a.getDataPointId() == 1;

    public MergeDataPointPermissionsParameterizedTest(Collection<DataPointAccess> permissions1,
                                                      Collection<DataPointAccess> permissions2,
                                                      Collection<DataPointAccess> permissionsExpected) {
        this.permissions1 = Collections.unmodifiableCollection(permissions1);
        this.permissions2 = Collections.unmodifiableCollection(permissions2);
        this.permissionsExpected = sort(permissionsExpected);
    }

    @Test
    public void when_mergeDataPointAccesses() {

        //when:
        List<DataPointAccess> result = sort(PermissionsUtils.mergeDataPointAccesses(permissions1, permissions2));

        //then:
        Assert.assertEquals(permissionsExpected, result);
    }

    @Test
    public void when_mergeDataPointAccesses_reverse_then_identity() {

        //when:
        List<DataPointAccess> result = sort(PermissionsUtils.mergeDataPointAccesses(permissions2, permissions1));

        //then:
        Assert.assertEquals(permissionsExpected, result);
    }

    @Test
    public void when_mergeDataPointAccesses_reverse_then_one() {

        //when:
        Set<DataPointAccess> result = PermissionsUtils.mergeDataPointAccesses(permissions2, permissions1, byDataPointIdFilter);

        //then:
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void when_mergeDataPointAccesses_then_one() {

        //when:
        Set<DataPointAccess> result = PermissionsUtils.mergeDataPointAccesses(permissions2, permissions1, byDataPointIdFilter);

        //then:
        Assert.assertEquals(1, result.size());
    }

    @Test
    public void when_mergeDataPointAccesses_filter() {

        //when:
        Set<DataPointAccess> result = PermissionsUtils.mergeDataPointAccesses(permissions1, permissions2, byDataPointIdFilter);
        Set<DataPointAccess> expected = filtering(permissionsExpected, byDataPointIdFilter);

        //then:
        Assert.assertEquals(expected, result);
    }

    @Test
    public void when_mergeDataPointAccesses_filter_reverse_then_identity() {

        //when:
        Set<DataPointAccess> result = PermissionsUtils.mergeDataPointAccesses(permissions2, permissions1, byDataPointIdFilter);
        Set<DataPointAccess> expected = filtering(permissionsExpected, byDataPointIdFilter);

        //then:
        Assert.assertEquals(expected, result);
    }

    @Test
    public void when_merge_filter_then_not_empty() {

        //when:
        Set<DataPointAccess> result = PermissionsUtils.mergeDataPointAccesses(permissions1, permissions2, byDataPointIdFilter);

        //then:
        Assert.assertNotEquals(0, result.size());
    }

    @Test
    public void when_merge_filter_reverse_then_not_empty() {

        //when:
        Set<DataPointAccess> result = PermissionsUtils.mergeDataPointAccesses(permissions2, permissions1, byDataPointIdFilter);

        //then:
        Assert.assertNotEquals(0, result.size());
    }

    private List<DataPointAccess> sort(Collection<DataPointAccess> permissions) {
        return permissions.stream().sorted(Comparator.comparing(DataPointAccess::getDataPointId)).collect(Collectors.toList());
    }

    private <T> Set<T> filtering(List<T> expected, Predicate<T> filter) {
        return expected.stream().filter(filter).collect(Collectors.toSet());
    }
}
