package org.scada_lts.permissions.migration;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.scada_lts.permissions.migration.InfoUtils.iterationInfo;
import static org.scada_lts.permissions.migration.InfoUtils.userInfo;
import static org.scada_lts.permissions.migration.InfoUtils.viewInfo;
import static org.scada_lts.permissions.migration.MigrationPermissionsUtils.*;

class ComplementPermissionsCommand extends AbstractMeasurmentCommand {

    private static final Log LOG = LogFactory.getLog(ComplementPermissionsCommand.class);

    private final Map<Accesses, UsersProfileVO> profiles;
    private final MigrationPermissionsService migrationPermissionsService;
    private final MigrationDataService migrationDataService;
    private final List<View> views;

    ComplementPermissionsCommand(Map<Accesses, UsersProfileVO> profiles,
                                 MigrationPermissionsService migrationPermissionsService,
                                 MigrationDataService migrationDataService,
                                 List<View> views) {
        this.profiles = profiles;
        this.migrationPermissionsService = migrationPermissionsService;
        this.migrationDataService = migrationDataService;
        this.views = views;
    }

    @Override
    public void work(List<User> users) {
        AtomicInteger userIte = new AtomicInteger();
        users.forEach(user -> {
            long start = System.nanoTime();
            String msg = iterationInfo(userInfo(user), userIte.incrementAndGet(), users.size());
            LOG.info(msg);
            List<View> viewsWithAccess = views.stream()
                    .filter(view -> view.getUserAccess(user) > ShareUser.ACCESS_NONE)
                    .collect(Collectors.toList());

            if(!viewsWithAccess.isEmpty()) {
                AtomicInteger viewIte = new AtomicInteger();

                Set<DataPointAccess> dataPointAccesses = reduceToObjectExisting(fromView(user, viewsWithAccess, viewIte), migrationDataService.getDataPointService());
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
            }
            printTime(start, msg + " - executed: ");
        });
    }

    private Set<DataPointAccess> fromView(User user, List<View> viewsWithAccess, AtomicInteger viewIte) {
        Set<DataPointAccess> dataPointAccesses = accessesBy(user, migrationPermissionsService.getDataPointUserPermissionsService());

        viewsWithAccess.forEach(view -> {
            LOG.info(iterationInfo(viewInfo(view), viewIte.incrementAndGet(), viewsWithAccess.size()));

            getShareUser(user, view).ifPresent(shareUser -> {

                Set<DataPointAccess> dataPointAccessesFromView = findDataPointAccessesFromView(view, shareUser);
                dataPointAccesses.addAll(dataPointAccessesFromView);

            });
        });
        return dataPointAccesses;
    }

    @Override
    public String getName() {
        return "complement-permissions";
    }
}
