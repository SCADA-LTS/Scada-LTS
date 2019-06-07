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

import com.serotonin.mango.vo.mailingList.MailingList;
import org.junit.Test;
import org.scada_lts.dao.mailingList.MailingListDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Test MailingListDAO
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class MailingListDaoTest extends TestDAO {

	private static final String XID = "fXid";
	private static final String NAME = "fName";

	private static final String SECOND_XID = "sXid";
	private static final String SECOND_NAME = "sName";

	private static final String UPDATE_XID = "uXid";
	private static final String UPDATE_NAME = "uName";

	private static final int LIST_SIZE = 2;

	@Test
	public void test() {

		MailingList mailingList = new MailingList();
		mailingList.setXid(XID);
		mailingList.setName(NAME);

		MailingList secondMailingList = new MailingList();
		secondMailingList.setXid(SECOND_XID);
		secondMailingList.setName(SECOND_NAME);

		MailingListDAO mailingListDAO = new MailingListDAO();

		//Insert objects
		int firstId = mailingListDAO.insert(mailingList);
		int secondId = mailingListDAO.insert(secondMailingList);
		mailingList.setId(firstId);
		secondMailingList.setId(secondId);

		//Select single object
		MailingList mailingListSelectId = mailingListDAO.getMailingList(firstId);
		assertTrue(mailingListSelectId.getId() == firstId);
		assertTrue(mailingListSelectId.getXid().equals(XID));
		assertTrue(mailingListSelectId.getName().equals(NAME));
		MailingList mailingListSelectXid = mailingListDAO.getMailingList(secondMailingList.getXid());
		assertTrue(mailingListSelectXid.getId() == secondId);
		assertTrue(mailingListSelectXid.getXid().equals(SECOND_XID));
		assertTrue(mailingListSelectXid.getName().equals(SECOND_NAME));

		//Select all objects
		List<MailingList> mailingLists = mailingListDAO.getMailingLists();
		//Check list size
		assertTrue(mailingLists.size() == LIST_SIZE);
		//Check IDs
		assertTrue(mailingLists.get(0).getId() == firstId);
		assertTrue(mailingLists.get(1).getId() == secondId);

		//Update
		MailingList mailingListUpdate = new MailingList();
		mailingListUpdate.setId(firstId);
		mailingListUpdate.setXid(UPDATE_XID);
		mailingListUpdate.setName(UPDATE_NAME);

		mailingListDAO.update(mailingListUpdate);
		MailingList mailingListSelectUpdate = mailingListDAO.getMailingList(firstId);
		assertTrue(mailingListSelectUpdate.getId() == mailingListUpdate.getId());
		assertTrue(mailingListSelectUpdate.getXid().equals(mailingListUpdate.getXid()));
		assertTrue(mailingListSelectUpdate.getName().equals(mailingListUpdate.getName()));

		//Delete
		mailingListDAO.delete(secondId);
		try{
			mailingListDAO.getMailingList(secondId);
		} catch(Exception e){
			assertTrue(e.getClass().equals(EmptyResultDataAccessException.class));
			assertTrue(e.getMessage().equals("Incorrect result size: expected 1, actual 0"));
		}
	}
}
