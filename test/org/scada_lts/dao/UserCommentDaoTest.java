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

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.DataPointVO.LoggingTypes;
import com.serotonin.mango.vo.UserComment;
import org.junit.Test;
import org.scada_lts.dao.impl.DAO;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Test UserCommentDAO
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class UserCommentDaoTest extends TestDAO {

	private static final int USER_ID = 1;
	private static final int COMMENT_TYPE = 2;
	private static final int TYPE_KEY = 2;
	private static final long TS = 1245;
	private static final String COMMENT_TEXT = "UC text";

	private static final int SECOND_ID = 2;
	private static final int SECOND_COMMENT_TYPE = 2;
	private static final int SECOND_TYPE_KEY = 2;
	private static final long SECOND_TS = 15;
	private static final String SECOND_COMMENT_TEXT = "secUC text";

	private static final int LIST_SIZE = 2;

	@Test
	public void Test() {

		//TODO It is necessary to insert User object before insert UserComment object
		DAO.getInstance().getJdbcTemp().update("insert into users ("
				+ "  username, password, email, phone, admin, disabled, homeUrl, receiveAlarmEmails, receiveOwnAuditEvents) "
				+ "values ('us','pass','mail','1234',true,false,'urlHo',3,true)");

		UserComment userComment = new UserComment();
		userComment.setUserId(USER_ID);
		userComment.setTs(TS);
		userComment.setComment(COMMENT_TEXT);

		UserComment secondUserComment = new UserComment();
		secondUserComment.setUserId(SECOND_ID);
		secondUserComment.setTs(SECOND_TS);
		secondUserComment.setComment(SECOND_COMMENT_TEXT);

		UserCommentDAO userCommentDAO = new UserCommentDAO();

		//Insert
		int firstId = userCommentDAO.insert(userComment, COMMENT_TYPE, TYPE_KEY);
		int secondId = userCommentDAO.insert(secondUserComment, SECOND_COMMENT_TYPE, SECOND_TYPE_KEY);
		userComment.setUserId(firstId);
		userComment.setUserId(secondId);

		//Update (put 'null' if object with given id exist)
		userCommentDAO.update(firstId);

		//Select all objects with TYPE_POINT
		DataPointVO dataPoint = new DataPointVO(LoggingTypes.ON_CHANGE);
		dataPoint.setId(2);
		List<UserComment> userCommentList = userCommentDAO.getPointComments(dataPoint);
		//Check list size
		assertTrue(userCommentList.size() == LIST_SIZE);
		//Check IDs (after update)
		assertTrue(userCommentList.get(0).getUserId() == secondId);
		assertTrue(userCommentList.get(1).getUserId() == 0);

		//Delete
		userCommentDAO.deleteUserCommentPoint(String.valueOf(secondId));
		List<UserComment> nextUserCommentList = userCommentDAO.getPointComments(dataPoint);
		assertTrue(nextUserCommentList.size() == 0);
	}
}
