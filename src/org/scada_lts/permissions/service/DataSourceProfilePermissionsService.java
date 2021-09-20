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
    public List<Integer> getPermissions(UsersProfileVO object) {
        return usersProfileDAO.selectDataSourcePermissionsByProfileId(object.getId());
    }

    @Override
    public void addOrUpdatePermissions(UsersProfileVO object, List<Integer> toAddOrUpdate) {
        usersProfileDAO.insertDataSourceUsersProfile(object.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(UsersProfileVO object, List<Integer> toRemove) {
        usersProfileDAO.deleteDataSourceUsersProfile(object.getId(), toRemove);
    }
}
