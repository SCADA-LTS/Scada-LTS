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
import org.scada_lts.dao.model.point.PointValue;
import org.scada_lts.dao.model.point.PointValueAdnnotation;
import org.scada_lts.dao.pointvalues.PointValueAdnnotationsDAO;
import org.scada_lts.dao.pointvalues.PointValueDAO;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;

/** 
 * Test PointValueDAO
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * 
 */
public class PointValueAdnnotationDaoTest extends TestDAO{
	
	private final long POINT_ID_1 = 1;
	private final long POINT_ID_2 = 2;
	
	@Test
	public void test() {
		
		//populate data
		// @formatter:off
		    // Adding datasource
			DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (`xid`,`name`,`dataSourceType`,`data`) VALUES ('DS_01','DS_TEST', 1,'');");
		
		// create point
			// ALPHANUMERIC		dataPointId 1
			DAO.getInstance().getJdbcTemp().update("INSERT INTO datapoints (`xid`,`dataSourceId`,`data`) VALUES ('T_04',1,'')");
			// IMAGE			dataPointId 2
			DAO.getInstance().getJdbcTemp().update("INSERT INTO datapoints (`xid`,`dataSourceId`,`data`) VALUES ('T_05',1,'')");
			
		//create values for point ALPHANUMERIC 150 char
		    // aadfadfasdfadfadfasdfasdfasdfasdf12ddddddddddd133333333aadfadfasdfadfadfasdfasdfasdfasdf12ddddddddddd133333333aadfadfasdfadfadfasdfasdfasdfasdf12ddddddddddd133333333
			MangoValue valueAlphanumeric1 = new AlphanumericValue("aadfadfasdfadfadfasdfasdfasdfasdf12ddddddddddd133333333aadfadfasdfadfadfasdfasdfasdfasdf12ddddddddddd133333333aadfadfasdfadfadfasdfasdfasdfasdf12ddddddddddd133333333");
			PointValueTime pvtForAlphanumeric1 = new PointValueTime(valueAlphanumeric1, 0);
			PointValue pointValueAlphanumeric1 = new PointValue();
			pointValueAlphanumeric1.setDataPointId(POINT_ID_1);
			pointValueAlphanumeric1.setPointValue(pvtForAlphanumeric1);

		    // 12abc
			MangoValue valueAlphanumeric2 = new AlphanumericValue("12abc");
			PointValueTime pvtForAlphanumeric2 = new PointValueTime(valueAlphanumeric2, 0);
			PointValue pointValueAlphanumeric2 = new PointValue();
			pointValueAlphanumeric2.setDataPointId(POINT_ID_1);
			pointValueAlphanumeric2.setPointValue(pvtForAlphanumeric2);
		  //save data in pointValueAdnnotation
		
		//create values for point IMAGE
		    // ?
			MangoValue valueImage1 = new ImageValue(Integer.parseInt("1"),1);
			PointValueTime pvtForImage1 = new PointValueTime(valueImage1, 0);
			PointValue pointValueImage1 = new PointValue();
			pointValueImage1.setDataPointId(POINT_ID_2);
			pointValueImage1.setPointValue(pvtForImage1);
			
		    // ?
			MangoValue valueImage2 = new ImageValue(Integer.parseInt("2"),1);
			PointValueTime pvtForImage2 = new PointValueTime(valueImage2, 0);
			PointValue pointValueImage2 = new PointValue();
			pointValueImage2.setDataPointId(POINT_ID_2);
			pointValueImage2.setPointValue(pvtForImage2);
		 //save data in pointValueAdnnotation
		
			//insert (values for every type point)
			PointValueDAO pointValuesDAO = new PointValueDAO();
			
			pointValuesDAO.create(pointValueAlphanumeric1);
			pointValuesDAO.create(pointValueAlphanumeric2);
			pointValuesDAO.create(pointValueImage1);
			pointValuesDAO.create(pointValueImage2);
			
			//insert (adnnotations for every value)
			//alphanumeric
			PointValueAdnnotation pointValueAdnnotationAlphanumericLongText = new PointValueAdnnotation();
			pointValueAdnnotationAlphanumericLongText.setPointValueId(1);
			pointValueAdnnotationAlphanumericLongText.setTextPointValueLong(pointValueAlphanumeric1.getPointValue().getStringValue());
			
			PointValueAdnnotation pointValueAdnnotationAlphanumericShortText = new PointValueAdnnotation();
			pointValueAdnnotationAlphanumericShortText.setPointValueId(2);
			pointValueAdnnotationAlphanumericShortText.setTextPointValueShort(pointValueAlphanumeric2.getPointValue().getStringValue());
			
			//image
			PointValueAdnnotation pointValueAdnnotationImage1= new PointValueAdnnotation();
			pointValueAdnnotationImage1.setPointValueId(3);
			pointValueAdnnotationImage1.setTextPointValueShort(pointValueAlphanumeric2.getPointValue().getStringValue());
			
			PointValueAdnnotation pointValueAdnnotationImage2= new PointValueAdnnotation();
			pointValueAdnnotationImage2.setPointValueId(4);
			pointValueAdnnotationImage2.setTextPointValueShort(pointValueAlphanumeric2.getPointValue().getStringValue());
						
			// changed value by USER;
			// sourceType = 1
			//TODO
			// changed value by EVENT_HANDLER
			// soruceType = 2;
			//TODO
			// changed value by ANONYMOUS
			// sourceType = 3
			//TODO
			//changed value by POINT_LINK
			// sourceType = 4
			
			
		//
		// @formatter:on
		// end populate data
		
			PointValueAdnnotationsDAO pointValuesAdnnotationsDAO = new PointValueAdnnotationsDAO();
		//CR
		  
			// create
			pointValuesAdnnotationsDAO.create(pointValueAdnnotationAlphanumericLongText);
			pointValuesAdnnotationsDAO.create(pointValueAdnnotationAlphanumericShortText);
			pointValuesAdnnotationsDAO.create(pointValueAdnnotationImage1);
			pointValuesAdnnotationsDAO.create(pointValueAdnnotationImage2);
			  
		  //
		
		// read
		  // TODO because valueAdnnotations don't have pk error when adnnotation more then one
		  //find (adnnotations for every pointValues in test)
		  PointValueAdnnotation pointValueAdnnotationNewLongText = pointValuesAdnnotationsDAO.findByIdPointValueAdnnotation(new Object[] {1});
		  boolean test1 = pointValueAdnnotationNewLongText.equals(pointValueAdnnotationAlphanumericLongText);
		  assertTrue(test1);
		  
		  PointValueAdnnotation pointValueAdnnotationNewShortText = pointValuesAdnnotationsDAO.findByIdPointValueAdnnotation(new Object[] {2});
		  boolean test2 = pointValueAdnnotationNewShortText.equals(pointValueAdnnotationAlphanumericShortText);
		  assertTrue(test2);
		  
		  PointValueAdnnotation pointValueAdnnotationNewImage1 = pointValuesAdnnotationsDAO.findByIdPointValueAdnnotation(new Object[] {3});
		  boolean test3 = pointValueAdnnotationNewImage1.equals(pointValueAdnnotationImage1);
		  assertTrue(test3);
		  
		  PointValueAdnnotation pointValueAdnnotationNewImage2 = pointValuesAdnnotationsDAO.findByIdPointValueAdnnotation(new Object[] {4});
		  boolean test4 = pointValueAdnnotationNewImage2.equals(pointValueAdnnotationImage2);
		  assertTrue(test4);
				  
		  //filtered (values for every type point)
		  List<PointValueAdnnotation> valuesAdnnotationsFiltered = pointValuesAdnnotationsDAO.filteredPointValueAdnnotations(
				  PointValueAdnnotationsDAO.POINT_VALUE_ADNNOTATIONS_FILTER_BASE_ON_POINT_VALUES_ID,
				  new Object[]{1}, 10);
		  boolean testFiltered = (valuesAdnnotationsFiltered.size() == 1);
		  
		  assertTrue(testFiltered);

		//end CR
		  
	}

}
