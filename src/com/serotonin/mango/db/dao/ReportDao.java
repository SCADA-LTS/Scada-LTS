/*
    Mango - Open Source M2M - http://mango.serotoninsoftware.com
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc.
    @author Matthew Lohbihler
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.db.dao;

import java.util.List;
import java.util.ResourceBundle;

import org.scada_lts.dao.report.ReportInstancePointDAO;
import org.scada_lts.mango.adapter.MangoReport;
import org.scada_lts.mango.service.ReportService;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.report.ReportDataStreamHandler;
import com.serotonin.mango.vo.report.ReportInstance;
import com.serotonin.mango.vo.report.ReportUserComment;
import com.serotonin.mango.vo.report.ReportVO;

/**
 * @author Matthew Lohbihler
 */
public class ReportDao {

	MangoReport reportService = new ReportService();

	//
	//
	// Report Templates
	//

	public List<ReportVO> getReports() {
		return reportService.getReports();
	}

	public List<ReportVO> getReports(int userId) {
		return reportService.getReports(userId);
	}

	public ReportVO getReport(int id) {
		return reportService.getReport(id);
	}

	public void saveReport(ReportVO report) {
		reportService.saveReport(report);
	}

	@Deprecated(since = "2.8.0")
	public void deleteReport(int reportId) {
		reportService.deleteReport(reportId);
	}

	//
	//
	// Report Instances
	//

	public List<ReportInstance> getReportInstances(int userId) {
		return reportService.getReportInstances(userId);
	}

	public ReportInstance getReportInstance(int id) {
		return reportService.getReportInstance(id);
	}

	public void deleteReportInstance(int id, int userId) {
		reportService.deleteReportInstance(id, userId);
	}

	public int purgeReportsBefore(final long time) {
		return reportService.purgeReportsBefore(time);
	}

	public void setReportInstancePreventPurge(int id, boolean preventPurge, int userId) {
		reportService.setReportInstancePreventPurge(id, preventPurge, userId);
	}

	/**
	 * This method should only be called by the ReportWorkItem.
	 */

	public void saveReportInstance(ReportInstance instance) {
		reportService.saveReportInstance(instance);
	}

	/**
	 * This method should only be called by the ReportWorkItem.
	 */

	public int runReport(final ReportInstance instance, List<ReportInstancePointDAO.PointInfo> points, ResourceBundle bundle) {
		return reportService.runReport(instance, points, bundle);
	}

	/**
	 * This method guarantees that the data is provided to the setData handler method grouped by point (points are not
	 * ordered), and sorted by time ascending.
	 */

	public void reportInstanceData(int instanceId, final ReportDataStreamHandler handler) {
		reportService.reportInstanceData(instanceId, handler);
	}

	public List<EventInstance> getReportInstanceEvents(int instanceId) {
		return reportService.getReportInstanceEvents(instanceId);
	}

	public List<ReportUserComment> getReportInstanceUserComments(int instanceId) {
		return reportService.getReportInstanceUserComments(instanceId);
	}
}
