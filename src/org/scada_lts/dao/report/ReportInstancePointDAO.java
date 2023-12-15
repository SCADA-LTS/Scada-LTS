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

import com.mysql.jdbc.Statement;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.view.text.TextRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.report.ReportChartCreator;
import com.serotonin.mango.vo.report.ReportInstance;
import com.serotonin.mango.vo.report.ReportPointInfo;
import com.serotonin.web.taglib.Functions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.SerializationData;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO for ReportInstanceUserComment
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class ReportInstancePointDAO {

	private static final Log LOG = LogFactory.getLog(ReportInstancePointDAO.class);

	public static final String COLUMN_NAME_P_ID = "id";
	public static final String COLUMN_NAME_P_DATA_SOURCE_NAME = "dataSourceName";
	public static final String COLUMN_NAME_P_POINT_NAME = "pointName";
	public static final String COLUMN_NAME_P_DATA_TYPE = "dataType";
	public static final String COLUMN_NAME_P_START_VALUE = "startValue";
	public static final String COLUMN_NAME_P_TEXT_RENDERER = "textRenderer";
	public static final String COLUMN_NAME_P_COLOUR = "colour";
	public static final String COLUMN_NAME_P_CONSOLIDATED_CHART = "consolidatedChart";

	public static final String COLUMN_NAME_REPORT_INSTANCE_ID = "reportInstanceId";

	// @formatter:off
	private static final String REPORT_INSTANCE_POINT_SELECT = ""
			+ "select "
				+ COLUMN_NAME_P_ID + ", "
				+ COLUMN_NAME_P_DATA_SOURCE_NAME + ", "
				+ COLUMN_NAME_P_POINT_NAME + ", "
				+ COLUMN_NAME_P_DATA_TYPE + ", "
				+ COLUMN_NAME_P_POINT_NAME + ", "
				+ COLUMN_NAME_P_DATA_TYPE + ", "
				+ COLUMN_NAME_P_START_VALUE + ", "
				+ COLUMN_NAME_P_TEXT_RENDERER + ", "
				+ COLUMN_NAME_P_COLOUR + ", "
				+ COLUMN_NAME_P_CONSOLIDATED_CHART + " "
			+ "from reportInstancePoints ";

	private static final String REPORT_INSTANCE_POINT_SELECT_WHERE = ""
				+ REPORT_INSTANCE_POINT_SELECT
			+ "where "
				+ COLUMN_NAME_REPORT_INSTANCE_ID + "=? ";

	private static final String REPORT_INSTANCE_POINT_INSERT = ""
			+ "insert into reportInstancePoints ("
				+ COLUMN_NAME_REPORT_INSTANCE_ID + ", "
				+ COLUMN_NAME_P_DATA_SOURCE_NAME + ", "
				+ COLUMN_NAME_P_POINT_NAME + ", "
				+ COLUMN_NAME_P_DATA_TYPE + ", "
				+ COLUMN_NAME_P_START_VALUE + ", "
				+ COLUMN_NAME_P_TEXT_RENDERER + ", "
				+ COLUMN_NAME_P_COLOUR + ", "
				+ COLUMN_NAME_P_CONSOLIDATED_CHART + ") "
			+ "values (?,?,?,?,?,?,?,?) ";

	// @formatter:on

	/*
		PointInfo class
	 */
	public static class PointInfo {
		private final DataPointVO point;
		private final String colour;
		private final boolean consolidatedChart;

		public PointInfo(DataPointVO point, String colour, boolean consolidatedChart) {
			this.point = point;
			this.colour = colour;
			this.consolidatedChart = consolidatedChart;
		}

		public DataPointVO getPoint() {
			return point;
		}

		public String getColour() {
			return colour;
		}

		public boolean isConsolidatedChart() {
			return consolidatedChart;
		}
	}

	public List<ReportPointInfo> getPointInfos(int instanceId) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("getPointInfos(int instanceId) instanceId:" + instanceId);
		}

		return DAO.getInstance().getJdbcTemp().query(REPORT_INSTANCE_POINT_SELECT_WHERE, new Object[]{instanceId}, new ReportPointInfoRowMapper());
	}

	@Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
	public int insert(final ReportInstance reportInstance, final DataPointVO point, final String name, final int dataType, final MangoValue startValue, final PointInfo pointInfo) {

		if (LOG.isTraceEnabled()) {
			LOG.trace("insert()");
		}

		KeyHolder keyHolder = new GeneratedKeyHolder();
		DAO.getInstance().getJdbcTemp().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement preparedStatement = connection.prepareStatement(REPORT_INSTANCE_POINT_INSERT, Statement.RETURN_GENERATED_KEYS);
				new ArgumentPreparedStatementSetter(new Object[] {
						reportInstance.getId(),
						Functions.truncate(point.getDeviceName(), ReportChartCreator.getDataSourceNameLengthForReport()),
						Functions.truncate(name, ReportChartCreator.getDataPointNameLengthForReport()),
						dataType,
						DataTypes.valueToString(startValue),
						new SerializationData().writeObject(point.getTextRenderer()),
						pointInfo.getColour(),
						DAO.boolToChar(pointInfo.isConsolidatedChart())
				}).setValues(preparedStatement);
				return preparedStatement;
			}
		}, keyHolder);

		return keyHolder.getKey().intValue();
	}

	private class ReportPointInfoRowMapper implements RowMapper<ReportPointInfo> {

		@Override
		public ReportPointInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
			ReportPointInfo reportPointInfo = new ReportPointInfo();
			reportPointInfo.setReportPointId(rs.getInt(COLUMN_NAME_P_ID));
			reportPointInfo.setDeviceName(rs.getString(COLUMN_NAME_P_DATA_SOURCE_NAME));
			reportPointInfo.setPointName(rs.getString(COLUMN_NAME_P_POINT_NAME));
			reportPointInfo.setDataType(rs.getInt(COLUMN_NAME_P_DATA_TYPE));

			String startValue = rs.getString(COLUMN_NAME_P_START_VALUE);
			if (startValue != null) {
				reportPointInfo.setStartValue(MangoValue.stringToValue(startValue, reportPointInfo.getDataType()));
			}

			reportPointInfo.setTextRenderer((TextRenderer) new SerializationData().readObject(rs.getBlob(COLUMN_NAME_P_TEXT_RENDERER).getBinaryStream()));
			reportPointInfo.setColour(rs.getString(COLUMN_NAME_P_COLOUR));
			reportPointInfo.setConsolidatedChart(DAO.charToBool(rs.getString(COLUMN_NAME_P_CONSOLIDATED_CHART)));

			return reportPointInfo;
		}
	}

}
