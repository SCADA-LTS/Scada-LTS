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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.scada_lts.permissions.migration.MigrationPermissionsUtils.generateDataPointAccess;
import static org.scada_lts.utils.PointHierarchyUtils.sort;

public class GetPointHierarchyByKeyTest {

    private PointHierarchyXidService service;

    private DataPointVO dataPoint1;
    private DataPointVO dataPoint6;

    private PointHierarchyNode folder1;
    private PointHierarchyNode folder2;
    private PointHierarchyNode folder3;
    private PointHierarchyNode folder4;
    private PointHierarchyNode folder5;
    private PointHierarchyNode folder6;
    private PointHierarchyNode emptyFolder;
    private PointHierarchyNode fakeFolder;

    private PointHierarchyNode point1;
    private PointHierarchyNode point2;
    private PointHierarchyNode point3;
    private PointHierarchyNode point4;
    private PointHierarchyNode point5;
    private PointHierarchyNode point6;


    @Before
    public void config() {
        PointHierarchyXidDAO pointHierarchyXidDAOMock = mock(PointHierarchyXidDAO.class);
        DataPointDAO dataPointDAOMock = mock(DataPointDAO.class);
        HierarchyDAO hierarchyDAOMock = mock(HierarchyDAO.class);

        folder1 = PointHierarchyNode.folderNode(1, "DIR_1", 0, "folder_1");
        folder2 = PointHierarchyNode.folderNode(2, "DIR_2", folder1.getKey(), "folder_2");
        folder3 = PointHierarchyNode.folderNode(3, "DIR_3", 0, "folder_3");
        folder4 = PointHierarchyNode.folderNode(4, "DIR_4", folder2.getKey(), "folder_4");
        folder5 = PointHierarchyNode.folderNode(5, "DIR_5", folder4.getKey(), "folder_5");
        folder6 = PointHierarchyNode.folderNode(6, "DIR_6", folder5.getKey(), "folder_6");
        emptyFolder = PointHierarchyNode.folderNode(7, "DIR_7", 0, "folder_7");
        fakeFolder = PointHierarchyNode.folderNode(8, "DIR_8", 8, "folder_8");

        when(hierarchyDAOMock.getHierarchy()).thenReturn(Arrays.asList(folder1, folder2, folder3,
                folder4, folder5, folder6, emptyFolder, fakeFolder));

        DataSourceVO<?> dataSource1 = TestUtils.newDataSource(33);
        DataSourceVO<?> dataSource2 = TestUtils.newDataSource(44);

        dataPoint1 = TestUtils.newPointSettable(1, 0);
        DataPointVO dataPoint2 = TestUtils.newPointSettable(2, dataSource1, folder6.getKey());
        DataPointVO dataPoint3 = TestUtils.newPointSettable(3, dataSource1, folder6.getKey());
        DataPointVO dataPoint4 = TestUtils.newPointNonSettable(4, dataSource1, folder3.getKey());
        DataPointVO dataPoint5 = TestUtils.newPointSettable(5, dataSource2, folder2.getKey());
        dataPoint6 = TestUtils.newPointNonSettable(6, dataSource2, folder1.getKey());

        point1 = PointHierarchyNode.pointNode(dataPoint1);
        point2 = PointHierarchyNode.pointNode(dataPoint2);
        point3 = PointHierarchyNode.pointNode(dataPoint3);
        point4 = PointHierarchyNode.pointNode(dataPoint4);
        point5 = PointHierarchyNode.pointNode(dataPoint5);
        point6 = PointHierarchyNode.pointNode(dataPoint6);

        when(dataPointDAOMock.getDataPoints())
                .thenReturn(Arrays.asList(dataPoint1, dataPoint2, dataPoint3, dataPoint4, dataPoint5, dataPoint6));

        service = new PointHierarchyXidService(pointHierarchyXidDAOMock, dataPointDAOMock, hierarchyDAOMock);
    }

