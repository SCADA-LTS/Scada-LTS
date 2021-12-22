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
import org.scada_lts.utils.AuditEventUtils;
import com.serotonin.mango.vo.event.MaintenanceEventVO;
import org.scada_lts.dao.DAO;
import org.scada_lts.dao.MaintenanceEventDAO;
import org.scada_lts.mango.adapter.MangoMaintenanceEvent;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

/**
 * MaintenanceEventDao service
 *
 * @author Mateusz Kaproń Abil'I.T. development team, sdt@abilit.eu
 */
public class MaintenanceEventService implements MangoMaintenanceEvent {

	private MaintenanceEventDAO maintenanceEventDAO = new MaintenanceEventDAO();

	@Override
	public String generateUniqueXid() {
		return DAO.getInstance().generateUniqueXid(MaintenanceEventVO.XID_PREFIX, "maintenanceEvents");
	}

	@Override
	public boolean isXidUnique(String xid, int excludeId) {
		return DAO.getInstance().isXidUnique(xid, excludeId, "maintenanceEvents");
	}

	@Override
	public List<MaintenanceEventVO> getMaintenanceEvents() {
		return maintenanceEventDAO.getMaintenanceEvents();
	}

	@Override
	public MaintenanceEventVO getMaintenanceEvent(int id) {
		return maintenanceEventDAO.getMaintenanceEvent(id);
	}

	@Override
	public MaintenanceEventVO getMaintenanceEvent(String xid) {
		try {
			return maintenanceEventDAO.getMaintenanceEvent(xid);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void saveMaintenanceEvent(MaintenanceEventVO maintenanceEvent) {
		if (maintenanceEvent.getId() == Common.NEW_ID) {
			maintenanceEvent.setId(maintenanceEventDAO.insert(maintenanceEvent));
			AuditEventUtils.raiseAddedEvent(AuditEventType.TYPE_MAINTENANCE_EVENT, maintenanceEvent);
		} else {
			MaintenanceEventVO oldMaintenanceEvent = maintenanceEventDAO.getMaintenanceEvent(maintenanceEvent.getId());
			maintenanceEventDAO.update(maintenanceEvent);
			AuditEventUtils.raiseChangedEvent(AuditEventType.TYPE_MAINTENANCE_EVENT, oldMaintenanceEvent, maintenanceEvent);
		}
	}

	@Override
	public void deleteMaintenanceEventsForDataSource(int dataSourceId) {
		maintenanceEventDAO.deleteMaintenanceEventsForDataSource(dataSourceId);
	}

	@Override
	public void deleteMaintenanceEvent(int maintenanceEventId) {
		maintenanceEventDAO.delete(maintenanceEventId);
	}
}
