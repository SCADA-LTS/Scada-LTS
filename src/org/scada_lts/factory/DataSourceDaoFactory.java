package org.scada_lts.factory;

import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.DataSourceDAO;
import org.scada_lts.dao.PostgresDataSourceDAO;
import org.scada_lts.dao.IDataSourceDAO;

public class DataSourceDaoFactory {

    private final DatabaseAccess databaseAccess;

    public DataSourceDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IDataSourceDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresDataSourceDAO();
        else
            return new DataSourceDAO();
    }
}
