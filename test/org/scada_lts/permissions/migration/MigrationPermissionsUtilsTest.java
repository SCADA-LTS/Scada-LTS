package org.scada_lts.permissions.migration;

import br.org.scadabr.db.utils.TestUtils;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.view.View;
import com.serotonin.mango.view.component.CompoundChild;
import com.serotonin.mango.view.component.EnhancedImageChartComponent;
import com.serotonin.mango.view.component.SimplePointComponent;
import com.serotonin.mango.view.component.ViewComponent;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import org.junit.Before;
import org.junit.Test;
import org.scada_lts.dao.DataPointUserDAO;
import org.scada_lts.dao.DataSourceDAO;
import org.scada_lts.dao.watchlist.WatchListDAO;
import org.scada_lts.mango.service.UsersProfileService;
import org.scada_lts.permissions.service.DataPointUserPermissionsService;
import org.scada_lts.permissions.service.DataSourceUserPermissionsService;
import org.scada_lts.permissions.service.WatchListUserPermissionsService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class MigrationPermissionsUtilsTest {

    @Before
    public void config() {
        DataPointVO dataPoint1 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint1.setId(11);

        DataPointVO dataPoint2 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint2.setId(22);

        DataPointVO dataPoint3 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint2.setId(33);

        DataPointVO dataPoint4 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint2.setId(44);


        SimplePointComponent pointComponent1 = (SimplePointComponent) ViewComponent.newInstance(SimplePointComponent.DEFINITION.getName());
        pointComponent1.tsetDataPoint(dataPoint1);

        SimplePointComponent pointComponent2 = (SimplePointComponent) ViewComponent.newInstance(SimplePointComponent.DEFINITION.getName());
        pointComponent2.tsetDataPoint(dataPoint2);

        SimplePointComponent pointComponent3 = (SimplePointComponent) ViewComponent.newInstance(SimplePointComponent.DEFINITION.getName());
        pointComponent3.tsetDataPoint(dataPoint3);

        SimplePointComponent pointComponent4 = (SimplePointComponent) ViewComponent.newInstance(SimplePointComponent.DEFINITION.getName());
        pointComponent4.tsetDataPoint(dataPoint4);

        EnhancedImageChartComponent enhancedImageChartComponent = (EnhancedImageChartComponent) ViewComponent.newInstance(EnhancedImageChartComponent.DEFINITION.getName());

        enhancedImageChartComponent.getChildComponents().add(new CompoundChild("",null, pointComponent3, new int[]{}));
        enhancedImageChartComponent.getChildComponents().add(new CompoundChild("",null, pointComponent4, new int[]{}));


        List<ViewComponent> viewComponents = new ArrayList<>();
        viewComponents.add(pointComponent1);
        viewComponents.add(pointComponent2);
        viewComponents.add(enhancedImageChartComponent);


        View view1 = new View();
        viewComponents.forEach(view1::addViewComponent);

        View view2 = new View();

        User user1 = TestUtils.newUser(123);
        User user2 = TestUtils.newUser(124);
        List<User> users = Arrays.asList(user1, user2);
        List<View> views = Arrays.asList(view1, view2);

        DataPointUserDAO dataPointUserDAO = mock(DataPointUserDAO.class);
        DataPointUserPermissionsService userPermissionsService = new DataPointUserPermissionsService(dataPointUserDAO);

        DataSourceDAO dataSourceDAO = mock(DataSourceDAO.class);
        DataSourceUserPermissionsService dataSourceUserPermissionsService = new DataSourceUserPermissionsService(dataSourceDAO);

        WatchListDAO watchListDAO = mock(WatchListDAO.class);
        WatchListUserPermissionsService watchListUserPermissionsService = new WatchListUserPermissionsService(watchListDAO);

        UsersProfileService usersProfileService = mock(UsersProfileService.class);
        UsersProfileVO usersProfile = new UsersProfileVO();
        when(usersProfileService.getUserProfileById(anyInt())).thenReturn(usersProfile);

        MigrationPermissionsUtils.updatePermissions(user1, "", usersProfileService, Accesses.empty(), new HashMap<>());

    }

    @Test
    public void verifyViewUserPermissions() {
    }

    @Test
    public void toViewAccesses() {
    }

    @Test
    public void verifyUserPermissions() {
    }

    @Test
    public void fromProfile() {
    }

    @Test
    public void accessesBy() {
    }

    @Test
    public void migratePermissions() {
    }

    @Test
    public void getShareUser() {
    }

    @Test
    public void findDataPointAccessesFromView() {
    }

    @Test
    public void merge() {
    }
}