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

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.serotonin.mango.util.SqlDataSourceUtils;
import com.serotonin.mango.vo.CommPortProxy;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.mango.service.DataSourceService;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.util.CommPortConfigException;
import com.serotonin.mango.vo.DataPointExtendedNameComparator;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.permission.Permissions;

import static org.scada_lts.utils.GetDataPointsUtils.*;

public class DataSourceEditController extends ParameterizableViewController {
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        DataSourceVO<?> dataSourceVO = null;
        User user = Common.getUser(request);
        Permissions.ensureAdmin(user);

        DataPointService dataPointService = new DataPointService();
        DataSourceService dataSourceService = new DataSourceService();

        // Get the id.
        int id = Common.NEW_ID;
        String idStr = request.getParameter("dsid");
        DataPointVO dp = null;
        if (idStr == null) {
            // Check for a data point id
            String pidStr = request.getParameter("pid");
            if (pidStr != null) {
                int pid = Integer.parseInt(pidStr);
                dp = dataPointService.getDataPoint(pid);
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
        } else {
            // Adding a new data source? Get the type id.
            int typeId = Integer.parseInt(request.getParameter("typeId"));

            // A new data source
            dataSourceVO = DataSourceVO.createDataSourceVO(typeId);
            dataSourceVO.setId(Common.NEW_ID);
            dataSourceVO.setXid(dataSourceService.generateUniqueXid());
        }

        // Set the id of the data source in the user object for the DWR.
        user.setEditDataSource(dataSourceVO);

        // Create the model.
        Map<String, Object> model = new HashMap<String, Object>();

        // The data source
        model.put("dataSource", dataSourceVO);

        // Reference data
        try {
            model.put("commPorts", getPorts(dataSourceVO));
        }
        catch (CommPortConfigException e) {
            model.put("commPortError", e.getMessage());
        }

        List<DataPointVO> allPoints = dataPointService.getDataPoints(dataSourceVO.getId(), null);

        List<DataPointVO> userPoints = filteringDataPointsByUser(user, allPoints, DataPointExtendedNameComparator.instance);
        List<DataPointVO> contextPoints = getDataPointsByContext(user, allPoints, dataPointService, DataPointExtendedNameComparator.instance);
        List<DataPointVO> points = new ArrayList<>();
        points.addAll(userPoints);
        points.addAll(contextPoints);
        List<DataPointVO> analogPoints = filteringDataPointsByNumericType(user, points, DataPointExtendedNameComparator.instance);

        model.put("userPoints", userPoints);
        model.put("contextPoints", contextPoints);
        model.put("analogPoints", analogPoints);
        model.put("selectWithLimitLowerCaseRegex", SqlDataSourceUtils.selectWithLimitLowerCaseEscape());
        return new ModelAndView(getViewName(), model);
    }

    private static List<CommPortProxy>  getPorts(DataSourceVO<?> dataSource) throws CommPortConfigException {
        if(DataSourceVO.Type.MODBUS_SERIAL == dataSource.getType()) {
            return Common.getSerialPorts();
        } else {
            return Common.getCommPorts();
        }
    }
}
