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

            Set<DataPointAccess> dataPointAccesses = reduceToObjectExisting(accessesBy(user, migrationPermissionsService.getDataPointUserPermissionsService()), migrationDataService.getDataPointService());
            Set<Integer> dataSourceAccesses = reduceToObjectExisting(accessesBy(user, migrationPermissionsService.getDataSourceUserPermissionsService()), migrationDataService.getDataSourceService());
            Set<WatchListAccess> watchListAccesses = reduceToObjectExisting(accessesBy(user, migrationPermissionsService.getWatchListUserPermissionsService()), migrationDataService.getWatchListService()::getWatchList, "watchlist: ");
            Set<ViewAccess> viewAccesses = reduceToObjectExisting(accessesBy(user, migrationPermissionsService.getViewUserPermissionsService()), migrationDataService.getViewService()::getView, "view: ");

            Accesses fromUser = new Accesses(viewAccesses, watchListAccesses, dataPointAccesses, dataSourceAccesses);
            Accesses fromProfile = fromProfile(user, profiles);

            Accesses accesses = MigrationPermissionsUtils.merge(fromUser, fromProfile);

            if(!accesses.isEmpty())
                updatePermissions(user, accesses, migrationDataService.getUsersProfileService(), profiles);
            else
                LOG.info(userInfo(user) + " no permissions.");

            printTime(start, msg + " - executed: ");
        });
    }

    @Override
    public String getName() {
        return "transfer-to-profile";
    }
}
