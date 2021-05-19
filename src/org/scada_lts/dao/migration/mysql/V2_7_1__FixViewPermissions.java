package org.scada_lts.dao.migration.mysql;


import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.scada_lts.dao.*;
import org.scada_lts.dao.watchlist.WatchListDAO;
import org.scada_lts.mango.adapter.MangoDataPoint;
import org.scada_lts.mango.adapter.MangoDataSource;
import org.scada_lts.mango.service.*;
import org.scada_lts.permissions.migration.MigrationDataService;
import org.scada_lts.permissions.migration.MigrationPermissions;
import org.scada_lts.permissions.migration.MigrationPermissionsService;
import org.scada_lts.permissions.migration.dao.*;
import org.scada_lts.permissions.service.*;

import java.util.List;
import java.util.stream.Collectors;

public class V2_7_1__FixViewPermissions extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {

        UserDAO userDAO = new OnlyMigrationUserDAO();
        UsersProfileDAO usersProfileDAO = new OnlyMigrationUsersProfileDAO();
        WatchListDAO watchListDAO = new OnlyMigrationWatchListDAO();
        DataPointDAO dataPointDAO = new OnlyMigrationDataPointDAO();
        DataSourceDAO dataSourceDAO = new OnlyMigrationDataSourceDAO();
        ViewDAO viewDAO = new OnlyMigrationViewDAO();
        DataPointUserDAO dataPointUserDAO = new OnlyMigrationDataPointUserDAO();
        UserCommentDAO userCommentDAO = new OnlyMigrationUserCommentDAO();
        PointEventDetectorDAO pointEventDetectorDAO = new OnlyMigrationPointEventDetectorDAO();

        PermissionsService<WatchListAccess, UsersProfileVO> watchListPermissionsService =
                new WatchListProfilePermissionsService(watchListDAO, usersProfileDAO);
        PermissionsService<DataPointAccess, UsersProfileVO> dataPointPermissionsService =
                new DataPointProfilePermissionsService(usersProfileDAO, dataPointUserDAO);
        PermissionsService<Integer, UsersProfileVO> dataSourcePermissionsService =
                new DataSourceProfilePermissionsService(usersProfileDAO, dataSourceDAO);
        PermissionsService<ViewAccess, UsersProfileVO> viewPermissionsService =
                new ViewProfilePermissionsService(viewDAO, usersProfileDAO);

        UsersProfileService usersProfileService = new UsersProfileService(usersProfileDAO, DAO.getInstance(), userDAO,
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
                dataPointUserPermissionsService, dataSourceUserPermissionsService,
                dataPointPermissionsService, dataSourcePermissionsService,
                viewPermissionsService, watchListPermissionsService);

        List<User> users = userService.getUsersWithProfile().stream()
                .filter(a -> !a.isAdmin())
                .collect(Collectors.toList());

        if (!users.isEmpty()) {

            ViewService viewService = new ViewService(viewDAO, new ViewGetShareUsers(viewDAO, usersProfileDAO));
            List<View> views = viewService.getViews();

            MangoDataPoint dataPointService = new OnlyMigrationDataPointService(dataPointDAO, userCommentDAO, pointEventDetectorDAO);
            MangoDataSource dataSourceService = new OnlyMigrationDataSourceService(dataSourceDAO, dataPointService);
            WatchListService watchListService = new WatchListService(watchListDAO, new WatchListGetShareUsers(watchListDAO, usersProfileDAO));

            MigrationPermissionsService migrationPermissionsService = new MigrationPermissionsService(dataPointUserPermissionsService,
                    dataSourceUserPermissionsService, watchListUserPermissionsService, viewUserPermissionsService);

            MigrationDataService migrationDataService = new MigrationDataService(dataPointService, dataSourceService, viewService,
                    watchListService, usersProfileService);

            MigrationPermissions migrationCommand = MigrationPermissions.newMigration(migrationPermissionsService, migrationDataService, views);
            migrationCommand.execute(users);
        }
    }
}
