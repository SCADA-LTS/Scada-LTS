package org.scada_lts.permissions.service;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import org.scada_lts.dao.IViewDAO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.List;

public class GetViewsWithAccess implements GetObjectsWithAccess<View, User> {

    private final IViewDAO viewDAO;

    public GetViewsWithAccess(IViewDAO viewDAO) {
        this.viewDAO = viewDAO;
    }

    @Override
    public List<View> getObjectsWithAccess(User user) {
        if(user.isAdmin())
            return viewDAO.selectViews();
        return viewDAO.selectViewWithAccess(user.getId(), user.getUserProfile());
    }

    @Override
    public List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User user) {
        if(user.isAdmin())
            return viewDAO.selectViewIdentifiers();
        return viewDAO.selectViewIdentifiersWithAccess(user.getId(), user.getUserProfile());
    }

    public static boolean hasViewReadPermission(User user, View view) {
        return user.isAdmin() || view.getUserId() == user.getId() || view.getUserAccess(user) >= ShareUser.ACCESS_READ;
    }

    public static boolean hasViewSetPermission(User user, View view) {
        return user.isAdmin() || view.getUserId() == user.getId() || view.getUserAccess(user) >= ShareUser.ACCESS_SET;
    }

    public static boolean hasViewOwnerPermission(User user, View view) {
        return user.isAdmin() || view.getUserId() == user.getId() || view.getUserAccess(user) >= ShareUser.ACCESS_OWNER;
    }
}
