package org.scada_lts.dao.migration.mysql;


import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.*;
import org.scada_lts.dao.watchlist.WatchListDAO;
import org.scada_lts.mango.service.UserService;
import org.scada_lts.mango.service.UsersProfileService;
import org.scada_lts.permissions.migration.MigrationDataService;
import org.scada_lts.permissions.migration.MigrationPermissions;
import org.scada_lts.permissions.migration.MigrationPermissionsService;
import org.scada_lts.permissions.migration.dao.*;
import org.scada_lts.permissions.service.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class V2_7_0_2__FixViewPermissions extends BaseJavaMigration {

    private static final Log LOG = LogFactory.getLog(V2_7_0_2__FixViewPermissions.class);

    @Override
    public void migrate(Context context) throws Exception {
        try {
            migratePermissions();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private void migratePermissions() {
        UserDAO userDAO = new OnlyMigrationUserDAO();
        UsersProfileDAO usersProfileDAO = new OnlyMigrationUsersProfileDAO();
        WatchListDAO watchListDAO = new OnlyMigrationWatchListDAO();
        DataPointDAO dataPointDAO = new OnlyMigrationDataPointDAO();
        DataSourceDAO dataSourceDAO = new OnlyMigrationDataSourceDAO();
        ViewDAO viewDAO = new OnlyMigrationViewDAO();
        DataPointUserDAO dataPointUserDAO = new OnlyMigrationDataPointUserDAO();
        UserCommentDAO userCommentDAO = new OnlyMigrationUserCommentDAO();

        PermissionsService<WatchListAccess, UsersProfileVO> watchListPermissionsService =
                new WatchListProfilePermissionsService(usersProfileDAO);
        PermissionsService<DataPointAccess, UsersProfileVO> dataPointPermissionsService =
                new DataPointProfilePermissionsService(usersProfileDAO);
        PermissionsService<Integer, UsersProfileVO> dataSourcePermissionsService =
                new DataSourceProfilePermissionsService(usersProfileDAO);
        PermissionsService<ViewAccess, UsersProfileVO> viewPermissionsService =
                new ViewProfilePermissionsService(usersProfileDAO);

        UsersProfileService usersProfileService = new UsersProfileService(usersProfileDAO, userDAO,
                watchListPermissionsService, dataPointPermissionsService,
                dataSourcePermissionsService, viewPermissionsService);

        PermissionsService<DataPointAccess, User> dataPointUserPermissionsService =
                new DataPointUserPermissionsService(dataPointUserDAO);
        PermissionsService<Integer, User> dataSourceUserPermissionsService =
                new DataSourceUserPermissionsService(dataSourceDAO);
        PermissionsService<WatchListAccess, User> watchListUserPermissionsService =
                new WatchListUserPermissionsService(watchListDAO);
        PermissionsService<ViewAccess, User> viewUserPermissionsService =
                new ViewUserPermissionsService(viewDAO);

        UserService userService = new UserService(userDAO, userCommentDAO, null,
                null, null, usersProfileService,
                dataPointUserPermissionsService, dataSourceUserPermissionsService);

        List<User> users = userService.getUsersWithProfile().stream()
                .filter(a -> !a.isAdmin())
                .collect(Collectors.toList());

        if (!users.isEmpty()) {
            MigrationPermissionsService migrationPermissionsService = new MigrationPermissionsService(dataPointUserPermissionsService,
                    dataSourceUserPermissionsService, watchListUserPermissionsService, viewUserPermissionsService);

            Map<Integer, DataPointVO>  dataPoints = dataPointDAO.getDataPoints().stream().collect(Collectors.toMap(DataPointVO::getId, a -> a));
            Map<Integer, DataSourceVO<?>> dataSources = dataSourceDAO.getDataSources().stream().collect(Collectors.toMap(DataSourceVO::getId, a -> a));
            Map<Integer, View> views = viewDAO.findAll().stream().collect(Collectors.toMap(View::getId, a -> a));
            Map<Integer, WatchList> watchLists = watchListDAO.findAll().stream().collect(Collectors.toMap(WatchList::getId, a -> a));

            MigrationDataService migrationDataService = new MigrationDataService(dataPoints, dataSources, views, watchLists, usersProfileService);

            MigrationPermissions migrationCommand = MigrationPermissions.newMigration(migrationPermissionsService, migrationDataService);
            migrationCommand.execute(users);

            views.clear();
            migrationDataService.clear();
        }

        users.clear();
    }
}
