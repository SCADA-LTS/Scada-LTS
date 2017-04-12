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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import com.serotonin.db.IntValuePair;

import br.org.scadabr.vo.scripting.ContextualizedScriptVO;

/** 
 * Test ScriptDAO
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * 
 */
public class ScriptDaoTest extends TestDAO {
	
	private final static int ADMIN_USER_ID=1;
		
	@Test
	public void test() {
		
		//populate data
		ContextualizedScriptVO scriptVO = new ContextualizedScriptVO();
		
		scriptVO.setName("test");
		List<IntValuePair> lstObjectsOnContext = new ArrayList<IntValuePair>();
		scriptVO.setObjectsOnContext(lstObjectsOnContext);
		List<IntValuePair> lstPointsOnContext = new ArrayList<IntValuePair>();
		scriptVO.setPointsOnContext(lstPointsOnContext);
		scriptVO.setScript("var i=1");
		scriptVO.setUserId(ADMIN_USER_ID);
		String xid = "XID_0"+1;
		scriptVO.setXid(xid);
		
		// end populate data
		
		//CRUD
		
		ScriptDAO scriptDAO = new ScriptDAO();
		
		//insert
		int id = scriptDAO.insert(scriptVO);
		scriptVO.setId(id);
		
		//update
		scriptVO.setName("updated");
		scriptDAO.update(scriptVO);
		
		// read
		ContextualizedScriptVO scriptVOFromDB = (ContextualizedScriptVO) scriptDAO.getScript(id);
		
		boolean res = scriptVOFromDB.equals(scriptVO);
		assertEquals(true, res);
		
		
		// delete
		scriptDAO.delete(id);
		try{
			scriptDAO.getScript(id);	
		} catch(Exception e) {
			assertTrue(e.getClass().equals(EmptyResultDataAccessException.class));
			assertTrue(e.getMessage().equals("Incorrect result size: expected 1, actual 0"));
		}
		
		//end CRUD
		
	}
}
