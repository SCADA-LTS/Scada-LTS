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
package org.scada_lts.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.SQLException;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.DataPointDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import com.serotonin.mango.vo.DataPointVO;

/**
 * @version 0.1
 * @author 	Radosław Jajko
 *
 */

public class DataPointDaoValueTest {
	
	private static DataPointDAO dataPointDAO = null;
	private static TestDAO testDao = new TestDAO();
	private static int bound	=	15;	//Create form 1 to BOUND elements;
	
	@BeforeClass
	public static void setUp() throws ClassNotFoundException, SQLException {
		
		//Initialize all database
		testDao.initialize();
		System.out.println("-- Database initialized succesfull!");
		
		//Insert DataSource object before insert DataPoint object
		DAO.getInstance().getJdbcTemp()
		.update("INSERT INTO datasources (xid, name, dataSourceType, data) values ('x1', 'dataName', 1, 0);");
		
		//Prepare 10 random DataPoints for "x1 - dataName - 1 - 0"	
		dataPointDAO = new DataPointDAO();
		for (int i = 1 ; i <= bound ; i++){
			
			//Create & Insert
			dataPointDAO.insert(genDataPoint(1, "x1", 1));
		}
	}
	
	@Test
	public void getExistingDataPoint(){
		
		assertNotNull(dataPointDAO.getDataPoint(1));
		for (int i = 1 ; i<=bound ; i++){
			assertEquals("Get DataPoint Id with ID = "+i,i, dataPointDAO.getDataPoint(i).getId());
		}
	}
	
	@Test
	public void getNotExistingDataPoint(){
		
		try{
			assertNull(dataPointDAO.getDataPoint(100));
		} catch( EmptyResultDataAccessException e){
			e.printStackTrace();
			Assert.fail("Catched Exception "+ e + " instead expected NULL value");
		}
		
	}
	
	
	

	@AfterClass
	public static void tearDown() throws ClassNotFoundException, SQLException {
		testDao.relax();
	}
	
	/**
	 *  generateRandomDataPoint based on dataSource
	 *  @param int 		dsID 	- DataSource ID no
	 *  @param String 	dsXid	- DataSource XID
	 *  @param int 		dsTypeId- DataSource TypeId (1 - virtual DataSource)
	 *  
	 *  @return dataPoint DataPointVO - random generated DataPoint
	 */

	private static DataPointVO genDataPoint(int dsID,String dsXid, int dsTypeId){
		
		DataPointVO dataPoint = new DataPointVO();
		
		Random randomGenerator = new Random();
		Integer generated_xid = randomGenerator.nextInt(99000)+1;
		
		dataPoint.setXid("DP_"+generated_xid);
		
		dataPoint.setDataSourceId(dsID);
		dataPoint.setDataSourceName("Name_"+generated_xid.toString());
		dataPoint.setDataSourceXid(dsXid);
		dataPoint.setDataSourceTypeId(dsTypeId);
		
		return dataPoint;
	}

}
