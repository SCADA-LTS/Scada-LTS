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
package com.serotonin.mango.web.mvc.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.AbstractController;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.ReportDao;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.vo.report.ReportChartCreator;
import com.serotonin.mango.vo.report.ReportChartCreator.PointStatistics;
import com.serotonin.mango.vo.report.ReportInstance;

/**
 * @author Matthew Lohbihler
 */
public class ReportChartController extends AbstractController {
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        int instanceId = Integer.parseInt(request.getParameter("instanceId"));
        ReportDao reportDao = new ReportDao();
        ReportInstance instance = reportDao.getReportInstance(instanceId);

        User user = Common.getUser(request);
        Permissions.ensureReportInstancePermission(user, instance);

        ReportChartCreator creator = new ReportChartCreator(ControllerUtils.getResourceBundle(request));
        creator.createContent(instance, reportDao, null, false);

        Map<String, byte[]> imageData = new HashMap<String, byte[]>();
        imageData.put(creator.getChartName(), creator.getImageData());
        for (PointStatistics pointStatistics : creator.getPointStatistics())
            imageData.put(pointStatistics.getChartName(), pointStatistics.getImageData());
        user.setReportImageData(imageData);

        return new ModelAndView(new ReportChartView(creator.getHtml()));
    }

    static class ReportChartView implements View {
        private final String content;

        public ReportChartView(String content) {
            this.content = content;
        }

        public String getContentType() {
            return null;
        }

        public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            response.getWriter().write(content);
        }
    }
}
