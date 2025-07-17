package org.scada_lts.factory;

import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.ViewHierarchyDAO;
import org.scada_lts.dao.PostgresViewHierarchyDAO;
import org.scada_lts.dao.IViewHierarchyDAO;

public class ViewHierarchyDaoFactory {

    private final DatabaseAccess databaseAccess;

    public ViewHierarchyDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IViewHierarchyDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresViewHierarchyDAO();
        else
            return new ViewHierarchyDAO();
    }
}
