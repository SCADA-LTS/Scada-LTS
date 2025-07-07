package org.scada_lts.factory;

import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.report.IReportInstanceDAO;
import org.scada_lts.dao.report.PostgresReportInstanceDAO;
import org.scada_lts.dao.report.ReportInstanceDAO;

public class ReportInstanceDaoFactory {

    private final DatabaseAccess databaseAccess;

    public ReportInstanceDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IReportInstanceDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresReportInstanceDAO();
        else
            return new ReportInstanceDAO();
    }
}
