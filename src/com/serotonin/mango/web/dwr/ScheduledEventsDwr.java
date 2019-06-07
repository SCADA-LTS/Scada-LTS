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

import java.util.List;

import org.joda.time.DateTime;

import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.ScheduledEventDao;
import com.serotonin.mango.vo.event.ScheduledEventVO;
import com.serotonin.mango.vo.permission.Permissions;
import com.serotonin.util.StringUtils;
import com.serotonin.web.dwr.DwrResponseI18n;

/**
 * @author Matthew Lohbihler
 * 
 */
public class ScheduledEventsDwr extends BaseDwr {
    //
    // /
    // / Public methods
    // /
    //
    public List<ScheduledEventVO> getScheduledEvents() {
        Permissions.ensureDataSourcePermission(Common.getUser());
        return new ScheduledEventDao().getScheduledEvents();
    }

    public ScheduledEventVO getScheduledEvent(int id) {
        Permissions.ensureDataSourcePermission(Common.getUser());

        if (id == Common.NEW_ID) {
            DateTime dt = new DateTime();
            ScheduledEventVO se = new ScheduledEventVO();
            se.setXid(new ScheduledEventDao().generateUniqueXid());
            se.setActiveYear(dt.getYear());
            se.setInactiveYear(dt.getYear());
            se.setActiveMonth(dt.getMonthOfYear());
            se.setInactiveMonth(dt.getMonthOfYear());
            return se;
        }
        return new ScheduledEventDao().getScheduledEvent(id);
    }

    public DwrResponseI18n saveScheduledEvent(int id, String xid, String alias, int alarmLevel, int scheduleType,
            boolean returnToNormal, boolean disabled, int activeYear, int activeMonth, int activeDay, int activeHour,
            int activeMinute, int activeSecond, String activeCron, int inactiveYear, int inactiveMonth,
            int inactiveDay, int inactiveHour, int inactiveMinute, int inactiveSecond, String inactiveCron) {
        Permissions.ensureDataSourcePermission(Common.getUser());

        // Validate the given information. If there is a problem, return an appropriate error message.
        ScheduledEventVO se = new ScheduledEventVO();
        se.setId(id);
        se.setXid(xid);
        se.setAlias(alias);
        se.setAlarmLevel(alarmLevel);
        se.setScheduleType(scheduleType);
        se.setReturnToNormal(returnToNormal);
        se.setDisabled(disabled);
        se.setActiveYear(activeYear);
        se.setActiveMonth(activeMonth);
        se.setActiveDay(activeDay);
        se.setActiveHour(activeHour);
        se.setActiveMinute(activeMinute);
        se.setActiveSecond(activeSecond);
        se.setActiveCron(activeCron);
        se.setInactiveYear(inactiveYear);
        se.setInactiveMonth(inactiveMonth);
        se.setInactiveDay(inactiveDay);
        se.setInactiveHour(inactiveHour);
        se.setInactiveMinute(inactiveMinute);
        se.setInactiveSecond(inactiveSecond);
        se.setInactiveCron(inactiveCron);

        DwrResponseI18n response = new DwrResponseI18n();
        ScheduledEventDao scheduledEventDao = new ScheduledEventDao();

        if (StringUtils.isEmpty(xid))
            response.addContextualMessage("xid", "validate.required");
        else if (!scheduledEventDao.isXidUnique(xid, id))
            response.addContextualMessage("xid", "validate.xidUsed");

        se.validate(response);

        // Save the scheduled event
        if (!response.getHasMessages())
            Common.ctx.getRuntimeManager().saveScheduledEvent(se);

        response.addData("seId", se.getId());
        return response;
    }

    public void deleteScheduledEvent(int seId) {
        Permissions.ensureDataSourcePermission(Common.getUser());
        new ScheduledEventDao().deleteScheduledEvent(seId);
        Common.ctx.getRuntimeManager().stopSimpleEventDetector(ScheduledEventVO.getEventDetectorKey(seId));
    }
}
