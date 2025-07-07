package org.scada_lts.factory;

import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.*;

public class MaintenanceEventDaoFactory {

    private final DatabaseAccess databaseAccess;

    public MaintenanceEventDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IMaintenanceEventDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresMaintenanceEventDAO();
        else
            return new MaintenanceEventDAO();
    }
}
