package utils;

import com.serotonin.mango.vo.User;
import org.scada_lts.permissions.service.PermissionsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermissionsServiceUserTestImpl<T> implements PermissionsService<T, User> {

    private Map<Integer, List<T>> permissions;

    public PermissionsServiceUserTestImpl(Map<Integer, List<T>> permissions) {
        this.permissions = permissions;
    }

    @Override
    public List<T> getPermissions(User user) {
        permissions.putIfAbsent(user.getId(), new ArrayList<>());
        return permissions.get(user.getId());
    }

    @Override
    public void addOrUpdatePermissions(User user, List<T> toAddOrUpdate) {
        permissions.putIfAbsent(user.getId(), new ArrayList<>());
        permissions.get(user.getId()).removeIf(a -> toAddOrUpdate.stream().anyMatch(b -> a.equals(b)));
        permissions.get(user.getId()).addAll(toAddOrUpdate);
    }

    @Override
    public void removePermissions(User user, List<T> toRemove) {
        permissions.get(user.getId()).removeAll(toRemove);
    }

}
