/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.db;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import net.bull.javamelody.internal.common.LOG;
import org.apache.commons.dbcp.BasicDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.dao.DataAccessException;

import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.mango.Common;

public class PostgreSQLAccess extends BasePooledAccess {
    @Override
    protected void initializeImpl(String propertyPrefix) {
        super.initializeImpl(propertyPrefix);
        ((BasicDataSource) dataSource).setInitialSize(3);
        ((BasicDataSource) dataSource).setMaxWait(-1);
        ((BasicDataSource) dataSource).setTestWhileIdle(true);
        ((BasicDataSource) dataSource).setTimeBetweenEvictionRunsMillis(10000);
        ((BasicDataSource) dataSource).setMinEvictableIdleTimeMillis(60000);
    }

    @Override
    protected void initializeImpl(String propertyPrefix, String dataSourceName)
    {
        super.initializeImpl(propertyPrefix, dataSourceName);
    }

    @Override
    protected String getUrl(String propertyPrefix) {
        String url = super.getUrl(propertyPrefix);
        if (url.indexOf('?') > 0)
            url += "&";
        else
            url += "?";
        url += "useUnicode=yes&characterEncoding=" + Common.UTF8;
        return url;
    }

    @Override
    public DatabaseAccess.DatabaseType getType() {
        return DatabaseAccess.DatabaseType.POSTGRES;
    }

    @Override
    protected String getDriverClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    protected boolean newDatabaseCheck(ExtendedJdbcTemplate ejt, ServletContext ctx) {

        boolean schemaExists = true;
        boolean baselineNotExist = false;

        try {
            ejt.execute("SELECT count(*) FROM users");
            LOG.info("schemaExists: " + schemaExists);
        } catch (DataAccessException e) {
            schemaExists = false;
            LOG.info("schemaExists: " + schemaExists);
        }

        try {
            ejt.execute("SELECT count(*) FROM schema_version");
            LOG.info("baselineNotExist: " + baselineNotExist);
        } catch (DataAccessException e) {
            baselineNotExist = true;
            LOG.info("baselineNotExist: " + baselineNotExist);
        }

        try {
            Flyway flyway = null;

            if (schemaExists) {
                if (baselineNotExist) {
                    flyway = Flyway.configure()
                            .baselineOnMigrate(true)
                            .dataSource(getDataSource())
                            .locations("org.scada_lts.dao.migration.postgresql")
                            .table("schema_version")
                            .load();

                    flyway.baseline();
                    flyway.migrate();
                }
            } else {
                if (baselineNotExist) {
                    flyway = Flyway.configure()
                            .baselineOnMigrate(true)
                            .dataSource(getDataSource())
                            .locations("org.scada_lts.dao.migration.postgresql")
                            .table("schema_version")
                            .load();

                    flyway.migrate();
                }
            }

            if (flyway == null) {
                flyway = Flyway.configure()
                        .dataSource(getDataSource())
                        .locations("org.scada_lts.dao.migration.postgresql")
                        .table("schema_version")
                        .load();
            }

            flyway.migrate();

        } catch (Exception e) {
            LOG.warn("Flyway migration failed", e);
        }

        return false;
    }

    @Override
    public double applyBounds(double value) {
        if (Double.isNaN(value))
            return 0;
        if (value == Double.POSITIVE_INFINITY)
            return Double.MAX_VALUE;
        if (value == Double.NEGATIVE_INFINITY)
            return -Double.MAX_VALUE;

        return value;
    }

    @Override
    public void executeCompress(ExtendedJdbcTemplate ejt) {
        // no op
    }

    @Override
    public PreparedStatement prepareStatement(Connection connection, String sql, String generatedKey) throws SQLException {
        if (!sql.toLowerCase().contains("returning")) {
            sql += " RETURNING " + generatedKey;
        }
        return connection.prepareStatement(sql);
    }
}
