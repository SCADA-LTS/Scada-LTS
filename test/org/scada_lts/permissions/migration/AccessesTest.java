package org.scada_lts.permissions.migration;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(Parameterized.class)
public class AccessesTest {

    @Parameterized.Parameters(name= "{index}: \nbase: {0}, toCompare: {1}, \nequals: {2}\n\n")
    public static Object[][] accesses() {

        Accesses base = new Accesses(Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2)).collect(Collectors.toSet()),
                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2)).collect(Collectors.toSet()),
                Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2)).collect(Collectors.toSet()),
                Stream.of(12).collect(Collectors.toSet()));

        return new Object[][] {
                new Object[] {base,
                        new Accesses(Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2), new ViewAccess(3, 0)).collect(Collectors.toSet()),
                                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2)).collect(Collectors.toSet()),
                                Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2)).collect(Collectors.toSet()),
                                Stream.of(12).collect(Collectors.toSet())),
                        true
                },
                new Object[] {base,
                        new Accesses(Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2)).collect(Collectors.toSet()),
                                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2), new WatchListAccess(3, 0)).collect(Collectors.toSet()),
                                Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2)).collect(Collectors.toSet()),
                                Stream.of(12).collect(Collectors.toSet())),
                        true
                },
                new Object[] {base,
                        new Accesses(Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2)).collect(Collectors.toSet()),
                                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2)).collect(Collectors.toSet()),
                                Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2), new DataPointAccess(3, 0)).collect(Collectors.toSet()),
                                Stream.of(12).collect(Collectors.toSet())),
                        true
                },
                new Object[] {base,
                        new Accesses(Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2)).collect(Collectors.toSet()),
                                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2)).collect(Collectors.toSet()),
                                Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2)).collect(Collectors.toSet()),
                                Stream.of(12).collect(Collectors.toSet())),
                        true
                },
                new Object[] {base,
                        new Accesses(Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2), new ViewAccess(3, 0)).collect(Collectors.toSet()),
                                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2), new WatchListAccess(3, 0)).collect(Collectors.toSet()),
                                Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2), new DataPointAccess(3, 0)).collect(Collectors.toSet()),
                                Stream.of(12).collect(Collectors.toSet())),
                        true
                },
                new Object[] {base,
                        new Accesses(Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2), new ViewAccess(2, 0)).collect(Collectors.toSet()),
                                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2), new WatchListAccess(2, 0)).collect(Collectors.toSet()),
                                Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2), new DataPointAccess(2, 0)).collect(Collectors.toSet()),
                                Stream.of(12).collect(Collectors.toSet())),
                        true
                },
                new Object[] {base,
                        new Accesses(Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2), new ViewAccess(3, 0)).collect(Collectors.toSet()),
                                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2)).collect(Collectors.toSet()),
                                Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2)).collect(Collectors.toSet()),
                                Stream.of(12).collect(Collectors.toSet())),
                        true
                },
                new Object[] {base,
                        new Accesses(Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2)).collect(Collectors.toSet()),
                                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2), new WatchListAccess(3, 0)).collect(Collectors.toSet()),
                                Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2)).collect(Collectors.toSet()),
                                Stream.of(12).collect(Collectors.toSet())),
                        true
                },
                new Object[] {
                        new Accesses(Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2)).collect(Collectors.toSet()),
                                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2)).collect(Collectors.toSet()),
                                Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2), new DataPointAccess(3, 0)).collect(Collectors.toSet()),
                                Stream.of(12).collect(Collectors.toSet())),
                        base,
                        true
                },
                new Object[] {
                        new Accesses(Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2)).collect(Collectors.toSet()),
                                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2)).collect(Collectors.toSet()),
                                Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2)).collect(Collectors.toSet()),
                                Stream.of(12).collect(Collectors.toSet())),
                        base,
                        true
                },
                new Object[] {
                        new Accesses(Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2), new ViewAccess(3, 0)).collect(Collectors.toSet()),
                                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2), new WatchListAccess(3, 0)).collect(Collectors.toSet()),
                                Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2), new DataPointAccess(3, 0)).collect(Collectors.toSet()),
                                Stream.of(12).collect(Collectors.toSet())),
                        base,
                        true
                },
                new Object[] {
                        new Accesses(Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2), new ViewAccess(2, 0)).collect(Collectors.toSet()),
                                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2), new WatchListAccess(2, 0), new WatchListAccess(3, 0)).collect(Collectors.toSet()),
                                Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2), new DataPointAccess(2, 0), new DataPointAccess(2, 0)).collect(Collectors.toSet()),
                                Stream.of(12).collect(Collectors.toSet())),
                        base,
                        true
                },
                new Object[] {
                        new Accesses(Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2), new ViewAccess(2, 0)).collect(Collectors.toSet()),
                                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2), new WatchListAccess(2, 0), new WatchListAccess(3, 1)).collect(Collectors.toSet()),
                                Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2), new DataPointAccess(2, 0), new DataPointAccess(2, 0)).collect(Collectors.toSet()),
                                Stream.of(12).collect(Collectors.toSet())),
                        base,
                        false
                },
                new Object[] {
                        new Accesses(Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2), new ViewAccess(2, 0)).collect(Collectors.toSet()),
                                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2), new WatchListAccess(2, 1), new WatchListAccess(3, 0)).collect(Collectors.toSet()),
                                Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2), new DataPointAccess(2, 0), new DataPointAccess(2, 0)).collect(Collectors.toSet()),
                                Stream.of(12).collect(Collectors.toSet())),
                        base,
                        false
                },
                new Object[] {
                        new Accesses(Stream.of(new ViewAccess(1, 1), new ViewAccess(2, 2), new ViewAccess(2, 0)).collect(Collectors.toSet()),
                                Stream.of(new WatchListAccess(1, 1), new WatchListAccess(2, 2), new WatchListAccess(2, 1), new WatchListAccess(3, 1)).collect(Collectors.toSet()),
                                Stream.of(new DataPointAccess(1, 1), new DataPointAccess(2, 2), new DataPointAccess(2, 0), new DataPointAccess(2, 0)).collect(Collectors.toSet()),
                                Stream.of(12).collect(Collectors.toSet())),
                        base,
                        false
                }


        };
    }

    private Accesses base;
    private Accesses toCompare;
    private boolean identical;

    public AccessesTest(Accesses base, Accesses toCompare, boolean identical) {
        this.base = base;
        this.toCompare = toCompare;
        this.identical = identical;
    }

    @Test
    public void test() {
        //then:
        if(identical)
            Assert.assertEquals(base, toCompare);
        else
            Assert.assertNotEquals(base, toCompare);
    }
}