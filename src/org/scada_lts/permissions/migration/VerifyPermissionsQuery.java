package org.scada_lts.permissions.migration;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.UsersProfileService;
import org.scada_lts.permissions.service.DataPointUserPermissionsService;
import org.scada_lts.permissions.service.DataSourceUserPermissionsService;
import org.scada_lts.permissions.service.WatchListUserPermissionsService;

import java.util.List;
import java.util.Optional;

class VerifyPermissionsQuery implements MigrationPermissions {

    private static final Log LOG = LogFactory.getLog(VerifyPermissionsQuery.class);

    private final UsersProfileService usersProfileService;
    private final DataPointUserPermissionsService dataPointUserPermissionsService;
    private final DataSourceUserPermissionsService dataSourceUserPermissionsService;
    private final WatchListUserPermissionsService watchListUserPermissionsService;

    VerifyPermissionsQuery(UsersProfileService usersProfileService,
                                  DataPointUserPermissionsService dataPointUserPermissionsService,
                                  DataSourceUserPermissionsService dataSourceUserPermissionsService,
                                  WatchListUserPermissionsService watchListUserPermissionsService) {
        this.usersProfileService = usersProfileService;
        this.dataPointUserPermissionsService = dataPointUserPermissionsService;
        this.dataSourceUserPermissionsService = dataSourceUserPermissionsService;
        this.watchListUserPermissionsService = watchListUserPermissionsService;
    }

    @Override
    public void execute(List<User> users, List<View> views) {

        LOG.info(getName() + "...");

        users.forEach(user -> {
            UsersProfileVO usersProfile = Optional.ofNullable(usersProfileService.getUserProfileById(user.getUserProfile())).orElse(new UsersProfileVO());
            MigrationPermissionsUtils.verifyUserPermissions(watchListUserPermissionsService, user, usersProfile,
                    usersProfile::getWatchlistPermissions, MigrationPermissionsUtils::accessesBy,
                    (access, accesses) -> accesses.stream().anyMatch(c -> c.getId() == access.getId() && c.getPermission() > access.getPermission()));
            MigrationPermissionsUtils.verifyUserPermissions(dataSourceUserPermissionsService, user, usersProfile,
                    usersProfile::getDataSourcePermissions, MigrationPermissionsUtils::accessesBy,
                    (a, b) -> false);
            MigrationPermissionsUtils.verifyUserPermissions(dataPointUserPermissionsService, user, usersProfile,
                    usersProfile::getDataPointPermissions, MigrationPermissionsUtils::accessesBy,
                    (access, accesses) -> accesses.stream().anyMatch(c -> c.getDataPointId() == access.getDataPointId() && c.getPermission() > access.getPermission()));
            MigrationPermissionsUtils.verifyViewUserPermissions(views, user, usersProfile);
        });

        LOG.info(getName() + "... end");
    }

    @Override
    public String getName() {
        return "verify-permissions";
    }
}
