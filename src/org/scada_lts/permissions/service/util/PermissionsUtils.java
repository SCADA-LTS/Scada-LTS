package org.scada_lts.permissions.service.util;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.permissions.service.PermissionsService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class PermissionsUtils {

    private static final Log LOG = LogFactory.getLog(PermissionsUtils.class);

    private PermissionsUtils() {}

    public static void updateWatchListPermissions(User user, PermissionsService<WatchListAccess, WatchList> service) {
        List<WatchListAccess> permissionsFromUser = user.getWatchListPermissions();
        update(user, permissionsFromUser, service, Comparator.comparing(WatchListAccess::getId));
    }

    public static void updateViewPermissions(User user, PermissionsService<ViewAccess, View> service) {
        List<ViewAccess> permissionsFromUser = user.getViewPermissions();
        update(user, permissionsFromUser, service, Comparator.comparing(ViewAccess::getId));
    }

    public static void updateDataPointPermissions(User user, PermissionsService<DataPointAccess, DataPointVO> service) {
        List<DataPointAccess> permissionsFromUser = user.getDataPointPermissions();
        update(user, permissionsFromUser, service, Comparator.comparing(DataPointAccess::getDataPointId));

    }

    public static void updateDataSourcePermissions(User user, PermissionsService<Integer, DataSourceVO<?>> service) {
        List<Integer> permissionsFromUser = user.getDataSourcePermissions();
        update(user, permissionsFromUser, service, Integer::compareTo);
    }

    private static <T, R> void updatePermissions(User user, List<T> accessesFromUser,
                                                    PermissionsService<T, R> service,
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

    private static <T, R> void update(User user,
                                      List<T> permissionsFromUser,
                                      PermissionsService<T, R> service,
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
}
