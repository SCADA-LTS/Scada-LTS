package org.scada_lts.permissions.migration;

import br.org.scadabr.db.utils.TestUtils;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.view.component.*;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.LoggingType;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.virtual.VirtualPointLocatorVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.scada_lts.mango.service.UsersProfileService;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class MigrationPermissionsUtilsTest {

    private DataPointVO dataPoint1;
    private DataPointVO dataPoint2;
    private DataPointVO dataPoint3;
    private DataPointVO dataPoint4;
    private User user1;

    @Before
    public void config() {
        dataPoint1 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint1.setId(11);

        dataPoint2 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint2.setId(22);

        dataPoint3 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint3.setId(33);

        dataPoint4 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint4.setId(44);

        user1 = TestUtils.newUser(123);

        UsersProfileService usersProfileService = mock(UsersProfileService.class);
        UsersProfileVO usersProfile = new UsersProfileVO();
        when(usersProfileService.getUserProfileById(anyInt())).thenReturn(usersProfile);

    }

    @Test
    public void when_findDataPointAccessesFromView_then_expected_list_set_permissions() {

        //given:
        ShareUser shareUser = new ShareUser(user1.getId(), ShareUser.ACCESS_SET);
        Set<DataPointAccess> dataPointAccessesExpected = new HashSet<>();

        dataPointAccessesExpected.add(new DataPointAccess(dataPoint1.getId(), shareUser.getAccessType()));
        dataPointAccessesExpected.add(new DataPointAccess(dataPoint2.getId(), shareUser.getAccessType()));
        dataPointAccessesExpected.add(new DataPointAccess(dataPoint3.getId(), shareUser.getAccessType()));
        dataPointAccessesExpected.add(new DataPointAccess(dataPoint4.getId(), shareUser.getAccessType()));

        SimplePointComponent pointComponent1 = (SimplePointComponent) ViewComponent.newInstance(SimplePointComponent.DEFINITION.getName());
        pointComponent1.tsetDataPoint(dataPoint1);

        SimplePointComponent pointComponent2 = (SimplePointComponent) ViewComponent.newInstance(SimplePointComponent.DEFINITION.getName());
        pointComponent2.tsetDataPoint(dataPoint2);

        SimplePointComponent pointComponent3 = (SimplePointComponent) ViewComponent.newInstance(SimplePointComponent.DEFINITION.getName());
        pointComponent3.tsetDataPoint(dataPoint3);

        SimplePointComponent pointComponent4 = (SimplePointComponent) ViewComponent.newInstance(SimplePointComponent.DEFINITION.getName());
        pointComponent4.tsetDataPoint(dataPoint4);

        SimplePointComponent pointComponent5 = (SimplePointComponent) ViewComponent.newInstance(SimplePointComponent.DEFINITION.getName());
        pointComponent4.tsetDataPoint(dataPoint4);

        SimpleCompoundComponent simpleCompoundComponent = (SimpleCompoundComponent) ViewComponent.newInstance(SimpleCompoundComponent.DEFINITION.getName());
        simpleCompoundComponent.getChildComponents().add(new CompoundChild("1",null, pointComponent3, new int[]{}));
        simpleCompoundComponent.getChildComponents().add(new CompoundChild("2",null, pointComponent4, new int[]{}));

        SimpleCompoundComponent simpleCompoundComponent2 = (SimpleCompoundComponent) ViewComponent.newInstance(SimpleCompoundComponent.DEFINITION.getName());
        simpleCompoundComponent2.getChildComponents().add(new CompoundChild("3",null, pointComponent5, new int[]{}));

        List<ViewComponent> viewComponents = new ArrayList<>();
        viewComponents.add(pointComponent1);
        viewComponents.add(pointComponent2);
        viewComponents.add(simpleCompoundComponent);
        viewComponents.add(simpleCompoundComponent2);

        View view = new View();
        viewComponents.forEach(view::addViewComponent);

        //when:
        Set<DataPointAccess> dataPointAccesses = MigrationPermissionsUtils.findDataPointAccessesFromView(view, shareUser);

        //then:
        Assert.assertEquals(dataPointAccessesExpected, dataPointAccesses);
    }


    @Test
    public void when_findDataPointAccessesFromView_then_expected_list_read_permissions() {

        //given:
        ShareUser shareUser = new ShareUser(user1.getId(), ShareUser.ACCESS_READ);
        Set<DataPointAccess> dataPointAccessesExpected = new HashSet<>();

        dataPointAccessesExpected.add(new DataPointAccess(dataPoint1.getId(), shareUser.getAccessType()));
        dataPointAccessesExpected.add(new DataPointAccess(dataPoint2.getId(), shareUser.getAccessType()));
        dataPointAccessesExpected.add(new DataPointAccess(dataPoint3.getId(), shareUser.getAccessType()));
        dataPointAccessesExpected.add(new DataPointAccess(dataPoint4.getId(), shareUser.getAccessType()));

        SimplePointComponent pointComponent1 = (SimplePointComponent) ViewComponent.newInstance(SimplePointComponent.DEFINITION.getName());
        pointComponent1.tsetDataPoint(dataPoint1);

        SimplePointComponent pointComponent2 = (SimplePointComponent) ViewComponent.newInstance(SimplePointComponent.DEFINITION.getName());
        pointComponent2.tsetDataPoint(dataPoint2);

        SimplePointComponent pointComponent3 = (SimplePointComponent) ViewComponent.newInstance(SimplePointComponent.DEFINITION.getName());
        pointComponent3.tsetDataPoint(dataPoint3);

        SimplePointComponent pointComponent4 = (SimplePointComponent) ViewComponent.newInstance(SimplePointComponent.DEFINITION.getName());
        pointComponent4.tsetDataPoint(dataPoint4);

        SimplePointComponent pointComponent5 = (SimplePointComponent) ViewComponent.newInstance(SimplePointComponent.DEFINITION.getName());
        pointComponent4.tsetDataPoint(dataPoint4);

        SimpleCompoundComponent simpleCompoundComponent = (SimpleCompoundComponent) ViewComponent.newInstance(SimpleCompoundComponent.DEFINITION.getName());
        simpleCompoundComponent.getChildComponents().add(new CompoundChild("1",null, pointComponent3, new int[]{}));
        simpleCompoundComponent.getChildComponents().add(new CompoundChild("2",null, pointComponent4, new int[]{}));

        SimpleCompoundComponent simpleCompoundComponent2 = (SimpleCompoundComponent) ViewComponent.newInstance(SimpleCompoundComponent.DEFINITION.getName());
        simpleCompoundComponent2.getChildComponents().add(new CompoundChild("3",null, pointComponent5, new int[]{}));

        List<ViewComponent> viewComponents = new ArrayList<>();
        viewComponents.add(pointComponent1);
        viewComponents.add(pointComponent2);
        viewComponents.add(simpleCompoundComponent);
        viewComponents.add(simpleCompoundComponent2);

        View view = new View();
        viewComponents.forEach(view::addViewComponent);

        //when:
        Set<DataPointAccess> dataPointAccesses = MigrationPermissionsUtils.findDataPointAccessesFromView(view, shareUser);

        //then:
        Assert.assertEquals(dataPointAccessesExpected, dataPointAccesses);
    }

    @Test
    public void when_selectDataPointAccesses_with_permission_read_and_set_for_same_datapoint_then_expected_set_permission() {

        //given:
        Set<DataPointAccess> expected = new HashSet<>();
        expected.add(new DataPointAccess(5, 2));

        Set<DataPointAccess> dataPointAccesses = new HashSet<>();
        dataPointAccesses.add(new DataPointAccess(5, 1));

        VirtualPointLocatorVO pointLocatorSettable = new VirtualPointLocatorVO();
        pointLocatorSettable.setSettable(true);

        DataPointVO dataPointSettable = new DataPointVO(LoggingType.ON_CHANGE.getCode());
        dataPointSettable.setId(5);
        dataPointSettable.setPointLocator(pointLocatorSettable);

        SimplePointComponent pointComponent1 = (SimplePointComponent) ViewComponent.newInstance(SimplePointComponent.DEFINITION.getName());
        pointComponent1.tsetDataPoint(dataPointSettable);

        View view = new View();
        view.setId(123);
        view.addViewComponent(pointComponent1);

        Map<Integer, List<DataPointVO>> dataPointsFromViews = MigrationPermissionsUtils.findDataPointsFromViews(Arrays.asList(view));
        ShareUser shareUser = new ShareUser(3, 2);

        //when:
        Set<DataPointAccess> result = MigrationPermissionsUtils.selectDataPointAccesses(dataPointsFromViews, dataPointAccesses, view, shareUser);

        //then:
        Assert.assertEquals(expected, result);
    }
}