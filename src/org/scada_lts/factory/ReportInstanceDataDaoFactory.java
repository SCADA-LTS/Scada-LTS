package org.scada_lts.factory;

import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.report.*;

public class ReportInstanceDataDaoFactory {

    private final DatabaseAccess databaseAccess;

    public ReportInstanceDataDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IReportInstanceDataDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresReportInstanceDataDAO();
        else
            return new ReportInstanceDataDAO();
    }
}
