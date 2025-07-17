package org.scada_lts.factory;

import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.report.*;

public class ReportInstancePointDaoFactory {

    private final DatabaseAccess databaseAccess;

    public ReportInstancePointDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IReportInstancePointDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresReportInstancePointDAO();
        else
            return new ReportInstancePointDAO();
    }
}
