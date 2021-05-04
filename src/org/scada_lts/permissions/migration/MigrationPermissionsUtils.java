package org.scada_lts.permissions.migration;

import br.org.scadabr.api.exception.DAOException;
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
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.UsersProfileService;
import org.scada_lts.permissions.service.*;
import org.scada_lts.permissions.service.util.PermissionsUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

final class MigrationPermissionsUtils {

    private static final Log LOG = LogFactory.getLog(MigrationPermissionsUtils.class);

    private MigrationPermissionsUtils() {}

    static void verifyViewUserPermissions(List<View> views, User user, UsersProfileVO usersProfile) {
        Set<ViewAccess> viewAccesses = toViewAccesses(views, user);
        AtomicInteger i = new AtomicInteger();
        viewAccesses.forEach(permission -> verify(user, usersProfile, permission, usersProfile::getViewPermissions,
                (access, fromProfile) -> fromProfile.stream().anyMatch(c -> c.getId() == access.getId()
                        && c.getPermission() > access.getPermission()), i.incrementAndGet(), viewAccesses.size()));
    }

    static Set<ViewAccess> toViewAccesses(List<View> views, User user) {
        return views.stream()
                .filter(view -> view.getUserAccess(user) > ShareUser.ACCESS_NONE)
                .map(view -> new ViewAccess(view.getId(), view.getUserAccess(user)))
                .collect(Collectors.toSet());
    }

    static <T, S extends PermissionsService<T, User>> void verifyUserPermissions(S permissionsService, User user,
                                                                                         UsersProfileVO usersProfile,
                                                                                         Supplier<List<T>> fromProfile,
                                                                                         BiFunction<User, S, Set<T>> fromUser,
                                                                                         BiPredicate<T, List<T>> or) {
        Set<T> accesses = fromUser.apply(user, permissionsService);
        AtomicInteger i = new AtomicInteger();
        accesses.forEach(permission -> verify(user, usersProfile, permission, fromProfile, or, i.incrementAndGet(), accesses.size()));
    }

    static Accesses fromProfile(User user, Map<Accesses, UsersProfileVO> profiles) {
        Accesses fromProfile = Accesses.empty();
        if(user.getUserProfile() != Common.NEW_ID) {
            fromProfile = profiles.entrySet().stream()
                    .filter(a -> a.getValue().getId() == user.getUserProfile())
                    .findFirst()
                    .map(Map.Entry::getKey)
                    .orElse(Accesses.empty());
        }
        return fromProfile;
    }

    static <T, U> Set<T> accessesBy(U user, PermissionsService<T, U> userPermissionsService) {
        return new HashSet<>(userPermissionsService.getPermissions(user));
    }

    static void updatePermissions(User user, String prefix, UsersProfileService usersProfileService,
                                  Accesses key, Map<Accesses, UsersProfileVO> profiles) {
        UsersProfileVO profile;
        if(!profiles.containsKey(key)) {
            profile = createProfile(prefix, usersProfileService, key);
            if(profile.getId() != Common.NEW_ID)
                profiles.put(key, profile);
        } else {
            profile = profiles.get(key);
            LOG.info(MessageFormat.format("profile: {0} (xid: {1}) exists", profile.getName(), profile.getXid()));
        }
        user.setUserProfile(profile);
        usersProfileService.updateUsersProfile(user, profile);
        LOG.info(MessageFormat.format("profile: {0} (xid: {1}) update for user: {2} (id: {3})", profile.getName(), profile.getXid(), user.getUsername(), user.getId()));
    }

