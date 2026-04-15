package org.scada_lts.cache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.scada_lts.dao.HierarchyDAO;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyComparator;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyDataSource;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyNode;
import org.scada_lts.dao.pointhierarchy.PointHierarchyDAO;
import org.scada_lts.web.beans.ApplicationBeans;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({PointHierarchyCache.class, ApplicationBeans.class})
// resources/org/powermock/extensions/configuration.properties is not working
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "com.sun.org.apache.xalan.*",
        "javax.activation.*", "javax.management.*"})
public class DeletePointHierarchyCacheTest {

    @Parameterized.Parameters(name = "{index}: toDeleteIds: {0}, parentId: {1}, pointHierarchy: {2}, folderHierarchy: {3}, pointHierarchyExpected: {4}")
    public static Object[][] data() {
        PointHierarchyNode point1root = new PointHierarchyNode(1,"DP1_XID",0,"title1", false, new PointHierarchyDataSource(7,"name7","DS7_XID",""));
        PointHierarchyNode point2root = new PointHierarchyNode(2,"DP2_XID",0,"title1", false, new PointHierarchyDataSource(7,"name7","DS7_XID",""));
        PointHierarchyNode point3root = new PointHierarchyNode(3,"DP3_XID",0,"title1", false, new PointHierarchyDataSource(7,"name7","DS7_XID",""));
        PointHierarchyNode point4folder4 = new PointHierarchyNode(4,"DP4_XID",4,"title4", false, new PointHierarchyDataSource(8,"name8","DS8_XID",""));
        PointHierarchyNode point5folder4 = new PointHierarchyNode(5,"DP5_XID",4,"title4", false, new PointHierarchyDataSource(8,"name8","DS8_XID",""));
        PointHierarchyNode point6folder5 = new PointHierarchyNode(6,"DP6_XID",5,"title5", false, new PointHierarchyDataSource(9,"name9","DS9_XID",""));
        PointHierarchyNode point7folder5 = new PointHierarchyNode(7,"DP7_XID",5,"title5", false, new PointHierarchyDataSource(9,"name9","DS9_XID",""));
        PointHierarchyNode point8folder6 = new PointHierarchyNode(8,"DP8_XID",6,"title6", false, new PointHierarchyDataSource(10,"name10","DS10_XID",""));
        PointHierarchyNode point9folder6 = new PointHierarchyNode(9,"DP9_XID",6,"title6", false, new PointHierarchyDataSource(10,"name10","DS10_XID",""));
        PointHierarchyNode point10folder7 = new PointHierarchyNode(10,"DP10_XID",7,"title7", false, new PointHierarchyDataSource(10,"name10","DS10_XID",""));
        PointHierarchyNode point11folder7 = new PointHierarchyNode(11,"DP11_XID",7,"title7", false, new PointHierarchyDataSource(10,"name10","DS10_XID",""));

        PointHierarchyNode folder4root = new PointHierarchyNode(4,"FD1_XID",0,"title1", true, null);
        PointHierarchyNode folder5root = new PointHierarchyNode(5,"FD2_XID",0,"title2", true, null);
        PointHierarchyNode folder6root = new PointHierarchyNode(6,"FD3_XID",0,"title3", true, null);
        PointHierarchyNode folder7folder6 = new PointHierarchyNode(7,"FD3_XID",6,"title3", true, null);

        return new Object[][] {
                new Object[] {
                        Arrays.asList(
                                1, 3
                        ), 0,
                        Arrays.asList(point1root, point2root, point3root, point4folder4, point5folder4, point6folder5, point7folder5, point8folder6, point9folder6, point10folder7, point11folder7),
                        Arrays.asList(folder4root, folder5root, folder6root, folder7folder6),
                        Arrays.asList(folder4root, folder5root, folder6root, point2root),
                },
                new Object[]{
                        Arrays.asList(
                                1, 3
                        ), 4,
                        Arrays.asList(point1root, point2root, point3root, point4folder4, point5folder4, point6folder5, point7folder5, point8folder6, point9folder6, point10folder7, point11folder7),
                        Arrays.asList(folder4root, folder5root, folder6root, folder7folder6),
                        Arrays.asList(point4folder4, point5folder4),
                },
                new Object[]{
                        Arrays.asList(
                                1, 3
                        ), 5,
                        Arrays.asList(point1root, point2root, point3root, point4folder4, point5folder4, point6folder5, point7folder5, point8folder6, point9folder6, point10folder7, point11folder7),
                        Arrays.asList(folder4root, folder5root, folder6root, folder7folder6),
                        Arrays.asList(point6folder5, point7folder5),
                },
                new Object[]{
                        Arrays.asList(
                                1, 3
                        ), 6,
                        Arrays.asList(point1root, point2root, point3root, point4folder4, point5folder4, point6folder5, point7folder5, point8folder6, point9folder6, point10folder7, point11folder7),
                        Arrays.asList(folder4root, folder5root, folder6root, folder7folder6),
                        Arrays.asList(folder7folder6, point8folder6, point9folder6),
                },

                new Object[]{
                        Arrays.asList(
                                1, 3
                        ), 7,
                        Arrays.asList(point1root, point2root, point3root, point4folder4, point5folder4, point6folder5, point7folder5, point8folder6, point9folder6, point10folder7, point11folder7),
                        Arrays.asList(folder4root, folder5root, folder6root, folder7folder6),
                        Arrays.asList(point10folder7, point11folder7),
                },


                new Object[]{
                        Arrays.asList(
                                7, 8, 9
                        ), 0,
                        Arrays.asList(point1root, point2root, point3root, point4folder4, point5folder4, point6folder5, point7folder5, point8folder6, point9folder6, point10folder7, point11folder7),
                        Arrays.asList(folder4root, folder5root, folder6root, folder7folder6),
                        Arrays.asList(folder4root, folder5root, folder6root, point1root, point2root, point3root),
                },
                new Object[]{
                        Arrays.asList(
                                7, 8, 9
                        ), 4,
                        Arrays.asList(point1root, point2root, point3root, point4folder4, point5folder4, point6folder5, point7folder5, point8folder6, point9folder6, point10folder7, point11folder7),
                        Arrays.asList(folder4root, folder5root, folder6root, folder7folder6),
                        Arrays.asList(point4folder4, point5folder4),
                },
                new Object[]{
                        Arrays.asList(
                                7, 8, 9
                        ), 5,
                        Arrays.asList(point1root, point2root, point3root, point4folder4, point5folder4, point6folder5, point7folder5, point8folder6, point9folder6, point10folder7, point11folder7),
                        Arrays.asList(folder4root, folder5root, folder6root, folder7folder6),
                        Arrays.asList(point6folder5),
                },
                new Object[]{
                        Arrays.asList(
                                7, 8, 9
                        ), 6,
                        Arrays.asList(point1root, point2root, point3root, point4folder4, point5folder4, point6folder5, point7folder5, point8folder6, point9folder6, point10folder7, point11folder7),
                        Arrays.asList(folder4root, folder5root, folder6root, folder7folder6),
                        Arrays.asList(folder7folder6),
                },
                new Object[]{
                        Arrays.asList(
                                7, 8, 9
                        ), 7,
                        Arrays.asList(point1root, point2root, point3root, point4folder4, point5folder4, point6folder5, point7folder5, point8folder6, point9folder6, point10folder7, point11folder7),
                        Arrays.asList(folder4root, folder5root, folder6root, folder7folder6),
                        Arrays.asList(point10folder7, point11folder7),
                },
                new Object[]{
                        Arrays.asList(
                                3, 5, 6, 7, 8, 10
                        ), 0,
                        Arrays.asList(point1root, point2root, point3root, point4folder4, point5folder4, point6folder5, point7folder5, point8folder6, point9folder6, point10folder7, point11folder7),
                        Arrays.asList(folder4root, folder5root, folder6root, folder7folder6),
                        Arrays.asList(folder4root, folder5root, folder6root, point1root, point2root),
                },
                new Object[]{
                        Arrays.asList(
                                3, 5, 6, 7, 8, 10
                        ), 4,
                        Arrays.asList(point1root, point2root, point3root, point4folder4, point5folder4, point6folder5, point7folder5, point8folder6, point9folder6, point10folder7, point11folder7),
                        Arrays.asList(folder4root, folder5root, folder6root, folder7folder6),
                        Arrays.asList(point4folder4),
                },
                new Object[]{
                        Arrays.asList(
                                3, 5, 6, 7, 8, 10
                        ), 5,
                        Arrays.asList(point1root, point2root, point3root, point4folder4, point5folder4, point6folder5, point7folder5, point8folder6, point9folder6, point10folder7, point11folder7),
                        Arrays.asList(folder4root, folder5root, folder6root, folder7folder6),
                        Arrays.asList(),
                },
                new Object[]{
                        Arrays.asList(
                                3, 5, 6, 7, 8, 10
                        ), 6,
                        Arrays.asList(point1root, point2root, point3root, point4folder4, point5folder4, point6folder5, point7folder5, point8folder6, point9folder6, point10folder7, point11folder7),
                        Arrays.asList(folder4root, folder5root, folder6root, folder7folder6),
                        Arrays.asList(folder7folder6, point9folder6),
                },
                new Object[]{
                        Arrays.asList(
                                3, 5, 6, 7, 8, 10
                        ), 7,
                        Arrays.asList(point1root, point2root, point3root, point4folder4, point5folder4, point6folder5, point7folder5, point8folder6, point9folder6, point10folder7, point11folder7),
                        Arrays.asList(folder4root, folder5root, folder6root, folder7folder6),
                        Arrays.asList(point11folder7),
                }
        };
    }

