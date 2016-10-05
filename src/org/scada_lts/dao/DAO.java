/*
 * (c) 2015 Abil'I.T. http://abilit.eu/
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
package org.scada_lts.dao;

import java.util.ArrayList;
import java.util.Arrays;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.serotonin.mango.Common;


/** 
 * Data Abstract Object
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class DAO {

	private static final Log LOG = LogFactory.getLog(DAO.class);
	private NamedParameterJdbcTemplate namedParamJdbcTemplate;
	private JdbcTemplate jdbcTemplate;	
	private static DAO instance;
	
	private DAO() {
		try {
			LOG.trace("Create DAO");
			DataSource ds = Common.ctx.getDatabaseAccess().getDataSource();
			namedParamJdbcTemplate = new NamedParameterJdbcTemplate(ds);
			jdbcTemplate = new JdbcTemplate(ds);
		} catch (Exception e) {
			LOG.error(e);
		}
	}
	
	/**
	 * Get id.
	 * @return Method queryForObject() can also return "null"
	 */
	protected int getId() {
		return jdbcTemplate.queryForObject("select @@identity", Integer.class);
	}

	public static DAO getInstance() {
		if (instance == null) {
			instance = new DAO();
		} 
		return instance;
	}
	
	/**
	 * Method utility needed because of mango compatibility.
	 * @param s
	 * @return
	 */
	public static boolean charToBool(String s) {
		return "Y".equals(s);
	}
	
	/**
	 *  
	 * @param b
	 * @return
	 */
	public static String boolToChar(boolean b) {
		return b ? "Y" : "N";
	}

	
	/**
	 * Return get jdbcTemplate
	 * @return
	 */
	public JdbcTemplate getJdbcTemp() {
		return jdbcTemplate;
	}
	
	/**
	 * Set jdbcTemplate
	 * @see TestDAO
	 * @param jdbcTemplate
	 */
	public void setJdbcTemp(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	/**
	 * Return NamedParameterJdbcTemplate
	 * @return
	 */
	public NamedParameterJdbcTemplate getNamedParameterJdbcTemp() {
		return namedParamJdbcTemplate;
	}
	
	/**
	 * From example https://www.mkyong.com/java/java-append-values-into-an-object-array/ 
	 * @param obj
	 * @param newObj
	 * @return
	 */
	public Object[] appendValue(Object[] obj, Object newObj) {

		ArrayList<Object> temp = new ArrayList<Object>(Arrays.asList(obj));
		temp.add(newObj);
		return temp.toArray();

	}

	public static String generateUniqueXid(String xidPrefix, String string) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static boolean isXidUnique(String xid, int excludeId, String tableName) {
		return DAO.getInstance().getJdbcTemp().queryForObject("select count(*) from " + tableName
				+ " where xid=? and id<>?", new Object[] { xid, excludeId }, Integer.class) == 0;
	}
	
}
