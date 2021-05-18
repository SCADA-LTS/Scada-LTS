package org.scada_lts.permissions.migration;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;

import java.util.List;

import static org.scada_lts.permissions.migration.MigrationPermissionsUtils.*;

class VerifyPermissionsQuery extends AbstractMeasurmentCommand {

    private final MigrationDataService migrationDataService;
    private final MigrationPermissionsService migrationPermissionsService;

    VerifyPermissionsQuery(MigrationDataService migrationDataService, MigrationPermissionsService migrationPermissionsService) {
        this.migrationDataService = migrationDataService;
        this.migrationPermissionsService = migrationPermissionsService;
    }

    @Override
    public void work(List<User> users) {

        users.forEach(user -> {

            verifyUserDataSourcePermissions(user, migrationDataService.getUsersProfileService(),
                    migrationDataService.getDataSourceService(),
                    migrationPermissionsService.getDataSourceUserPermissionsService());

            verifyUserDataPointPermissions(user, migrationDataService.getUsersProfileService(),
                    migrationDataService.getDataPointService(),
                    migrationPermissionsService.getDataPointUserPermissionsService());

            verifyUserWatchListPermissions(user, migrationDataService.getUsersProfileService(),
                    migrationDataService.getWatchListService(),
                    migrationPermissionsService.getWatchListUserPermissionsService());

            verifyUserViewPermissions(user, migrationDataService.getUsersProfileService(),
                    migrationDataService.getViewService(),
                    migrationPermissionsService.getViewUserPermissionsService());

        });
    }

    @Override
    public String getName() {
        return "verify-permissions";
    }
}
