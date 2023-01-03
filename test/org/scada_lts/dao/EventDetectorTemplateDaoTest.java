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
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import br.org.scadabr.vo.eventDetectorTemplate.EventDetectorTemplateVO;
import org.scada_lts.dao.impl.EventDetectorTemplateDAO;

/** 
 * Test EventDetectorTemplateDAO
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * 
 */
public class EventDetectorTemplateDaoTest extends TestDAO{
	
	@Test
	public void test() throws Exception {

		// populate data
		List<PointEventDetectorVO> lstPointsEventDetector = new ArrayList<PointEventDetectorVO>();
		
		lstPointsEventDetector.add(createEventDetectorVO(1));
		lstPointsEventDetector.add(createEventDetectorVO(2));
		lstPointsEventDetector.add(createEventDetectorVO(3));
		
		EventDetectorTemplateVO eventDetectorTempVO = new EventDetectorTemplateVO();
	 
		eventDetectorTempVO.setName("test");
		eventDetectorTempVO.setEventDetectors(lstPointsEventDetector);
		// end papulate data
		
		// CRUD start
		EventDetectorTemplateDAO eventDetectorTemplateDAO = new EventDetectorTemplateDAO();
	    
		//insert
		int id = eventDetectorTemplateDAO.insertEventDetectorTemplate(eventDetectorTempVO);
		eventDetectorTempVO.setId(id);
		
		//next insert
		eventDetectorTempVO.setName("nextTest");
		int idNext = eventDetectorTemplateDAO.insertEventDetectorTemplate(eventDetectorTempVO);
		eventDetectorTempVO.setId(idNext);
				
		
		//update (no op because not have update)
		
		// select 
		EventDetectorTemplateVO eventDetectorTempVOFromDB = eventDetectorTemplateDAO.getEventDetectorTemplate(idNext);
		
		//check insert and select
		assertEquals(true, eventDetectorTempVOFromDB.equals(eventDetectorTempVOFromDB));
		
		// delete (no op because not have delete)
		
		// CRUD end
		
		// additional method
		List<EventDetectorTemplateVO> lstEventDetectorTempVOFromDBWithoutDetectors = eventDetectorTemplateDAO.getEventDetectorTemplatesWithoutDetectors();
		
		//check getEventDetectorTemplatesWithoutDetectors();
		
		for (EventDetectorTemplateVO edtvo: lstEventDetectorTempVOFromDBWithoutDetectors) {
			assertEquals(true, (edtvo.getEventDetectors()==null));
		}
	}
	
}
