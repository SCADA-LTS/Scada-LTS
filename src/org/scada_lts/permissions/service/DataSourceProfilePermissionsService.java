package org.scada_lts.permissions.service;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import org.scada_lts.dao.IUsersProfileDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataSourceProfilePermissionsService implements PermissionsService<Integer, UsersProfileVO> {

    private final IUsersProfileDAO usersProfileDAO;

    public DataSourceProfilePermissionsService(IUsersProfileDAO usersProfileDAO) {
        this.usersProfileDAO = usersProfileDAO;
    }

    @Override
    public List<Integer> getPermissions(UsersProfileVO profile) {
        return usersProfileDAO.selectDataSourcePermissionsByProfileId(profile.getId());
    }

    @Override
    public void addOrUpdatePermissions(UsersProfileVO profile, List<Integer> toAddOrUpdate) {
        usersProfileDAO.insertDataSourceUsersProfile(profile.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(UsersProfileVO profile, List<Integer> toRemove) {
        usersProfileDAO.deleteDataSourceUsersProfile(profile.getId(), toRemove);
    }
}
