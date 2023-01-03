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

package org.scada_lts.dao;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.scada_lts.dao.impl.DAO;
import org.scada_lts.dao.model.point.PointValue;
import org.scada_lts.dao.pointvalues.PointValueDAO;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.BinaryValue;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataImage.types.MultistateValue;
import com.serotonin.mango.rt.dataImage.types.NumericValue;


/** 
 * Test PointValueDAO
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * 
 */
public class PointValuesDaoTest extends TestDAO {
	
	private final long POINT_ID_1 = 1;
	private final long POINT_ID_2 = 2;
	private final long POINT_ID_3 = 3;
	private final long POINT_ID_4 = 4;
	private final long POINT_ID_5 = 5;
	
	@Test
	public void test() {
		
		//populate data
		// @formatter:off
		    // Adding datasource
			DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (`xid`,`name`,`dataSourceType`,`data`) VALUES ('DS_01','DS_TEST', 1,'');");
		
		// create point
			// NUMERIC 			dataPointId 1
			DAO.getInstance().getJdbcTemp().update("INSERT INTO datapoints (`xid`,`dataSourceId`,`data`) VALUES ('T_01',1,'')");
			// BINARY  			dataPointId 2
			DAO.getInstance().getJdbcTemp().update("INSERT INTO datapoints (`xid`,`dataSourceId`,`data`) VALUES ('T_02',1,'')");
			// MULTISTATE 		dataPointId 3
			DAO.getInstance().getJdbcTemp().update("INSERT INTO datapoints (`xid`,`dataSourceId`,`data`) VALUES ('T_03',1,'')");
			// ALPHANUMERIC		dataPointId 4
			DAO.getInstance().getJdbcTemp().update("INSERT INTO datapoints (`xid`,`dataSourceId`,`data`) VALUES ('T_04',1,'')");
			// IMAGE			dataPointId 5
			DAO.getInstance().getJdbcTemp().update("INSERT INTO datapoints (`xid`,`dataSourceId`,`data`) VALUES ('T_05',1,'')");
		
			
		// create values for point Numeric
		    // 1.0
			MangoValue valueNumeric1 = new NumericValue(15.01);
			PointValueTime pvtForNumeric1 = new PointValueTime(valueNumeric1, 0);
			PointValue pointValueNumeric1 = new PointValue();
			pointValueNumeric1.setDataPointId(POINT_ID_1);
			pointValueNumeric1.setPointValue(pvtForNumeric1);
		    // 2
			MangoValue valueNumeric2 = new NumericValue(2.01);
			PointValueTime pvtForNumeric2 = new PointValueTime(valueNumeric2, 1);
			PointValue pointValueNumeric2 = new PointValue();
			pointValueNumeric2.setDataPointId(POINT_ID_1);
			pointValueNumeric2.setPointValue(pvtForNumeric2);

		//create values for point BINARY
		    // true
			MangoValue valueBinary1= new BinaryValue(true);
			PointValueTime pvtForBinary1 = new PointValueTime(valueBinary1, 0);
			PointValue pointValueBinary1 = new PointValue();
			pointValueBinary1.setDataPointId(POINT_ID_2);
			pointValueBinary1.setPointValue(pvtForBinary1);
			// false
			MangoValue valueBinary2= new BinaryValue(false);
			PointValueTime pvtForBinary2 = new PointValueTime(valueBinary2, 1);
			PointValue pointValueBinary2 = new PointValue();
			pointValueBinary2.setDataPointId(POINT_ID_2);
			pointValueBinary2.setPointValue(pvtForBinary2);
		
		//create values for point MULTISTATE
		    // 1
			MangoValue valueMultistate1= new MultistateValue(1);
			PointValueTime pvtForMultistate1 = new PointValueTime(valueMultistate1, 0);
			PointValue pointValueMultistate1 = new PointValue();
			pointValueMultistate1.setDataPointId(POINT_ID_3);
			pointValueMultistate1.setPointValue(pvtForMultistate1);
			
		    // 2
			MangoValue valueMultistate2 = new MultistateValue(2);
			PointValueTime pvtForMultistate2 = new PointValueTime(valueMultistate2, 0);
			PointValue pointValueMultistate2 = new PointValue();
			pointValueMultistate2.setDataPointId(POINT_ID_3);
			pointValueMultistate2.setPointValue(pvtForMultistate2);

		//create values for point ALPHANUMERIC 150 char
		    // 3
			MangoValue valueAlphanumeric1 = new AlphanumericValue("aadfadfasdfadfadfasdfasdfasdfasdf12ddddddddddd133333333aadfadfasdfadfadfasdfasdfasdfasdf12ddddddddddd133333333aadfadfasdfadfadfasdfasdfasdfasdf12ddddddddddd133333333");
			PointValueTime pvtForAlphanumeric1 = new PointValueTime(valueAlphanumeric1, 0);
			PointValue pointValueAlphanumeric1 = new PointValue();
			pointValueAlphanumeric1.setDataPointId(POINT_ID_4);
			pointValueAlphanumeric1.setPointValue(pvtForAlphanumeric1);

		    // 4
			MangoValue valueAlphanumeric2 = new AlphanumericValue("12");
			PointValueTime pvtForAlphanumeric2 = new PointValueTime(valueAlphanumeric2, 0);
			PointValue pointValueAlphanumeric2 = new PointValue();
			pointValueAlphanumeric2.setDataPointId(POINT_ID_4);
			pointValueAlphanumeric2.setPointValue(pvtForAlphanumeric2);
		  //save data in pointValueAdnnotation
		
		//create values for point IMAGE
		    // 5
			MangoValue valueImage1 = new ImageValue(Integer.parseInt("1"),1);
			PointValueTime pvtForImage1 = new PointValueTime(valueImage1, 0);
			PointValue pointValueImage1 = new PointValue();
			pointValueImage1.setDataPointId(POINT_ID_5);
			pointValueImage1.setPointValue(pvtForImage1);
			
		    // 6
			MangoValue valueImage2 = new ImageValue(Integer.parseInt("2"),1);
			PointValueTime pvtForImage2 = new PointValueTime(valueImage2, 0);
			PointValue pointValueImage2 = new PointValue();
			pointValueImage2.setDataPointId(POINT_ID_5);
			pointValueImage2.setPointValue(pvtForImage2);
		 //save data in pointValueAdnnotation
			
		//
		// @formatter:on
		// end populate data
		
		//CR (Create Read)
		
		PointValueDAO pointValuesDAO = new PointValueDAO();
		
		//insert (values for every type point)
		  pointValuesDAO.create(pointValueNumeric1);
		  pointValuesDAO.create(pointValueNumeric2);
		  pointValuesDAO.create(pointValueBinary1);
		  pointValuesDAO.create(pointValueBinary2);
		  pointValuesDAO.create(pointValueMultistate1);
		  pointValuesDAO.create(pointValueMultistate2);
		  pointValuesDAO.create(pointValueAlphanumeric1);
		  pointValuesDAO.create(pointValueAlphanumeric2);
		  pointValuesDAO.create(pointValueImage1);
		  pointValuesDAO.create(pointValueImage2);
		  
		  //
		
		// read
		  //find (values for every type point)
		  PointValue pv1 = pointValuesDAO.findById(new Object[] {1});
		  pointValueNumeric1.setId(1);
		  boolean test1 = pv1.equals(pointValueNumeric1);
		  assertTrue(test1);
		  
		  PointValue pv2 = pointValuesDAO.findById(new Object[] {2});
		  pointValueNumeric2.setId(2);
		  boolean test2 = pv2.equals(pointValueNumeric2);
		  assertTrue(test2);
		  
		  PointValue pv3 = pointValuesDAO.findById(new Object[] {3});
		  pointValueBinary1.setId(3);
		  boolean test3 = pv3.equals(pointValueBinary1);
		  assertTrue(test3);
		  
		  PointValue pv4 = pointValuesDAO.findById(new Object[] {4});
		  pointValueBinary2.setId(4);
		  boolean test4 = pv4.equals(pointValueBinary2);
		  assertTrue(test4);
		  
		  PointValue pv5 = pointValuesDAO.findById(new Object[] {5});
		  pointValueMultistate1.setId(5);
		  boolean test5 = pv5.equals(pointValueMultistate1);
		  assertTrue(test5);
		  
		  PointValue pv6 = pointValuesDAO.findById(new Object[] {6});
		  pointValueMultistate2.setId(6);
		  boolean test6 = pv6.equals(pointValueMultistate2);
		  assertTrue(test6);
		  
		  PointValue pv7 = pointValuesDAO.findById(new Object[] {7});
		  pointValueAlphanumeric1.setId(7);
		  boolean test7 = pv7.equals(pointValueAlphanumeric1);
		  // Not true because we must use savePointValueImp from PointValueService to save pointValueAlphanumeric
		  assertTrue(test7==false);
		  
		  PointValue pv8 = pointValuesDAO.findById(new Object[] {8});
		  pointValueAlphanumeric2.setId(8);
		  boolean test8 = pv8.equals(pointValueAlphanumeric2);
		// Not true because we must use savePointValueImp from PointValueService to save pointValueAlphanumeric
		  assertTrue(test8==false);
		  
		  PointValue pv9 = pointValuesDAO.findById(new Object[] {9});
		  pointValueImage1.setId(9);
		  boolean test9 = pv9.equals(pointValueImage1);
		  // Not true because we must use savePointValueImp from PointValueService to save pointValueAlphanumeric
		  assertTrue(test9==false);
		  
		  PointValue pv10 = pointValuesDAO.findById(new Object[] {10});
		  pointValueImage2.setId(10);
		  boolean test10 = pv10.equals(pointValueImage2);
		// Not true because we must use savePointValueImp from PointValueService to save pointValueAlphanumeric
		  assertTrue(test10==false);
		  
		  //findAll (values for every type point)
		  List<PointValue> values = pointValuesDAO.findAll();
		  boolean testAllValues = (values.size() == 10);
		  assertTrue(testAllValues);
		  
		  //filtered (values for every type point)
		  List<PointValue> valuesFiltered = pointValuesDAO.filtered(
				  PointValueDAO.POINT_VALUE_FILTER_AT_TIME_STAMP_BASE_ON_DATA_POINT_ID, 
				  new Object[]{1,1}, 10);
		  boolean testFiltered = (valuesFiltered.size() == 1);
		  
		  assertTrue(testFiltered);
		  
		  //TODO every method with every filter defined test
		  
		  List<PointValue> lst = pointValuesDAO.filtered(
					PointValueDAO.POINT_VALUE_FILTER_LAST_BASE_ON_DATA_POINT_ID, 
					new Object[]{POINT_ID_1},1);
		   boolean testFiltered1 = (lst !=null && lst.size() >0 && (lst.get(0).getPointValue().getDoubleValue()==2.01));
		
		   assertTrue(testFiltered1);
		   
		   List<PointValue> lstFilterBinary = pointValuesDAO.filtered(
					PointValueDAO.POINT_VALUE_FILTER_LAST_BASE_ON_DATA_POINT_ID, 
					new Object[]{POINT_ID_2},1);
		   boolean testFiltered2 = (lstFilterBinary !=null && lstFilterBinary.size() >0 && (lstFilterBinary.get(0).getPointValue().getBooleanValue()==false));
		
		   assertTrue(testFiltered2);
		   
		   List<PointValue> lstFilterBinaryNull = pointValuesDAO.filtered(
					PointValueDAO.POINT_VALUE_FILTER_LAST_BASE_ON_DATA_POINT_ID, 
					new Object[]{POINT_ID_4},1);
		   boolean testFiltered3 = (lstFilterBinaryNull !=null && lstFilterBinaryNull.size() == 1 );
		
		   assertTrue(testFiltered3);
		   
		   // We must uniq pk on pointValues (id, dataPointId, dataType,ts) create index for: pointValue, ts, dataPointId, dataType
		   Long latestPointValue = pointValuesDAO.getLatestPointValue(5);
		   
		   boolean testLatestPointValue = (latestPointValue==0);
		   assertTrue(testLatestPointValue);


		//end CR
		// @formatter:on
		  
	}
}
