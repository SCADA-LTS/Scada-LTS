package org.scada_lts.factory;

import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.mailingList.IMailingListDAO;
import org.scada_lts.dao.mailingList.MailingListDAO;
import org.scada_lts.dao.mailingList.PostgresMailingListDAO;

public class MailingListDaoFactory {

    private final DatabaseAccess databaseAccess;

    public MailingListDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IMailingListDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresMailingListDAO();
        else
            return new MailingListDAO();
    }
}
