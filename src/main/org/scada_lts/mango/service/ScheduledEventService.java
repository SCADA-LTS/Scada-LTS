/*
 * (c) 2016 Abil'I.T. http://abilit.eu/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.scada_lts.mango.service;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.AuditEventUtils;
import com.serotonin.mango.vo.event.ScheduledEventVO;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.ScheduledEventDAO;
import org.scada_lts.mango.adapter.MangoScheduledEvent;

import java.util.List;

/**
 * ScheduledEventDAO service
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class ScheduledEventService implements MangoScheduledEvent {

	private ScheduledEventDAO scheduledEventDAO = new ScheduledEventDAO();

	@Override
	public String generateUniqueXid() {
		return DAO.getInstance().generateUniqueXid(ScheduledEventVO.XID_PREFIX, "scheduledEvents");
	}

	@Override
	public boolean isXidUnique(String xid, int excludeId) {
		return DAO.getInstance().isXidUnique(xid, excludeId, "scheduledEvents");
	}

	@Override
	public List<ScheduledEventVO> getScheduledEvents() {
		return scheduledEventDAO.getScheduledEvents();
	}

	@Override
	public ScheduledEventVO getScheduledEvent(int id) {
		return scheduledEventDAO.getScheduledEvent(id);
	}

	@Override
	public ScheduledEventVO getScheduledEvent(String xid) {
		return scheduledEventDAO.getScheduledEvent(xid);
	}

	@Override
	public void saveScheduledEvent(ScheduledEventVO scheduledEvent) {
		if (scheduledEvent.getId() == Common.NEW_ID) {
			scheduledEvent.setId(scheduledEventDAO.insert(scheduledEvent));
			AuditEventUtils.raiseAddedEvent(AuditEventType.TYPE_SCHEDULED_EVENT, scheduledEvent);
		} else {
			ScheduledEventVO oldScheduledEvent = scheduledEventDAO.getScheduledEvent(scheduledEvent.getId());
			scheduledEventDAO.update(scheduledEvent);
			AuditEventUtils.raiseChangedEvent(AuditEventType.TYPE_SCHEDULED_EVENT, oldScheduledEvent, scheduledEvent);
		}
	}

	@Override
	public void deleteScheduledEvent(int scheduledEventId) {
		scheduledEventDAO.delete(scheduledEventId);
	}
}
