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
package org.scada_lts.mango.adapter;

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.report.ReportDataStreamHandler;
import com.serotonin.mango.vo.report.ReportInstance;
import com.serotonin.mango.vo.report.ReportUserComment;
import com.serotonin.mango.vo.report.ReportVO;
import org.scada_lts.dao.report.ReportInstancePointDAO;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * ReportService adapter
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public interface MangoReport {

	List<ReportVO> getReports();

	List<ReportVO> getReports(int userId);

	ReportVO getReport(int id);

	ReportVO getReport(String xid);

	void saveReport(ReportVO report);

	void deleteReport(int reportId);

	List<ReportInstance> getReportInstances(int userId);

    List<ReportInstance> getReportInstances();

    ReportInstance getReportInstance(int id);

	void deleteReportInstance(int id, int userId);

	int purgeReportsBefore(final long time);

	void setReportInstancePreventPurge(int id, boolean preventPurge, int userId);

    void setReportInstancePreventPurge(int id, boolean preventPurge);

    void saveReportInstance(ReportInstance instance);

	int runReport(final ReportInstance instance, List<ReportInstancePointDAO.PointInfo> points, ResourceBundle bundle);

	void reportInstanceData(int instanceId, final ReportDataStreamHandler handler);

	List<EventInstance> getReportInstanceEvents(int instanceId);

	List<ReportUserComment> getReportInstanceUserComments(int instanceId);

	List<ReportVO> search(User user, Map<String, String> query);

	boolean hasReportReadPermission(User user, ReportVO report);

	boolean hasReportSetPermission(User user, ReportVO report);

	boolean hasReportOwnerPermission(User user, ReportVO report);

	boolean hasReportInstanceReadPermission(User user, ReportInstance report);

	boolean hasReportInstanceSetPermission(User user, ReportInstance report);

	boolean hasReportInstanceOwnerPermission(User user, ReportInstance report);

	boolean hasReportInstanceReadPermission(User user, int reportInstanceId);

	boolean hasReportInstanceSetPermission(User user, int reportInstanceId);

	boolean hasReportInstanceOwnerPermission(User user, int reportInstanceId);
}
