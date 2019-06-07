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

import com.serotonin.mango.vo.publish.PublishedPointVO;
import com.serotonin.mango.vo.publish.PublisherVO;
import com.serotonin.mango.vo.publish.httpSender.HttpSenderVO;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Test PublisherDAO
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class PublisherDaoTest extends TestDAO {

	private static final String XID = "first xid";
	private static final String NAME = "fName";

	private static final String SECOND_XID = "second xid";
	private static final String SECOND_NAME= "sName";

	private static final String UPDATE_XID = "update xid";
	private static final String UPDATE_NAME = "uName";

	private static final int LIST_SIZE = 2;

	@Test
	public void test() {

		HttpSenderVO httpSender = new HttpSenderVO();
		httpSender.setXid(XID);
		httpSender.setName(NAME);

		HttpSenderVO secondHttpSender = new HttpSenderVO();
		secondHttpSender.setXid(SECOND_XID);
		secondHttpSender.setName(SECOND_NAME);

		PublisherDAO publisherDAO = new PublisherDAO();

		//Insert objects
		int firstId = publisherDAO.insert(httpSender);
		int secondId = publisherDAO.insert(secondHttpSender);
		httpSender.setId(firstId);
		httpSender.setId(secondId);

		//Select single object
		HttpSenderVO httpSenderSelectId = (HttpSenderVO) publisherDAO.getPublisher(firstId);
		assertTrue(httpSenderSelectId.getId() == firstId);
		assertTrue(httpSenderSelectId.getXid().equals(XID));
		assertTrue(httpSenderSelectId.getName().equals(NAME));
		HttpSenderVO httpSenderSelectXid = (HttpSenderVO) publisherDAO.getPublisher(secondHttpSender.getXid());
		assertTrue(httpSenderSelectXid.getId() == secondId);
		assertTrue(httpSenderSelectXid.getXid().equals(SECOND_XID));
		assertTrue(httpSenderSelectXid.getName().equals(SECOND_NAME));

		//Select all objects
		List<PublisherVO<? extends PublishedPointVO>> publisherList = publisherDAO.getPublishers();
		//Check list size
		assertTrue(publisherList.size() == LIST_SIZE);
		//Check IDs
		assertTrue(publisherList.get(0).getId() == firstId);
		assertTrue(publisherList.get(1).getId() == secondId);

		//Update
		HttpSenderVO httpSenderUpdate = new HttpSenderVO();
		httpSenderUpdate.setId(httpSenderSelectId.getId());
		httpSenderUpdate.setXid(UPDATE_XID);
		httpSenderUpdate.setName(UPDATE_NAME);

		publisherDAO.update(httpSenderUpdate);
		HttpSenderVO httpSenderSelectUpdate = (HttpSenderVO) publisherDAO.getPublisher(httpSenderSelectId.getId());
		assertTrue(httpSenderSelectUpdate.getId() == httpSenderSelectId.getId());
		assertTrue(httpSenderSelectUpdate.getXid().equals(UPDATE_XID));
		assertTrue(httpSenderSelectUpdate.getName().equals(UPDATE_NAME));

		//Delete
		publisherDAO.delete(firstId);
		assertTrue(publisherDAO.getPublisher(firstId) == null);
	}
}
