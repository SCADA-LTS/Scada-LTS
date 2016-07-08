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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.WatchListDao;
import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.types.ImageValue;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.WatchList;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.web.dwr.BaseDwr;
import com.serotonin.mango.web.taglib.Functions;

/**
 * @author Matthew Lohbihler
 */
public class MobileWatchListController extends WatchListController {
    public static final String KEY_WATCHLIST_DATA = "watchListData";

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {
        User user = Common.getUser(request);
        WatchListDao watchListDao = new WatchListDao();

        // Check for a watchlist id parameter. If given, update the user.
        try {
            int watchListId = Integer.parseInt(request.getParameter("watchListId"));

            WatchList watchList = watchListDao.getWatchList(watchListId);
            Permissions.ensureWatchListPermission(user, watchList);
            user.setSelectedWatchList(watchListId);
            watchListDao.saveSelectedWatchList(user.getId(), watchList.getId());
        }
        catch (NumberFormatException e) {
            // no op
        }

        Map<String, Object> model = createModel(request);

        // Get the selected watchlist.
        int watchListId = (Integer) model.get(KEY_SELECTED_WATCHLIST);

        // Get the point data.
        List<MobileWatchListState> states = new ArrayList<MobileWatchListState>();
        RuntimeManager rtm = Common.ctx.getRuntimeManager();
        for (DataPointVO pointVO : new WatchListDao().getWatchList(watchListId).getPointList()) {
            MobileWatchListState state = createState(request, rtm, pointVO);
            states.add(state);
        }

        model.put(KEY_WATCHLIST_DATA, states);

        return new ModelAndView(getViewName(), model);
    }

    private MobileWatchListState createState(HttpServletRequest request, RuntimeManager rtm, DataPointVO pointVO) {
        MobileWatchListState state = new MobileWatchListState();
        state.setId(Integer.toString(pointVO.getId()));
        state.setName(pointVO.getExtendedName());

        // Get the data point status from the data image.
        DataPointRT pointRT = rtm.getDataPoint(pointVO.getId());
        if (pointRT == null)
            state.setDisabled(true);
        else {
            PointValueTime pvt = pointRT.getPointValue();
            state.setTime(Functions.getTime(pvt));

            if (pvt != null && pvt.getValue() instanceof ImageValue) {
                // Text renderers don't help here. Create a thumbnail.
                Map<String, Object> model = new HashMap<String, Object>();
                model.put("point", pointVO);
                model.put("pointValue", pvt);
                state.setValue(BaseDwr.generateContent(request, "imageValueThumbnail.jsp", model));
            }
            else
                state.setValue(Functions.getHtmlText(pointVO, pvt));
        }

        return state;
    }
}
