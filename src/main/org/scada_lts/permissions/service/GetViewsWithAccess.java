package org.scada_lts.permissions.service;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.PermissionException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.IViewDAO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.Collections;
import java.util.List;

public class GetViewsWithAccess implements GetObjectsWithAccess<View, User> {

    private static final Log LOG = LogFactory.getLog(GetViewsWithAccess.class);

    private final IViewDAO viewDAO;

    public GetViewsWithAccess(IViewDAO viewDAO) {
        this.viewDAO = viewDAO;
    }

    @Override
    public List<View> getObjectsWithAccess(User user) {
        if(user == null) {
            LOG.warn("user is null");
            return Collections.emptyList();
        }
        if(user.isAdmin())
            return viewDAO.findAll();
        return viewDAO.selectViewWithAccess(user.getId(), user.getUserProfile());
    }

    @Override
    public List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User user) {
        if(user == null) {
            LOG.warn("user is null");
            return Collections.emptyList();
        }
        if(user.isAdmin())
            return viewDAO.findIdentifiers();
        return viewDAO.selectViewIdentifiersWithAccess(user.getId(), user.getUserProfile());
    }

    @Override
    public boolean hasReadPermission(User user, View object) {
        return GetViewsWithAccess.hasViewReadPermission(user, object);
    }

    @Override
    public boolean hasSetPermission(User user, View object) {
        return GetViewsWithAccess.hasViewSetPermission(user, object);
    }

    @Override
    public boolean hasOwnerPermission(User user, View object) {
        return GetViewsWithAccess.hasViewOwnerPermission(user, object);
    }

    public static boolean hasViewReadPermission(User user, View view) {
        if(user == null) {
            LOG.warn("user is null");
            return false;
        }
        if(view == null) {
            LOG.warn("view is null");
            return false;
        }
        return user.isAdmin() || view.getUserId() == user.getId() || view.getUserAccess(user) >= ShareUser.ACCESS_READ;
    }

    public static boolean hasViewSetPermission(User user, View view) {
        if(user == null) {
            LOG.warn("user is null");
            return false;
        }
        if(view == null) {
            LOG.warn("view is null");
            return false;
        }
        return user.isAdmin() || view.getUserId() == user.getId() || view.getUserAccess(user) >= ShareUser.ACCESS_SET;
    }

    public static boolean hasViewOwnerPermission(User user, View view) {
        if(user == null) {
            LOG.warn("user is null");
            return false;
        }
        if(view == null) {
            LOG.warn("view is null");
            return false;
        }
        return user.isAdmin() || view.getUserId() == user.getId() || view.getUserAccess(user) >= ShareUser.ACCESS_OWNER;
    }

    public static void ensureViewReadPermission(User user, View view) {
        if(!hasViewReadPermission(user, view)) {
            throw new PermissionException("User does not have permission to access the report", user);
        }
    }

    public static void ensureViewSetPermission(User user, View view) {
        if(!hasViewSetPermission(user, view)) {
            throw new PermissionException("User does not have permission to access the report", user);
        }
    }

    public static void ensureViewOwnerPermission(User user, View view) {
        if(!hasViewOwnerPermission(user, view)) {
            throw new PermissionException("User does not have permission to access the report", user);
        }
    }
}
