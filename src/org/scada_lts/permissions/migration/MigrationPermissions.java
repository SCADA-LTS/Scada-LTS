package org.scada_lts.permissions.migration;

import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import org.scada_lts.mango.service.UsersProfileService;
import org.scada_lts.permissions.service.DataPointUserPermissionsService;
import org.scada_lts.permissions.service.DataSourceUserPermissionsService;
import org.scada_lts.permissions.service.WatchListUserPermissionsService;

import java.util.List;

public interface MigrationPermissions {
    void execute(List<User> users, List<View> views);
    String getName();

    static MigrationPermissions newMigration(UsersProfileService usersProfileService,
                                          DataPointUserPermissionsService dataPointUserPermissionsService,
                                          DataSourceUserPermissionsService dataSourceUserPermissionsService,
                                          WatchListUserPermissionsService watchListUserPermissionsService) {
        return new MigrationPermissionsCommand(usersProfileService, dataPointUserPermissionsService,
                dataSourceUserPermissionsService, watchListUserPermissionsService);
    }
}
