package org.scada_lts.serorepl.db;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;

public class DaoUtils {

    protected DataSourceTransactionManager tm;
    protected DataSource dataSource;

    public DaoUtils(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected DataSourceTransactionManager getTransactionManager() {
        if (this.tm == null) {
            this.tm = new DataSourceTransactionManager(this.dataSource);
        }

        return this.tm;
    }

    protected TransactionTemplate getTransactionTemplate() {
        return new TransactionTemplate(this.getTransactionManager());
    }
}
