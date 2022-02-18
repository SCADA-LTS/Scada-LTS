package org.scada_lts.service.pointhierarchy;

import br.org.scadabr.db.utils.TestUtils;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.scada_lts.dao.DataPointDAO;
import org.scada_lts.dao.HierarchyDAO;
import org.scada_lts.dao.model.pointhierarchy.PointHierarchyNode;
import org.scada_lts.dao.pointhierarchy.PointHierarchyXidDAO;
import org.scada_lts.utils.PointHierarchyUtils;

import java.util.Arrays;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.scada_lts.permissions.migration.MigrationPermissionsUtils.generateDataPointAccess;

public class GetPointHierarchyRootTest {

    private PointHierarchyXidService service;

    private DataPointVO dataPoint1;
    private DataPointVO dataPoint2;
    private DataPointVO dataPoint3;
    private DataPointVO dataPoint4;
    private DataPointVO dataPoint5;
    private DataPointVO dataPoint6;

    private PointHierarchyNode emptyFolder;
    private PointHierarchyNode fakeFolder;

    @Before
    public void config() {
        PointHierarchyXidDAO pointHierarchyXidDAOMock = mock(PointHierarchyXidDAO.class);
        DataPointDAO dataPointDAOMock = mock(DataPointDAO.class);
        HierarchyDAO hierarchyDAOMock = mock(HierarchyDAO.class);

        PointHierarchyNode folder1 = PointHierarchyNode.folderNode(1, "DIR_1", 0, "folder_1");
        PointHierarchyNode folder2 = PointHierarchyNode.folderNode(2, "DIR_2", folder1.getKey(), "folder_2");
        PointHierarchyNode folder3 = PointHierarchyNode.folderNode(3, "DIR_3", 0, "folder_3");
        PointHierarchyNode folder4 = PointHierarchyNode.folderNode(4, "DIR_4", folder2.getKey(), "folder_4");
        PointHierarchyNode folder5 = PointHierarchyNode.folderNode(5, "DIR_5", folder4.getKey(), "folder_5");
        PointHierarchyNode folder6 = PointHierarchyNode.folderNode(6, "DIR_6", folder5.getKey(), "folder_6");
        emptyFolder = PointHierarchyNode.folderNode(7, "DIR_7", 0, "folder_7");
        fakeFolder = PointHierarchyNode.folderNode(8, "DIR_8", 8, "folder_8");

        when(hierarchyDAOMock.getHierarchy()).thenReturn(Arrays.asList(folder1, folder2, folder3, folder4,
                folder5, folder6, emptyFolder, fakeFolder));

        DataSourceVO<?> dataSource1 = TestUtils.newDataSource(33);
        DataSourceVO<?> dataSource2 = TestUtils.newDataSource(44);

        dataPoint1 = TestUtils.newPointSettable(1, 0);
        dataPoint2 = TestUtils.newPointSettable(2, dataSource1, folder6.getKey());
        dataPoint3 = TestUtils.newPointSettable(3, dataSource1, folder6.getKey());
        dataPoint4 = TestUtils.newPointNonSettable(4, dataSource1, folder3.getKey());
        dataPoint5 = TestUtils.newPointSettable(5, dataSource2, folder2.getKey());
        dataPoint6 = TestUtils.newPointNonSettable(6, dataSource2, folder1.getKey());

        when(dataPointDAOMock.getDataPoints())
                .thenReturn(Arrays.asList(dataPoint1, dataPoint2, dataPoint3, dataPoint4, dataPoint5, dataPoint6));

        service = new PointHierarchyXidService(pointHierarchyXidDAOMock, dataPointDAOMock, hierarchyDAOMock);
    }

