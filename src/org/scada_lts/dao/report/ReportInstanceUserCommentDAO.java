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
package org.scada_lts.dao.report;

import com.serotonin.mango.vo.UserComment;
import com.serotonin.mango.vo.report.ReportUserComment;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO for ReportUserComment
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class ReportInstanceUserCommentDAO {

	private static final Log LOG = LogFactory.getLog(ReportInstanceUserCommentDAO.class);

	public static final String COLUMN_NAME_ID = "reportInstanceId";
	public static final String COLUMN_NAME_USERNAME = "username";
	public static final String COLUMN_NAME_COMMENT_TYPE = "commentType";
	public static final String COLUMN_NAME_TYPE_KEY = "typeKey";
	public static final String COLUMN_NAME_TS = "ts";
	public static final String COLUMN_NAME_COMMENT_TEXT = "commentText";

	private static final String COLUMN_NAME_RIP_ID = "id";
	private static final String COLUMN_NAME_RIP_POINT_NAME = "pointName";

	// @formatter:off
	private static final String REPORT_USER_COMMENT_SELECT = ""
			+ "select "
				+ "rc." + COLUMN_NAME_USERNAME + ", "
				+ "rc." + COLUMN_NAME_COMMENT_TYPE + ", "
				+ "rc." + COLUMN_NAME_TYPE_KEY + ", "
				+ "rp." + COLUMN_NAME_RIP_POINT_NAME + ", "
				+ "rc." + COLUMN_NAME_TS + ", "
				+ "rc." + COLUMN_NAME_COMMENT_TEXT + " "
			+ "from reportInstanceUserComments rc "
			+ "left join reportInstancePoints rp on "
				+ "rc." + COLUMN_NAME_TYPE_KEY + "="
				+ "rp." + COLUMN_NAME_RIP_ID + " "
			+ "and "
				+ "rc." + COLUMN_NAME_COMMENT_TYPE
				+ "=" + UserComment.TYPE_POINT + " "
			+ "where "
				+ "rc." + COLUMN_NAME_ID + "=? "
			+ "order by "
				+ "rc." + COLUMN_NAME_TS + " ";

	public static final String REPORT_USER_COMMENT_SELECT_WHERE = ""
			+ "select "
				+ COLUMN_NAME_USERNAME + ", "
				+ COLUMN_NAME_TYPE_KEY + ", "
				+ COLUMN_NAME_TS + ", "
				+ COLUMN_NAME_COMMENT_TEXT + " "
			+ "from reportInstanceUserComments "
			+ "where "
				+ COLUMN_NAME_ID + "=? "
			+ "and "
				+ COLUMN_NAME_COMMENT_TYPE + "=? "
			+ "order by "
				+ COLUMN_NAME_TS + " ";
	// @formatter:on

	private class ReportCommentRowMapper implements RowMapper<ReportUserComment> {

		@Override
		public ReportUserComment mapRow(ResultSet rs, int rowNum) throws SQLException {
			ReportUserComment reportUserComment = new ReportUserComment();
			reportUserComment.setUsername(rs.getString(COLUMN_NAME_USERNAME));
			reportUserComment.setCommentType(rs.getInt(COLUMN_NAME_COMMENT_TYPE));
			reportUserComment.setTypeKey(rs.getInt(COLUMN_NAME_TYPE_KEY));
			reportUserComment.setPointName(rs.getString(COLUMN_NAME_RIP_POINT_NAME));
			reportUserComment.setTs(rs.getLong(COLUMN_NAME_TS));
			reportUserComment.setComment(rs.getString(COLUMN_NAME_COMMENT_TEXT));
			return reportUserComment;
		}
	}

	public List<ReportUserComment> getReportUserComments(int instanceId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getReportUserComment(int instanceId) instanceId:" + instanceId);
		}

		return DAO.getInstance().getJdbcTemp().query(REPORT_USER_COMMENT_SELECT, new Object[]{instanceId}, new ReportCommentRowMapper());
	}
}
