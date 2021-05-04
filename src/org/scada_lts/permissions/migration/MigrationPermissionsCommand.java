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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MigrationPermissionsCommand implements MigrationPermissions {

    private static final Log LOG = LogFactory.getLog(MigrationPermissionsCommand.class);

    private final UsersProfileService usersProfileService;
    private final DataPointUserPermissionsService dataPointUserPermissionsService;
    private final DataSourceUserPermissionsService dataSourceUserPermissionsService;
    private final WatchListUserPermissionsService watchListUserPermissionsService;

    MigrationPermissionsCommand(UsersProfileService usersProfileService,
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

        LOG.info(getName() + " - users size: " + users.size() + " views size: " + views.size());

        Map<Accesses, UsersProfileVO> profiles = new HashMap<>();
        usersProfileService.getUsersProfiles()
                .forEach(profile -> profiles.put(new Accesses(profile), profile));

        MigrationPermissions verify = new VerifyPermissionsQuery(usersProfileService, dataPointUserPermissionsService,
                dataSourceUserPermissionsService, watchListUserPermissionsService);
        MigrationPermissions complementPermissions = new ComplementPermissionsCommand(profiles, usersProfileService, dataPointUserPermissionsService,
                dataSourceUserPermissionsService, watchListUserPermissionsService);
        MigrationPermissions transferToProfile = new TransferToProfileCommand(profiles, usersProfileService, dataPointUserPermissionsService,
                dataSourceUserPermissionsService, watchListUserPermissionsService);

        verify.execute(users, views);
        complementPermissions.execute(users, views);
        transferToProfile.execute(users, views);
        verify.execute(users, views);

        profiles.clear();

        LOG.info(getName() + "... end");
    }

    @Override
    public String getName() {
        return "migration-permissions";
    }
}
