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

import com.serotonin.mango.vo.report.ReportVO;
import org.junit.Test;
import org.scada_lts.dao.report.ReportDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Test ReportDAO
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class ReportDaoTest extends TestDAO {

	private static final int USER_ID = 1;
	private static final String NAME = "report name";

	private static final String SECOND_NAME = "sReport";

	private static final String UPDATE_NAME = "upReport";

	private static final int LIST_SIZE = 2;

	@Test
	public void test() {

		ReportVO report = new ReportVO();
		report.setUserId(USER_ID);
		report.setName(NAME);

		ReportVO secondReport = new ReportVO();
		secondReport.setUserId(USER_ID);
		secondReport.setName(SECOND_NAME);

		ReportDAO reportDAO = new ReportDAO();

		//Insert objects
		int firstId = reportDAO.insert(report);
		int secondId = reportDAO.insert(secondReport);
		report.setId(firstId);
		secondReport.setId(secondId);

		//Select single object
		ReportVO reportSelect = reportDAO.getReport(firstId);
		assertTrue(reportSelect.getId() == firstId);
		assertTrue(reportSelect.getName().equals(NAME));
		assertTrue(reportSelect.getUserId() == USER_ID);

		//Select all objects
		List<ReportVO> reportListAll = reportDAO.getReports();
		//Check size
		assertTrue(reportListAll.size() == LIST_SIZE);
		//Check IDs
		assertTrue(reportListAll.get(0).getId() == firstId);
		assertTrue(reportListAll.get(1).getId() == secondId);

		//Select all objects which contain specific userId
		List<ReportVO> reportListId = reportDAO.getReports(report.getUserId());
		//Check size
		assertTrue(reportListId.size() == LIST_SIZE);
		//Check ID
		assertTrue(reportListId.get(0).getId() == firstId);

		//Update
		ReportVO reportUpdate = new ReportVO();
		reportUpdate.setId(firstId);
		reportUpdate.setUserId(USER_ID);
		reportUpdate.setName(UPDATE_NAME);

		reportDAO.update(reportUpdate);
		ReportVO reportSelectUpdate = reportDAO.getReport(report.getId());
		assertTrue(reportSelectUpdate.getId() == report.getId());
		assertTrue(reportSelectUpdate.getUserId() == USER_ID);
		assertTrue(reportSelectUpdate.getName().equals(UPDATE_NAME));

		//Delete
		reportDAO.delete(firstId);
		assertTrue(reportDAO.getReport(firstId) == null);
	}
}
