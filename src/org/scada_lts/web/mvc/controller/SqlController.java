/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.scada_lts.web.mvc.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.web.mvc.form.SqlForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.serotonin.db.spring.ConnectionCallbackVoid;
import com.serotonin.db.spring.ExtendedJdbcTemplate;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.DatabaseAccess;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.util.SerializationHelper;

/**
 * Controller for SQL tab
 * Based on SqlController from Mango by Matthew Lohbihler
 * 
 * @author Marcin Gołda
 */
@Controller
@RequestMapping("/sql.shtm") 
public class SqlController {
	
	private static final Log LOG = LogFactory.getLog(SqlController.class);

	@RequestMapping(method = RequestMethod.GET)
	protected ModelAndView createForm(HttpServletRequest request)
			throws Exception {
		LOG.trace("/sql.shtm");
		
		Permissions.ensureAdmin(request);
		
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("form", new SqlForm());
		return new ModelAndView("sql", model);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	protected ModelAndView executeSQL(HttpServletRequest request, HttpServletResponse response){
		LOG.trace("/sql.shtm");
		Permissions.ensureAdmin(request);
		
		final SqlForm form = new SqlForm(request.getParameter("sqlString"));
		executeCommand(form, request);

		Map<String, Object> model = new HashMap<String, Object>();
		model.put("form", form);
		return new ModelAndView("sql", model);
	}
	
	private void executeCommand(final SqlForm form, HttpServletRequest request){
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
        	form.setError(e.getMessage());
            LOG.debug(e);
        }
	}
}
