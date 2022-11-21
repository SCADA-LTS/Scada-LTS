package org.scada_lts.permissions.migration;

import br.org.scadabr.api.exception.DAOException;
import br.org.scadabr.vo.permission.Permission;
import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.Common;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.view.component.CompoundChild;
import com.serotonin.mango.view.component.CompoundComponent;
import com.serotonin.mango.view.component.PointComponent;
import com.serotonin.mango.view.component.ViewComponent;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.*;
import org.scada_lts.permissions.service.*;
import org.scada_lts.permissions.service.util.PermissionsUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;
import java.util.stream.Collectors;

import static org.scada_lts.permissions.migration.InfoUtils.*;

public final class MigrationPermissionsUtils {

    private static final Log LOG = LogFactory.getLog(MigrationPermissionsUtils.class);

    private static final AtomicInteger i = new AtomicInteger();

    private MigrationPermissionsUtils() {}

    public static void updatePermissions(User user, Set<DataPointAccess> dataPointAccessesFromView,
                               Map<Accesses, UsersProfileVO> profiles,
                               MigrationPermissionsService migrationPermissionsService,
                               MigrationDataService migrationDataService) {
        Set<DataPointAccess> dataPointAccesses = reduceToObjectExistingDataPoint(dataPointAccessesFromView, migrationDataService::getDataPoint);
        Set<Integer> dataSourceAccesses = reduceToObjectExisting(accessesBy(user, migrationPermissionsService.getDataSourceUserPermissionsService()), migrationDataService::getDataSource);
        Set<WatchListAccess> watchListAccesses = reduceToObjectExisting(accessesBy(user, migrationPermissionsService.getWatchListUserPermissionsService()), migrationDataService::getWatchList, "watchlist: ");
        Set<ViewAccess> viewAccesses = reduceToObjectExisting(accessesBy(user, migrationPermissionsService.getViewUserPermissionsService()), migrationDataService::getView, "view: ");

        Accesses fromUser = new Accesses(viewAccesses, watchListAccesses, dataPointAccesses, dataSourceAccesses);
        Accesses fromProfile = fromProfile(user, profiles);

        Accesses accesses = MigrationPermissionsUtils.merge(fromUser, fromProfile);

        if(!accesses.isEmpty()) {
            updatePermissions(user, accesses, migrationDataService.getUsersProfileService(), profiles);
        } else
            LOG.info(userInfo(user) + " no permissions.");
    }

    static void verifyUserWatchListPermissions(User user, UsersProfileService usersProfileService,
                                               MigrationDataService watchListService,
                                               PermissionsService<WatchListAccess, User> watchListUserPermissionsService) {
        UsersProfileVO usersProfile = usersProfileService.getUserProfileById(user.getUserProfile());

        if (usersProfile == null) {
            usersProfile = new UsersProfileVO();
        }

        verifyUserPermissions(watchListUserPermissionsService, user, usersProfile,
                usersProfile::getWatchlistPermissions, containsPermission(),
                existsObject(watchListService::getWatchList));
    }


    static void verifyUserViewPermissions(User user, UsersProfileService usersProfileService,
                                          MigrationDataService viewService,
                                          PermissionsService<ViewAccess, User> viewUserPermissionsService) {
        UsersProfileVO usersProfile = usersProfileService.getUserProfileById(user.getUserProfile());

        if (usersProfile == null) {
            usersProfile = new UsersProfileVO();
        }

        verifyUserPermissions(viewUserPermissionsService, user, usersProfile,
                usersProfile::getViewPermissions, containsPermission(),
                existsObject(viewService::getView));
    }

    static void verifyUserDataSourcePermissions(User user, UsersProfileService usersProfileService,
                                                MigrationDataService dataSourceService,
                                                PermissionsService<Integer, User> dataSourceUserPermissionsService) {
        UsersProfileVO usersProfile = usersProfileService.getUserProfileById(user.getUserProfile());

        if (usersProfile == null) {
            usersProfile = new UsersProfileVO();
        }

        verifyUserPermissions(dataSourceUserPermissionsService, user, usersProfile,
                usersProfile::getDataSourcePermissions, containsPermission(1),
                existsDataSource(dataSourceService));
    }

