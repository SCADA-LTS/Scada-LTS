package org.scada_lts.factory;

import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.*;

public class ScheduledEventDaoFactory {

    private final DatabaseAccess databaseAccess;

    public ScheduledEventDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IScheduledEventDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresScheduledEventDAO();
        else
            return new ScheduledEventDAO();
    }
}
