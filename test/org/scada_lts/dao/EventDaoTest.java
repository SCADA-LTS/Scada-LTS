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

import org.junit.Test;
import org.scada_lts.dao.event.EventDAO;
import org.scada_lts.dao.model.event.Event;
import org.springframework.dao.EmptyResultDataAccessException;

import br.org.scadabr.vo.scripting.ContextualizedScriptVO;

/**
 * Test EventDAO
 *
 * @author Grzesiek Bylica Abil'I.T. development team, sdt@abilit.eu
 */
public class EventDaoTest extends TestDAO {

		private final static int ADMIN_USER_ID=1;
		
		@Test
		public void test() {
			
			//populate data
			Event event = new Event();
			
			event.setEventType(4);
			event.setTypeRef1(9);
			event.setTypeRef2(18);
			event.setActiveTimestamp(1464022806190L);
			event.setRtnApplicable(false);
			event.setRtnTimestamp(1464027257301L);
			event.setRtnCause(1);
			event.setAlarmLevel(2);
			event.setMessage("");
			event.setAckTS(1464022806190L);
			//event.setActUserId(ADMIN_USER_ID);
			//event.setUserName("admin");
			// null
			//event.setAlternateAckSource();
			
			
			// end populate data
			
			//CR
			
			EventDAO eventDAO = new EventDAO();
			
			//insert (create)
			long id = eventDAO.create(event);
			event.setId(id);
			
			// read 
			// (findById)
			Event eventFromDB = eventDAO.findById(id);
			
			boolean res = eventFromDB.equals(event);
			assertEquals(true, res);
			
			//(findAll)
			
			//(findFiltered)
			
			//end CR
			
		}
}


