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
package com.serotonin.mango.web.mvc.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractFormController;
import org.springframework.web.util.WebUtils;

import com.serotonin.db.spring.ConnectionCallbackVoid;
import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.DatabaseAccess;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.web.mvc.form.SqlForm;
import com.serotonin.util.SerializationHelper;

public class SqlController extends AbstractFormController {
    private static final Log LOG = LogFactory.getLog(SqlController.class);
    private String formView;

    public void setFormView(String formView) {
        this.formView = formView;
    }

    @Override
    protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response, BindException errors)
            throws Exception {
        Permissions.ensureAdmin(request);
        return showForm(request, errors, formView);
    }

    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest request, HttpServletResponse response,
            Object command, BindException errors) throws Exception {
        Permissions.ensureAdmin(request);

        final SqlForm form = (SqlForm) command;
        DatabaseAccess databaseAccess = Common.ctx.getDatabaseAccess();
        try {
            if (WebUtils.hasSubmitParameter(request, "query")) {
                databaseAccess.doInConnection(new ConnectionCallbackVoid() {
                    public void doInConnection(Connection conn) throws SQLException {
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(form.getSqlString());

                        ResultSetMetaData meta = rs.getMetaData();
                        int columns = meta.getColumnCount();
                        List<String> headers = new ArrayList<String>(columns);
                        for (int i = 0; i < columns; i++)
                            headers.add(meta.getColumnLabel(i + 1));

                        List<List<Object>> data = new LinkedList<List<Object>>();
                        List<Object> row;
                        while (rs.next()) {
                            row = new ArrayList<Object>(columns);
                            data.add(row);
                            for (int i = 0; i < columns; i++) {
                                if (meta.getColumnType(i + 1) == Types.CLOB)
                                    row.add(rs.getString(i + 1));
                                else if (meta.getColumnType(i + 1) == Types.LONGVARBINARY
                                        || meta.getColumnType(i + 1) == Types.BLOB || meta.getColumnType(i + 1) == Types.BINARY) {
                                    Object o;
                                    if (Common.getEnvironmentProfile().getString("db.type").equals("postgres")){
                                        o = SerializationHelper.readObject(rs.getBinaryStream(i + 1));
                                    }
                                    else{
                                        o = SerializationHelper.readObject(rs.getBlob(i + 1).getBinaryStream());
                                    }
                                    row.add("Serialized data(" + o + ")");
                                }
                                else
                                    row.add(rs.getObject(i + 1));
                            }
                        }

                        form.setHeaders(headers);
                        form.setData(data);
                    }
                });
            }
            else if (WebUtils.hasSubmitParameter(request, "update")) {
                ExtendedJdbcTemplate ejt = new ExtendedJdbcTemplate();
                ejt.setDataSource(databaseAccess.getDataSource());
                int result = ejt.update(form.getSqlString());
                form.setUpdateResult(result);
            }
        }
        catch (RuntimeException e) {
            errors.rejectValue("sqlString", "", e.getMessage());
            LOG.debug(e);
        }

        return showForm(request, response, errors);
    }
}
