package utils;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import com.serotonin.mango.vo.User;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.permissions.service.PermissionsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermissionsServiceTestImpl<T, R> implements PermissionsService<T, R> {

    private Map<Integer, List<T>> permissions;

    public PermissionsServiceTestImpl(Map<Integer, List<T>> permissions) {
        this.permissions = permissions;
    }

    @Override
    public List<R> getObjectsWithAccess(User user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> getPermissions(User user) {
        permissions.putIfAbsent(user.getId(), new ArrayList<>());
        return permissions.get(user.getId());
    }

    @Override
    public List<T> getPermissionsByProfile(UsersProfileVO profile) {
        throw new UnsupportedOperationException();
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

    @Override
    public List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User user) {
        throw new UnsupportedOperationException();
    }
}
