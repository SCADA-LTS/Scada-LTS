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
package com.serotonin.mango.view.custom;

import java.util.ArrayList;
import java.util.List;

import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;

/**
 * @author Matthew Lohbihler
 */
public class CustomView {
    private final User authorityUser;
    private final List<CustomViewComponent> components = new ArrayList<CustomViewComponent>();
    private final List<DataPointVO> pointCache = new ArrayList<DataPointVO>();

    public CustomView(User authorityUser) {
        this.authorityUser = authorityUser;
    }

    public User getAuthorityUser() {
        return authorityUser;
    }

    public int addPoint(DataPointVO dataPointVO, boolean raw, String disabledValue, boolean time) {
        CustomViewPoint point = new CustomViewPoint(components.size(), dataPointVO, raw, disabledValue, time);
        components.add(point);
        return point.getId();
    }

    public int addChart(long duration, int width, int height, List<CustomViewChartPoint> points) {
        CustomViewChart chart = new CustomViewChart(duration, components.size(), width, height, points);
        components.add(chart);
        return chart.getId();
    }

    public List<CustomViewComponent> getComponents() {
        return components;
    }

    synchronized public DataPointVO getPoint(String xid) {
        for (DataPointVO dp : pointCache) {
            if (dp.getXid().equals(xid))
                return dp;
        }

        DataPointVO dp = new DataPointDao().getDataPoint(xid);
        if (dp != null) {
            // Check permissions.
            Permissions.ensureDataPointSetPermission(authorityUser, dp);

            pointCache.add(dp);
        }
        return dp;
    }
}
