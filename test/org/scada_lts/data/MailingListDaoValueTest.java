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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scada_lts.dao.mailingList.MailingListDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import com.serotonin.mango.vo.mailingList.MailingList;

/**
 * @version 0.1
 * @author 	Radosław Jajko
 *
 */

public class MailingListDaoValueTest{
	
	private static MailingListDAO mailingListDao = null;
	private static ArrayList<Integer> listIds = null;
	private static TestDAO testDAO	= new TestDAO();
	private static int bound	=	5;
	
	@BeforeClass
	public static void setUp() throws ClassNotFoundException, SQLException{
		
		//Initialize all database
		testDAO.initialize();
		System.out.println("-- Database initialized succesfull!");
		
		mailingListDao = new MailingListDAO();
		listIds = new ArrayList<Integer>();
		
		for (int i=1; i <= bound; i++){
			listIds.add(mailingListDao.insert(genMailingList()));
		}
		
	}
	
	@Test
	public void createAndSelectMailingList(){
		
		listIds.add(mailingListDao.insert(genMailingList("Test")));
		MailingList mailingListSelectedId = mailingListDao.getMailingList(bound+1);
		
		assertTrue(mailingListSelectedId.getId() == bound+1);
		assertTrue(mailingListSelectedId.getName().equals("Test"));
		
		mailingListDao.delete(bound+1);
		listIds.remove(bound);
		
		assertTrue(listIds.size() == bound);
		
	}
	
	@Test
	public void selectMailingList(){
		
		//Select all objects
		List<MailingList> mailingLists = mailingListDao.getMailingLists();
		//Check list size
		assertEquals(bound, mailingLists.size());
		
		//Check MailingListID's
		for (int i = 1; i <= bound; i++){
		
			assertTrue(listIds.get(i) == mailingLists.get(i).getId());
		}
		
	}
	
	@Test
	public void updateMailingList(){
		
		MailingList updatedMailingList = genMailingList("Update");
		updatedMailingList.setId(1);
		
		mailingListDao.update(updatedMailingList);
		MailingList selectedUpdatedMailingList = mailingListDao.getMailingList(1);
		assertTrue(selectedUpdatedMailingList.getId() == updatedMailingList.getId());
		assertTrue(selectedUpdatedMailingList.getXid().equals(updatedMailingList.getXid()));
		assertTrue(selectedUpdatedMailingList.getName().equals(updatedMailingList.getName()));
		
	}
	
	@Test
	public void deleteMailingList(){
		
		mailingListDao.delete(bound);
		try{
			assertNull(mailingListDao.getMailingList(bound));
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
			Assert.fail("Catched Exception "+ e + "Instead expected NULL value");
		}
		bound--;
	}
	
	
	@AfterClass
	public static void tearDown() throws ClassNotFoundException, SQLException{
		testDAO.relax();
	}
	
	public static MailingList genMailingList(){
		
		MailingList mailingList = new MailingList();
		
		Random randomGenerator = new Random();
		Integer generated_xid = randomGenerator.nextInt(99000)+1;
		
		mailingList.setXid("ML_"+generated_xid);
		mailingList.setName("Name_"+generated_xid);
		
		return mailingList;

	}
	
	/**
	 *  Generate MailingList object
	 * 
	 * @param name - if null generates name based on XID
	 * @return mailingList
	 */
	
	public static MailingList genMailingList(String name){
		
		MailingList mailingList = new MailingList();
		
		Random randomGenerator = new Random();
		Integer generated_xid = randomGenerator.nextInt(99000)+1;
		
		mailingList.setXid("ML_"+generated_xid);
		mailingList.setName(name);
		
		return mailingList;

	}
	

}
