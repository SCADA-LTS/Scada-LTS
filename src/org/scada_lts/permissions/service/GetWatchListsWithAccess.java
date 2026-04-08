package org.scada_lts.permissions.service;

import com.serotonin.mango.view.ShareUser;
import com.serotonin.mango.vo.GetExtendedNameComparator;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.scada_lts.dao.watchlist.IWatchListDAO;
import org.scada_lts.dao.watchlist.WatchListDAO;
import org.scada_lts.web.beans.ApplicationBeans;

import java.util.Collections;
import java.util.List;

import static org.scada_lts.permissions.service.util.GetSortUtils.getAndSort;

public class GetWatchListsWithAccess implements GetObjectsWithAccess<WatchList, User> {

    private static final Log LOG = LogFactory.getLog(GetWatchListsWithAccess.class);

    private final IWatchListDAO watchListDAO;

    public GetWatchListsWithAccess() {
        this.watchListDAO = ApplicationBeans.getBean("watchListDAO", IWatchListDAO.class);
    }

    public GetWatchListsWithAccess(IWatchListDAO watchListDAO) {
        this.watchListDAO = watchListDAO;
    }

    @Override
    public List<WatchList> getObjectsWithAccess(User user) {
        if(user == null) {
            LOG.warn("user is null");
            return Collections.emptyList();
        }
        return getAndSort(user, watchListDAO::findAll,
                watchListDAO::selectWatchListsWithAccess,
                GetExtendedNameComparator.instance);
    }

    @Override
    public List<ScadaObjectIdentifier> getObjectIdentifiersWithAccess(User user) {
        if(user == null) {
            LOG.warn("user is null");
            return Collections.emptyList();
        }
        return getAndSort(user, watchListDAO::findIdentifiers,
                watchListDAO::selectWatchListIdentifiersWithAccess,
                GetExtendedNameComparator.instance);
    }

    @Override
    public boolean hasReadPermission(User user, WatchList object) {
        return GetWatchListsWithAccess.hasWatchListReadPermission(user, object);
    }

    @Override
    public boolean hasSetPermission(User user, WatchList object) {
        return GetWatchListsWithAccess.hasWatchListSetPermission(user, object);
    }

    @Override
    public boolean hasOwnerPermission(User user, WatchList object) {
        return GetWatchListsWithAccess.hasWatchListOwnerPermission(user, object);
    }

    public static boolean hasWatchListReadPermission(User user, WatchList watchList) {
        if(user == null) {
            LOG.warn("user is null");
            return false;
        }
        if(watchList == null) {
            LOG.warn("watchList is null");
            return false;
        }
        return user.isAdmin() || watchList.getUserId() == user.getId() || watchList.getUserAccess(user) >= ShareUser.ACCESS_READ;
    }

    public static boolean hasWatchListSetPermission(User user, WatchList watchList) {
        if(user == null) {
            LOG.warn("user is null");
            return false;
        }
        if(watchList == null) {
            LOG.warn("watchList is null");
            return false;
        }
        return user.isAdmin() || watchList.getUserId() == user.getId() || watchList.getUserAccess(user) >= ShareUser.ACCESS_SET;
    }

    public static boolean hasWatchListOwnerPermission(User user, WatchList watchList) {
        if(user == null) {
            LOG.warn("user is null");
            return false;
        }
        if(watchList == null) {
            LOG.warn("watchList is null");
            return false;
        }
        return user.isAdmin() || watchList.getUserId() == user.getId() || watchList.getUserAccess(user) >= ShareUser.ACCESS_OWNER;
    }
}
