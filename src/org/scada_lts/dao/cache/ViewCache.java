package org.scada_lts.dao.cache;

import br.org.scadabr.vo.permission.ViewAccess;
import com.serotonin.mango.util.LoggingUtils;
import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.view.View;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.ViewDAO;
import org.scada_lts.dao.model.BaseObjectIdentifier;
import org.scada_lts.dao.model.ScadaObjectIdentifier;

import java.util.List;

public class ViewCache implements ViewCacheable {

    private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(ViewCache.class);
    private final ViewDAO viewDAO;

    public ViewCache(ViewDAO viewDAO) {
        this.viewDAO = viewDAO;
    }

    @Override
    public List<BaseObjectIdentifier> findIdentifiers() {
        LOG.info("no cache: findIdentifiers");
        return viewDAO.findBaseIdentifiers();
    }

    @Override
    public View save(View view) {
        LOG.info("no cache: " + LoggingUtils.viewInfo(view));
        return viewDAO.save(view);
    }

    @Override
    public void update(View view) {
        LOG.info("no cache: " + LoggingUtils.viewInfo(view));
        viewDAO.update(view);
    }

    @Override
    public void delete(int viewId) {
        LOG.info("no cache: viewId: " + viewId);
        viewDAO.delete(viewId);
    }

    @Override
    public List<ShareUser> selectShareUsers(int viewId) {
        LOG.info("no cache: viewId: " + viewId);
        return viewDAO.selectShareUsers(viewId);
    }

    @Override
    public List<ShareUser> selectShareUsersFromProfile(int viewId) {
        LOG.info("no cache: viewId: " + viewId);
        return viewDAO.selectShareUsersFromProfile(viewId);
    }

    @Override
    public List<ViewAccess> selectViewPermissions(int userId) {
        LOG.info("no cache: userId: " + userId);
        return viewDAO.selectViewPermissions(userId);
    }

    @Override
    public int[] insertPermissions(int userId, List<ViewAccess> toAddOrUpdate) {
        LOG.info("no cache: userId: " + userId + ", toAddOrUpdate: " + toAddOrUpdate);
        return viewDAO.insertPermissions(userId, toAddOrUpdate);
    }

    @Override
    public int[] deletePermissions(int userId, List<ViewAccess> toRemove) {
        LOG.info("no cache: userId: " + userId + ", toAddOrUpdate: " + toRemove);
        return viewDAO.deletePermissions(userId, toRemove);
    }

    @Override
    public View findById(int viewId) {
        LOG.info("no cache: viewId: " + viewId);
        return viewDAO.findById(viewId);
    }

    @Override
    public void deleteViewForUser(int viewId, int userId) {
        LOG.info("no cache: viewId: " + viewId + ", userId: " + userId);
    }
}
