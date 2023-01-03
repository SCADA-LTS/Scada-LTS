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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.scada_lts.dao.event.UserEventDAO;
import org.scada_lts.dao.impl.DAO;
import org.scada_lts.dao.model.event.UserEvent;

/** 
 * Test UserEventDAO test
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * 
 */
public class UserEventDaoTest extends TestDAO {
	
	private final static int ADMIN_USER_ID=1;
	
	@Test
	public void test() {
		
		//populate data
		
		DAO.getInstance().getJdbcTemp().update("insert events ("
				+ "`typeId`,"
				+ "`typeRef1`,"
				+ "`typeRef2`,"
				+ "`activeTs`,"
				+ "`rtnApplicable`,"
				+ "`rtnTs`,"
				+ "`rtnCause`,"
				+ "`alarmLevel`,"
				+ "`message`,"
				+ "`ackTs`,"
				+ "`ackUserId`,"
				+ "`alternateAckSource`"
				+ ") values ("
				+ "4,"
				+ "1,"
				+ "1,"
				+ "1464022806190,"
				+ "'Y',"
				+ "1464027257301,"
				+ "1,"
				+ "2,"
				+ "'event.pointLink.sourceUnavailable|',"
				+ "null,"
				+ "null,"
				+ "null)");
		
		UserEvent userEvent = new UserEvent();
		
		userEvent.setEventId(1);
		userEvent.setSilenced(false);
		userEvent.setUserId(ADMIN_USER_ID);
		
		// end populate data
		
		UserEventDAO userEventDAO = new UserEventDAO();
		
		//CR
		
		//insert
		Object[] pk = userEventDAO.create(userEvent);
		
		// read
		UserEvent userEventFromDB =  userEventDAO.findById(pk);
		
		boolean res = userEventFromDB.equals(userEvent);
		assertEquals(true, res);
		
		List<UserEvent> lstUserEventFromDB = userEventDAO.findAll();
		boolean resFindAll = lstUserEventFromDB.size()==1;
		assertEquals(true, resFindAll);
		
		//end CR
		
	}
	
}
