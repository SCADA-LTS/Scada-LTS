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
    public List<View> selectViews() {
        return viewDAO.selectViews();
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
    public int insertView(View view) {
        return viewDAO.insertView(view);
    }

    @Override
    public void updateView(View view) {
        viewDAO.updateView(view);
    }

    @Override
    public void deleteView(View view) {
        viewDAO.deleteView(view);
    }

    @Override
    public void deleteViewForUser(int viewId) {}

    @Override
    public void deleteViewForUser(int viewId, int userId) {}
}
