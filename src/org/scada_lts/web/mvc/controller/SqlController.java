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

import java.sql.ResultSetMetaData;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Collections;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.web.mvc.form.SqlForm;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.serotonin.mango.Common;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.util.SerializationHelper;
import org.springframework.web.util.WebUtils;

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

	@GetMapping
	public ModelAndView createForm(HttpServletRequest request)
			throws Exception {
		LOG.trace("/sql.shtm");
		
		Permissions.ensureAdmin(request);
		
		Map<String, Object> model = new HashMap<>();
		model.put("form", new SqlForm());
		return new ModelAndView("sql", model);
	}
	
	@PostMapping
    public ModelAndView executeSQL(HttpServletRequest request, HttpServletResponse response){
		LOG.trace("/sql.shtm");
		Permissions.ensureAdmin(request);
		
		final SqlForm form = new SqlForm(request.getParameter("sqlString"));
		executeCommand(form, request);

		Map<String, Object> model = new HashMap<>();
		model.put("form", form);
		return new ModelAndView("sql", model);
	}

    private static void executeCommand(final SqlForm form, final HttpServletRequest request) {
        DAO dao = DAO.getInstance();
        if(dao != null) {
            JdbcTemplate jdbcTemplate = dao.getJdbcTemp();
            if (jdbcTemplate != null) {
                execute(form, request, jdbcTemplate);
            } else {
                form.setError("JDBC not initialized.");
            }
        } else {
            form.setError("DAO not initialized.");
        }
    }

    private static void execute(final SqlForm form,
                                final HttpServletRequest request,
                                final JdbcTemplate jdbcTemplate) {
        if (WebUtils.hasSubmitParameter(request, "query")) {
            try {
                List<String> columnNames = columnNames(form.getSqlString(), jdbcTemplate);
                List<List<Object>> query = query(form.getSqlString(), jdbcTemplate);

                form.setHeaders(columnNames);
                form.setData(query);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                form.setError(ex.getMessage());
            }
        }
        else if (WebUtils.hasSubmitParameter(request, "update")) {
            try {
                int result = update(form.getSqlString(), jdbcTemplate);
                form.setUpdateResult(result);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                form.setError(ex.getMessage());
            }
        }
    }

    private static int update(final String sql, final JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.update(sql);
    }

    private static List<List<Object>> query(final String sql, final JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.query(sql, (rs, index) -> {
            ResultSetMetaData meta = rs.getMetaData();
            int columns = meta.getColumnCount();
            List<Object> row = new LinkedList<>();
            for (int i = 0; i < columns; i++) {
                if (meta.getColumnType(i + 1) == Types.CLOB)
                    row.add(rs.getString(i + 1));
                else if (meta.getColumnType(i + 1) == Types.LONGVARBINARY
                        || meta.getColumnType(i + 1) == Types.BLOB || meta.getColumnType(i + 1) == Types.BINARY) {
                    Object o;
                    if (Common.getEnvironmentProfile().getString("db.type").equals("postgres")) {
                        o = SerializationHelper.readObject(rs.getBinaryStream(i + 1));
                    } else {
                        o = SerializationHelper.readObject(rs.getBlob(i + 1).getBinaryStream());
                    }
                    row.add("Serialized data(" + o + ")");
                } else
                    row.add(rs.getObject(i + 1));
            }
            return row;
        });
    }

    private static List<String> columnNames(final String sql, final JdbcTemplate jdbcTemplate) {
        List<List<String>> data = getColumnNames(sql, jdbcTemplate);
        if(data.isEmpty())
            return Collections.emptyList();
        return data.get(0);
    }

    private static List<List<String>> getColumnNames(final String sql, final JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.query(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setMaxRows(1);
            return preparedStatement;
        }, (rs, index) -> {
            ResultSetMetaData meta = rs.getMetaData();
            int columns = meta.getColumnCount();
            List<String> headers = new ArrayList<>(columns);
            for (int i = 0; i < columns; i++)
                headers.add(meta.getColumnLabel(i + 1));
            return headers;
        });
    }
}
