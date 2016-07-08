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

import java.sql.SQLException;

import javax.servlet.ServletContext;

import org.springframework.dao.DataAccessException;

import com.serotonin.db.spring.ExtendedJdbcTemplate;

public class MSSQLAccess extends BasePooledAccess {
    public MSSQLAccess(ServletContext ctx) {
        super(ctx);
    }

    @Override
    public DatabaseType getType() {
        return DatabaseType.MSSQL;
    }

    @Override
    protected String getDriverClassName() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    @Override
    protected boolean newDatabaseCheck(ExtendedJdbcTemplate ejt) {
        try {
            ejt.execute("select count(*) from users");
        }
        catch (DataAccessException e) {
            if (e.getCause() instanceof SQLException) {
                SQLException se = (SQLException) e.getCause();
                if ("S0002".equals(se.getSQLState())) {
                    // This state means a missing table. Assume that the schema needs to be created.
                    createSchema("/WEB-INF/db/createTables-mssql.sql");
                    return true;
                }
            }
            throw e;
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
