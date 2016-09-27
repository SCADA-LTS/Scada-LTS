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

import com.serotonin.mango.vo.report.ReportInstance;
import org.junit.Test;
import org.scada_lts.dao.report.ReportInstanceDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

import static org.junit.Assert.assertTrue;

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

	@Test
	public void test() {
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
		expectedException.expect(EmptyResultDataAccessException.class);
		expectedException.expectMessage("Incorrect result size: expected 1, actual 0");
		reportInstanceDAO.getReportInstance(secondId);
	}
}
