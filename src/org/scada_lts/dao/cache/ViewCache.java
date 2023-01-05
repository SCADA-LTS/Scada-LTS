package org.scada_lts.dao.cache;

import br.org.scadabr.vo.permission.ViewAccess;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import org.scada_lts.dao.IViewDAO;

import java.util.List;

public class ViewCache implements ViewCachable {

    private final IViewDAO viewDAO;

    public ViewCache(IViewDAO viewDAO) {
        this.viewDAO = viewDAO;
    }

    @Override
    public List<View> findAll() {
        return viewDAO.findAll();
    }

    @Override
    public View save(View view) {
        return viewDAO.save(view);
    }

    @Override
    public void update(View view) {
        viewDAO.update(view);
    }

    @Override
    public void delete(int id) {
        viewDAO.delete(id);
    }

    @Override
    public List<ShareUser> selectShareUsers(int viewId) {
        return viewDAO.selectShareUsers(viewId);
    }

    @Override
    public List<ShareUser> selectShareUsersFromProfile(int viewId) {
        return viewDAO.selectShareUsersFromProfile(viewId);
    }

    @Override
    public List<ViewAccess> selectViewPermissions(int userId) {
        return viewDAO.selectViewPermissions(userId);
    }

    @Override
    public int[] insertPermissions(int userId, List<ViewAccess> toAddOrUpdate) {
        return viewDAO.insertPermissions(userId, toAddOrUpdate);
    }

    @Override
    public int[] deletePermissions(int userId, List<ViewAccess> toRemove) {
        return viewDAO.deletePermissions(userId, toRemove);
    }

    @Override
    public View findById(int viewId) {
        return viewDAO.findById(viewId);
    }

    @Override
    public View findByXid(String viewXid) {
        return viewDAO.findByXid(viewXid);
    }

    @Override
    public void deleteViewForUser(int viewId) {}

    @Override
    public void deleteViewForUser(int viewId, int userId) {}
}
