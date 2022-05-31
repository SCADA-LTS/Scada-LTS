package org.scada_lts.permissions.service;

import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import org.scada_lts.dao.ViewDAO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.List;

public class GetViewsWithAccess implements GetObjectsWithAccess<View, User> {

    private final ViewDAO viewDAO;

    public GetViewsWithAccess() {
        this.viewDAO = new ViewDAO();
    }

    public GetViewsWithAccess(ViewDAO viewDAO) {
        this.viewDAO = viewDAO;
    }

    @Override
    public List<View> getObjectsWithAccess(User user) {
        if(user.isAdmin())
            return viewDAO.findAll();
        return viewDAO.selectViewWithAccess(user.getId(), user.getUserProfile());
    }

    @Override
    public List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User user) {
        if(user.isAdmin())
            return viewDAO.selectViewIdentifiers();
        return viewDAO.selectViewIdentifiersWithAccess(user.getId(), user.getUserProfile());
    }
}
