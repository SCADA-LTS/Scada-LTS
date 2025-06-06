package org.scada_lts.factory;

import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.DataPointDAO;
import org.scada_lts.dao.PostgresDataPointDAO;
import org.scada_lts.dao.IDataPointDAO;

public class DataPointDaoFactory {

    private final DatabaseAccess databaseAccess;

    public DataPointDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IDataPointDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresDataPointDAO();
        else
            return new DataPointDAO();
    }
}