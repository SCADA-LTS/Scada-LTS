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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.db.dao.DataSourceDao;
import com.serotonin.mango.util.CommPortConfigException;
import com.serotonin.mango.vo.DataPointExtendedNameComparator;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.Permissions;

public class DataSourceEditController extends ParameterizableViewController {
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        DataSourceVO<?> dataSourceVO = null;
        User user = Common.getUser(request);

        // Get the id.
        int id = Common.NEW_ID;
        String idStr = request.getParameter("dsid");
        if (idStr == null) {
            // Check for a data point id
            String pidStr = request.getParameter("pid");
            if (pidStr == null) {
                // Adding a new data source? Get the type id.
                int typeId = Integer.parseInt(request.getParameter("typeId"));

                Permissions.ensureAdmin(user);

                // A new data source
                dataSourceVO = DataSourceVO.createDataSourceVO(typeId);
                dataSourceVO.setId(Common.NEW_ID);
                dataSourceVO.setXid(new DataSourceDao().generateUniqueXid());
            }
            else {
                int pid = Integer.parseInt(pidStr);
                DataPointVO dp = new DataPointDao().getDataPoint(pid);
                if (dp == null)
                    throw new ShouldNeverHappenException("DataPoint not found with id " + pid);
                id = dp.getDataSourceId();
            }
        }
        else
            // An existing configuration.
            id = Integer.parseInt(idStr);

        if (id != Common.NEW_ID) {
            dataSourceVO = Common.ctx.getRuntimeManager().getDataSource(id);
            if (dataSourceVO == null)
                throw new ShouldNeverHappenException("DataSource not found with id " + id);
            Permissions.ensureDataSourcePermission(user, id);
        }

        // Set the id of the data source in the user object for the DWR.
        user.setEditDataSource(dataSourceVO);

        // Create the model.
        Map<String, Object> model = new HashMap<String, Object>();

        // The data source
        model.put("dataSource", dataSourceVO);

        // Reference data
        try {
            model.put("commPorts", Common.getCommPorts());
        }
        catch (CommPortConfigException e) {
            model.put("commPortError", e.getMessage());
        }

        List<DataPointVO> allPoints = new DataPointDao().getDataPoints(DataPointExtendedNameComparator.instance, false);
        List<DataPointVO> userPoints = new LinkedList<DataPointVO>();
        List<DataPointVO> analogPoints = new LinkedList<DataPointVO>();
        for (DataPointVO dp : allPoints) {
            if (Permissions.hasDataPointReadPermission(user, dp)) {
                userPoints.add(dp);
                if (dp.getPointLocator().getDataTypeId() == DataTypes.NUMERIC)
                    analogPoints.add(dp);
            }
        }
        model.put("userPoints", userPoints);
        model.put("analogPoints", analogPoints);

        return new ModelAndView(getViewName(), model);
    }
}
