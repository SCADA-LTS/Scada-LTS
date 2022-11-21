package com.serotonin.mango.db.dao;

import java.util.List;

import org.scada_lts.mango.adapter.MangoMaintenanceEvent;
import org.scada_lts.mango.service.MaintenanceEventService;

import com.serotonin.mango.vo.event.MaintenanceEventVO;

public class MaintenanceEventDao {

	private MangoMaintenanceEvent maintenanceEventSerice = new MaintenanceEventService();

    public String generateUniqueXid() {
		return maintenanceEventSerice.generateUniqueXid();
    }

    public boolean isXidUnique(String xid, int excludeId) {
		return maintenanceEventSerice.isXidUnique(xid, excludeId);
    }

    public List<MaintenanceEventVO> getMaintenanceEvents() {
		return maintenanceEventSerice.getMaintenanceEvents();
    }

    public MaintenanceEventVO getMaintenanceEvent(int id) {
		return maintenanceEventSerice.getMaintenanceEvent(id);
    }

    public MaintenanceEventVO getMaintenanceEvent(String xid) {
		return maintenanceEventSerice.getMaintenanceEvent(xid);
    }

    public void saveMaintenanceEvent(final MaintenanceEventVO me) {
		maintenanceEventSerice.saveMaintenanceEvent(me);
    }

    public void deleteMaintenanceEventsForDataSource(int dataSourceId) {
		maintenanceEventSerice.deleteMaintenanceEventsForDataSource(dataSourceId);
    }

    public void deleteMaintenanceEvent(final int maintenanceEventId) {
		maintenanceEventSerice.deleteMaintenanceEvent(maintenanceEventId);
    }
}
