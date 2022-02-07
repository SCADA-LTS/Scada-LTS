package org.scada_lts.permissions.service;

import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.dao.watchlist.WatchListDAO;

import java.util.List;

public class GetWatchListsWithAccess implements GetObjectsWithAccess<WatchList, User> {

    private final WatchListDAO watchListDAO;

    public GetWatchListsWithAccess() {
        this.watchListDAO = new WatchListDAO();
    }

    public GetWatchListsWithAccess(WatchListDAO watchListDAO) {
        this.watchListDAO = watchListDAO;
    }

    @Override
    public List<WatchList> getObjectsWithAccess(User user) {
        return watchListDAO.selectWatchListsWithAccess(user.getId(), user.getUserProfile());
    }

    @Override
    public List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User user) {
        return watchListDAO.selectWatchListIdentifiersWithAccess(user.getId(), user.getUserProfile());
    }
}
