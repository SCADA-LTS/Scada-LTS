package org.scada_lts.factory;

import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.*;

public class PointLinkDaoFactory {

    private final DatabaseAccess databaseAccess;

    public PointLinkDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IPointLinkDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresPointLinkDAO();
        else
            return new PointLinkDAO();
    }
}
