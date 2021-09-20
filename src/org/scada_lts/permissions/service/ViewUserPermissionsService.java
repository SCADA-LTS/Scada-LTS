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
    public List<ViewAccess> getPermissions(User object) {
        return viewDAO.selectViewPermissions(object.getId());
    }

    @Override
    public void addOrUpdatePermissions(User object, List<ViewAccess> toAddOrUpdate) {
        viewDAO.insertPermissions(object.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(User object, List<ViewAccess> toRemove) {
        viewDAO.deletePermissions(object.getId(), toRemove);
    }
}
