package org.scada_lts.permissions.migration;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;

import java.util.List;

import static org.scada_lts.permissions.migration.MigrationPermissionsUtils.containsPermission;
import static org.scada_lts.permissions.migration.MigrationPermissionsUtils.existsObject;
import static org.scada_lts.permissions.migration.MigrationPermissionsUtils.verifyUserPermissions;

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
            UsersProfileVO usersProfile = migrationDataService.getUsersProfileService().getUserProfileById(user.getUserProfile());

            if (usersProfile == null) {
                usersProfile = new UsersProfileVO();
            }

            verifyUserPermissions(migrationPermissionsService.getWatchListUserPermissionsService(), user, usersProfile,
                    usersProfile::getWatchlistPermissions, containsPermission(),
                    existsObject(migrationDataService.getWatchListService()::getWatchList));

            verifyUserPermissions(migrationPermissionsService.getDataSourceUserPermissionsService(), user, usersProfile,
                    usersProfile::getDataSourcePermissions, containsPermission(1),
                    existsObject(migrationDataService.getDataSourceService()));

            verifyUserPermissions(migrationPermissionsService.getDataPointUserPermissionsService(), user, usersProfile,
                    usersProfile::getDataPointPermissions, containsPermission(new DataPointAccess()),
                    existsObject(migrationDataService.getDataPointService()));

            verifyUserPermissions(migrationPermissionsService.getViewUserPermissionsService(), user, usersProfile,
                    usersProfile::getViewPermissions, containsPermission(),
                    existsObject(migrationDataService.getViewService()::getView));

        });
    }

    @Override
    public String getName() {
        return "verify-permissions";
    }
}
