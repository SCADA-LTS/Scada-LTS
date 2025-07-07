package org.scada_lts.factory;

import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.event.CompoundEventDetectorDAO;
import org.scada_lts.dao.event.ICompoundEventDetectorDAO;
import org.scada_lts.dao.event.PostgresCompoundEventDetectorDAO;

public class CompoundEventDetectorDaoFactory {

    private final DatabaseAccess databaseAccess;

    public CompoundEventDetectorDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public ICompoundEventDetectorDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresCompoundEventDetectorDAO();
        else
            return new CompoundEventDetectorDAO();
    }
}
