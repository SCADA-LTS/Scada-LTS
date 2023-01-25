package org.scada_lts.dao;

import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.scada_lts.dao.cache.PointEventDetectorCache;
import org.scada_lts.dao.cache.PointEventDetectorDaoWithCache;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class IsEventDetectorXidUniqueTest {

    @Parameterized.Parameters(name= "{index}: detectors: {0}, {1}, {2}, xid: {3}, exclude id: {4}, result: {5}")
    public static Object[][] primeNumbers() {
        return new Object[][] {
                new Object[] {
                        new PointEventDetectorVO(1, "PED_123"),
                        new PointEventDetectorVO(2, "PED_124"),
                        new PointEventDetectorVO(3, "PED_125"),
                        "PED_126",
                        0,
                        true
                },
                new Object[] {
                        new PointEventDetectorVO(1, "PED_123"),
                        new PointEventDetectorVO(2, "PED_124"),
                        new PointEventDetectorVO(3, "PED_125"),
                        "PED_126",
                        0,
                        true
                },
                new Object[] {
                        new PointEventDetectorVO(1, "PED_123"),
                        new PointEventDetectorVO(2, "PED_124"),
                        new PointEventDetectorVO(3, "PED_125"),
                        "PED_125",
                        0,
                        false
                },
                new Object[] {
                        new PointEventDetectorVO(1, "PED_123"),
                        new PointEventDetectorVO(2, "PED_124"),
                        new PointEventDetectorVO(3, "PED_125"),
                        "PED_125",
                        0,
                        false
                },
                new Object[] {
                        new PointEventDetectorVO(1, "PED_123"),
                        new PointEventDetectorVO(2, "PED_124"),
                        new PointEventDetectorVO(3, "PED_125"),
                        "PED_126",
                        1,
                        true
                },
                new Object[] {
                        new PointEventDetectorVO(1, "PED_123"),
                        new PointEventDetectorVO(2, "PED_124"),
                        new PointEventDetectorVO(3, "PED_125"),
                        "PED_126",
                        1,
                        true
                },
                new Object[] {
                        new PointEventDetectorVO(1, "PED_123"),
                        new PointEventDetectorVO(2, "PED_124"),
                        new PointEventDetectorVO(3, "PED_125"),
                        "PED_123",
                        1,
                        true
                },
                new Object[] {
                        new PointEventDetectorVO(1, "PED_123"),
                        new PointEventDetectorVO(2, "PED_124"),
                        new PointEventDetectorVO(3, "PED_125"),
                        "PED_123",
                        1,
                        true
                },
                new Object[] {
                        new PointEventDetectorVO(1, "PED_123"),
                        new PointEventDetectorVO(2, "PED_124"),
                        new PointEventDetectorVO(3, "PED_125"),
                        "PED_123",
                        2,
                        false
                },
                new Object[] {
                        new PointEventDetectorVO(1, "PED_123"),
                        new PointEventDetectorVO(2, "PED_124"),
                        new PointEventDetectorVO(3, "PED_125"),
                        "PED_123",
                        2,
                        false
                },
                new Object[] {
                        new PointEventDetectorVO(1, null),
                        new PointEventDetectorVO(2, null),
                        new PointEventDetectorVO(3, null),
                        "PED_126",
                        1,
                        true
                },
                new Object[] {
                        new PointEventDetectorVO(1, "PED_123"),
                        new PointEventDetectorVO(2, null),
                        new PointEventDetectorVO(3, null),
                        "PED_123",
                        2,
                        false
                },
        };
    }


    private final PointEventDetectorVO pointEventDetector1;
    private final PointEventDetectorVO pointEventDetector2;
    private final PointEventDetectorVO pointEventDetector3;
    private final String xid;
    private final int exclusiveId;
    private final boolean expected;

    public IsEventDetectorXidUniqueTest(PointEventDetectorVO pointEventDetector1, PointEventDetectorVO pointEventDetector2,
                                        PointEventDetectorVO pointEventDetector3, String xid, int exclusiveId, boolean expected) {
        this.pointEventDetector1 = pointEventDetector1;
        this.pointEventDetector2 = pointEventDetector2;
        this.pointEventDetector3 = pointEventDetector3;
        this.xid = xid;
        this.exclusiveId = exclusiveId;
        this.expected = expected;
    }

    private IPointEventDetectorDAO subject;

    @Before
    public void config() {
        PointEventDetectorDAO pointEventDetectorDaoMock = mock(PointEventDetectorDAO.class);
        PointEventDetectorCache pointEventDetectorCache = new PointEventDetectorCache(pointEventDetectorDaoMock);
        subject = new PointEventDetectorDaoWithCache(pointEventDetectorCache);

        when(pointEventDetectorDaoMock.getPointEventDetector(eq(pointEventDetector1.getXid()))).thenReturn(pointEventDetector1);
        when(pointEventDetectorDaoMock.getPointEventDetector(eq(pointEventDetector2.getXid()))).thenReturn(pointEventDetector2);
        when(pointEventDetectorDaoMock.getPointEventDetector(eq(pointEventDetector3.getXid()))).thenReturn(pointEventDetector3);
    }

    @Test
    public void when_isEventDetectorXidUnique() {

        //when
        boolean result = subject.isEventDetectorXidUnique(-1, xid, exclusiveId);

        //then:
        assertEquals(expected, result);
    }
}