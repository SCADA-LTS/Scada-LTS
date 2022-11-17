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

import com.serotonin.mango.util.SqlDataSourceUtils;
import com.serotonin.mango.vo.dataSource.sql.SqlDataSourceVO;
import com.serotonin.web.i18n.I18NUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Matthew Lohbihler
 */
public class JdbcOperationsTester extends Thread implements TestingUtility {
    private static final int MAX_ROWS = 50;

    private final ResourceBundle bundle;
    private final SqlDataSourceVO vo;

    private boolean done;
    private String errorMessage;
    private List<List<String>> resultTable = new ArrayList<>();

    public JdbcOperationsTester(ResourceBundle bundle, SqlDataSourceVO vo) {
        this.bundle = bundle;
        this.vo = vo;
        start();
    }

    @Override
    public void run() {
        try {

            JdbcOperations jdbcOperations = SqlDataSourceUtils.createJdbcOperations(vo);

            if (vo.isRowBasedQuery()) {
                this.resultTable = jdbcOperations.query(vo.getSelectStatement(), new ResultSetExtractor<List<List<String>>>() {
                    @Override
                    public List<List<String>> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                        return getRowData(resultSet);
                    }
                });
            } else {
                this.resultTable = jdbcOperations.query(vo.getSelectStatement(), new ResultSetExtractor<List<List<String>>>() {
                    @Override
                    public List<List<String>> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                        return getColumnData(resultSet);
                    }
                });
            }
        }
        catch (Exception e) {
            errorMessage = e.getClass() + ": " + e.getMessage();
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

    private List<List<String>> getRowData(ResultSet rs) throws SQLException {
        // Get the column info.
        ResultSetMetaData meta = rs.getMetaData();
        int columns = meta.getColumnCount();
        List<List<String>> resultTable = new ArrayList<>();
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
        return resultTable;
    }

    private List<List<String>> getColumnData(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columns = meta.getColumnCount();

        boolean data = rs.next();

        // Add the headers.
        List<String> row = new ArrayList<String>();
        List<List<String>> resultTable = new ArrayList<>();
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
        return resultTable;
    }
}
