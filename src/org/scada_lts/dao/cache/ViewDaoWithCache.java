package org.scada_lts.dao.cache;

import br.org.scadabr.vo.permission.ViewAccess;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import com.serotonin.mango.vo.User;
import org.scada_lts.dao.IViewDAO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.permissions.service.GetViewsWithAccess;

import java.util.List;
import java.util.stream.Collectors;

public class ViewDaoWithCache implements IViewDAO {

    private final ViewCachable viewCache;

    public ViewDaoWithCache(ViewCachable viewCache) {
        this.viewCache = viewCache;
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
        return viewCache.findAll();
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
    public View findById(Integer id) {
        return findAll().stream().filter(a -> a.getId() == id).findAny().orElse(null);
    }

    @Override
    public View findByName(String name) {
        return findAll().stream().filter(a -> a.getName().equals(name)).findAny().orElse(null);
    }

    @Override
    public View findByXid(String xid) {
        return findAll().stream().filter(a -> a.getXid().equals(xid)).findAny().orElse(null);
    }

    @Override
    public List<ScadaObjectIdentifier> findIdentifiers() {
        return findAll().stream()
                .map(a -> new ScadaObjectIdentifier(a.getId(), a.getXid(), a.getName()))
                .collect(Collectors.toList());
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
    @Deprecated
    public void deleteViewForUser(int viewId) {}

    @Override
    public void deleteViewForUser(int viewId, int userId) {
        viewCache.deleteViewForUser(viewId, userId);
    }

    @Override
    public View findByXid(String xid, boolean forceFromDatabase) {
        if(forceFromDatabase)
            viewCache.resetAll();
        return findByXid(xid);
    }

    @Override
    public View findById(int id, boolean forceFromDatabase) {
        if(forceFromDatabase)
            viewCache.reset(id);
        return findById(id);
    }
}