    static void verifyUserDataPointPermissions(User user, UsersProfileService usersProfileService,
                                               MigrationDataService dataPointService,
                                               PermissionsService<DataPointAccess, User> dataPointUserPermissionsService) {
        UsersProfileVO usersProfile = usersProfileService.getUserProfileById(user.getUserProfile());

        if (usersProfile == null) {
            usersProfile = new UsersProfileVO();
        }

        verifyUserPermissions(dataPointUserPermissionsService, user, usersProfile,
                usersProfile::getDataPointPermissions, containsPermission(new DataPointAccess()),
                existsDataPoint(dataPointService));
    }

    static void verifyUserWatchListPermissions(User user, UsersProfileVO usersProfile,
                                               MigrationDataService watchListService,
                                               Set<WatchListAccess> oldAccesses) {

        verifyUserPermissions(oldAccesses, user, usersProfile,
                usersProfile::getWatchlistPermissions, containsPermission(),
                existsObject(watchListService::getWatchList));
    }


    static void verifyUserViewPermissions(User user, UsersProfileVO usersProfile,
                                          MigrationDataService viewService,
                                          Set<ViewAccess> oldAccesses) {
        verifyUserPermissions(oldAccesses, user, usersProfile,
                usersProfile::getViewPermissions, containsPermission(),
                existsObject(viewService::getView));
    }

    static void verifyUserDataSourcePermissions(User user, UsersProfileVO usersProfile,
                                                MigrationDataService dataSourceService,
                                                Set<Integer> oldAccesses) {
        verifyUserPermissions(oldAccesses, user, usersProfile,
                usersProfile::getDataSourcePermissions, containsPermission(1),
                existsDataSource(dataSourceService));
    }

    static void verifyUserDataPointPermissions(User user, UsersProfileVO usersProfile,
                                               MigrationDataService dataPointService,
                                               Set<DataPointAccess> oldAccesses) {

        verifyUserPermissions(oldAccesses, user, usersProfile,
                usersProfile::getDataPointPermissions, containsPermission(new DataPointAccess()),
                existsDataPoint(dataPointService));
    }

    static Accesses fromProfile(User user, Map<Accesses, UsersProfileVO> profiles) {
        Accesses fromProfile = Accesses.empty();
        if(user.getUserProfile() != Common.NEW_ID) {
            fromProfile = profiles.entrySet().stream()
                    .filter(a -> a.getValue().getId() == user.getUserProfile())
                    .map(Map.Entry::getKey)
                    .reduce(Accesses.empty(), MigrationPermissionsUtils::merge);
        }
        return fromProfile;
    }

    static <T, U> Set<T> accessesBy(U user, PermissionsService<T, U> userPermissionsService) {
        return new HashSet<>(userPermissionsService.getPermissions(user));
    }

    static Optional<ShareUser> getShareUser(User user, View view) {
        Set<ShareUser> shareUsers = view.getViewUsers().stream()
                .filter(a -> a.getUserId() == user.getId())
                .filter(a -> a.getAccessType() > ShareUser.ACCESS_NONE)
                .collect(Collectors.toSet());
        if(shareUsers.isEmpty())
            return Optional.empty();
        return PermissionsUtils.reduce(shareUsers, ShareUser::getAccessType, ShareUser::getUserId)
                .stream()
                .findFirst();
    }

    static Set<DataPointAccess> findDataPointAccessesFromView(View view, ShareUser shareUser) {
        Set<DataPointAccess> dataPointAccessesFromView = new HashSet<>();
        view.getViewComponents()
                .forEach(viewComponent ->
                        findDataPointAccesses(viewComponent,
                                shareUser.getAccessType(), dataPointAccessesFromView));
        return dataPointAccessesFromView;
    }

