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
package com.serotonin.mango.web.dwr.beans;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.serotonin.web.i18n.I18NUtils;

/**
 * @author Matthew Lohbihler
 */
public class SqlStatementTester extends Thread implements TestingUtility {
    private static final int MAX_ROWS = 50;

    private final ResourceBundle bundle;
    private final String driverClassname;
    private final String connectionUrl;
    private final String username;
    private final String password;
    private final String selectStatement;
    private final boolean rowBasedQuery;

    private boolean done;
    private String errorMessage;
    private final List<List<String>> resultTable = new ArrayList<List<String>>();

    public SqlStatementTester(ResourceBundle bundle, String driverClassname, String connectionUrl, String username,
            String password, String selectStatement, boolean rowBasedQuery) {
        this.bundle = bundle;
        this.driverClassname = driverClassname;
        this.connectionUrl = connectionUrl;
        this.username = username;
        this.password = password;
        this.selectStatement = selectStatement;
        this.rowBasedQuery = rowBasedQuery;
        start();
    }

    @Override
    public void run() {
        Connection conn = null;
        try {
            DriverManager.registerDriver((Driver) Class.forName(driverClassname).newInstance());
            DriverManager.setLoginTimeout(5000);
            conn = DriverManager.getConnection(connectionUrl, username, password);
            PreparedStatement stmt = conn.prepareStatement(selectStatement);
            ResultSet rs = stmt.executeQuery();

            if (rowBasedQuery)
                getRowData(rs);
            else
                getColumnData(rs);

            rs.close();
        }
        catch (Exception e) {
            errorMessage = e.getClass() + ": " + e.getMessage();
        }
        finally {
            try {
                if (conn != null)
                    conn.close();
            }
            catch (SQLException e) {
                // no op
            }
        }
        done = true;
    }

    public boolean isDone() {
        return done;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<List<String>> getResultTable() {
        return resultTable;
    }

    public void cancel() {
        // no op
    }

    private void getRowData(ResultSet rs) throws SQLException {
        // Get the column info.
        ResultSetMetaData meta = rs.getMetaData();
        int columns = meta.getColumnCount();

        List<String> row = new ArrayList<String>();
        for (int i = 1; i <= columns; i++)
            row.add(meta.getColumnLabel(i) + " (" + meta.getColumnTypeName(i) + ")");
        resultTable.add(row);

        while (rs.next()) {
            row = new ArrayList<String>();

            for (int i = 1; i <= columns; i++)
                row.add(rs.getString(i));

            resultTable.add(row);
            if (resultTable.size() > MAX_ROWS)
                // Seriously, that ought to be enough
                break;
        }
    }

    private void getColumnData(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columns = meta.getColumnCount();

        boolean data = rs.next();

        // Add the headers.
        List<String> row = new ArrayList<String>();
        row.add(I18NUtils.getMessage(bundle, "dsEdit.sql.tester.columnName"));
        row.add(I18NUtils.getMessage(bundle, "dsEdit.sql.tester.columnType"));
        row.add(I18NUtils.getMessage(bundle, "dsEdit.sql.tester.value"));
        resultTable.add(row);

        for (int i = 1; i <= columns; i++) {
            row = new ArrayList<String>();
            row.add(meta.getColumnLabel(i));
            row.add(meta.getColumnTypeName(i));

            String value;
            if (data)
                value = rs.getString(i);
            else
                value = I18NUtils.getMessage(bundle, "common.noData");

            row.add(value);
            resultTable.add(row);
        }
    }
}