    @Test
    public void when_getPointHierarchyRoot_with_admin() {
        //given:
        User user = TestUtils.newUser(123);
        user.setAdmin(true);

        PointHierarchyNode folder1 = PointHierarchyNode.folderNode(1, "DIR_1", 0, "folder_1");
        PointHierarchyNode folder2 = PointHierarchyNode.folderNode(2, "DIR_2", folder1.getKey(), "folder_2");
        PointHierarchyNode folder3 = PointHierarchyNode.folderNode(3, "DIR_3", 0, "folder_3");
        PointHierarchyNode folder4 = PointHierarchyNode.folderNode(4, "DIR_4", folder2.getKey(), "folder_4");
        PointHierarchyNode folder5 = PointHierarchyNode.folderNode(5, "DIR_5", folder4.getKey(), "folder_5");
        PointHierarchyNode folder6 = PointHierarchyNode.folderNode(6, "DIR_6", folder5.getKey(), "folder_6");

        PointHierarchyNode point1 = PointHierarchyNode.pointNode(dataPoint1);
        PointHierarchyNode point2 = PointHierarchyNode.pointNode(dataPoint2);
        PointHierarchyNode point3 = PointHierarchyNode.pointNode(dataPoint3);
        PointHierarchyNode point4 = PointHierarchyNode.pointNode(dataPoint4);
        PointHierarchyNode point5 = PointHierarchyNode.pointNode(dataPoint5);
        PointHierarchyNode point6 = PointHierarchyNode.pointNode(dataPoint6);

        folder1.getChildren().add(folder2);
        folder1.getChildren().add(point6);
        folder2.getChildren().add(folder4);
        folder2.getChildren().add(point5);
        folder3.getChildren().add(point4);
        folder4.getChildren().add(folder5);
        folder5.getChildren().add(folder6);
        folder6.getChildren().add(point2);
        folder6.getChildren().add(point3);

        PointHierarchyNode adminRootExpected = PointHierarchyNode.rootNode();

        adminRootExpected.getChildren().add(point1);
        adminRootExpected.getChildren().add(folder1);
        adminRootExpected.getChildren().add(folder3);

        PointHierarchyUtils.sort(Arrays.asList(folder1, folder2, folder3, folder4, folder5, folder6, emptyFolder, fakeFolder,
                point1, point2, point3, point4, point5, point6, adminRootExpected));

        //when:
        PointHierarchyNode rootResult = service.getPointHierarchyRoot(user);

        //then:
        Assert.assertNotNull(rootResult.getChildren());
        Assert.assertFalse(rootResult.getChildren().isEmpty());
        Assert.assertTrue(equals(adminRootExpected, rootResult));
    }

    @Test
    public void when_getPointHierarchyWithEmptyRoot_with_admin_and_empty_dirs() {
        //given:
        User user = TestUtils.newUser(123);
        user.setAdmin(true);

        PointHierarchyNode folder1 = PointHierarchyNode.folderNode(1, "DIR_1", 0, "folder_1");
        PointHierarchyNode folder2 = PointHierarchyNode.folderNode(2, "DIR_2", folder1.getKey(), "folder_2");
        PointHierarchyNode folder3 = PointHierarchyNode.folderNode(3, "DIR_3", 0, "folder_3");
        PointHierarchyNode folder4 = PointHierarchyNode.folderNode(4, "DIR_4", folder2.getKey(), "folder_4");
        PointHierarchyNode folder5 = PointHierarchyNode.folderNode(5, "DIR_5", folder4.getKey(), "folder_5");
        PointHierarchyNode folder6 = PointHierarchyNode.folderNode(6, "DIR_6", folder5.getKey(), "folder_6");
        PointHierarchyNode point1 = PointHierarchyNode.pointNode(dataPoint1);
        PointHierarchyNode point2 = PointHierarchyNode.pointNode(dataPoint2);
        PointHierarchyNode point3 = PointHierarchyNode.pointNode(dataPoint3);
        PointHierarchyNode point4 = PointHierarchyNode.pointNode(dataPoint4);
        PointHierarchyNode point5 = PointHierarchyNode.pointNode(dataPoint5);
        PointHierarchyNode point6 = PointHierarchyNode.pointNode(dataPoint6);

        folder1.getChildren().add(folder2);
        folder1.getChildren().add(point6);
        folder2.getChildren().add(folder4);
        folder2.getChildren().add(point5);
        folder3.getChildren().add(point4);
        folder4.getChildren().add(folder5);
        folder5.getChildren().add(folder6);
        folder6.getChildren().add(point2);
        folder6.getChildren().add(point3);

        PointHierarchyNode adminRootExpected = PointHierarchyNode.rootNode();

        adminRootExpected.getChildren().add(point1);
        adminRootExpected.getChildren().add(folder1);
        adminRootExpected.getChildren().add(folder3);
        adminRootExpected.getChildren().add(emptyFolder);

        PointHierarchyUtils.sort(Arrays.asList(folder1, folder2, folder3, folder4, folder5, folder6, emptyFolder, fakeFolder,
                point1, point2, point3, point4, point5, point6, adminRootExpected));

        //when:
        PointHierarchyNode rootResult = service.getPointHierarchyWithEmptyRoot(user);

        //then:
        Assert.assertNotNull(rootResult.getChildren());
        Assert.assertFalse(rootResult.getChildren().isEmpty());
        Assert.assertTrue(equals(adminRootExpected, rootResult));

    }

