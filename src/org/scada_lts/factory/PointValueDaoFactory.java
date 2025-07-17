package org.scada_lts.factory;

import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.pointvalues.IPointValueDAO;
import org.scada_lts.dao.pointvalues.PointValueDAO;
import org.scada_lts.dao.pointvalues.PostgresPointValueDAO;


public class PointValueDaoFactory {

    private final DatabaseAccess databaseAccess;

    public PointValueDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IPointValueDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresPointValueDAO();
        else
            return new PointValueDAO();
    }
}
