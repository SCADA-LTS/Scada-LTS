package org.scada_lts.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.JdbcTemplate;


public class TestDAO implements ITestDAO{

	static final Log LOG = LogFactory.getLog(TestDAO.class);
	
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String URL = "jdbc:mysql://localhost";
	private static final String PORT = "3306";
	private static final String USER = "root";
	private static final String PASS = "root";
	private static final int MAX_ACTIVE = 10;
	private static final int MAX_IDLE = 5;
	private static final int INITIAL_SIZE = 5;
	
	private String database = "scadalts_test_";
	private Connection conn = null;
	private Statement stmt = null;
	
	@Override
	public void initialize() throws ClassNotFoundException, SQLException {
		
		java.util.Date data = new java.util.Date();
		LOG.info("Set up database for test: "+data.getTime());
		database = database + data.getTime();
		LOG.info("DATABASE name: "+database);
		
		LOG.info("Create database:" + database);
		
		try{
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(URL+":"+PORT,USER,PASS);
			stmt = conn.createStatement();
			String sql = "CREATE DATABASE " + database;
			stmt.executeUpdate(sql);
		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
		
		LOG.info("database created");
		LOG.info("Create schema: ");
		
		BasicDataSource dataSource = new BasicDataSource();
		
		dataSource.setDriverClassName(JDBC_DRIVER);
		dataSource.setUsername(USER);
		dataSource.setPassword(PASS);
		
		dataSource.setUrl(URL + ":" + PORT + "/" + database);
		dataSource.setMaxActive(MAX_ACTIVE);
		dataSource.setMaxIdle(MAX_IDLE);
		dataSource.setInitialSize(INITIAL_SIZE);
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		DAO.getInstance().setJdbcTemp(jdbcTemplate);
		
		
		ScriptRunner runner = new ScriptRunner(dataSource.getConnection(),false,false);
		
		InputStream in = getClass().getResourceAsStream("createTables-mysql.sql");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		try{
			LOG.info("Run script: createTables-mysql.sql");
			runner.runScript(reader);
		} catch (IOException e){
			e.printStackTrace();
		}
			
	}

	@Override
	public void relax() throws ClassNotFoundException, SQLException{
		
		LOG.info("Dropping database: ");
		try{
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(URL + ":" + PORT, USER, PASS);
			stmt = conn.createStatement();
			String sql = "DROP DATABASE "+ database;
			stmt.executeUpdate(sql);
		} finally {
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}
	

}
