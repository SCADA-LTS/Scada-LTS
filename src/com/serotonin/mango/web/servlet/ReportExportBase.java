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
package com.serotonin.mango.web.servlet;

import java.io.IOException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.ReportDao;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.vo.report.EventCsvStreamer;
import com.serotonin.mango.vo.report.ReportCsvStreamer;
import com.serotonin.mango.vo.report.ReportInstance;
import com.serotonin.mango.vo.report.UserCommentCsvStreamer;

/**
 * @author Matthew Lohbihler
 */
abstract public class ReportExportBase extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected static final int CONTENT_REPORT = 1;
    protected static final int CONTENT_EVENTS = 2;
    protected static final int CONTENT_COMMENTS = 3;

    protected void execute(HttpServletRequest request, HttpServletResponse response, int content) throws IOException {
        // Get the report instance id
        int instanceId = Integer.parseInt(request.getParameter("instanceId"));

        // Get the report instance
        ReportDao reportDao = new ReportDao();
        ReportInstance instance = reportDao.getReportInstance(instanceId);

        // Ensure the user is allowed access.
        Permissions.ensureReportInstancePermission(Common.getUser(request), instance);

        // Stream the content.
        response.setContentType("text/csv");

        ResourceBundle bundle = Common.getBundle();
        if (content == CONTENT_REPORT) {
            ReportCsvStreamer creator = new ReportCsvStreamer(response.getWriter(), bundle);
            reportDao.reportInstanceData(instanceId, creator);
        }
        else if (content == CONTENT_EVENTS)
            new EventCsvStreamer(response.getWriter(), reportDao.getReportInstanceEvents(instanceId), bundle);
        else if (content == CONTENT_COMMENTS)
            new UserCommentCsvStreamer(response.getWriter(), reportDao.getReportInstanceUserComments(instanceId),
                    bundle);
    }
}
