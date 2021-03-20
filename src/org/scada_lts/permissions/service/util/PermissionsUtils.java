package org.scada_lts.permissions.service.util;

import br.org.scadabr.vo.permission.ViewAccess;
import br.org.scadabr.vo.permission.WatchListAccess;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.DataPointAccess;
import org.scada_lts.permissions.service.PermissionsService;

import java.util.ArrayList;
import java.util.List;

public final class PermissionsUtils {

    private PermissionsUtils() {}

    public static void updateWatchListPermissions(User user, PermissionsService<WatchListAccess, WatchList> operation) {
        user.setWatchListPermissions(getActualPermissions(user, user.getWatchListPermissions(), operation));
    }

    public static void updateViewPermissions(User user, PermissionsService<ViewAccess, View> operation) {
        user.setViewPermissions(getActualPermissions(user, user.getViewPermissions(), operation));
    }

    public static void updateDataPointPermissions(User user, PermissionsService<DataPointAccess, DataPointVO> operation) {
        user.setDataPointPermissions(getActualPermissions(user, user.getDataPointPermissions(), operation));
    }

    public static void updateDataSourcePermissions(User user, PermissionsService<Integer, DataSourceVO> operation) {
        user.setDataSourcePermissions(getActualPermissions(user, user.getDataSourcePermissions(), operation));
    }

    private static <T, R> List<T> getActualPermissions(User user, List<T> accessesFromUser,
                                                    PermissionsService<T, R> operation) {
        List<T> accessesFromDatabase = operation.getPermissions(user);
        if(!accessesFromUser.equals(accessesFromDatabase)) {
            if(!accessesFromDatabase.isEmpty() && !accessesFromUser.containsAll(accessesFromDatabase)) {
                operation.removePermissions(user, diff(accessesFromUser, accessesFromDatabase));
                accessesFromDatabase = operation.getPermissions(user);
            }
            if((accessesFromDatabase.isEmpty() || !accessesFromDatabase.containsAll(accessesFromUser))
                    && !accessesFromUser.isEmpty()) {
                operation.addOrUpdatePermissions(user, diff(accessesFromDatabase, accessesFromUser));
                accessesFromDatabase = operation.getPermissions(user);
            }
        }
        return accessesFromDatabase;
    }

    private static <T> List<T> diff(List<T> in, List<T> base) {
        List<T> notExistIn = new ArrayList<>(base);
        notExistIn.removeAll(in);
        return notExistIn;
    }
}