    static Optional<ShareUser> getShareUser(User user, View view) {
        return view.getViewUsers().stream()
                .filter(a -> a.getUserId() == user.getId())
                .filter(a -> a.getAccessType() > ShareUser.ACCESS_NONE)
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

    static void updatePermissions(List<View> views, User user, Set<DataPointAccess> dataPointAccesses,
                                  DataSourceUserPermissionsService dataSourceUserPermissionsService,
                                  WatchListUserPermissionsService watchListUserPermissionsService,
                                  UsersProfileService usersProfileService,
                                  Map<Accesses, UsersProfileVO> profiles) {
        Set<Integer> dataSourceAccesses = MigrationPermissionsUtils.accessesBy(user, dataSourceUserPermissionsService);
        Set<WatchListAccess> watchListAccesses = MigrationPermissionsUtils.accessesBy(user, watchListUserPermissionsService);
        Set<ViewAccess> viewAccesses = MigrationPermissionsUtils.toViewAccesses(views, user);

        Accesses fromUser = new Accesses(viewAccesses, watchListAccesses, dataPointAccesses, dataSourceAccesses);
        Accesses fromProfile = MigrationPermissionsUtils.fromProfile(user, profiles);

        Accesses accesses = MigrationPermissionsUtils.merge(fromUser, fromProfile);
        if(!accesses.isEmpty())
            MigrationPermissionsUtils.updatePermissions(user, "profile_", usersProfileService, accesses, profiles);
    }

    private static UsersProfileVO createProfile(String prefix, UsersProfileService usersProfileService, Accesses key) {
        String profileName = prefix + System.nanoTime();
        UsersProfileVO usersProfile = newProfile(profileName,
                key.getViewAccesses(),
                key.getWatchListAccesses(),
                key.getDataPointAccesses(),
                key.getDataSourceAccesses());

        boolean saved = saveUsersProfile(usersProfileService, usersProfile);
        int limit = 100;
        while (!saved && limit > 0) {
            String name = prefix + System.nanoTime();
            usersProfile.setName(name);
            saved = saveUsersProfile(usersProfileService, usersProfile);
            --limit;
        }
        LOG.info(MessageFormat.format("profile: {0} (xid: {1}) created: {2}", usersProfile.getName(), usersProfile.getXid(), saved));
        return usersProfile;
    }

    private static Accesses merge(Accesses accesses1, Accesses accesses2) {
        Set<ViewAccess> viewAccesses = PermissionsUtils.merge(accesses1.getViewAccesses(), accesses2.getViewAccesses());
        Set<WatchListAccess> watchListAccesses = PermissionsUtils.merge(accesses1.getWatchListAccesses(), accesses2.getWatchListAccesses());
        Set<Integer> dataSourceAccesses = PermissionsUtils.mergeInt(accesses1.getDataSourceAccesses(), accesses2.getDataSourceAccesses());
        Set<DataPointAccess> dataPointAccesses = PermissionsUtils.mergeDataPointAccesses(accesses1.getDataPointAccesses(), accesses2.getDataPointAccesses());
        return new Accesses(viewAccesses,watchListAccesses,dataPointAccesses,dataSourceAccesses);
    }

    private static <T> void verify(User user, UsersProfileVO usersProfile, T permission,
                                   Supplier<List<T>> supplier, BiPredicate<T, List<T>> or,
                                   int i, int max) {
        if(usersProfile != null && (supplier.get().contains(permission) || or.test(permission, supplier.get())))
            LOG.info(MessageFormat.format("permission {0} for user {1} (id: {2}) transfered to {3} (xid: {4}) {5}/{6}",
                    permission, user.getUsername(), user.getId(), usersProfile.getName(), usersProfile.getXid(), i, max));
        else if(usersProfile != null && usersProfile.getId() != Common.NEW_ID) {
            LOG.info(MessageFormat.format("permission {0} for user {1} (id: {2}) not transfered to {3} (xid: {4}) {5}/{6}",
                    permission, user.getUsername(), user.getId(), usersProfile.getName(), usersProfile.getXid(), i, max));
        } else {
            LOG.info(MessageFormat.format("permission {0} for user {1} (id: {2}) not transfered {3}/{4}",
                    permission, user.getUsername(), user.getId(), i, max));
        }
    }

    private static UsersProfileVO newProfile(String profileName,
                                             Set<ViewAccess> viewAccesses,
                                             Set<WatchListAccess> watchListAccesses,
                                             Set<DataPointAccess> dataPointAccesses,
                                             Set<Integer> dataSourceAccesses) {
        UsersProfileVO usersProfile = new UsersProfileVO();
        usersProfile.setName(profileName);
        usersProfile.setDataPointPermissions(new ArrayList<>(dataPointAccesses));
        usersProfile.setDataSourcePermissions(new ArrayList<>(dataSourceAccesses));
        usersProfile.setWatchlistPermissions(new ArrayList<>(watchListAccesses));
        usersProfile.setViewPermissions(new ArrayList<>(viewAccesses));
        return usersProfile;
    }

    private static boolean saveUsersProfile(UsersProfileService usersProfileService, UsersProfileVO usersProfile) {
        try {
            usersProfileService.saveUsersProfile(usersProfile);
            return true;
        } catch (DAOException e) {
            LOG.error(e);
            return false;
        }
    }

    private static void findDataPointAccesses(ViewComponent viewComponent, int accessType, Set<DataPointAccess> dataPointAccesses) {
        if(viewComponent instanceof CompoundComponent) {
            CompoundComponent compoundComponent = (CompoundComponent) viewComponent;
            compoundComponent.getChildComponents().stream()
                    .filter(a -> a.getViewComponent() != null)
                    .map(CompoundChild::getViewComponent)
                    .forEach(a -> findDataPointAccesses(a, accessType, dataPointAccesses));
        }
        if(viewComponent instanceof PointComponent) {
            PointComponent pointComponent = (PointComponent) viewComponent;
            if(pointComponent.tgetDataPoint() != null)
                dataPointAccesses.add(new DataPointAccess(pointComponent.tgetDataPoint().getId(), accessType));
        }
    }
}
