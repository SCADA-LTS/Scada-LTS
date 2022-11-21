package org.scada_lts.permissions.migration;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.scada_lts.permissions.migration.InfoUtils.profileInfo;
import static org.scada_lts.permissions.migration.MigrationPermissionsUtils.reduceToObjectExisting;


class MigrationPermissionsCommand extends AbstractMeasurmentCommand {

    private static final Log LOG = LogFactory.getLog(MigrationPermissionsCommand.class);

    private final MigrationPermissionsService migrationPermissionsService;
    private final MigrationDataService migrationDataService;

    MigrationPermissionsCommand(MigrationPermissionsService migrationPermissionsService,
                                MigrationDataService migrationDataService) {
        this.migrationPermissionsService = migrationPermissionsService;
        this.migrationDataService = migrationDataService;
    }

    @Override
    public void work(List<User> users) {
        String msg = getName() + " - views size: " + migrationDataService.getViews().size();
        LOG.info(msg);

        Map<Accesses, UsersProfileVO> profiles = new HashMap<>();
        migrationDataService.getUsersProfileService().getUsersProfiles()
                .forEach(profile -> {
                    Accesses key = reduceToObjectExisting(new Accesses(profile), migrationDataService);
                    UsersProfileVO reduceProfile = MigrationPermissionsUtils.newProfile(profile.getName(), key);
                    reduceProfile.setId(profile.getId());
                    reduceProfile.setXid(profile.getXid());
                    profiles.put(key, reduceProfile);
                    LOG.info(profileInfo(reduceProfile));
                });

        msg = getName() + " - profiles size: " + profiles.size();
        LOG.info(msg);

        MigrationPermissions verify = new VerifyPermissionsFromUserQuery(migrationDataService,
                migrationPermissionsService);
        MigrationPermissions complementPermissions = new ComplementPermissionsCommand(profiles,
                migrationPermissionsService, migrationDataService);
        MigrationPermissions transferToProfile = new TransferToProfileCommand(profiles,
                migrationPermissionsService, migrationDataService);

        VerifyPermissionsFromUserAndProfileQuery fromUserAndProfileVerify =
                new VerifyPermissionsFromUserAndProfileQuery(migrationDataService, migrationPermissionsService);

        fromUserAndProfileVerify.init(users);
        fromUserAndProfileVerify.execute(users);
        verify.execute(users);
        complementPermissions.execute(users);
        transferToProfile.execute(users);
        fromUserAndProfileVerify.execute(users);
        verify.execute(users);
        fromUserAndProfileVerify.clear();

        profiles.clear();
    }

    @Override
    public String getName() {
        return "migration-permissions";
    }
}
