package org.scada_lts.permissions.service.util;

import br.org.scadabr.vo.permission.Permission;
import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.permissions.service.PermissionsService;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PermissionsUtils {

    private static final Log LOG = LogFactory.getLog(PermissionsUtils.class);

    private PermissionsUtils() {}

    public static void updateWatchListPermissions(UsersProfileVO profile, PermissionsService<WatchListAccess, UsersProfileVO> service) {
        List<WatchListAccess> permissionsFromUser = profile.getWatchlistPermissions();
        update(profile, permissionsFromUser, service, Comparator.comparing(WatchListAccess::getId));
    }

    public static void updateViewPermissions(UsersProfileVO profile, PermissionsService<ViewAccess, UsersProfileVO> service) {
        List<ViewAccess> permissionsFromUser = profile.getViewPermissions();
        update(profile, permissionsFromUser, service, Comparator.comparing(ViewAccess::getId));
    }

    public static void updateDataPointPermissions(UsersProfileVO profile, PermissionsService<DataPointAccess, UsersProfileVO> service) {
        List<DataPointAccess> permissionsFromUser = profile.getDataPointPermissions();
        update(profile, permissionsFromUser, service, Comparator.comparing(DataPointAccess::getDataPointId));
    }

    public static void updateDataSourcePermissions(UsersProfileVO profile, PermissionsService<Integer, UsersProfileVO> service) {
        List<Integer> permissionsFromUser = profile.getDataSourcePermissions();
        update(profile, permissionsFromUser, service, Integer::compareTo);
    }

    public static void updateDataPointPermissions(User user, PermissionsService<DataPointAccess, User> service) {
        List<DataPointAccess> permissionsFromUser = user.getDataPointPermissions();
        update(user, permissionsFromUser, service, Comparator.comparing(DataPointAccess::getDataPointId));

    }

    public static void updateDataSourcePermissions(User user, PermissionsService<Integer, User> service) {
        List<Integer> permissionsFromUser = user.getDataSourcePermissions();
        update(user, permissionsFromUser, service, Integer::compareTo);
    }

    public static <T extends Permission> Set<T> merge(Set<T> accesses1, Set<T> accesses2) {
        return mergeToSet(accesses1, accesses2, Permission::getPermission, Permission::getId, a -> true);
    }

    public static List<ShareUser> merge(List<ShareUser> accesses1, List<ShareUser> accesses2) {
        return mergeToList(accesses1, accesses2, ShareUser::getAccessType, ShareUser::getUserId, a -> true);
    }

    public static Set<Integer> mergeInt(Set<Integer> accesses1, Set<Integer> accesses2) {
        return Stream.concat(accesses1.stream(), accesses2.stream())
                .collect(Collectors.toSet());
    }

    public static Set<DataPointAccess> mergeDataPointAccesses(Collection<DataPointAccess> accesses1,
                                                              Collection<DataPointAccess> accesses2,
                                                              Predicate<DataPointAccess> filter) {
        return mergeToSet(accesses1, accesses2, DataPointAccess::getPermission, DataPointAccess::getDataPointId, filter);
    }

    public static Set<DataPointAccess> mergeDataPointAccesses(Collection<DataPointAccess> accesses1,
                                                              Collection<DataPointAccess> accesses2) {
        return mergeToSet(accesses1, accesses2, DataPointAccess::getPermission, DataPointAccess::getDataPointId, a -> true);
    }

    public static <T> Set<T> reduce(Set<T> accesses, ToIntFunction<T> getAccess, ToIntFunction<T> getId) {
        return new HashSet<>(reduceStream(accesses.stream(), getAccess, getId, a -> true));
    }

    private static <T, U> void updatePermissions(U user, List<T> accessesFromUser,
                                                    PermissionsService<T, U> service,
                                                 Comparator<T> comparator) {
        List<T> accessesFromDatabase = service.getPermissions(user);
        if(!sortEquals(accessesFromUser, accessesFromDatabase, comparator)) {
            if(!accessesFromDatabase.isEmpty() && !accessesFromUser.containsAll(accessesFromDatabase)) {
                List<T> notExistInUser = diff(accessesFromUser, accessesFromDatabase);
                service.removePermissions(user, notExistInUser);
                accessesFromDatabase.removeAll(notExistInUser);
            }
            if((accessesFromDatabase.isEmpty() || !accessesFromDatabase.containsAll(accessesFromUser))
                    && !accessesFromUser.isEmpty()) {
                List<T> noExistInDatabase = diff(accessesFromDatabase, accessesFromUser);
                service.addOrUpdatePermissions(user, noExistInDatabase);
            }
        }
    }

    private static <T, U> void update(U user,
                                      List<T> permissionsFromUser,
                                      PermissionsService<T, U> service,
                                      Comparator<T> comparator) {
        updatePermissions(user, permissionsFromUser, service, comparator);
        List<T> permissionsFromDatabase = service.getPermissions(user);
        if(!sortEquals(permissionsFromDatabase, permissionsFromUser, comparator))
            LOG.error("Update permissions failed, database: " + permissionsFromDatabase + ", user: " + permissionsFromUser);
    }

    private static <T> boolean sortEquals(List<T> accessesFromUser,
                                          List<T> accessesFromDatabase,
                                          Comparator<T> comparator) {
        return sort(accessesFromUser, comparator).equals(sort(accessesFromDatabase, comparator));
    }

    private static <T> List<T> diff(List<T> in, List<T> base) {
        List<T> notExistIn = new ArrayList<>(base);
        notExistIn.removeAll(in);
        return notExistIn;
    }

    private static <T> List<T> sort(List<T> permissionsFromUser, Comparator<T> comparator) {
        return permissionsFromUser.stream().sorted(comparator).collect(Collectors.toList());
    }

    private static <T> List<T> mergeToList(Collection<T> accesses1, Collection<T> accesses2,
                                         ToIntFunction<T> getAccess, ToIntFunction<T> getId,
                                         Predicate<T> filter) {
        return new ArrayList<>(mergeToSet(accesses1, accesses2, getAccess, getId, filter));
    }

    private static <T> Set<T> mergeToSet(Collection<T> accesses1, Collection<T> accesses2,
                                         ToIntFunction<T> getAccess, ToIntFunction<T> getId,
                                         Predicate<T> filter) {
        Collection<T> collection = mergeToCollection(accesses1, accesses2, getAccess, getId, filter);
        if(collection instanceof HashSet) {
            return (Set<T>)collection;
        }
        return new HashSet<>(collection);
    }

    private static <T> Collection<T> mergeToCollection(Collection<T> accesses1, Collection<T> accesses2,
                                           ToIntFunction<T> getAccess, ToIntFunction<T> getId,
                                           Predicate<T> filter) {
        if(!accesses1.isEmpty() && !accesses2.isEmpty())
            return reduceStream(Stream.concat(accesses1.stream(), accesses2.stream()), getAccess, getId, filter);
        if(!accesses1.isEmpty())
            return reduceStream(accesses1.stream(), getAccess, getId, filter);
        return reduceStream(accesses2.stream(), getAccess, getId, filter);
    }

    private static <T> Collection<T> reduceStream(Stream<T> accesses,
                                                  ToIntFunction<T> getAccess,
                                                  ToIntFunction<T> getId,
                                                  Predicate<T> filter) {
        return accesses.filter(filter)
                .collect(toMapReduce(getAccess, getId))
                .values();
    }

    private static <T> Collector<T, ?, Map<Integer, T>> toMapReduce(ToIntFunction<T> getAccess, ToIntFunction<T> getId) {
        return Collectors.toMap(getId::applyAsInt, a -> a, (a, b) -> getAccess.applyAsInt(a) > getAccess.applyAsInt(b) ? a : b);
    }
}
