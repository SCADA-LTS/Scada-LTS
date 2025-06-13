package org.scada_lts.factory;

import com.serotonin.mango.db.DatabaseAccess;
import org.scada_lts.dao.report.IReportDAO;
import org.scada_lts.dao.report.PostgresReportDAO;
import org.scada_lts.dao.report.ReportDAO;

public class ReportDaoFactory {

    private final DatabaseAccess databaseAccess;

    public ReportDaoFactory(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
    }

    public IReportDAO newInstance() {
        DatabaseAccess.DatabaseType databaseType = databaseAccess.getType();
        if (databaseType == DatabaseAccess.DatabaseType.POSTGRES)
            return new PostgresReportDAO();
        else
            return new ReportDAO();
    }
}
