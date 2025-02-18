package org.scada_lts.utils;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.maint.BackgroundProcessing;
import org.junit.*;
import utils.TestUtils;
import com.serotonin.mango.vo.DataPointVO;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static utils.mock.MockitoUtils.mockBackgroundProcessing;
import static utils.mock.MockitoUtils.mockContextWrapper;

@RunWith(Parameterized.class)
public class CyclicDependencyValidationUtilsTest {

    @Parameterized.Parameters(name = "{index}: starDataPointId: {0}, findDataPointId: {1}, cyclicDependency: {2}")
    public static Object[][] data() {
        return new Object[][] {
                { 1, 1, true },
                { 2, 1, false },
                { 3, 1, false },
                { 4, 1, false },
                { 5, 1, false },
                { 6, 1, false },
                { 7, 1, false },
                { 1, 2, true },
                { 2, 2, true },
                { 3, 2, true },
                { 4, 2, true },
                { 5, 2, false },
                { 6, 2, false },
                { 7, 2, false },
                { 1, 3, true },
                { 2, 3, true },
                { 3, 3, true },
                { 4, 3, true },
                { 5, 3, false },
                { 6, 3, false },
                { 7, 3, false },
                { 1, 4, true },
                { 2, 4, true },
                { 3, 4, true },
                { 4, 4, true },
                { 5, 4, false },
                { 6, 4, false },
                { 7, 4, false },
                { 1, 5, false },
                { 2, 5, false },
                { 3, 5, false },
                { 4, 5, false },
                { 5, 5, true },
                { 6, 5, true },
                { 7, 5, false },
                { 1, 6, false },
                { 2, 6, false },
                { 3, 6, false },
                { 4, 6, false },
                { 5, 6, false },
                { 6, 6, true },
                { 7, 6, false }
        };
    }
    private final int starDataPointId;
    private final int findDataPointId;
    private final boolean cyclicDependencyExpected;

    public CyclicDependencyValidationUtilsTest(int starDataPointId, int findDataPointId, boolean cyclicDependency) {
        this.starDataPointId = starDataPointId;
        this.findDataPointId = findDataPointId;
        this.cyclicDependencyExpected = cyclicDependency;
    }

    private static Map<Integer, DataPointVO> DATA_POINTS;

    private static BackgroundProcessing backgroundProcessing;

    @AfterClass
    public static void clean() {
        backgroundProcessing.terminate();
    }

    @BeforeClass
    public static void config() {

        backgroundProcessing = mockBackgroundProcessing();
        Common.ctx = mockContextWrapper(backgroundProcessing);

        List<IntValuePair> context1 = new ArrayList<>();
        context1.add(new IntValuePair(2, "p2"));
        context1.add(new IntValuePair(3, "p3"));
        context1.add(new IntValuePair(4, "p4"));

        List<IntValuePair> context2 = new ArrayList<>();
        context2.add(new IntValuePair(3, "p3"));

        List<IntValuePair> context3 = new ArrayList<>();
        context3.add(new IntValuePair(4, "p4"));

        List<IntValuePair> context4 = new ArrayList<>();
        context4.add(new IntValuePair(2, "p2"));

        List<IntValuePair> context5 = new ArrayList<>();
        context5.add(new IntValuePair(5, "p5"));

        DataPointVO dataPoint1 = TestUtils.newMetaPointSettable(1, -1, context1);
        DataPointVO dataPoint2 = TestUtils.newMetaPointSettable(2, -1, context2);
        DataPointVO dataPoint3 = TestUtils.newMetaPointSettable(3, -1, context3);
        DataPointVO dataPoint4 = TestUtils.newMetaPointSettable(4, -1, context4);
        DataPointVO dataPoint5 = TestUtils.newMetaPointSettable(5, -1, new ArrayList<>());
        DataPointVO dataPoint6 = TestUtils.newMetaPointSettable(6, -1, context5);
        DataPointVO dataPoint7 = TestUtils.newPointSettable(7, -1);

        DATA_POINTS = Map.of(dataPoint1.getId(), dataPoint1,
                dataPoint2.getId(), dataPoint2,
                dataPoint3.getId(), dataPoint3,
                dataPoint4.getId(), dataPoint4,
                dataPoint5.getId(), dataPoint5,
                dataPoint6.getId(), dataPoint6,
                dataPoint7.getId(), dataPoint7);
    }

    @Test
    public void when_isCyclicDependency() {

        //when:
        boolean cyclicDependencyResult = ValidationUtils.isCyclicDependency(starDataPointId, findDataPointId, DATA_POINTS, 100);

        //then:
        Assert.assertEquals(cyclicDependencyExpected, cyclicDependencyResult);
    }
}