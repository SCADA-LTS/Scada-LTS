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

import org.junit.Test;
import org.scada_lts.dao.impl.DAO;
import org.scada_lts.mango.adapter.MangoPointValues;
import org.scada_lts.mango.service.PointValueService;

import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.AlphanumericValue;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.vo.AnonymousUser;

/** 
 * Test PointValueService
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * 
 */
public class PointValueServiceTest extends TestDAO {
	
	private final int POINT_ID_1 = 1;
	
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
			//create values for point ALPHANUMERIC 150 char
		    // 3
			MangoValue valueAlphanumeric1 = new AlphanumericValue("aadfadfasdfadfadfasdfasdfasdfasdf12ddddddddddd133333333aadfadfasdfadfadfasdfasdfasdfasdf12ddddddddddd133333333aadfadfasdfadfadfasdfasdfasdfasdf12ddddddddddd133333333");
			PointValueTime pvtForAlphanumeric1 = new PointValueTime(valueAlphanumeric1, 0);
			
			MangoPointValues mpv = new PointValueService();
			
			mpv.savePointValueImpl(POINT_ID_1, pvtForAlphanumeric1, new AnonymousUser(), false);
			
			//TODO test async
			//mpv.savePointValue(POINT_ID_1, pvtForAlphanumeric1);
			
			//TODO don't work because save run batchWrite from Context.
			//PointValueTime pvt = mpv.getLatestPointValue(POINT_ID_1);
			//assertTrue(pvtForAlphanumeric1.equals(pvt));			
	}
	
}
