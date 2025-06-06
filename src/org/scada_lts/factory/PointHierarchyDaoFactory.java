package org.scada_lts.factory;


import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.pointhierarchy.IPointHierarchyDAO;
import org.scada_lts.dao.pointhierarchy.PointHierarchyDAO;
import org.scada_lts.dao.pointhierarchy.PostgresPointHierarchyDAO;

public class PointHierarchyDaoFactory {

    private final DatabaseAccess databaseAccess;

    public PointHierarchyDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IPointHierarchyDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresPointHierarchyDAO();
        else
            return new PointHierarchyDAO();
    }
}
