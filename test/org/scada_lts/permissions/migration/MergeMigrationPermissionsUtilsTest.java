package org.scada_lts.permissions.migration;

import br.org.scadabr.db.utils.TestUtils;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import org.junit.Before;
import org.scada_lts.mango.service.UsersProfileService;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;


public class MergeMigrationPermissionsUtilsTest {


    private DataPointVO dataPoint1;
    private DataPointVO dataPoint2;
    private DataPointVO dataPoint3;
    private DataPointVO dataPoint4;
    private User user1;
    private User user2;

    @Before
    public void config() {
        dataPoint1 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint1.setId(11);

        dataPoint2 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint2.setId(22);

        dataPoint3 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint2.setId(33);

        dataPoint4 = new DataPointVO(DataPointVO.LoggingTypes.ON_CHANGE);
        dataPoint2.setId(44);

        user1 = TestUtils.newUser(123);
        user2 = TestUtils.newUser(124);

        UsersProfileService usersProfileService = mock(UsersProfileService.class);
        UsersProfileVO usersProfile = new UsersProfileVO();
        when(usersProfileService.getUserProfileById(anyInt())).thenReturn(usersProfile);

    }
}
