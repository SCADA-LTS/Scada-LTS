package org.scada_lts.serorepl.db;

import com.serotonin.db.spring.GenericTransactionTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

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

    protected GenericTransactionTemplate getTransactionTemplate() {
        return new GenericTransactionTemplate(this.getTransactionManager());
    }
}
