package org.scada_lts.dao.cache;

import br.org.scadabr.vo.permission.ViewAccess;
import com.serotonin.mango.Common;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import org.scada_lts.dao.IViewDAO;
import org.scada_lts.dao.ViewDAO;
import org.scada_lts.dao.model.BaseObjectIdentifier;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.permissions.service.GetViewsWithAccess;
import org.scada_lts.permissions.service.ViewGetShareUsers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ViewDaoWithCache implements IViewDAO {

    private final ViewCacheable viewCache;

    public ViewDaoWithCache(ViewCacheable viewCache) {
        this.viewCache = viewCache;
    }

    @Override
    public void init() {
        List<View> views = new ViewDAO().findAll();
        for(View view: views) {
            applyShareUsers(view);
            viewCache.put(view);
        }
    }

    @Override
    public View save(View view) {
        return viewCache.save(view);
    }

    @Override
    public void update(View view) {
        viewCache.update(view);
    }

    @Override
    public void delete(Integer id) {
        viewCache.delete(id);
    }

    @Override
    public List<View> findAll() {
        List<BaseObjectIdentifier> identifiers = viewCache.findIdentifiers();
        List<View> views = new ArrayList<>();
        for(BaseObjectIdentifier identifier: identifiers) {
            View view = viewCache.findById(identifier.getId());
            if(view != null) {
                applyShareUsers(view);
                views.add(view);
            }
        }
        return views;
    }

    @Override
    public List<ViewAccess> selectViewPermissions(int userId) {
        return viewCache.selectViewPermissions(userId);
    }

    @Override
    public int[] insertPermissions(int userId, List<ViewAccess> toAddOrUpdate) {
        return viewCache.insertPermissions(userId, toAddOrUpdate);
    }

    @Override
    public int[] deletePermissions(int userId, List<ViewAccess> toRemove) {
        return viewCache.deletePermissions(userId, toRemove);
    }

    @Override
    public List<ShareUser> selectShareUsers(int viewId) {
        return viewCache.selectShareUsers(viewId);
    }

    @Override
    public List<ShareUser> selectShareUsersFromProfile(int viewId) {
        return viewCache.selectShareUsersFromProfile(viewId);
    }

    @Override
    public View findById(Integer viewId) {
        if(viewId == null || viewId == Common.NEW_ID || viewId == 0) {
            return null;
        }
        return viewCache.findById(viewId);
    }

    @Override
    public View findByName(String name) {
        if(name == null) {
            return null;
        }
        return findAll().stream()
                .filter(a -> a.getName() != null)
                .filter(a -> a.getName().equals(name))
                .findAny().orElse(null);
    }

    @Override
    public View findByXid(String xid) {
        if(xid == null) {
            return null;
        }
        return findAll().stream()
                .filter(a -> a.getXid() != null)
                .filter(a -> a.getXid().equals(xid))
                .findAny().orElse(null);
    }

    @Override
    public List<ScadaObjectIdentifier> findIdentifiers() {
        return viewCache.findIdentifiers().stream().map(a -> {
            View view = viewCache.findById(a.getId());
            ScadaObjectIdentifier scadaObjectIdentifier = new ScadaObjectIdentifier(a.getId(), a.getXid(), view.getName());
            return scadaObjectIdentifier;
        }).collect(Collectors.toList());
    }

    @Override
    public List<BaseObjectIdentifier> findBaseIdentifiers() {
        return viewCache.findIdentifiers();
    }

    @Override
    public List<ScadaObjectIdentifier> selectViewIdentifiersWithAccess(int userId, int profileId) {
        return selectViewWithAccess(userId, profileId)
                .stream()
                .map(a -> new ScadaObjectIdentifier(a.getId(), a.getXid(), a.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<View> selectViewWithAccess(int userId, int profileId) {
        return findAll().stream()
                .filter(view -> GetViewsWithAccess.hasViewReadPermission(User.onlyIdAndProfile(userId, profileId), view))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteViewForUser(int viewId, int userId) {
        viewCache.deleteViewForUser(viewId, userId);
    }

    private void applyShareUsers(View view) {
        ViewGetShareUsers viewGetShareUsers = new ViewGetShareUsers(this);
        view.setViewUsers(viewGetShareUsers.getShareUsersWithProfile(view));
    }
}