    static List<DataPointVO> findDataPointsFromView(View view) {
        List<DataPointVO> dataPointAccessesFromView = new ArrayList<>();
        view.getViewComponents()
                .forEach(viewComponent ->
                        findDataPoints(viewComponent, dataPointAccessesFromView));
        return dataPointAccessesFromView;
    }

    public static void updatePermissions(User user, Accesses accesses, UsersProfileService usersProfileService,
                                  Map<Accesses, UsersProfileVO> profiles) {
        if(user.getUserProfile() != Common.NEW_ID
                && updateProfile(user, accesses, usersProfileService, profiles)) {
                return;
        }
        updatePermissions(user, user.getUsername() +  "_", usersProfileService, accesses, profiles);
    }

    private static boolean updateProfile(User user, Accesses currentKey, UsersProfileService usersProfileService,
                                         Map<Accesses, UsersProfileVO> profiles) {
        Optional<UsersProfileVO> fromUserProfile = profiles.values().stream()
                .filter(usersProfileVO -> usersProfileVO.getId() == user.getUserProfile())
                .findAny();
        final String prefix = fromUserProfile.map(UsersProfileVO::getName).orElse("profile");

        if(fromUserProfile.isPresent()) {
            UsersProfileVO profile = fromUserProfile.get();
            Accesses fromUserKey = new Accesses(profile);
            Accesses formUserUpdateKey = merge(currentKey, fromUserKey);

            Optional<UsersProfileVO> existWithPrefixProfile = profiles.values().stream()
                    .filter(usersProfileVO -> usersProfileVO.getName().contains(prefix + "_"))
                    .findAny();

            if(existWithPrefixProfile.isPresent()) {

                UsersProfileVO withPrefixProfile = existWithPrefixProfile.get();
                Accesses withPrefixProfileKey = new Accesses(withPrefixProfile);
                Accesses withPrefixProfileUpdateKey = merge(currentKey, withPrefixProfileKey);
                withPrefixProfile.setDataPointPermissions(new ArrayList<>(withPrefixProfileUpdateKey.getDataPointAccesses()));
                withPrefixProfile.setDataSourcePermissions(new ArrayList<>(withPrefixProfileUpdateKey.getDataSourceAccesses()));
                withPrefixProfile.setViewPermissions(new ArrayList<>(withPrefixProfileUpdateKey.getViewAccesses()));
                withPrefixProfile.setWatchlistPermissions(new ArrayList<>(withPrefixProfileUpdateKey.getWatchListAccesses()));
                try {
                    usersProfileService.saveUsersProfile(withPrefixProfile);
                    profiles.remove(withPrefixProfileKey);
                    profiles.put(withPrefixProfileUpdateKey, withPrefixProfile);
                    updatePermissions(user, prefix + "_", usersProfileService, withPrefixProfileUpdateKey, profiles);
                } catch (DAOException e) {
                    LOG.warn(e.getMessage());
                    return false;
                }
            } else {
                updatePermissions(user, prefix + "_", usersProfileService, formUserUpdateKey, profiles);
            }
        } else {
            updatePermissions(user, prefix + "_", usersProfileService, currentKey, profiles);
        }
        return true;
    }

    static void printTime(long start, String msg) {
        LOG.info(msg + (System.nanoTime() - start)/1000000 + "[ms]");
    }

    static Accesses reduceToObjectExisting(Accesses accesses, MigrationDataService migrationDataService) {
        return new Accesses(reduceToObjectExisting(accesses.getViewAccesses(), migrationDataService::getView, "view: "),
                reduceToObjectExisting(accesses.getWatchListAccesses(), migrationDataService::getWatchList, "watchlist: "),
                reduceToObjectExistingDataPoint(accesses.getDataPointAccesses(), migrationDataService::getDataPoint),
                reduceToObjectExisting(accesses.getDataSourceAccesses(), migrationDataService::getDataSource));
    }

