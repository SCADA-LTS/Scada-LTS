package br.org.scadabr.vo;

import br.org.scadabr.vo.permission.Permission;
import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Random;


@RunWith(Parameterized.class)
public class EqualsHashcodePermissionTest {

    @Parameterized.Parameters(name= "{index}: \npermission1: {0}, permission2: {1}, permission3: {2}, equals12: {3}, equals23: {4}, equals13: {5} \n")
    public static Object[][] data() {
        Random random = new Random();
        int randomId = random.nextInt(10000);
        int randomPermission = random.nextInt(3);
        return new Object[][] {
                new Object[] {
                        new WatchListAccess(1, 2),
                        new WatchListAccess(1, 2),
                        new WatchListAccess(1, 2),
                        true,
                        true,
                        true
                }, new Object[] {
                        new WatchListAccess(1, 2),
                        new WatchListAccess(2, 2),
                        new WatchListAccess(3, 2),
                        false,
                        false,
                        false
                }, new Object[] {
                        new ViewAccess(1, 2),
                        new WatchListAccess(1, 2),
                        new WatchListAccess(1, 2),
                        false,
                        true,
                        false
                }, new Object[] {
                        new WatchListAccess(1, 2),
                        new ViewAccess(1, 2),
                        new WatchListAccess(1, 2),
                        false,
                        false,
                        true
                }, new Object[] {
                        new WatchListAccess(1, 2),
                        new WatchListAccess(1, 2),
                        new ViewAccess(1, 2),
                        true,
                        false,
                        false
                }, new Object[] {
                        new ViewAccess(1, 2),
                        new ViewAccess(1, 2),
                        new ViewAccess(1, 2),
                        true,
                        true,
                        true
                }, new Object[] {
                        new ViewAccess(1, 2),
                        new ViewAccess(2, 2),
                        new ViewAccess(3, 2),
                        false,
                        false,
                        false
                }, new Object[] {
                        new WatchListAccess(1, 2),
                        new ViewAccess(1, 2),
                        new ViewAccess(1, 2),
                        false,
                        true,
                        false
                }, new Object[] {
                        new ViewAccess(1, 2),
                        new WatchListAccess(1, 2),
                        new ViewAccess(1, 2),
                        false,
                        false,
                        true
                }, new Object[] {
                        new ViewAccess(1, 2),
                        new ViewAccess(1, 2),
                        new WatchListAccess(1, 2),
                        true,
                        false,
                        false
                }, new Object[] {
                        new WatchListAccess(2, 2),
                        new WatchListAccess(2, 2),
                        new WatchListAccess(2, 2),
                        true,
                        true,
                        true
                }, new Object[] {
                        new WatchListAccess(2, 2),
                        new WatchListAccess(2, 1),
                        new WatchListAccess(2, 1),
                        false,
                        true,
                        false
                }, new Object[] {
                        new ViewAccess(2, 2),
                        new WatchListAccess(2, 2),
                        new WatchListAccess(2, 2),
                        false,
                        true,
                        false
                }, new Object[] {
                        new WatchListAccess(2, 2),
                        new ViewAccess(2, 2),
                        new WatchListAccess(2, 2),
                        false,
                        false,
                        true
                }, new Object[] {
                        new WatchListAccess(2, 2),
                        new WatchListAccess(2, 2),
                        new ViewAccess(2, 2),
                        true,
                        false,
                        false
                }, new Object[] {
                        new ViewAccess(2, 2),
                        new ViewAccess(2, 2),
                        new ViewAccess(2, 2),
                        true,
                        true,
                        true
                }, new Object[] {
                        new ViewAccess(2, 2),
                        new ViewAccess(2, 1),
                        new ViewAccess(2, 2),
                        false,
                        false,
                        true
                }, new Object[] {
                        new WatchListAccess(2, 2),
                        new ViewAccess(2, 2),
                        new ViewAccess(2, 2),
                        false,
                        true,
                        false
                }, new Object[] {
                        new ViewAccess(2, 2),
                        new WatchListAccess(2, 2),
                        new ViewAccess(2, 2),
                        false,
                        false,
                        true
                }, new Object[] {
                        new ViewAccess(2, 2),
                        new ViewAccess(2, 2),
                        new WatchListAccess(2, 2),
                        true,
                        false,
                        false
                }, new Object[] {
                        new ViewAccess(randomId, randomPermission),
                        new ViewAccess(randomId, randomPermission),
                        new ViewAccess(randomId, randomPermission),
                        true,
                        true,
                        true
                }, new Object[] {
                        new ViewAccess(randomId, randomPermission),
                        new ViewAccess(randomId, randomPermission),
                        new WatchListAccess(randomId, randomPermission),
                        true,
                        false,
                        false
                }, new Object[] {
                        new ViewAccess(randomId, randomPermission),
                        new WatchListAccess(randomId, randomPermission),
                        new ViewAccess(randomId, randomPermission),
                        false,
                        false,
                        true
                }, new Object[] {
                        new WatchListAccess(randomId, randomPermission),
                        new ViewAccess(randomId, randomPermission),
                        new ViewAccess(randomId, randomPermission),
                        false,
                        true,
                        false
                },
        };
    }

    private Permission permission1;
    private Permission permission2;
    private Permission permission3;
    private boolean equals12;
    private boolean equals23;
    private boolean equals13;

    public EqualsHashcodePermissionTest(Permission permission1, Permission permission2, Permission permission3,
                                        boolean equals12, boolean equals23, boolean equals13) {
        this.permission1 = permission1;
        this.permission2 = permission2;
        this.permission3 = permission3;
        this.equals12 = equals12;
        this.equals23 = equals23;
        this.equals13 = equals13;
    }

    @Test
    public void when_equals_inverse() {
        Assert.assertEquals(equals12, permission1.equals(permission2));
        Assert.assertEquals(equals12, permission2.equals(permission1));

        Assert.assertEquals(equals23, permission3.equals(permission2));
        Assert.assertEquals(equals23, permission2.equals(permission3));

        Assert.assertEquals(equals13, permission1.equals(permission3));
        Assert.assertEquals(equals13, permission3.equals(permission1));
    }

    @Test
    public void when_equals_transitivity() {
        Assert.assertEquals(equals12, permission1.equals(permission2));
        Assert.assertEquals(equals23, permission2.equals(permission3));
        Assert.assertEquals(equals13, permission1.equals(permission3));
    }

    @Test
    public void when_equals_same() {
        Assert.assertEquals(permission1, permission1);
        Assert.assertEquals(permission2, permission2);
        Assert.assertEquals(permission3, permission3);
    }

    @Test
    public void when_equals_invariability() {
        for (int i = 0; i < 100; i++) {
            Assert.assertEquals(equals12, permission1.equals(permission2));
            Assert.assertEquals(equals23, permission2.equals(permission3));
            Assert.assertEquals(equals13, permission1.equals(permission3));
        }
    }

    @Test
    public void when_hashcode_different_then_not_equals() {
        if(permission1.hashCode() != permission2.hashCode()) {
            Assert.assertNotEquals(permission1, permission2);
        }

        if(permission2.hashCode() != permission3.hashCode()) {
            Assert.assertNotEquals(permission2, permission3);
        }

        if(permission1.hashCode() != permission3.hashCode()) {
            Assert.assertNotEquals(permission1, permission3);
        }
    }
}
