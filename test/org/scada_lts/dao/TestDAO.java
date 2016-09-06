package org.scada_lts.dao;

import java.sql.*;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.springframework.jdbc.core.JdbcTemplate;


public class TestDAO {
	
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/";

	static final String USER = "root";
	static final String PASS = "root";
	
	private String database = "scadalts_test_";
	   
	@Before
    public void setUp() throws ClassNotFoundException, SQLException {
		
		java.util.Date date= new java.util.Date();
		System.out.println(new Timestamp(date.getTime()));
		 
		database = database+date.getTime();
		
		
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);  
			stmt = conn.createStatement();
			String sql = "CREATE DATABASE " + database;
			stmt.executeUpdate(sql);
		} finally {
		  if(stmt!=null)
		    stmt.close();
		  if(conn!=null)
		    conn.close();
		}
		
		try {
			DAO.getInstance();
		} catch (Exception e) {
			//nothing
		}
		
		BasicDataSource dataSource = new BasicDataSource();

		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		dataSource.setUrl("jdbc:mysql://localhost:3306/"+database);
		dataSource.setMaxActive(10);
		dataSource.setMaxIdle(5);
		dataSource.setInitialSize(5);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		DAO.getInstance().setJdbcTemp(jdbcTemplate);
		
	    System.out.println("0");
    }

	
	@After
	public void tearDown() {
		//drop database;
		System.out.println("1");
	}
	
	
	

}
