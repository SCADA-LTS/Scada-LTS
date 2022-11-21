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
package com.serotonin.mango.db.dao;

import java.util.List;

import org.scada_lts.mango.adapter.MangoScheduledEvent;
import org.scada_lts.mango.service.ScheduledEventService;

import com.serotonin.mango.vo.event.ScheduledEventVO;

/**
 * @author Matthew Lohbihler
 * 
 */
public class ScheduledEventDao {

	MangoScheduledEvent scheduledEventService = new ScheduledEventService();

    public String generateUniqueXid() {
        return scheduledEventService.generateUniqueXid();
    }

    public boolean isXidUnique(String xid, int excludeId) {
		return scheduledEventService.isXidUnique(xid, excludeId);
    }

    public List<ScheduledEventVO> getScheduledEvents() {
		return scheduledEventService.getScheduledEvents();
    }

    public ScheduledEventVO getScheduledEvent(int id) {
		return scheduledEventService.getScheduledEvent(id);
    }

    public ScheduledEventVO getScheduledEvent(String xid) {
		return scheduledEventService.getScheduledEvent(xid);
    }

    public void saveScheduledEvent(final ScheduledEventVO se) {
		scheduledEventService.saveScheduledEvent(se);
    }

    public void deleteScheduledEvent(final int scheduledEventId) {
		scheduledEventService.deleteScheduledEvent(scheduledEventId);
    }
}
