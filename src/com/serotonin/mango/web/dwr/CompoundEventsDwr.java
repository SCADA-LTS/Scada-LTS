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
package com.serotonin.mango.web.dwr;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.serotonin.mango.Common;
import org.scada_lts.mango.service.ServiceInstances;
import com.serotonin.mango.vo.DataPointExtendedNameComparator;
import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.vo.event.CompoundEventDetectorVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;
import com.serotonin.mango.vo.event.ScheduledEventVO;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.mango.web.dwr.beans.EventSourceBean;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * @author Matthew Lohbihler
 */
public class CompoundEventsDwr extends BaseDwr {
    //
    // /
    // / Public methods
    // /
    //
    public Map<String, Object> getInitData() {
        User user = Common.getUser();
        Permissions.ensureDataSourcePermission(user);

        Map<String, Object> model = new HashMap<String, Object>();

        // All existing compound events.
        model.put("compoundEvents", ServiceInstances.CompoundEventDetectorService.getCompoundEventDetectors());

        // Get the data points
        List<EventSourceBean> dataPoints = new LinkedList<EventSourceBean>();
        EventSourceBean source;
        for (DataPointVO dp : ServiceInstances.DataPointService.getDataPoints(DataPointExtendedNameComparator.instance, true)) {
            if (!Permissions.hasDataSourcePermission(user, dp.getDataSourceId()))
                continue;

            source = new EventSourceBean();
            source.setId(dp.getId());
            source.setName(dp.getExtendedName());
            for (PointEventDetectorVO ped : dp.getEventDetectors()) {
                if (ped.isRtnApplicable())
                    source.getEventTypes().add(ped.getEventType());
            }

            if (source.getEventTypes().size() > 0)
                dataPoints.add(source);
        }
        model.put("dataPoints", dataPoints);

        // Get the scheduled events
        List<EventTypeVO> scheduledEvents = new LinkedList<EventTypeVO>();
        for (ScheduledEventVO se : ServiceInstances.ScheduledEventService.getScheduledEvents())
            scheduledEvents.add(se.getEventType());
        model.put("scheduledEvents", scheduledEvents);

        return model;
    }

    public CompoundEventDetectorVO getCompoundEvent(int id) {
        Permissions.ensureDataSourcePermission(Common.getUser());

        if (id == Common.NEW_ID) {
            CompoundEventDetectorVO vo = new CompoundEventDetectorVO();
            vo.setXid(ServiceInstances.CompoundEventDetectorService.generateUniqueXid());
            return vo;
        }
        return ServiceInstances.CompoundEventDetectorService.getCompoundEventDetector(id);
    }

    public DwrResponseI18n saveCompoundEvent(int id, String xid, String name, int alarmLevel, boolean returnToNormal,
            String condition, boolean disabled) {
        Permissions.ensureDataSourcePermission(Common.getUser());

        // Validate the given information. If there is a problem, return an appropriate error message.
        CompoundEventDetectorVO ced = new CompoundEventDetectorVO();
        ced.setId(id);
        ced.setXid(xid);
        ced.setName(name);
        ced.setAlarmLevel(alarmLevel);
        ced.setReturnToNormal(returnToNormal);
        ced.setCondition(condition);
        ced.setDisabled(disabled);

        // Check that condition is ok.
        DwrResponseI18n response = new DwrResponseI18n();

        if (StringUtils.isEmpty(xid))
            response.addContextualMessage("xid", "validate.required");
        else if (!ServiceInstances.CompoundEventDetectorService.isXidUnique(xid, id))
            response.addContextualMessage("xid", "validate.xidUsed");

        ced.validate(response);

        // Save it
        if (!response.getHasMessages()) {
            boolean success = Common.ctx.getRuntimeManager().saveCompoundEventDetector(ced);

            if (!success)
                response.addData("warning", new LocalizableMessage("compoundDetectors.validation.initError"));
        }

        response.addData("cedId", ced.getId());
        return response;
    }

    public void deleteCompoundEvent(int cedId) {
        Permissions.ensureDataSourcePermission(Common.getUser());
        ServiceInstances.CompoundEventDetectorService.deleteCompoundEventDetector(cedId);
        Common.ctx.getRuntimeManager().stopCompoundEventDetector(cedId);
    }

    public DwrResponseI18n validateCondition(String condition) {
        DwrResponseI18n response = new DwrResponseI18n();
        CompoundEventDetectorVO.validate(condition, response);
        return response;
    }
}