    @Test
    public void when_getPointHierarchyRoot_with_non_admin() {
        //given:
        User user = TestUtils.newUser(123);
        user.setAdmin(false);

        PointHierarchyNode folder1 = PointHierarchyNode.folderNode(1, "DIR_1", 0, "folder_1");
        PointHierarchyNode point1 = PointHierarchyNode.pointNode(dataPoint1);
        PointHierarchyNode point6 = PointHierarchyNode.pointNode(dataPoint6);

        PointHierarchyNode nonadminRootExpected = PointHierarchyNode.rootNode();

        folder1.getChildren().add(point6);
        nonadminRootExpected.getChildren().add(point1);
        nonadminRootExpected.getChildren().add(folder1);

        PointHierarchyUtils.sort(Arrays.asList(folder1, emptyFolder, fakeFolder, point1, point6, nonadminRootExpected));

        DataPointAccess result1 = generateDataPointAccess(new ShareUser(user.getId(), ShareUser.ACCESS_READ), dataPoint1);
        DataPointAccess result6 = generateDataPointAccess(new ShareUser(user.getId(), ShareUser.ACCESS_SET), dataPoint6);
        user.setDataPointPermissions(Arrays.asList(result1, result6));

        //when:
        PointHierarchyNode rootResult = service.getPointHierarchyRoot(user);

        //then:
        Assert.assertNotNull(rootResult.getChildren());
        Assert.assertFalse(rootResult.getChildren().isEmpty());
        Assert.assertTrue(equals(nonadminRootExpected, rootResult));
    }

    @Test
    public void when_getPointHierarchyWithEmptyRoot_with_non_admin_and_empty_dirs() {
        //given:
        User user = TestUtils.newUser(123);
        user.setAdmin(false);

        PointHierarchyNode folder1 = PointHierarchyNode.folderNode(1, "DIR_1", 0, "folder_1");
        PointHierarchyNode folder2 = PointHierarchyNode.folderNode(2, "DIR_2", folder1.getKey(), "folder_2");
        PointHierarchyNode folder3 = PointHierarchyNode.folderNode(3, "DIR_3", 0, "folder_3");
        PointHierarchyNode folder4 = PointHierarchyNode.folderNode(4, "DIR_4", folder2.getKey(), "folder_4");
        PointHierarchyNode folder5 = PointHierarchyNode.folderNode(5, "DIR_5", folder4.getKey(), "folder_5");
        PointHierarchyNode folder6 = PointHierarchyNode.folderNode(6, "DIR_6", folder5.getKey(), "folder_6");

        PointHierarchyNode point1 = PointHierarchyNode.pointNode(dataPoint1);
        PointHierarchyNode point6 = PointHierarchyNode.pointNode(dataPoint6);

        PointHierarchyNode nonadminRootExpected = PointHierarchyNode.rootNode();

        folder1.getChildren().add(point6);
        folder1.getChildren().add(folder2);
        folder2.getChildren().add(folder4);
        folder4.getChildren().add(folder5);
        folder5.getChildren().add(folder6);
        nonadminRootExpected.getChildren().add(point1);
        nonadminRootExpected.getChildren().add(folder1);
        nonadminRootExpected.getChildren().add(folder3);
        nonadminRootExpected.getChildren().add(emptyFolder);

        PointHierarchyUtils.sort(Arrays.asList(folder1, folder3, emptyFolder, fakeFolder, point1, point6, nonadminRootExpected));

        DataPointAccess result1 = generateDataPointAccess(new ShareUser(user.getId(), ShareUser.ACCESS_READ), dataPoint1);
        DataPointAccess result6 = generateDataPointAccess(new ShareUser(user.getId(), ShareUser.ACCESS_SET), dataPoint6);
        user.setDataPointPermissions(Arrays.asList(result1, result6));

        //when:
        PointHierarchyNode rootResult = service.getPointHierarchyWithEmptyRoot(user);

        //then:
        Assert.assertNotNull(rootResult.getChildren());
        Assert.assertFalse(rootResult.getChildren().isEmpty());
        Assert.assertTrue(equals(nonadminRootExpected, rootResult));
    }

    private static boolean equals(PointHierarchyNode expected, PointHierarchyNode result) {
        if(!expected.equals(result))
            return false;
        if(expected.isFolder() != result.isFolder())
            return false;
        if(expected.isFolder()) {
            if(!expected.getChildren().equals(result.getChildren()))
                return false;
            int size = expected.getChildren().size();
            for (int i = 0; i < size; i++) {
                PointHierarchyNode exp = expected.getChildren().get(i);
                PointHierarchyNode res = result.getChildren().get(i);
                if(!equals(exp, res))
                    return false;
            }
        }
        return true;
    }
}