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
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.dataSource.http.HttpImagePointLocatorVO;

/**
 * @author Matthew Lohbihler
 */
public class WebcamLiveFeedController extends ParameterizableViewController {
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        int pointId = Integer.parseInt(request.getParameter("pointId"));
        DataPointDao dataPointDao = new DataPointDao();
        DataPointVO dp = dataPointDao.getDataPoint(pointId);

        if (!(dp.getPointLocator() instanceof HttpImagePointLocatorVO))
            throw new Exception("Point is not an HTTP Image point");

        // User user = Common.getUser(request);
        // Permissions.ensureDataPointReadPermission(user, dp);

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("code", ((HttpImagePointLocatorVO) dp.getPointLocator()).getWebcamLiveFeedCode());

        return new ModelAndView(getViewName(), model);
    }
}
