package org.scada_lts.permissions.migration;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.scada_lts.permissions.migration.MigrationPermissionsUtils.reduceToObjectExisting;


class MigrationPermissionsCommand extends AbstractMeasurmentCommand {

    private static final Log LOG = LogFactory.getLog(MigrationPermissionsCommand.class);

    private final MigrationPermissionsService migrationPermissionsService;
    private final MigrationDataService migrationDataService;
    private final List<View> views;

    MigrationPermissionsCommand(MigrationPermissionsService migrationPermissionsService,
                                MigrationDataService migrationDataService,
                                List<View> views) {
        this.migrationPermissionsService = migrationPermissionsService;
        this.migrationDataService = migrationDataService;
        this.views = views;
    }

    @Override
    public void work(List<User> users) {
        String msg = getName() + " - views size: " + views.size();
        LOG.info(msg);

        Map<Accesses, UsersProfileVO> profiles = new HashMap<>();
        migrationDataService.getUsersProfileService().getUsersProfiles()
                .forEach(profile -> {
                    Accesses key = reduceToObjectExisting(new Accesses(profile), migrationDataService);
                    UsersProfileVO reduceProfile = MigrationPermissionsUtils.newProfile(profile.getName(), key);
                    reduceProfile.setId(profile.getId());
                    reduceProfile.setXid(profile.getXid());
                    profiles.put(key, reduceProfile);
                });

        MigrationPermissions verify = new VerifyPermissionsQuery(migrationDataService,
                migrationPermissionsService);
        MigrationPermissions complementPermissions = new ComplementPermissionsCommand(profiles,
                migrationPermissionsService, migrationDataService, views);
        MigrationPermissions transferToProfile = new TransferToProfileCommand(profiles,
                migrationPermissionsService, migrationDataService);

        verify.execute(users);
        //complementPermissions.execute(users);
        transferToProfile.execute(users);
        verify.execute(users);

        profiles.clear();
    }

    @Override
    public String getName() {
        return "migration-permissions";
    }
}
