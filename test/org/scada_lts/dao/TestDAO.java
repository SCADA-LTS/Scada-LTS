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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.scada_lts.dao.impl.DAO;
import org.springframework.jdbc.core.JdbcTemplate;

import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.web.i18n.LocalizableMessage;


/** 
 * Test TestDAO
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * 
 */
public class TestDAO {
	
	static final Log LOG = LogFactory.getLog(TestDAO.class);
	
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String URL = "jdbc:mysql://127.0.0.1";
	private static final String PORT = "3306";
	private static final String USER = "root";
	private static final String PASS = "root";
	private static final int MAX_ACTIVE = 10;
	private static final int MAX_IDLE = 5;
	private static final int INITIAL_SIZE = 5;
	
	private String database = "scadalts_test_";
	private Connection conn = null;
	private Statement stmt = null;
	
	@Rule
	public final ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setUp() {
		try {

			java.util.Date date = new java.util.Date();
			LOG.info("Set up database for test "+date.getTime());
			database = database + date.getTime();
			LOG.info("DATABASE NAME:"+database);
			
			
			LOG.info("Create database:"+database);
			try {
				Class.forName(JDBC_DRIVER);
				conn = DriverManager.getConnection(URL+":"+PORT, USER, PASS);
				stmt = conn.createStatement();
				String sql = "CREATE DATABASE " + database;
				stmt.executeUpdate(sql);
			} finally {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			}
	
			LOG.info("Create schema");
			BasicDataSource dataSource = new BasicDataSource();
	
			dataSource.setDriverClassName(JDBC_DRIVER);
			dataSource.setUsername("root");
			dataSource.setPassword("root");
			
			dataSource.setUrl(URL+":"+PORT+ "/" + database);
			dataSource.setMaxActive(MAX_ACTIVE);
			dataSource.setMaxIdle(MAX_IDLE);
			dataSource.setInitialSize(INITIAL_SIZE);
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			DAO.getInstance().setJdbcTemp(jdbcTemplate);
			DAO.getInstance().setTest(true);

			Flyway flyway = Flyway.configure()
					.baselineOnMigrate(true)
					.dataSource(DAO.getInstance().getJdbcTemp().getDataSource())
					.locations("org.scada_lts.dao.migration.mysql")
					.table("schema_version")
					.load();
	        flyway.migrate();
		} catch (ClassNotFoundException | SQLException | FlywayException  e){
			e.printStackTrace();
		}

	}

	@After
	public void tearDown() {
		// drop database;
		LOG.info("End test ");
	}
	
	// populate data
	PointEventDetectorVO createEventDetectorVO(int id) {
		PointEventDetectorVO ped = new PointEventDetectorVO();
		List<LocalizableMessage> lstLocalizableMessage = new ArrayList<LocalizableMessage>();
		LocalizableMessage localizableMessage = new LocalizableMessage("test");
		lstLocalizableMessage.add(localizableMessage);
		// TODO 
		//ped.addProperties(lstLocalizableMessage);
		
		ped.setAlarmLevel(id);
		ped.setAlias("aliasTest");
		ped.setAlphanumericState("alphanumericStateTest");
		ped.setBinaryState(true);
		ped.setChangeCount(2);
		ped.setDetectorType(2);
		ped.setDuration(2);
		ped.setDurationType(2);
		ped.setId(1);
		ped.setLimit(23423.0);
		ped.setMultistateState(2);
		ped.setWeight(24.0);
		ped.setXid("XID_0"+id);
		
		return ped;
	}

}
