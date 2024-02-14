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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.apache.commons.dbcp.BasicDataSource;
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
        try {
            //ejt.execute("select count(*) from users");
            Class.forName(getDriverClassName());
            Connection conn = DriverManager.getConnection(Common.getEnvironmentProfile().getString("db.url.public"),
                    Common.getEnvironmentProfile().getString("db.username"),
                    Common.getEnvironmentProfile().getString("db.password"));
            ResultSet res = conn.createStatement().executeQuery("SELECT 1 from pg_database WHERE datname='scadabr'");
            if (res.next()){
                //se possui registros então é porque a base de dados existe
                conn.close();
                return false;
            }
            else{
                //como a base de dados não existe então ela deverá ser criada
                conn.createStatement().executeUpdate("CREATE DATABASE scadabr WITH OWNER = postgres ENCODING = 'UTF8'");
                conn.close();
                //uma vez com o banco de dados criado procederemos com a criação das tabelas
                conn = DriverManager.getConnection(Common.getEnvironmentProfile().getString("db.url"),
                    Common.getEnvironmentProfile().getString("db.username"),
                    Common.getEnvironmentProfile().getString("db.password"));
                createSchema("/WEB-INF/db/createTables-postgresql.sql", ctx);
                conn.close();
                return true;                
            }
        } catch (SQLException ex) {
            //Logger.getLogger(PostgreSQLAccess.class.getName()).log(Level.SEVERE, null, ex);          
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(PostgreSQLAccess.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (DataAccessException e) {
//            if (e.getCause() instanceof SQLException) {
//                SQLException se = (SQLException) e.getCause();
//                if ("42S02".equals(se.getSQLState())) {
//                    // This state means a missing table. Assume that the schema needs to be created.
//                    createSchema("/WEB-INF/db/createTables-postgresql.sql");
//                    return true;
//                }
//            }
//            throw e;
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
}
