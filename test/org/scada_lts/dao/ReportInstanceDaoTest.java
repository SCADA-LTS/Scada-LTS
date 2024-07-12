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

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.view.text.AnalogRenderer;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.report.*;
import com.serotonin.util.StringUtils;
import com.serotonin.web.i18n.I18NUtils;
import org.junit.Before;
import org.junit.Test;
import org.scada_lts.dao.report.ReportInstanceDAO;
import org.scada_lts.dao.report.ReportInstanceDataDAO;
import org.scada_lts.dao.report.ReportInstancePointDAO;
import org.scada_lts.dao.report.ReportInstanceUserCommentDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.ResourceBundle;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test ReportInstanceDAO
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class ReportInstanceDaoTest extends TestDAO {

	private static final int USER_ID = 1;
	private static final String NAME = "fName";
	private static final int INCLUDE_EVENTS = 1;
	private static final boolean INCLUDE_USER_COMMENT = false;
	private static final long REPORT_START_TIME = 2;
	private static final long REPORT_END_TIME = 5214314;
	private static final long RUN_START_TIME = 3234;
	private static final long RUN_END_TIME = 1232144;
	private static final int RECORD_COUNT = 32;
	private static final boolean PREVENT_PURGE = true;

	private static final String SECOND_NAME = "sName";
	private static final int SECOND_INCLUDE_EVENTS = 2;
	private static final boolean SECOND_INCLUDE_USER_COMMENT = true;
	private static final long SECOND_REPORT_START_TIME = 21;
	private static final long SECOND_REPORT_END_TIME = 14314;
	private static final long SECOND_RUN_START_TIME = 1134;
	private static final long SECOND_RUN_END_TIME = 17744;
	private static final int SECOND_RECORD_COUNT = 2;
	private static final boolean SECOND_PREVENT_PURGE = true;

	private static final long UPDATE_REPORT_START_TIME = 1;
	private static final long UPDATE_REPORT_END_TIME = 52314;
	private static final long UPDATE_RUN_START_TIME = 34;
	private static final long UPDATE_RUN_END_TIME = 2144;
	private static final int UPDATE_RECORD_COUNT = 5;
	private static final boolean UPDATE_PREVENT_PURGE = false;

	private static final int LIST_SIZE = 2;

	private ReportInstance reportInstance;
	private AnalogRenderer analogRenderer;
	private DataPointVO dataPointVO;
	private MangoValue startValue;
	private ReportInstancePointDAO.PointInfo pointInfo;

	@Before
	public void beforeTest() {
		/*
			Create mock for some necessary objects
		 */
		reportInstance = mock(ReportInstance.class);
		when(reportInstance.getId()).thenReturn(INSTANCE_ID);

		analogRenderer = new AnalogRenderer();
		analogRenderer.setFormat("form");
		dataPointVO = mock(DataPointVO.class);
		when(dataPointVO.getDeviceName()).thenReturn(DATA_SOURCE_NAME).thenReturn(SECOND_DATA_SOURCE_NAME);
		when(dataPointVO.getTextRenderer()).thenReturn(analogRenderer);

		startValue = mock(MangoValue.class);
		when(startValue.toString()).thenReturn("mValString").thenReturn("secondMVal");

		pointInfo = mock(ReportInstancePointDAO.PointInfo.class);
		when(pointInfo.getColour()).thenReturn("blue").thenReturn("red");
		when(pointInfo.isConsolidatedChart()).thenReturn(true);
	}

	@Test
	public void testReportInstanceDAO() {
		ReportInstance reportInstance = new ReportInstance();
		reportInstance.setUserId(USER_ID);
		reportInstance.setName(NAME);
		reportInstance.setIncludeEvents(INCLUDE_EVENTS);
		reportInstance.setIncludeUserComments(INCLUDE_USER_COMMENT);
		reportInstance.setReportStartTime(REPORT_START_TIME);
		reportInstance.setReportEndTime(REPORT_END_TIME);
		reportInstance.setRunStartTime(RUN_START_TIME);
		reportInstance.setRunEndTime(RUN_END_TIME);
		reportInstance.setRecordCount(RECORD_COUNT);
		reportInstance.setPreventPurge(PREVENT_PURGE);

		ReportInstance secondReportInstance = new ReportInstance();
		secondReportInstance.setUserId(USER_ID);
		secondReportInstance.setName(SECOND_NAME);
		secondReportInstance.setIncludeEvents(SECOND_INCLUDE_EVENTS);
		secondReportInstance.setIncludeUserComments(SECOND_INCLUDE_USER_COMMENT);
		secondReportInstance.setReportStartTime(SECOND_REPORT_START_TIME);
		secondReportInstance.setReportEndTime(SECOND_REPORT_END_TIME);
		secondReportInstance.setRunStartTime(SECOND_RUN_START_TIME);
		secondReportInstance.setRunEndTime(SECOND_RUN_END_TIME);
		secondReportInstance.setRecordCount(SECOND_RECORD_COUNT);
		secondReportInstance.setPreventPurge(SECOND_PREVENT_PURGE);

		ReportInstanceDAO reportInstanceDAO = new ReportInstanceDAO();

		//Insert
		int firstId = reportInstanceDAO.insert(reportInstance);
		int secondId = reportInstanceDAO.insert(secondReportInstance);
		reportInstance.setId(firstId);
		secondReportInstance.setId(secondId);

		//Select single object
		ReportInstance reportInstanceSelect = reportInstanceDAO.getReportInstance(firstId);
		assertTrue(reportInstanceSelect.getId() == firstId);
		assertTrue(reportInstanceSelect.getUserId() == USER_ID);
		assertTrue(reportInstanceSelect.getName().equals(NAME));
		assertTrue(reportInstanceSelect.getIncludeEvents() == INCLUDE_EVENTS);
		assertTrue(reportInstanceSelect.isIncludeUserComments() == INCLUDE_USER_COMMENT);
		assertTrue(reportInstanceSelect.getReportStartTime() == REPORT_START_TIME);
		assertTrue(reportInstanceSelect.getReportEndTime() == REPORT_END_TIME);
		assertTrue(reportInstanceSelect.getRunStartTime() == RUN_START_TIME);
		assertTrue(reportInstanceSelect.getRunEndTime() == RUN_END_TIME);
		assertTrue(reportInstanceSelect.getRecordCount() == RECORD_COUNT);
		assertTrue(reportInstanceSelect.isPreventPurge() == PREVENT_PURGE);

		//Select all objects which contain specific userId
		List<ReportInstance> reportInstancesList = reportInstanceDAO.getReportInstances(firstId);
		//Check size
		assertTrue(reportInstancesList.size() == LIST_SIZE);
		//Check IDs
		assertTrue(reportInstancesList.get(0).getId() == firstId);
		assertTrue(reportInstancesList.get(1).getId() == secondId);

		//Update time
		ReportInstance updateTimeReportInstance = new ReportInstance();
		updateTimeReportInstance.setId(firstId);
		updateTimeReportInstance.setUserId(USER_ID);
		updateTimeReportInstance.setReportStartTime(UPDATE_REPORT_START_TIME);
		updateTimeReportInstance.setReportEndTime(UPDATE_REPORT_END_TIME);
		updateTimeReportInstance.setRunStartTime(UPDATE_RUN_START_TIME);
		updateTimeReportInstance.setRunEndTime(UPDATE_RUN_END_TIME);
		updateTimeReportInstance.setRecordCount(UPDATE_RECORD_COUNT);

		reportInstanceDAO.updateTime(updateTimeReportInstance);
		ReportInstance selectUpdateReportInstance = reportInstanceDAO.getReportInstance(updateTimeReportInstance.getId());
		assertTrue(selectUpdateReportInstance.getId() == updateTimeReportInstance.getId());
		assertTrue(selectUpdateReportInstance.getUserId() == updateTimeReportInstance.getUserId());
		assertTrue(selectUpdateReportInstance.getReportStartTime() == updateTimeReportInstance.getReportStartTime());
		assertTrue(selectUpdateReportInstance.getReportEndTime() == updateTimeReportInstance.getReportEndTime());
		assertTrue(selectUpdateReportInstance.getRunStartTime() == updateTimeReportInstance.getRunStartTime());
		assertTrue(selectUpdateReportInstance.getRunEndTime() == updateTimeReportInstance.getRunEndTime());
		assertTrue(selectUpdateReportInstance.getRecordCount() == updateTimeReportInstance.getRecordCount());

		//Update prevent purge
		reportInstanceDAO.updatePreventPurge(firstId, UPDATE_PREVENT_PURGE, USER_ID);
		selectUpdateReportInstance = reportInstanceDAO.getReportInstance(firstId);
		assertTrue(selectUpdateReportInstance.getId() == firstId);
		assertTrue(selectUpdateReportInstance.isPreventPurge() == UPDATE_PREVENT_PURGE);

		//Delete
		reportInstanceDAO.delete(secondId, USER_ID);
		reportInstanceDAO.deleteReportBefore(updateTimeReportInstance.getReportEndTime()+1);
		reportInstancesList = reportInstanceDAO.getReportInstances(firstId);
		assertTrue(reportInstancesList.size() == 0);
		assertTrue(reportInstanceDAO.getReportInstance(secondId) == null);
	}

	@Test
	public void testReportInstanceDataDAO() {

		DAO.getInstance().getJdbcTemp().update("INSERT INTO datasources (`xid`,`name`,`dataSourceType`,`data`) VALUES ('DS_01','DS_TEST', 1,'')");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO datapoints (`xid`,`dataSourceId`,`data`) VALUES ('T_01',1,'')");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO pointvalues (dataPointId, dataType, pointValue, ts) values(1,1,2.0,123)");
		DAO.getInstance().getJdbcTemp().update("insert into reportInstances "
				+ "  (userId, name, includeEvents, includeUserComments, reportStartTime, reportEndTime, runStartTime, "
				+ "runEndTime, recordCount, preventPurge) " + "  values (" + USER_ID + ", '" + NAME + "', " + INCLUDE_EVENTS + ", " + INCLUDE_USER_COMMENT
				+ ", " + REPORT_START_TIME + ", " + REPORT_END_TIME + "," + RUN_START_TIME + "," + RUN_END_TIME + "," + RECORD_COUNT + "," + PREVENT_PURGE + ")");

		ReportInstancePointDAO reportInstancePointDAO = new ReportInstancePointDAO();
		reportInstancePointDAO.insert(reportInstance, DATA_TYPE, startValue, pointInfo);

		ReportInstanceDataDAO reportInstanceDataDAO = new ReportInstanceDataDAO();

		ReportInstance reportInst = new ReportInstance();
		reportInst.setReportStartTime(1);

		String timestampSql = "and ${field}>=?";
		Object[] params = new Object[] {reportInst.getReportStartTime(), INSTANCE_ID, DATA_TYPE};

		reportInstanceDataDAO.insert(params, INSTANCE_ID, StringUtils.replaceMacro(timestampSql, "field", "ts"));

		//TODO
		ResourceBundle bundle = mock(ResourceBundle.class);
		String userLabel = I18NUtils.getMessage(bundle, "common.user");
		String setPointLabel = I18NUtils.getMessage(bundle, "annotation.eventHandler");
		String anonymousLabel = I18NUtils.getMessage(bundle, "annotation.anonymous");
		String deletedLabel = I18NUtils.getMessage(bundle, "common.deleted");

		String annotationCase = "    case pva.sourceType" //
				+ "      when 1 then concat('" + userLabel + ": ',ifnull(u.username,'" + deletedLabel + "')) " //
				+ "      when 2 then '" + setPointLabel + "'" //
				+ "      when 3 then '" + anonymousLabel + "'" //
				+ "      else concat('Unknown source type: ', pva.sourceType)" //
				+ "    end ";

		reportInstanceDataDAO.insertReportInstanceDataAnnotations(annotationCase, INSTANCE_ID);


		/*
			setReportValue test
		 */
		ReportDataValue reportDataValue = new ReportDataValue();
		ReportDataStreamHandler reportDataStreamHandler = mock(ReportDataStreamHandler.class);
		ReportPointInfo reportPointInfo = mock(ReportPointInfo.class);

		reportInstanceDataDAO.setReportValue(reportPointInfo, reportDataValue, reportDataStreamHandler);
	}

	private static final int INSTANCE_ID = 1;

	private static final String DATA_SOURCE_NAME = "fDSN";
	private static final String POINT_NAME = "fPointName";
	private static final int DATA_TYPE = 1;

	private static final String SECOND_DATA_SOURCE_NAME = "sDSN";
	private static final String SECOND_POINT_NAME = "sPointName";
	private static final int SECOND_DATA_TYPE = 2;


	@Test
	public void testReportInstancePointDAO() {

		DAO.getInstance().getJdbcTemp().update("insert into reportInstances "
				+ "  (userId, name, includeEvents, includeUserComments, reportStartTime, reportEndTime, runStartTime, "
				+ "runEndTime, recordCount, preventPurge) " + "  values (" + USER_ID + ", '" + NAME + "', " + INCLUDE_EVENTS + ", " + INCLUDE_USER_COMMENT
						+ ", " + REPORT_START_TIME + ", " + REPORT_END_TIME + "," + RUN_START_TIME + "," + RUN_END_TIME + "," + RECORD_COUNT + "," + PREVENT_PURGE + ")");

		ReportInstancePointDAO reportInstancePointDAO = new ReportInstancePointDAO();

		//Insert objects
		int firstId = reportInstancePointDAO.insert(reportInstance, DATA_TYPE, startValue, pointInfo);
		int secondId = reportInstancePointDAO.insert(reportInstance, SECOND_DATA_TYPE, startValue, pointInfo);

		//Select all objects
		List<ReportPointInfo> reportPointInfoList = reportInstancePointDAO.getPointInfos(INSTANCE_ID);
		assertTrue(reportPointInfoList.size()==LIST_SIZE);
		assertTrue(reportPointInfoList.get(0).getReportPointId() == firstId);
		assertTrue(reportPointInfoList.get(0).getDeviceName().equals(DATA_SOURCE_NAME));
		assertTrue(reportPointInfoList.get(0).getPointName().equals(POINT_NAME));
		assertTrue(reportPointInfoList.get(0).getDataType() == DATA_TYPE);
		assertTrue(reportPointInfoList.get(1).getReportPointId() == secondId);
		assertTrue(reportPointInfoList.get(1).getDeviceName().equals(SECOND_DATA_SOURCE_NAME));
		assertTrue(reportPointInfoList.get(1).getPointName().equals(SECOND_POINT_NAME));
		assertTrue(reportPointInfoList.get(1).getDataType() == SECOND_DATA_TYPE);
	}

	private static final String USERNAME = "fUsername";
	private static final int COMMENT_TYPE = 1;
	private static final int TYPE_KEY = 1;
	private static final long TS = 20;
	private static final String COMMENT_TEXT = "fCommentText";

	@Test
	public void testReportInstanceUserCommentDAO() {

		DAO.getInstance().getJdbcTemp().update("insert into reportInstances "
				+ "  (userId, name, includeEvents, includeUserComments, reportStartTime, reportEndTime, runStartTime, "
				+ "runEndTime, recordCount, preventPurge) " + "  values (" + USER_ID + ", '" + NAME + "', " + INCLUDE_EVENTS + ", " + INCLUDE_USER_COMMENT
				+ ", " + REPORT_START_TIME + ", " + REPORT_END_TIME + "," + RUN_START_TIME + "," + RUN_END_TIME + "," + RECORD_COUNT + "," + PREVENT_PURGE + ")");
		DAO.getInstance().getJdbcTemp().update("INSERT INTO reportinstanceusercomments (reportInstanceId, username, commentType, typeKey, ts, commentText) values (1, 'fName', 1, 1, 20, 'fCom')");

		ReportInstanceUserCommentDAO reportInstanceUserCommentDAO = new ReportInstanceUserCommentDAO();

		//Insert objects
		List<ReportUserComment> reportUserCommentList = reportInstanceUserCommentDAO.getReportUserComments(INSTANCE_ID);
		assertTrue(reportUserCommentList.size() == 1);
		assertTrue(reportUserCommentList.get(0).getUsername().equals("fName"));
		assertTrue(reportUserCommentList.get(0).getCommentType() == 1);
		assertTrue(reportUserCommentList.get(0).getComment().equals("fCom"));
	}
}
