package org.scada_lts.permissions.migration;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;


class VerifyPermissionsFromUserAndProfileQuery extends AbstractMeasurmentCommand {

    private final MigrationDataService migrationDataService;
    private final MigrationPermissionsService migrationPermissionsService;

    private final Map<Integer, Set<Integer>> dataSourceAccesses;
    private final Map<Integer, Set<DataPointAccess>> dataPointAccesses;
    private final Map<Integer, Set<ViewAccess>> viewAccesses;
    private final Map<Integer, Set<WatchListAccess>> watchListAccesses;

    VerifyPermissionsFromUserAndProfileQuery(MigrationDataService migrationDataService, MigrationPermissionsService migrationPermissionsService) {
        this.migrationDataService = migrationDataService;
        this.migrationPermissionsService = migrationPermissionsService;
        this.dataSourceAccesses = new HashMap<>();
        this.dataPointAccesses = new HashMap<>();
        this.viewAccesses = new HashMap<>();
        this.watchListAccesses = new HashMap<>();
    }

    @Override
    public void work(List<User> users) {

        users.forEach(user -> {

            UsersProfileVO usersProfile = migrationDataService.getUsersProfileService().getUserProfileById(user.getUserProfile());

            if (usersProfile == null) {
                usersProfile = new UsersProfileVO();
            }

            MigrationPermissionsUtils.verifyUserDataSourcePermissions(user, usersProfile,
                    migrationDataService,
                    dataSourceAccesses.get(user.getId()));

            MigrationPermissionsUtils.verifyUserDataPointPermissions(user, usersProfile,
                    migrationDataService,
                    dataPointAccesses.get(user.getId()));

            MigrationPermissionsUtils.verifyUserWatchListPermissions(user, usersProfile,
                    migrationDataService,
                    watchListAccesses.get(user.getId()));

            MigrationPermissionsUtils.verifyUserViewPermissions(user, usersProfile,
                    migrationDataService,
                    viewAccesses.get(user.getId()));

        });
    }

    @Override
    public String getName() {
        return "verify-permissions-from-user-and-profile";
    }

    public void clear() {
        dataSourceAccesses.clear();
        dataPointAccesses.clear();
        viewAccesses.clear();
        watchListAccesses.clear();
    }

    public void init(List<User> users) {
        clear();
        users.forEach(user -> {
            UsersProfileVO usersProfile = migrationDataService.getUsersProfileService().getUserProfileById(user.getUserProfile());

            if (usersProfile == null) {
                usersProfile = new UsersProfileVO();
            }

            setAccesses(user, usersProfile::getDataSourcePermissions,
                    migrationPermissionsService.getDataSourceUserPermissionsService()::getPermissions, dataSourceAccesses);
            setAccesses(user, usersProfile::getDataPointPermissions,
                        migrationPermissionsService.getDataPointUserPermissionsService()::getPermissions, dataPointAccesses);
            setAccesses(user, usersProfile::getViewPermissions,
                        migrationPermissionsService.getViewUserPermissionsService()::getPermissions, viewAccesses);
            setAccesses(user, usersProfile::getWatchlistPermissions,
                        migrationPermissionsService.getWatchListUserPermissionsService()::getPermissions, watchListAccesses);
        });
    }


    private <T> void setAccesses(User user,
                                 Supplier<List<T>> fromProfile,
                                 Function<User, List<T>> fromUser,
                                 Map<Integer, Set<T>> accesses) {
        accesses.putIfAbsent(user.getId(), new HashSet<>());
        accesses.get(user.getId()).addAll(fromProfile.get());
        accesses.get(user.getId()).addAll(fromUser.apply(user));
    }
}
