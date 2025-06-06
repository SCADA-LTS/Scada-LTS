package org.scada_lts.factory;

import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.IPointEventDetectorDAO;
import org.scada_lts.dao.PointEventDetectorDAO;
import org.scada_lts.dao.PostgresPointEventDetectorDAO;

public class PointEventDetectorDaoFactory {

    private final DatabaseAccess databaseAccess;

    public PointEventDetectorDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IPointEventDetectorDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresPointEventDetectorDAO();
        else
            return new PointEventDetectorDAO();
    }
}
