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

import com.serotonin.mango.vo.link.PointLinkVO;
import org.junit.Test;
import org.scada_lts.dao.impl.PointLinkDAO;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Test PointLinkDAO
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class PointLinkDaoTest extends TestDAO {

	private static final String XID = "xid";
	private static final int SOURCE_POINT_ID = 1;
	private static final int TARGET_POINT_ID = 2;
	private static final String SCRIPT = "t";
	private static final int EVENT_TYPE = 1;
	private static final boolean DISABLED = true;

	private static final String SECOND_XID = "sec point xid";
	private static final int SECOND_SOURCE_POINT_ID = 9;
	private static final int SECOND_TARGET_POINT_ID = 4;
	private static final String SECOND_SCRIPT = "sec point script";
	private static final int SECOND_EVENT_TYPE = 2;
	private static final boolean SECOND_DISABLED = true;

	private static final String UPDATE_XID = "update point xid";
	private static final int UPDATE_SOURCE_POINT_ID = 7;
	private static final int UPDATE_TARGET_POINT_ID = 5;
	private static final String UPDATE_SCRIPT = "update point script";
	private static final int UPDATE_EVENT_TYPE = 3;
	private static final boolean UPDATE_DISABLED = false;

	private static final int LIST_SIZE = 2;

	@Test
	public void test() {

		PointLinkVO pointLink = new PointLinkVO();
		pointLink.setXid(XID);
		pointLink.setSourcePointId(SOURCE_POINT_ID);
		pointLink.setTargetPointId(TARGET_POINT_ID);
		pointLink.setScript(SCRIPT);
		pointLink.setEvent(EVENT_TYPE);
		pointLink.setDisabled(DISABLED);

		PointLinkVO secondPointLink = new PointLinkVO();
		secondPointLink.setXid(SECOND_XID);
		secondPointLink.setSourcePointId(SECOND_SOURCE_POINT_ID);
		secondPointLink.setTargetPointId(SECOND_TARGET_POINT_ID);
		secondPointLink.setScript(SECOND_SCRIPT);
		secondPointLink.setEvent(SECOND_EVENT_TYPE);
		secondPointLink.setDisabled(SECOND_DISABLED);

		PointLinkDAO pointLinkDAO = new PointLinkDAO();

		//Insert objects
		int firstId = pointLinkDAO.insert(pointLink);
		int secondId = pointLinkDAO.insert(secondPointLink);

		//Select single object by id
		PointLinkVO pointLinkSelectId = pointLinkDAO.getPointLink(firstId);
		assertTrue(pointLinkSelectId.getId() == firstId);
		assertTrue(pointLinkSelectId.getXid().equals(XID));
		assertTrue(pointLinkSelectId.getSourcePointId() == SOURCE_POINT_ID);
		assertTrue(pointLinkSelectId.getTargetPointId() == TARGET_POINT_ID);
		assertTrue(pointLinkSelectId.getScript().equals(SCRIPT));
		assertTrue(pointLinkSelectId.getEvent() == EVENT_TYPE);
		assertTrue(pointLinkSelectId.isDisabled() == DISABLED);

		//Select single object by xid
		PointLinkVO pointLinkSelectXid = pointLinkDAO.getPointLink(XID);
		assertTrue(pointLinkSelectXid.getId() == firstId);
		assertTrue(pointLinkSelectXid.getXid().equals(XID));
		assertTrue(pointLinkSelectXid.getSourcePointId() == SOURCE_POINT_ID);
		assertTrue(pointLinkSelectXid.getTargetPointId() == TARGET_POINT_ID);
		assertTrue(pointLinkSelectXid.getScript().equals(SCRIPT));
		assertTrue(pointLinkSelectXid.getEvent() == EVENT_TYPE);
		assertTrue(pointLinkSelectXid.isDisabled() == DISABLED);

		//Select all objects
		List<PointLinkVO> pointLinkList = pointLinkDAO.getPointLinks();
		//Check list size
		assertTrue(pointLinkList.size() == LIST_SIZE);
		//Check IDs
		assertTrue(pointLinkList.get(0).getId() == firstId);
		assertTrue(pointLinkList.get(1).getId() == secondId);

		//Update
		PointLinkVO pointLinkUpdate = new PointLinkVO();
		pointLinkUpdate.setId(firstId);
		pointLinkUpdate.setXid(UPDATE_XID);
		pointLinkUpdate.setSourcePointId(UPDATE_SOURCE_POINT_ID);
		pointLinkUpdate.setTargetPointId(UPDATE_TARGET_POINT_ID);
		pointLinkUpdate.setScript(UPDATE_SCRIPT);
		pointLinkUpdate.setEvent(UPDATE_EVENT_TYPE);
		pointLinkUpdate.setDisabled(UPDATE_DISABLED);

		pointLinkDAO.update(pointLinkUpdate);

		//Delete
		pointLinkDAO.delete(firstId);
		assertTrue(pointLinkDAO.getPointLink(firstId) == null);
	}

}
