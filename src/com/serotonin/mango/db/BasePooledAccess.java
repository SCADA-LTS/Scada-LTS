/*
 * Mango - Open Source M2M - http://mango.serotoninsoftware.com Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
 * 
 * @author Matthew Lohbihler
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.mango.Common;

/**
 * @author Matthew Lohbihler
 */
abstract public class BasePooledAccess extends DatabaseAccess
{
    private final Log log = LogFactory.getLog(BasePooledAccess.class);
    protected DataSource dataSource;
    protected boolean dataSourceFound = false;

    public BasePooledAccess(ServletContext ctx)
    {
        super(ctx);
    }

    public BasePooledAccess(ServletContext ctx, String dbPrefix)
    {
        super(ctx, dbPrefix);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void initializeImpl(String propertyPrefix, String dataSourceName)
    {
        log.info("Initializing pooled connection manager");

        if(Common.getEnvironmentProfile().getString(propertyPrefix + getDbPrefix() + "datasource", "false").equals("true"))
        {
            try
            {
                log.info("Looking for Datasource: " + Common.getEnvironmentProfile().getString(propertyPrefix + getDbPrefix() + "datasourceName"));
                dataSource = (DataSource) new InitialContext().lookup(Common.getEnvironmentProfile().getString(propertyPrefix + getDbPrefix() + "datasourceName"));
                Connection conn = dataSource.getConnection();
                log.info("DataSource meta: " + conn.getMetaData().getDatabaseProductName() + " " + conn.getMetaData().getDatabaseProductVersion());
                dataSourceFound = true;
            }
            catch(NamingException e)
            {
                log.info("Datasource not found!" + e.getLocalizedMessage());
            }
            catch(SQLException e)
            {
                log.error("SQL Exception: " + e.getLocalizedMessage());
            }
        }
    }

    @Override
    protected void initializeImpl(String propertyPrefix)
    {
        log.info("Initializing pooled connection manager");
        dataSource = new BasicDataSource();
        ((BasicDataSource) dataSource).setDriverClassName(getDriverClassName());
        ((BasicDataSource) dataSource).setUrl(getUrl(propertyPrefix));
        ((BasicDataSource) dataSource).setUsername(Common.getEnvironmentProfile().getString(propertyPrefix + getDbPrefix() + "username"));
        ((BasicDataSource) dataSource).setPassword(getDatabasePassword(propertyPrefix));
        ((BasicDataSource) dataSource).setMaxActive(Common.getEnvironmentProfile().getInt(propertyPrefix + getDbPrefix() + "pool.maxActive", 10));
        ((BasicDataSource) dataSource).setMaxIdle(Common.getEnvironmentProfile().getInt(propertyPrefix + getDbPrefix() + "pool.maxIdle", 10));
    }

    protected String getUrl(String propertyPrefix)
    {
        return Common.getEnvironmentProfile().getString(propertyPrefix + getDbPrefix() + "url");
    }

    abstract protected String getDriverClassName();

    @Override
    public void runScript(String[] script, OutputStream out)
    {
        ExtendedJdbcTemplate ejt = new ExtendedJdbcTemplate();
        ejt.setDataSource(dataSource);

        StringBuilder statement = new StringBuilder();

        for(String line : script)
        {
            // Trim whitespace
            line = line.trim();

            // Skip comments
            if(line.startsWith("--"))
                continue;

            statement.append(line);
            statement.append(" ");
            if(line.endsWith(";"))
            {
                // Execute the statement
                ejt.execute(statement.toString());
                statement.delete(0, statement.length() - 1);
            }
        }
    }

    protected void createSchema(String scriptFile)
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(ctx.getResourceAsStream(scriptFile)));

        List<String> lines = new ArrayList<String>();
        try
        {
            String line;
            while((line = in.readLine()) != null)
                lines.add(line);

            String[] script = new String[lines.size()];
            lines.toArray(script);
            runScript(script, null);
        }
        catch(IOException ioe)
        {
            throw new ShouldNeverHappenException(ioe);
        }
        finally
        {
            try
            {
                in.close();
            }
            catch(IOException ioe)
            {
                log.warn("", ioe);
            }
        }
    }

    @Override
    public void terminate()
    {
        log.info("Stopping database");
        try
        {
            if(dataSourceFound)
                ((BasicDataSource) dataSource).close();
        }
        catch(SQLException e)
        {
            log.warn("", e);
        }
    }

    @Override
    public DataSource getDataSource()
    {
        return dataSource;
    }

    @Override
    public File getDataDirectory()
    {
        return null;
    }
}
