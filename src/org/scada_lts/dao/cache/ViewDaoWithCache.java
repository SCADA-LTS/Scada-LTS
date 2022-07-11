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

    public ViewDaoWithCache(ViewCachable viewDAO) {
        this.viewCache = viewDAO;
    }

    @Override
    public int insertView(View view) {
        return viewCache.insertView(view);
    }

    @Override
    public void updateView(View view) {
        viewCache.updateView(view);
    }

    @Override
    public void deleteView(View view) {
        viewCache.deleteView(view);
    }

    @Override
    public List<View> selectViews() {
        return viewCache.selectViews();
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
    public View selectView(int id) {
        return selectViews().stream().filter(a -> a.getId() == id).findAny().orElse(null);
    }

    @Override
    public View selectViewByName(String name) {
        return selectViews().stream().filter(a -> a.getName().equals(name)).findAny().orElse(null);
    }

    @Override
    public View selectViewByXid(String xid) {
        return selectViews().stream().filter(a -> a.getXid().equals(xid)).findAny().orElse(null);
    }

    @Override
    public List<ScadaObjectIdentifier> selectViewIdentifiers() {
        return selectViews().stream()
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
        return selectViews().stream()
                .filter(view -> GetViewsWithAccess.hasViewReadPermission(User.onlyIdAndProfile(userId, profileId), view))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteViewForUser(int viewId) {}

    @Override
    public void deleteViewForUser(int viewId, int userId) {}
}
