package org.scada_lts.factory;

import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.IUsersProfileDAO;
import org.scada_lts.dao.PostgresUsersProfileDAO;
import org.scada_lts.dao.UsersProfileDAO;


public class UsersProfileDaoFactory {

    private final DatabaseAccess databaseAccess;

    private UsersProfileDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IUsersProfileDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresUsersProfileDAO();
        else
            return new UsersProfileDAO();
    }
}
