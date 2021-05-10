package org.scada_lts.dao.migration.mysql;


import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.mango.service.*;
import org.scada_lts.permissions.migration.MigrationDataService;
import org.scada_lts.permissions.migration.MigrationPermissions;
import org.scada_lts.permissions.migration.MigrationPermissionsService;
import org.scada_lts.permissions.service.*;

import java.util.List;
import java.util.stream.Collectors;

public class V2_7_1__FixViewPermissions extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        UserService userService = new UserService();
        List<User> users = userService.getUsersWithProfile().stream()
                .filter(a -> !a.isAdmin())
                .collect(Collectors.toList());
        if (!users.isEmpty()) {
            UsersProfileService usersProfileService = new UsersProfileService();
            DataPointUserPermissionsService dataPointUserPermissionsService = new DataPointUserPermissionsService();
            DataSourceUserPermissionsService dataSourceUserPermissionsService = new DataSourceUserPermissionsService();
            WatchListUserPermissionsService watchListUserPermissionsService = new WatchListUserPermissionsService();
            ViewUserPermissionsService viewUserPermissionsService = new ViewUserPermissionsService();

            ViewService viewService = new ViewService();
            List<View> views = viewService.getViews();
            ViewGetShareUsers viewGetShareUsers = new ViewGetShareUsers();
            views.forEach(view -> view.setViewUsers(viewGetShareUsers.getShareUsersWithProfile(view)));

            DataPointService dataPointService = new DataPointService();
            DataSourceService dataSourceService = new DataSourceService();
            WatchListService watchListService = new WatchListService();

            MigrationPermissionsService migrationPermissionsService = new MigrationPermissionsService(dataPointUserPermissionsService,
                    dataSourceUserPermissionsService, watchListUserPermissionsService,
                    viewUserPermissionsService);

            MigrationDataService migrationDataService = new MigrationDataService(dataPointService, dataSourceService, viewService,
                    watchListService, usersProfileService);

            MigrationPermissions migrationCommand = MigrationPermissions.newMigration(migrationPermissionsService, migrationDataService, views);
            migrationCommand.execute(users);
        }
    }
}
