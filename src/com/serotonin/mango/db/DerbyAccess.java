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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.derby.jdbc.EmbeddedXADataSource40;
import org.apache.derby.tools.ij;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.db.spring.ConnectionCallbackVoid;
import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.db.spring.GenericRowMapper;
import com.serotonin.mango.Common;

public class DerbyAccess extends DatabaseAccess {
    private final Log log = LogFactory.getLog(DerbyAccess.class);

    private static final double LARGEST_POSITIVE = 1.79769E+308;
    private static final double SMALLEST_POSITIVE = 2.225E-307;
    private static final double LARGEST_NEGATIVE = -2.225E-307;
    private static final double SMALLEST_NEGATIVE = -1.79769E+308;

    private EmbeddedXADataSource40 dataSource;

    public DerbyAccess(ServletContext ctx) {
        super(ctx);
    }

    public DerbyAccess(ServletContext ctx, String dbPrefix) {
        super(ctx, dbPrefix);
    }

    @Override
    public DatabaseType getType() {
        return DatabaseType.DERBY;
    }

    @Override
    protected void initializeImpl(String propertyPrefix) {
        log.info("Initializing derby connection manager");
        dataSource = new EmbeddedXADataSource40();
        dataSource.setCreateDatabase("create");

        dataSource.setDatabaseName(getUrl(propertyPrefix));
        dataSource.setDataSourceName("mangoDataSource");

        // Creation of a connection will optionally create the database.
        Connection c = DataSourceUtils.getConnection(dataSource);
        DataSourceUtils.releaseConnection(c, dataSource);
    }

    protected void initializeImpl(String propertyPrefix, String dataSourceName)
    {

        log.info("Not implemented! Back to the original initImpl()");
        this.initializeImpl(propertyPrefix);
    }

    private String getUrl(String propertyPrefix)
    {
        String name = Common.getEnvironmentProfile().getString(propertyPrefix + "db.url", "~/../../mangoDB");
        if (name.startsWith("~"))
            name = ctx.getRealPath(name.substring(1));
        return name;
    }

    @Override
    public void terminate() {
        log.info("Stopping database");
        dataSource.setDatabaseName("");
        dataSource.setShutdownDatabase("shutdown");
        Connection conn = null;
        try {
            conn = DataSourceUtils.getConnection(dataSource);
        }
        catch (CannotGetJdbcConnectionException e) {
            SQLException se = (SQLException) e.getCause();
            if ("XJ015".equals(se.getSQLState())) {
                log.debug("Stopped database");
                // A SQL code indicating that the system was successfully shut down. We can ignore this.
            }
            else
                throw e;
        }
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    protected boolean newDatabaseCheck(ExtendedJdbcTemplate ejt) {
        int count = ejt.queryForInt("select count(1) from sys.systables where tablename='USERS'", 0);
        if (count == 0) {
            // The users table wasn't found, so assume that this is a new Mango instance.
            // Create the tables
            try {
                FileOutputStream out = new FileOutputStream("createTables.log");
                Connection conn = DataSourceUtils.getConnection(dataSource);
                org.apache.derby.tools.ij.runScript(conn,
                        ctx.getResourceAsStream("/WEB-INF/db/createTables-derby.sql"), "ASCII", out, Common.UTF8);
                DataSourceUtils.releaseConnection(conn, dataSource);
                out.flush();
                out.close();
            }
            catch (Exception e) {
                // Should never happen, so just wrap in a runtime exception and rethrow.
                throw new ShouldNeverHappenException(e);
            }

            return true;
        }

        return false;
    }

    @Override
    protected void postInitialize(ExtendedJdbcTemplate ejt) {
        updateIndentityStarts(ejt);
    }

    @Override
    public void runScript(String[] script, final OutputStream out) throws Exception {
        StringBuilder sb = new StringBuilder();
        for (String line : script)
            sb.append(line).append("\r\n");
        final InputStream in = new ByteArrayInputStream(sb.toString().getBytes("ASCII"));

        Common.ctx.getDatabaseAccess().doInConnection(new ConnectionCallbackVoid() {
            public void doInConnection(Connection conn) {
                try {
                    ij.runScript(conn, in, "ASCII", out, Common.UTF8);
                }
                catch (UnsupportedEncodingException e) {
                    throw new ShouldNeverHappenException(e);
                }
            }
        });
    }

    @Override
    public File getDataDirectory() {
        return new File(getUrl(""));
    }

    /**
     * This method updates the tables with identity autoincrement values that are the maximum of the current
     * autoincrement value or max(id)+1. This ensures that updates to tables that may have occurred are handled, and
     * prevents cases where inserts are attempted with identities that already exist.
     */
    private void updateIndentityStarts(ExtendedJdbcTemplate ejt) {
       /* List<IdentityStart> starts = ejt.query("select t.tablename, c.columnname, c.autoincrementvalue " + //
                "from sys.syscolumns c join sys.systables t on c.referenceid = t.tableid " + //
                "where t.tabletype='T' and c.autoincrementvalue is not null", new GenericRowMapper<IdentityStart>() {
            @Override
            public IdentityStart mapRow(ResultSet rs, int index) throws SQLException {
                IdentityStart is = new IdentityStart();
                is.table = rs.getString(1);
                is.column = rs.getString(2);
                is.aiValue = rs.getInt(3);
                return is;
            }
        });

        for (IdentityStart is : starts) {
            int maxId = ejt.queryForInt("select max(" + is.column + ") from " + is.table);
            if (is.aiValue <= maxId)
                ejt.execute("alter table " + is.table + " alter column " + is.column + " restart with " + (maxId + 1));
        }*/
    }

    class IdentityStart {
        String table;
        String column;
        int aiValue;
    }

    @Override
    public double applyBounds(double value) {
        if (value > 0) {
            if (value < SMALLEST_POSITIVE)
                value = SMALLEST_POSITIVE;
            else if (value > LARGEST_POSITIVE)
                value = LARGEST_POSITIVE;
        }
        else if (value < 0) {
            if (value < SMALLEST_NEGATIVE)
                value = SMALLEST_NEGATIVE;
            else if (value > LARGEST_NEGATIVE)
                value = LARGEST_NEGATIVE;
        }
        else if (Double.isNaN(value))
            value = 0;

        return value;
    }

    @Override
    public void executeCompress(ExtendedJdbcTemplate ejt) {
        compressTable(ejt, "pointValues");
        compressTable(ejt, "pointValueAnnotations");
        compressTable(ejt, "events");
        compressTable(ejt, "reportInstanceData");
        compressTable(ejt, "reportInstanceDataAnnotations");
        compressTable(ejt, "reportInstanceEvents");
        compressTable(ejt, "reportInstanceUserComments");
    }

    private void compressTable(ExtendedJdbcTemplate ejt, final String tableName) {
        ejt.call(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection conn) throws SQLException {
                CallableStatement cs = conn.prepareCall("call SYSCS_UTIL.SYSCS_COMPRESS_TABLE(?, ?, ?)");
                cs.setString(1, "APP");
                cs.setString(2, tableName.toUpperCase());
                cs.setShort(3, (short) 0);
                return cs;
            }
        }, Collections.EMPTY_LIST);
    }
}
