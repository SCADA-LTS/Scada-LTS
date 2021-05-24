package org.scada_lts.permissions.migration;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.scada_lts.permissions.migration.InfoUtils.iterationInfo;
import static org.scada_lts.permissions.migration.InfoUtils.userInfo;
import static org.scada_lts.permissions.migration.MigrationPermissionsUtils.*;

class TransferToProfileCommand extends AbstractMeasurmentCommand {

    private static final Log LOG = LogFactory.getLog(TransferToProfileCommand.class);

    private final Map<Accesses, UsersProfileVO> profiles;
    private final MigrationPermissionsService migrationPermissionsService;
    private final MigrationDataService migrationDataService;

    TransferToProfileCommand(Map<Accesses, UsersProfileVO> profiles,
                                 MigrationPermissionsService migrationPermissionsService,
                                 MigrationDataService migrationDataService) {
        this.profiles = profiles;
        this.migrationPermissionsService = migrationPermissionsService;
        this.migrationDataService = migrationDataService;
    }

    @Override
    public void work(List<User> users) {

        AtomicInteger userIte = new AtomicInteger();
        users.forEach(user -> {
            long start = System.nanoTime();
            String msg = iterationInfo(userInfo(user), userIte.incrementAndGet(), users.size());
            LOG.info(msg);
            Set<DataPointAccess> dataPointAccesses = accessesBy(user, migrationPermissionsService.getDataPointUserPermissionsService());
            updatePermissions(user, dataPointAccesses, profiles, migrationPermissionsService, migrationDataService);
            printTime(start, msg + " - executed: ");
        });
    }

    @Override
    public String getName() {
        return "transfer-to-profile";
    }
}
