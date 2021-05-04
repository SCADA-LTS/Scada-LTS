package org.scada_lts.permissions.migration;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.UsersProfileService;
import org.scada_lts.permissions.service.DataPointUserPermissionsService;
import org.scada_lts.permissions.service.DataSourceUserPermissionsService;
import org.scada_lts.permissions.service.WatchListUserPermissionsService;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.scada_lts.permissions.migration.MigrationPermissionsUtils.updatePermissions;

class ComplementPermissionsCommand implements MigrationPermissions {

    private static final Log LOG = LogFactory.getLog(ComplementPermissionsCommand.class);

    private final Map<Accesses, UsersProfileVO> profiles;
    private final UsersProfileService usersProfileService;
    private final DataPointUserPermissionsService dataPointUserPermissionsService;
    private final DataSourceUserPermissionsService dataSourceUserPermissionsService;
    private final WatchListUserPermissionsService watchListUserPermissionsService;

    ComplementPermissionsCommand(Map<Accesses, UsersProfileVO> profiles,
                                        UsersProfileService usersProfileService,
                                        DataPointUserPermissionsService dataPointUserPermissionsService,
                                        DataSourceUserPermissionsService dataSourceUserPermissionsService,
                                        WatchListUserPermissionsService watchListUserPermissionsService) {
        this.profiles = profiles;
        this.usersProfileService = usersProfileService;
        this.dataPointUserPermissionsService = dataPointUserPermissionsService;
        this.dataSourceUserPermissionsService = dataSourceUserPermissionsService;
        this.watchListUserPermissionsService = watchListUserPermissionsService;
    }

    @Override
    public void execute(List<User> users, List<View> views) {

        LOG.info(getName() + "...");

        AtomicInteger userIte = new AtomicInteger();
        users.forEach(user -> {
            LOG.info(MessageFormat.format("user: {0} (id: {1}) - {2}/{3}", user.getUsername(), user.getId(), userIte.incrementAndGet(), users.size()));

            List<View> viewsWithAccess = views.stream()
                    .filter(view -> view.getUserAccess(user) > ShareUser.ACCESS_NONE)
                    .collect(Collectors.toList());

            if(!viewsWithAccess.isEmpty()) {
                AtomicInteger viewIte = new AtomicInteger();
                viewsWithAccess.forEach(view -> {
                    LOG.info(MessageFormat.format("view: {0} (id: {1}) - {2}/{3}", view.getName(), view.getId(), viewIte.incrementAndGet(), viewsWithAccess.size()));

                    MigrationPermissionsUtils.getShareUser(user, view).ifPresent(shareUser -> {

                        Set<DataPointAccess> dataPointAccessesFromView = MigrationPermissionsUtils.findDataPointAccessesFromView(view, shareUser);
                        Set<DataPointAccess> dataPointAccesses = MigrationPermissionsUtils.accessesBy(user, dataPointUserPermissionsService);
                        dataPointAccessesFromView.addAll(dataPointAccesses);

                        updatePermissions(views, user, dataPointAccessesFromView, dataSourceUserPermissionsService,
                                watchListUserPermissionsService, usersProfileService, profiles);
                    });

                });
            }
        });

        LOG.info(getName() + "... end");
    }

    @Override
    public String getName() {
        return "complement-permissions";
    }
}
