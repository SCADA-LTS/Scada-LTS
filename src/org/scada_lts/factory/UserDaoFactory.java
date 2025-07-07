package org.scada_lts.factory;

import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.IUserDAO;
import org.scada_lts.dao.UserDAO;
import org.scada_lts.dao.PostgresUserDAO;

public class UserDaoFactory {

    private final DatabaseAccess databaseAccess;

    public UserDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IUserDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresUserDAO();
        else
            return new UserDAO();
    }
}
