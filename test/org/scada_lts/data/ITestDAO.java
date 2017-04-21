package org.scada_lts.data;

import java.sql.SQLException;

public interface ITestDAO {


	/* 
	 * This method set up custom database on a server
	 */
	
	public void initialize() throws ClassNotFoundException, SQLException;
	
	/*
	 *  This method drop table from a server
	 */
	public void relax() throws ClassNotFoundException, SQLException;

}