    static <T extends Permission> Set<T> reduceToObjectExisting(Set<T> objects, IntFunction<?> verify, String msg) {
        return objects.stream()
                .map(a -> exists(verify, a, msg) ? a : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    static Set<Integer> reduceToObjectExisting(Set<Integer> objects, IntFunction<?> verify) {
        return objects.stream()
                .map(a -> exists(verify, a) ? a : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    static Set<DataPointAccess> reduceToObjectExistingDataPoint(Set<DataPointAccess> objects, IntFunction<?> verify) {
        return objects.stream()
                .map(a -> exists(verify, a) ? a : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    public static Accesses merge(Accesses accesses1, Accesses accesses2) {
        Set<ViewAccess> viewAccesses = PermissionsUtils.merge(accesses1.getViewAccesses(), accesses2.getViewAccesses());
        Set<WatchListAccess> watchListAccesses = PermissionsUtils.merge(accesses1.getWatchListAccesses(), accesses2.getWatchListAccesses());
        Set<Integer> dataSourceAccesses = PermissionsUtils.mergeInt(accesses1.getDataSourceAccesses(), accesses2.getDataSourceAccesses());
        Set<DataPointAccess> dataPointAccesses = PermissionsUtils.mergeDataPointAccesses(accesses1.getDataPointAccesses(), accesses2.getDataPointAccesses());
        return new Accesses(viewAccesses,watchListAccesses,dataPointAccesses,dataSourceAccesses);
    }

    static UsersProfileVO newProfile(String profileName, Accesses accesses) {
        UsersProfileVO usersProfile = new UsersProfileVO();
        usersProfile.setName(profileName);
        usersProfile.setDataPointPermissions(new ArrayList<>(accesses.getDataPointAccesses()));
        usersProfile.setDataSourcePermissions(new ArrayList<>(accesses.getDataSourceAccesses()));
        usersProfile.setWatchlistPermissions(new ArrayList<>(accesses.getWatchListAccesses()));
        usersProfile.setViewPermissions(new ArrayList<>(accesses.getViewAccesses()));
        return usersProfile;
    }

    static void findDataPointAccesses(ViewComponent viewComponent, int accessType, Set<DataPointAccess> dataPointAccesses) {
        if(viewComponent instanceof CompoundComponent) {
            CompoundComponent compoundComponent = (CompoundComponent) viewComponent;
            compoundComponent.getChildComponents().stream()
                    .filter(a -> a.getViewComponent() != null)
                    .map(CompoundChild::getViewComponent)
                    .forEach(a -> findDataPointAccesses(a, accessType, dataPointAccesses));
        }
        if(viewComponent instanceof PointComponent) {
            PointComponent pointComponent = (PointComponent) viewComponent;
            if(pointComponent.tgetDataPoint() != null) {
                LOG.info(dataPointInfo("Found ", pointComponent.tgetDataPoint()));
                dataPointAccesses.add(new DataPointAccess(pointComponent.tgetDataPoint().getId(), accessType));
            }
        }
    }

    static void findDataPoints(ViewComponent viewComponent, List<DataPointVO> dataPoints) {
        if(viewComponent instanceof CompoundComponent) {
            CompoundComponent compoundComponent = (CompoundComponent) viewComponent;
            compoundComponent.getChildComponents().stream()
                    .filter(a -> a.getViewComponent() != null)
                    .map(CompoundChild::getViewComponent)
                    .forEach(a -> findDataPoints(a, dataPoints));
        }
        if(viewComponent instanceof PointComponent) {
            PointComponent pointComponent = (PointComponent) viewComponent;
            if(pointComponent.tgetDataPoint() != null) {
                LOG.info(dataPointInfo("Found ", pointComponent.tgetDataPoint()));
                DataPointVO dataPoint = pointComponent.tgetDataPoint();
                if(dataPoint.getPointLocator() != null) {
                    dataPoint.setSettable(dataPoint.getPointLocator().isSettable());
                }
                dataPoints.add(dataPoint);
            }
        }
    }

    public static DataPointAccess generateDataPointAccess(ShareUser shareUser, DataPointVO dataPoint) {
        return new DataPointAccess(dataPoint.getId(), !dataPoint.isSettable() && shareUser.getAccessType() > ShareUser.ACCESS_READ ? ShareUser.ACCESS_READ : shareUser.getAccessType());
    }

    public static Map<Integer, List<DataPointVO>> findDataPointsFromViews(List<View> views) {
        Map<Integer, List<DataPointVO>> dataPoints = new HashMap<>();
        LOG.info("search-datapoints");
        views.forEach(a -> {
            LOG.info(viewInfo(a));
            dataPoints.put(a.getId(), findDataPointsFromView(a));
        });
        LOG.info("search-datapoints end");
        return dataPoints;
    }

    public static Set<DataPointAccess> selectDataPointAccesses(Map<Integer, List<DataPointVO>> dataPointsFromViews, Set<DataPointAccess> dataPointAccesses, View view, ShareUser shareUser) {
        return dataPointsFromViews.get(view.getId()).stream()
                .map(dataPoint -> generateDataPointAccess(shareUser, dataPoint))
                .filter(access -> includeAccessIfNotContainsOrHigherPermission(dataPointAccesses, access))
                .collect(Collectors.toSet());
    }

    private static boolean includeAccessIfNotContainsOrHigherPermission(Set<DataPointAccess> dataPointAccesses, DataPointAccess access) {
        return dataPointAccesses.isEmpty() || dataPointAccesses.stream().allMatch(a -> a.getDataPointId() != access.getDataPointId() || a.getPermission() < access.getPermission());
    }

    private static <T extends Permission> BiPredicate<T, List<T>> containsPermission() {
        return (access, accesses) -> accesses.contains(access)
                || accesses.stream().anyMatch(c -> c.getId() == access.getId() && c.getPermission() > access.getPermission());
    }

    private static BiPredicate<DataPointAccess, List<DataPointAccess>> containsPermission(DataPointAccess dataPointAccess) {
        return (access, accesses) -> accesses.contains(access)
                || accesses.stream().anyMatch(c -> c.getDataPointId() == access.getDataPointId() && c.getPermission() > access.getPermission());
    }

    private static BiPredicate<Integer, List<Integer>> containsPermission(Integer dataSourceAccess) {
        return (access, accesses) -> accesses.contains(access);
    }

    private static <T extends Permission> Predicate<T> existsObject(IntFunction<?> verify) {
        return (access) -> exists(verify, access, "object: ");
    }

    private static Predicate<Integer> existsDataSource(MigrationDataService dataSourceService) {
        return (access) ->  exists(dataSourceService::getDataSource, access);
    }

    private static Predicate<DataPointAccess> existsDataPoint(MigrationDataService dataPointService) {
        return (access) ->  exists(dataPointService::getDataPoint, access);
    }

    private static <T, S extends PermissionsService<T, User>> void verifyUserPermissions(S permissionsService, User user,
                                                                                         UsersProfileVO usersProfile,
                                                                                         Supplier<List<T>> fromProfile,
                                                                                         BiPredicate<T, List<T>> containsPermission,
                                                                                         Predicate<T> existsObject) {
        Set<T> fromUserAccesses = new HashSet<>(permissionsService.getPermissions(user));
        AtomicInteger i = new AtomicInteger();
        fromUserAccesses.forEach(permission -> {
            boolean transferred = containsPermission.test(permission, fromProfile.get());
            boolean exists = existsObject.test(permission);
            boolean ok = transferred || !exists || isNonPermission(permission);
            String msg = verifyInfo(permission, user, transferred, exists, ok, usersProfile);
            LOG.info(iterationInfo(msg, i.incrementAndGet(), fromUserAccesses.size()));
        });
    }

    private static <T> void verifyUserPermissions(Set<T> oldAccesses, User user,
                                                  UsersProfileVO usersProfile,
                                                  Supplier<List<T>> fromProfile,
                                                  BiPredicate<T, List<T>> containsPermission,
                                                  Predicate<T> existsObject) {
        AtomicInteger i = new AtomicInteger();
        oldAccesses.forEach(permission -> {
            boolean transferred = containsPermission.test(permission, fromProfile.get());
            boolean exists = existsObject.test(permission);
            boolean ok = transferred || !exists || isNonPermission(permission);
            String msg = verifyInfo(permission, user, transferred, exists, ok, usersProfile);
            LOG.info(iterationInfo(msg, i.incrementAndGet(), oldAccesses.size()));
        });
    }

    private static <T> boolean isNonPermission(T permission) {
        return permission instanceof Permission ? isNonPermission((Permission) permission) : (permission instanceof DataPointAccess && isNonPermission((DataPointAccess) permission));
    }

    private static UsersProfileVO createProfile(String prefix, UsersProfileService usersProfileService, Accesses key) {
        String profileName = newProfileName(prefix);
        UsersProfileVO usersProfile = newProfile(profileName, key);

        boolean saved = saveUsersProfile(usersProfileService, usersProfile);
        int limit = 100;
        while ((!saved || usersProfile.getId() == Common.NEW_ID) && limit > 0) {
            String name = newProfileName(prefix);
            usersProfile.setName(name);
            saved = saveUsersProfile(usersProfileService, usersProfile);
            --limit;
        }
        LOG.info(MessageFormat.format("{0} created: {1}", profileInfo(usersProfile), saved));
        return usersProfile;
    }

    private static String newProfileName(String prefix) {
        return prefix + i.incrementAndGet();
    }

    private static boolean saveUsersProfile(UsersProfileService usersProfileService, UsersProfileVO usersProfile) {
        try {
            usersProfileService.saveUsersProfile(usersProfile);
            return true;
        } catch (DAOException e) {
            LOG.error(e);
            return false;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
    }

    private static void updatePermissions(User user, String prefix, UsersProfileService usersProfileService,
                                          Accesses key, Map<Accesses, UsersProfileVO> profiles) {


        UsersProfileVO profile;
        if(!profiles.containsKey(key)) {
            profile = createProfile(prefix, usersProfileService, key);
            if(profile.getId() != Common.NEW_ID)
                profiles.put(key, profile);
        } else {
            profile = profiles.get(key);
            LOG.info(MessageFormat.format("{0} exists", profileInfo(profile)));
        }
        if(user.getUserProfile() != profile.getId()) {
            user.setUserProfile(profile);
            usersProfileService.updateUsersProfile(user, profile);
            LOG.info(MessageFormat.format("{0} has been assigned to {1}", profileInfo(profile), userInfo(user)));
        } else {
            LOG.info(MessageFormat.format("{0} is already assigned to {1}", profileInfo(profile), userInfo(user)));
        }
    }

    private static <T extends Permission> boolean exists(IntFunction<?> verify, T a, String msg) {
        try {
            Object object = verify.apply(a.getId());
            if(object == null) {
                LOG.info(msg + a.getId() + ", msg: does not exist");
                return false;
            }
            return true;
        } catch (Exception ex) {
            LOG.warn(msg + a.getId() + ", msg: " + (ex.getMessage() == null ? "no message" : ex.getMessage()));
            return false;
        }
    }

    private static boolean exists(IntFunction<?> verify, DataPointAccess a) {
        try {
            Object object = verify.apply(a.getDataPointId());
            if(object == null) {
                LOG.info("datapoint: " +  a.getDataPointId() + ", msg: does not exist");
                return false;
            }
            return true;
        } catch (Exception ex) {
            LOG.warn("datapoint: " + a.getDataPointId() + ", msg: " + (ex.getMessage() == null ? "no message" : ex.getMessage()));
            return false;
        }
    }

    private static boolean exists(IntFunction<?> verify, Integer a) {
        try {
            Object object = verify.apply(a);
            if(object == null) {
                LOG.info("datasource: " +  a + ", msg: does not exist");
                return false;
            }
            return true;
        } catch (Exception ex) {
            LOG.warn("datasource: " + a + ", msg: " + (ex.getMessage() == null ? "no message" : ex.getMessage()));
            return false;
        }
    }

    private static boolean isNonPermission(Permission permission) {
        return permission.getPermission() < 1;
    }

    private static boolean isNonPermission(DataPointAccess permission) {
        return permission.getPermission() < 1;
    }
}