    private final int[] toDeleteIds;
    private final int parentId;
    private final List<PointHierarchyNode> pointHierarchy;
    private final List<PointHierarchyNode> folderHierarchy;
    private final List<PointHierarchyNode> pointHierarchyExpected;


    public DeletePointHierarchyCacheTest(List<Integer> toDeleteIds, int parentId, List<PointHierarchyNode> pointHierarchy,
                                         List<PointHierarchyNode> folderHierarchy, List<PointHierarchyNode> pointHierarchyExpected) {
        this.toDeleteIds = toDeleteIds.stream().mapToInt(a -> a).toArray();
        this.parentId = parentId;
        this.pointHierarchy = pointHierarchy;
        this.folderHierarchy = folderHierarchy;
        this.pointHierarchyExpected = pointHierarchyExpected;
    }

    private PointHierarchyCache pointHierarchyCache;

    @Before
    public void config() throws Exception {

        HierarchyDAO hierarchyDAOMock = mock(HierarchyDAO.class);
        PointHierarchyDAO pointHierarchyDAOMock = mock(PointHierarchyDAO.class);

        Collections.sort(folderHierarchy, PointHierarchyComparator.getInst());

        when(hierarchyDAOMock.getHierarchy()).thenReturn(folderHierarchy);
        when(pointHierarchyDAOMock.getPointsHierarchy()).thenReturn(pointHierarchy);

        PowerMockito.whenNew(HierarchyDAO.class)
                .withAnyArguments()
                .thenReturn(hierarchyDAOMock);

        PowerMockito.whenNew(PointHierarchyDAO.class)
                .withAnyArguments()
                .thenReturn(pointHierarchyDAOMock);

        PowerMockito.mockStatic(ApplicationBeans.class);

        when(ApplicationBeans.getHierarchyDAOBean())
                .thenReturn(hierarchyDAOMock);
        when(ApplicationBeans.getPointHierarchyDaoBean())
                .thenReturn(pointHierarchyDAOMock);

        this.pointHierarchyCache = new PointHierarchyCache(true);
    }

    @Test
    public void when_delete() throws Exception {
        //when:
        pointHierarchyCache.delete(toDeleteIds);

        //and:
        List<PointHierarchyNode> result = pointHierarchyCache.getOnBaseParentId(parentId);

        //then:
        Assert.assertEquals(pointHierarchyExpected, result);
    }
}
