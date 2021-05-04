package org.scada_lts.permissions.migration;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
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

import static org.scada_lts.permissions.migration.MigrationPermissionsUtils.updatePermissions;

class TransferToProfileCommand implements MigrationPermissions {

    private static final Log LOG = LogFactory.getLog(TransferToProfileCommand.class);

    private final Map<Accesses, UsersProfileVO> profiles;
    private final UsersProfileService usersProfileService;
    private final DataPointUserPermissionsService userPermissionsService;
    private final DataSourceUserPermissionsService dataSourceUserPermissionsService;
    private final WatchListUserPermissionsService watchListUserPermissionsService;

    TransferToProfileCommand(Map<Accesses, UsersProfileVO> profiles,
                                    UsersProfileService usersProfileService,
                                    DataPointUserPermissionsService userPermissionsService,
                                    DataSourceUserPermissionsService dataSourceUserPermissionsService,
                                    WatchListUserPermissionsService watchListUserPermissionsService) {
        this.profiles = profiles;
        this.usersProfileService = usersProfileService;
        this.userPermissionsService = userPermissionsService;
        this.dataSourceUserPermissionsService = dataSourceUserPermissionsService;
        this.watchListUserPermissionsService = watchListUserPermissionsService;
    }

    @Override
    public void execute(List<User> users, List<View> views) {

        LOG.info(getName() + "...");

        AtomicInteger userIte = new AtomicInteger();
        users.forEach(user -> {
            LOG.info(MessageFormat.format("user: {0} (id: {1}) - {2}/{3}", user.getUsername(), user.getId(), userIte.incrementAndGet(), users.size()));
            Set<DataPointAccess> dataPointAccesses = MigrationPermissionsUtils.accessesBy(user, userPermissionsService);
            updatePermissions(views, user, dataPointAccesses, dataSourceUserPermissionsService,
                    watchListUserPermissionsService, usersProfileService, profiles);
        });

        LOG.info(getName() + "... end");
    }

    @Override
    public String getName() {
        return "transfer-to-profile";
    }
}
