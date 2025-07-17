package org.scada_lts.factory;

import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.*;

public class SynopticPanelDaoFactory {

    private final DatabaseAccess databaseAccess;

    public SynopticPanelDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public ISynopticPanelDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresSynopticPanelDAO();
        else
            return new SynopticPanelDAO();
    }
}
