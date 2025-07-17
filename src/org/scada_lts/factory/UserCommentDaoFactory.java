package org.scada_lts.factory;


import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.UserCommentDAO;
import org.scada_lts.dao.PostgresUserCommentDAO;
import org.scada_lts.dao.IUserCommentDAO;

public class UserCommentDaoFactory {

    private final DatabaseAccess databaseAccess;

    public UserCommentDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IUserCommentDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresUserCommentDAO();
        else
            return new UserCommentDAO();
    }
}
