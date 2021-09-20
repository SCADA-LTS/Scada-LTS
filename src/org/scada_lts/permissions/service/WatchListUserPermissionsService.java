package org.scada_lts.permissions.service;

import br.org.scadabr.vo.permission.WatchListAccess;
import com.serotonin.mango.vo.User;
import org.scada_lts.dao.watchlist.WatchListDAO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WatchListUserPermissionsService implements PermissionsService<WatchListAccess, User> {

    private final WatchListDAO watchListDAO;

    public WatchListUserPermissionsService(WatchListDAO watchListDAO) {
        this.watchListDAO = watchListDAO;
    }

    @Override
    public List<WatchListAccess> getPermissions(User object) {
        return watchListDAO.selectWatchListPermissions(object.getId());
    }

    @Override
    public void addOrUpdatePermissions(User object, List<WatchListAccess> toAddOrUpdate) {
        watchListDAO.insertPermissions(object.getId(), toAddOrUpdate);
    }

    @Override
    public void removePermissions(User object, List<WatchListAccess> toRemove) {
        watchListDAO.deletePermissions(object.getId(), toRemove);
    }

}
