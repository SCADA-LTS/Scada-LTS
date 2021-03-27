package org.scada_lts.permissions.service;

import br.org.scadabr.vo.usersProfiles.UsersProfileVO;
import org.scada_lts.dao.DataSourceDAO;
import org.scada_lts.dao.UsersProfileDAO;

import java.util.List;

public class DataSourceProfilePermissionsService implements PermissionsService<Integer, UsersProfileVO> {

    private final DataSourceDAO dataSourceDAO;
    private final UsersProfileDAO usersProfileDAO;

    public DataSourceProfilePermissionsService() {
        this.usersProfileDAO = new UsersProfileDAO();
        this.dataSourceDAO = new DataSourceDAO();
    }

    public DataSourceProfilePermissionsService(UsersProfileDAO usersProfileDAO, DataSourceDAO dataPointUserDAO) {
        this.usersProfileDAO = usersProfileDAO;
        this.dataSourceDAO = dataPointUserDAO;
    }
    @Override
    public List<Integer> getPermissions(UsersProfileVO profile) {
        return dataSourceDAO.selectDataSourcePermissionsByProfileId(profile.getId());
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
