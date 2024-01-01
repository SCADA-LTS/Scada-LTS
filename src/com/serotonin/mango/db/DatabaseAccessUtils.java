package com.serotonin.mango.db;

import java.sql.Driver;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

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

    public static void unregisterDriver() {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if(driver != null) {
                try {
                    DriverManager.deregisterDriver(driver);
                    LOG.info(String.format("Unregistered jdbc driver: %s", driver.getClass().getName()));
                } catch (SQLException e) {
                    LOG.info(String.format("Error unregister jdbc driver: %s, msg: %s", driver.getClass().getName(), e.getMessage()));
                }
            }
        }
    }
}
