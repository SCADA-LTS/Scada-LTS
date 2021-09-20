package org.scada_lts.permissions.service;

import br.org.scadabr.vo.permission.WatchListAccess;
import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import org.scada_lts.dao.IUsersProfileDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WatchListProfilePermissionsService implements PermissionsService<WatchListAccess, UsersProfileVO>{

    private final IUsersProfileDAO usersProfileDAO;

    public WatchListProfilePermissionsService(IUsersProfileDAO usersProfileDAO) {
        this.usersProfileDAO = usersProfileDAO;
    }

    @Override
    public List<WatchListAccess> getPermissions(UsersProfileVO object) {
        return usersProfileDAO.selectWatchListPermissionsByProfileId(object.getId());
    }

    @Override
    public void addOrUpdatePermissions(UsersProfileVO object, List<WatchListAccess> toAddOrUpdate) {
        usersProfileDAO.insertWatchListUsersProfile(object.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(UsersProfileVO object, List<WatchListAccess> toRemove) {
        usersProfileDAO.deleteWatchListUsersProfile(object.getId(), toRemove);
    }
}
