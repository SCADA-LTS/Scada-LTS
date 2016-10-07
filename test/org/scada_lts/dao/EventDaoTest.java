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

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.DataSourceEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.web.i18n.LocalizableMessage;

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
			
			//DATA SOURCE
			
			EventType type = new DataSourceEventType(1,1);
			long activeTS = 10;
			boolean applicable = true;
			int alarmLevel = 3;
						
			EventInstance e = new EventInstance(type, activeTS,	applicable, alarmLevel, null, null);
			
			
			
			// end populate data
			
			//CR
			
			EventDAO eventDAO = new EventDAO();
			
			//insert (create)
			int id = (int) eventDAO.create(e)[0];
			e.setId(id);
			
			// read 
			// (findById)
			EventInstance eventFromDB = eventDAO.findById(new Object[] {id});
			
			boolean res = eventFromDB.equals(e);
			assertEquals(true, res);
			
			//(findAll)
			
			//(findFiltered)
			
			//end CR
			
		}
}


