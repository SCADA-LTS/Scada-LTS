package org.scada_lts.factory;


import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.watchlist.IWatchListDAO;
import org.scada_lts.dao.watchlist.WatchListDAO;
import org.scada_lts.dao.watchlist.PostgresWatchListDAO;

public class WatchListDaoFactory {

    private final DatabaseAccess databaseAccess;

    public WatchListDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IWatchListDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresWatchListDAO();
        else
            return new WatchListDAO();
    }
}
