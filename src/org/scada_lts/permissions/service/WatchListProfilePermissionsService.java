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
    public List<WatchListAccess> getPermissions(UsersProfileVO profile) {
        return usersProfileDAO.selectWatchListPermissionsByProfileId(profile.getId());
    }

    @Override
    public void addOrUpdatePermissions(UsersProfileVO profile, List<WatchListAccess> toAddOrUpdate) {
        usersProfileDAO.insertWatchListUsersProfile(profile.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(UsersProfileVO profile, List<WatchListAccess> toRemove) {
        usersProfileDAO.deleteWatchListUsersProfile(profile.getId(), toRemove);
    }
}
