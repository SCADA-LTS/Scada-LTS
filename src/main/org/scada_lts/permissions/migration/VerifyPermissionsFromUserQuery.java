package org.scada_lts.permissions.migration;

import com.serotonin.mango.vo.User;

import java.util.List;

import static org.scada_lts.permissions.migration.MigrationPermissionsUtils.*;

class VerifyPermissionsFromUserQuery extends AbstractMeasurmentCommand {

    private final MigrationDataService migrationDataService;
    private final MigrationPermissionsService migrationPermissionsService;

    VerifyPermissionsFromUserQuery(MigrationDataService migrationDataService, MigrationPermissionsService migrationPermissionsService) {
        this.migrationDataService = migrationDataService;
        this.migrationPermissionsService = migrationPermissionsService;
    }

    @Override
    public void work(List<User> users) {

        users.forEach(user -> {

            verifyUserDataSourcePermissions(user, migrationDataService.getUsersProfileService(),
                    migrationDataService,
                    migrationPermissionsService.getDataSourceUserPermissionsService());

            verifyUserDataPointPermissions(user, migrationDataService.getUsersProfileService(),
                    migrationDataService,
                    migrationPermissionsService.getDataPointUserPermissionsService());

            verifyUserWatchListPermissions(user, migrationDataService.getUsersProfileService(),
                    migrationDataService,
                    migrationPermissionsService.getWatchListUserPermissionsService());

            verifyUserViewPermissions(user, migrationDataService.getUsersProfileService(),
                    migrationDataService,
                    migrationPermissionsService.getViewUserPermissionsService());

        });
    }

    @Override
    public String getName() {
        return "verify-permissions-from-user";
    }
}