    @Test
    public void when_getPointHierarchyByKey_with_admin() {
        //given:
        User user = TestUtils.newUser(123);
        user.setAdmin(true);

        List<PointHierarchyNode> expected = Arrays.asList(folder1, folder2, folder3, folder4, folder5, folder6, fakeFolder,
                point1, point2, point3, point4, point5, point6, PointHierarchyNode.rootNode());
        sort(expected);

        for(int i = 0 ; i < 8 ; i++) {
            //when:
            List<PointHierarchyNode> rootResult = service.getPointHierarchyByKey(user, i);
            List<PointHierarchyNode> exp = getExp(expected, i);

            //then:
            Assert.assertEquals(exp, rootResult);
        }

    }

    @Test
    public void when_getPointHierarchyWithEmptyByKey_with_admin_and_dir_empty() {
        //given:
        User user = TestUtils.newUser(123);
        user.setAdmin(true);

        List<PointHierarchyNode> expected = Arrays.asList(folder1, folder2, folder3, folder4, folder5, folder6, emptyFolder,
                fakeFolder, point1, point2, point3, point4, point5, point6, PointHierarchyNode.rootNode());
        sort(expected);

        for(int i = 0 ; i < 8 ; i++) {
            //when:
            List<PointHierarchyNode> rootResult = service.getPointHierarchyWithEmptyByKey(user, i);
            List<PointHierarchyNode> exp = getExp(expected, i);

            //then:
            Assert.assertEquals(exp, rootResult);
        }

    }

    @Test
    public void when_getPointHierarchyByKey_with_non_admin() {
        //given:
        User user = TestUtils.newUser(123);
        user.setAdmin(false);
        List<PointHierarchyNode> expected = Arrays.asList(folder1, fakeFolder, point1, point6, PointHierarchyNode.rootNode());
        sort(expected);

        DataPointAccess result1 = generateDataPointAccess(new ShareUser(user.getId(), ShareUser.ACCESS_READ), dataPoint1);
        DataPointAccess result6 = generateDataPointAccess(new ShareUser(user.getId(), ShareUser.ACCESS_SET), dataPoint6);
        user.setDataPointPermissions(Arrays.asList(result1, result6));

        for(int i = 0 ; i < 8 ; i++) {
            //when:
            List<PointHierarchyNode> rootResult = service.getPointHierarchyByKey(user, i);
            List<PointHierarchyNode> exp = getExp(expected, i);

            //then:
            Assert.assertEquals(exp, rootResult);
        }
    }

    @Test
    public void when_getPointHierarchyWithEmptyByKey_with_non_admin_and_dir_empty() {
        //given:
        User user = TestUtils.newUser(123);
        user.setAdmin(false);

        List<PointHierarchyNode> expected = Arrays.asList(folder1, folder2, folder3, folder4, folder5, folder6,
                emptyFolder, fakeFolder, point1, point6, PointHierarchyNode.rootNode());
        sort(expected);

        DataPointAccess result1 = generateDataPointAccess(new ShareUser(user.getId(), ShareUser.ACCESS_READ), dataPoint1);
        DataPointAccess result6 = generateDataPointAccess(new ShareUser(user.getId(), ShareUser.ACCESS_SET), dataPoint6);
        user.setDataPointPermissions(Arrays.asList(result1, result6));

        for(int i = 0 ; i < 8 ; i++) {
            //when:
            List<PointHierarchyNode> rootResult = service.getPointHierarchyWithEmptyByKey(user, i);
            List<PointHierarchyNode> exp = getExp(expected, i);

            //then:
            Assert.assertEquals(exp, rootResult);
        }
    }


    private List<PointHierarchyNode> getExp(List<PointHierarchyNode> expected, int i) {
        return expected.stream().filter(a -> a.getParentId() == i)
                .sorted(Comparator.comparing(PointHierarchyNode::getTitle))
                .collect(Collectors.toList());
    }
}