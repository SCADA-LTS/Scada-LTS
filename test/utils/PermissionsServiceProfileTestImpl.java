package utils;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import org.scada_lts.permissions.service.PermissionsService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermissionsServiceProfileTestImpl<T> implements PermissionsService<T, UsersProfileVO> {

    private Map<Integer, List<T>> permissions;

    public PermissionsServiceProfileTestImpl(Map<Integer, List<T>> permissions) {
        this.permissions = permissions;
    }
    
    @Override
    public List<T> getPermissions(UsersProfileVO user) {
        permissions.putIfAbsent(user.getId(), new ArrayList<>());
        return permissions.get(user.getId());
    }

    @Override
    public void addOrUpdatePermissions(UsersProfileVO user, List<T> toAddOrUpdate) {
        permissions.putIfAbsent(user.getId(), new ArrayList<>());
        permissions.get(user.getId()).removeIf(a -> toAddOrUpdate.stream().anyMatch(b -> a.equals(b)));
        permissions.get(user.getId()).addAll(toAddOrUpdate);
    }

    @Override
    public void removePermissions(UsersProfileVO user, List<T> toRemove) {
        permissions.get(user.getId()).removeAll(toRemove);
    }

}
