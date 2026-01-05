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
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.DataTypes;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;
import com.serotonin.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.scada_lts.mango.service.DataPointService;
import org.scada_lts.permissions.service.GetDataPointsWithAccess;
import org.springframework.ui.Model;

import com.serotonin.mango.db.dao.DataPointDao;
import com.serotonin.mango.vo.DataPointExtendedNameComparator;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.permission.Permissions;

/**
 * @author Matthew Lohbihler
 */
public final class ControllerUtils {

    private static final Log LOG = LogFactory.getLog(ControllerUtils.class);

    private ControllerUtils() {}

    @Deprecated(since = "2.8.1")
    public static void addPointListDataToModel(User user, int pointId, Map<String, Object> model) {
        List<DataPointVO> allPoints = new DataPointDao().getDataPoints(DataPointExtendedNameComparator.instance, false);
        List<DataPointVO> userPoints = new LinkedList<DataPointVO>();
        int pointIndex = -1;
        for (DataPointVO dp : allPoints) {
            if (Permissions.hasDataPointReadPermission(user, dp)) {
                userPoints.add(dp);
                if (dp.getId() == pointId)
                    pointIndex = userPoints.size() - 1;
            }
        }
        model.put("userPoints", userPoints);

        // Determine next and previous ids
        if (pointIndex > 0)
            model.put("prevId", userPoints.get(pointIndex - 1).getId());
        if (pointIndex < userPoints.size() - 1)
            model.put("nextId", userPoints.get(pointIndex + 1).getId());
    }

    @Deprecated(since = "2.8.1")
    public static void addPointListDataToModel(User user, int pointId, Model model){
        List<DataPointVO> allPoints = new DataPointDao().getDataPoints(DataPointExtendedNameComparator.instance, false);
        List<DataPointVO> userPoints = new LinkedList<DataPointVO>();
        int pointIndex = -1;
        for (DataPointVO dp : allPoints) {
            if (Permissions.hasDataPointReadPermission(user, dp)) {
                userPoints.add(dp);
                if (dp.getId() == pointId)
                    pointIndex = userPoints.size() - 1;
            }
        }
        model.addAttribute("userPoints", userPoints);

        // Determine next and previous ids
        if (pointIndex > 0)
        	model.addAttribute("prevId", userPoints.get(pointIndex - 1).getId());
        if (pointIndex < userPoints.size() - 1)
            model.addAttribute("nextId", userPoints.get(pointIndex + 1).getId());
    }

    public static void addPointListDataToModel(BiConsumer<String, Object> model, User user, DataPointVO point) {

        if(point == null) {
            acceptModel(model, Collections.emptyList(), -1, -1);
            return;
        }

        DataPointService dataPointService = new DataPointService();

        int prevId = dataPointService.getDataPointIdWithAccessPrev(user, point.getExtendedName());
        int nextId = dataPointService.getDataPointIdWithAccessNext(user, point.getExtendedName());
        List<DataPointVO> userPoints = new ArrayList<>();
        if (GetDataPointsWithAccess.hasDataPointReadPermission(user, point))
            userPoints.add(point);

        acceptModel(model, userPoints, prevId, nextId);
    }

    public static String getHomeUrl(User user) {
        if(StringUtils.isEmpty(user.getHomeUrl())) {
            return "/watch_list.shtm";
        }
        return user.getHomeUrl().startsWith("/") ? user.getHomeUrl() : "/" + user.getHomeUrl();
    }

    public static List<DataPointVO> getUserPoints(User user, List<DataPointVO> allPoints, Comparator<DataPointVO> comparator) {

        List<DataPointVO> userPoints = new ArrayList<>();
        for (DataPointVO dp : allPoints) {
            addPointIfHasPermission(dp, user, userPoints);
        }
        if(comparator != null) {
            userPoints.sort(comparator);
        }
        return userPoints;
    }

    public static List<DataPointVO> getContextPoints(User user, List<DataPointVO> allPoints,
                                                     DataPointService dataPointService,
                                                     Comparator<DataPointVO> comparator) {

        List<DataPointVO> contextPoints = new ArrayList<>();
        for (DataPointVO dp : allPoints) {
            if(dp.getPointLocator() instanceof MetaPointLocatorVO) {
                MetaPointLocatorVO pointLocatorVO = dp.getPointLocator();
                Set<Integer> ids = pointLocatorVO.getContext().stream().map(IntValuePair::getKey).collect(Collectors.toSet());
                List<DataPointVO> dataPoints = dataPointService.getDataPoints(ids);
                for(DataPointVO dataPoint: dataPoints) {
                    addPointIfHasPermission(dataPoint, user, contextPoints);
                }
            }
        }
        if(comparator != null) {
            contextPoints.sort(comparator);
        }
        return contextPoints;
    }

    public static List<DataPointVO> getAnalogPoints(User user, List<DataPointVO> allPoints,
                                                    Comparator<DataPointVO> comparator) {
        List<DataPointVO> analogPoints = new ArrayList<>();
        for (DataPointVO dp : allPoints) {
            if (dp.getPointLocator().getDataTypeId() == DataTypes.NUMERIC) {
                addPointIfHasPermission(dp, user, analogPoints);
            }
        }
        if(comparator != null) {
            analogPoints.sort(comparator);
        }
        return analogPoints;
    }

    private static void addPointIfHasPermission(DataPointVO dp, User user, List<DataPointVO> userPoints) {
        if (GetDataPointsWithAccess.hasDataPointReadPermission(user, dp)) {
            userPoints.add(dp);
        }
    }

    private static void acceptModel(BiConsumer<String, Object> model, List<DataPointVO> userPoints, int prevId, int nextId) {

        model.accept("userPoints", userPoints);

        // Determine next and previous ids
        if (prevId > 0)
            model.accept("prevId", prevId);
        if (nextId > 0)
            model.accept("nextId", nextId);
    }
}
