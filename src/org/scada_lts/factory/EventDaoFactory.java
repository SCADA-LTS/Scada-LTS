package org.scada_lts.factory;


import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.event.PostgresEventDAO;
import org.scada_lts.dao.event.IEventDAO;

public class EventDaoFactory {

    private final DatabaseAccess databaseAccess;

    public EventDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IEventDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresEventDAO();
        else
            return new EventDAO();
    }
}
