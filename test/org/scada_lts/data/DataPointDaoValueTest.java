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
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.DataPointDAO;
import org.scada_lts.dao.model.point.PointValue;
import org.scada_lts.dao.pointvalues.PointValueDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.BinaryValue;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;
import com.serotonin.mango.rt.dataImage.types.MultistateValue;
import com.serotonin.mango.vo.DataPointVO;

/**
 * @version 0.2
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
	
	@Test
	public void numericValueTest(){
		
		PointValueDAO pointValuesDao = new PointValueDAO();
		
		PointValue testFloat 	= genPointValue(1, "2.3");
		PointValue testBoolean	= genPointValue(2, "True");
		PointValue testMultistate = genPointValue(3, "2");
		PointValue testString	= genPointValue(4, "Dam-is-closed");
		pointValuesDao.create(testFloat);
		pointValuesDao.create(testBoolean);
		pointValuesDao.create(testMultistate);
		pointValuesDao.create(testString);
		
		PointValue pv1 = pointValuesDao.findById(new Object[] {1});
		testFloat.setId(1);
		assertTrue(pv1.equals(testFloat));
		
		PointValue pv2 = pointValuesDao.findById(new Object[] {2});
		testBoolean.setId(2);
		assertTrue(pv2.equals(testBoolean));
		
		PointValue pv3 = pointValuesDao.findById(new Object[] {3});
		testMultistate.setId(3);
		assertTrue(pv3.equals(testMultistate));
		
		PointValue pv4 = pointValuesDao.findById(new Object[] {4});
		testString.setId(4);
		assertTrue(pv4.equals(testString));
			
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
	
	@SuppressWarnings("unused")
	private static DataPointVO genDataPoint(int dsID, String dsXid, int dsTypeId, String name){
		
		DataPointVO dataPoint = new DataPointVO();
		
		Random randomGenerator = new Random();
		Integer generated_xid = randomGenerator.nextInt(99000)+1;
		
		dataPoint.setXid("DP_"+generated_xid);
		dataPoint.setName(name);
		
		dataPoint.setDataSourceId(dsID);
		dataPoint.setDataSourceName("Name_"+generated_xid.toString());
		dataPoint.setDataSourceXid(dsXid);
		dataPoint.setDataSourceTypeId(dsTypeId);
		
		return dataPoint;
	}
	
	/**
	 * generate PointValue 
	 * @param type (int) - [1-5] Point data type
	 * 1 - Numeric data
	 * 2 - Binary data
	 * 3 - Multistate data
	 * 4 - Aplhanumeric data
	 * 5 - Image data
	 * @param value (String) - Insert correct value to specific type
	 * 
	 * @return pointValue
	 */
	
	private static PointValue genPointValue(int type, String value){
		
		MangoValue mangoValue = null;
		PointValue pointValue = new PointValue();
		switch (type){
			case 1: {
				mangoValue = new NumericValue(Float.parseFloat(value));
				PointValueTime pvt = new PointValueTime(mangoValue,0);
				pointValue.setDataPointId(type);
				pointValue.setPointValue(pvt);
				break;
			}
			case 2: {
				mangoValue = new BinaryValue(Boolean.parseBoolean(value));
				PointValueTime pvt = new PointValueTime(mangoValue,0);
				pointValue.setDataPointId(type);
				pointValue.setPointValue(pvt);
				break;
			}
			case 3: {
				mangoValue = new MultistateValue(3);
				PointValueTime pvt = new PointValueTime(mangoValue,0);
				pointValue.setDataPointId(type);
				pointValue.setPointValue(pvt);
				break;
			}
			case 4: {
				mangoValue = new AlphanumericValue(value);
				PointValueTime pvt = new PointValueTime(mangoValue,0);
				pointValue.setDataPointId(type);
				pointValue.setPointValue(pvt);
				break;
				
			}
			case 5: {
				mangoValue = new ImageValue(Integer.parseInt(value),1);
				PointValueTime pvt = new PointValueTime(mangoValue,0);
				pointValue.setDataPointId(type);
				pointValue.setPointValue(pvt);
				break;
				
			}
			default: {
				return null;
				
			}
		}
		
		return pointValue;
	}

}
