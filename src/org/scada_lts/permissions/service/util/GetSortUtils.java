package org.scada_lts.permissions.service.util;

import com.serotonin.mango.vo.GetExtendedName;
import com.serotonin.mango.vo.User;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class GetSortUtils {

    private GetSortUtils() {}

    public static <T extends GetExtendedName> List<T> getAndSort(User user, Supplier<List<T>> getObjectForAdmin,
                                                                 BiFunction<Integer, Integer, List<T>> getObjectForNonAdmin,
                                                                 Comparator<GetExtendedName> comparator) {
        List<T> result;
        if(user.isAdmin()) {
            result = getObjectForAdmin.get();
        } else {
            result = getObjectForNonAdmin.apply(user.getId(), user.getUserProfile());
        }
        result.sort(comparator);
        return result;
    }

    public static <T extends GetExtendedName> List<T> getAndSortByUser(User user, Supplier<List<T>> getObjectForAdmin,
                                                                 Function<User, List<T>> getObjectForNonAdmin,
                                                                 Comparator<GetExtendedName> comparator) {
        List<T> result;
        if(user.isAdmin()) {
            result = getObjectForAdmin.get();
        } else {
            result = getObjectForNonAdmin.apply(user);
        }
        result.sort(comparator);
        return result;
    }
}
