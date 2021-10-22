package org.scada_lts.permissions.service;


import br.org.scadabr.vo.permission.ViewAccess;
import com.serotonin.mango.vo.User;
import org.scada_lts.dao.ViewDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViewUserPermissionsService implements PermissionsService<ViewAccess, User> {

    private final ViewDAO viewDAO;

    public ViewUserPermissionsService(ViewDAO viewDAO) {
        this.viewDAO = viewDAO;
    }

    @Override
    public List<ViewAccess> getPermissions(User user) {
        return viewDAO.selectViewPermissions(user.getId());
    }

    @Override
    public void addOrUpdatePermissions(User user, List<ViewAccess> toAddOrUpdate) {
        viewDAO.insertPermissions(user.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(User user, List<ViewAccess> toRemove) {
        viewDAO.deletePermissions(user.getId(), toRemove);
    }
}
