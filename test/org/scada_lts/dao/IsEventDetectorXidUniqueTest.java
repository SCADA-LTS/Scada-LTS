package org.scada_lts.dao;

import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.dao.cache.PointEventDetectorCache;
import org.scada_lts.dao.cache.PointEventDetectorDaoWithCache;
import org.scada_lts.dao.model.PointEventDetectorCacheEntry;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class IsEventDetectorXidUniqueTest {

    @Parameterized.Parameters(name= "{index}: detectors: {0}, xid: {1}, exclude id: {2}, result: {3}")
    public static Object[][] primeNumbers() {
        return new Object[][] {
                new Object[] {
                        Arrays.asList(
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(1, "PED_123"), 4),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(2, "PED_124"), 4),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(3, "PED_125"), 4)
                        ),
                        "PED_126",
                        0,
                        true
                },
                new Object[] {
                        Arrays.asList(
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(1, "PED_123"), 4),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(2, "PED_124"), 5),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(3, "PED_125"), 6)
                        ),
                        "PED_126",
                        0,
                        true
                },
                new Object[] {
                        Arrays.asList(
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(1, "PED_123"), 4),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(2, "PED_124"), 4),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(3, "PED_125"), 4)
                        ),
                        "PED_125",
                        0,
                        false
                },
                new Object[] {
                        Arrays.asList(
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(1, "PED_123"), 4),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(2, "PED_124"), 5),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(3, "PED_125"), 6)
                        ),
                        "PED_125",
                        0,
                        false
                },
                new Object[] {
                        Arrays.asList(
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(1, "PED_123"), 4),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(2, "PED_124"), 4),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(3, "PED_125"), 4)
                        ),
                        "PED_126",
                        1,
                        true
                },
                new Object[] {
                        Arrays.asList(
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(1, "PED_123"), 4),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(2, "PED_124"), 5),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(3, "PED_125"), 6)
                        ),
                        "PED_126",
                        1,
                        true
                },
                new Object[] {
                        Arrays.asList(
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(1, "PED_123"), 4),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(2, "PED_124"), 4),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(3, "PED_125"), 4)
                        ),
                        "PED_123",
                        1,
                        true
                },
                new Object[] {
                        Arrays.asList(
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(1, "PED_123"), 4),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(2, "PED_124"), 5),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(3, "PED_125"), 6)
                        ),
                        "PED_123",
                        1,
                        true
                },
                new Object[] {
                        Arrays.asList(
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(1, "PED_123"), 4),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(2, "PED_124"), 4),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(3, "PED_125"), 4)
                        ),
                        "PED_123",
                        2,
                        false
                },
                new Object[] {
                        Arrays.asList(
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(1, "PED_123"), 4),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(2, "PED_124"), 5),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(3, "PED_125"), 6)
                        ),
                        "PED_123",
                        2,
                        false
                },
                new Object[] {
                        Arrays.asList(
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(1, null), 4),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(2, null), 4),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(3, null), 4)
                        ),
                        "PED_126",
                        1,
                        true
                },
                new Object[] {
                        Arrays.asList(
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(1, "PED_123"), 4),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(2, null), 5),
                                new PointEventDetectorCacheEntry(new PointEventDetectorVO(3, null), 6)
                        ),
                        "PED_123",
                        2,
                        false
                },
        };
    }


    private final List<PointEventDetectorCacheEntry> pointEventDetectors;
    private final String xid;
    private final int exclusiveId;
    private final boolean expected;

    public IsEventDetectorXidUniqueTest(List<PointEventDetectorCacheEntry> pointEventDetectors, String xid,
                                        int exclusiveId, boolean expected) {
        this.pointEventDetectors = pointEventDetectors;
        this.xid = xid;
        this.exclusiveId = exclusiveId;
        this.expected = expected;
    }

    private IPointEventDetectorDAO subject;

    @Before
    public void config() {
        PointEventDetectorDAO pointEventDetectorDaoMock = mock(PointEventDetectorDAO.class);
        subject = new PointEventDetectorDaoWithCache(new PointEventDetectorCache(pointEventDetectorDaoMock));
        when(pointEventDetectorDaoMock.getAll()).thenReturn(pointEventDetectors);
    }

    @Test
    public void when_isEventDetectorXidUnique() {

        //when
        boolean result = subject.isEventDetectorXidUnique(-1, xid, exclusiveId);

        //then:
        assertEquals(expected, result);
    }
}