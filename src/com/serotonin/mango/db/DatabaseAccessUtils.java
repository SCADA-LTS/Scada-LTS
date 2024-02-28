package com.serotonin.mango.db;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.SQLException;

public class DatabaseAccessUtils {

    private static final Log LOG = LogFactory.getLog(DatabaseAccessUtils.class);
    public String decrypt(String input) {
        return input;
    }

    public static void closeDataSource(DataSource dataSource) throws SQLException {
        if (dataSource instanceof BasicDataSource)
            ((BasicDataSource) dataSource).close();
        else
            dataSource.getConnection().close();
    }
}
