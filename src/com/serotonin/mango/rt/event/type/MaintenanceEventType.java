package com.serotonin.mango.rt.event.type;

import java.util.Map;

import com.serotonin.json.JsonException;
import com.serotonin.json.JsonObject;
import com.serotonin.json.JsonReader;
import com.serotonin.mango.db.dao.MaintenanceEventDao;

public class MaintenanceEventType extends EventType {
    private int maintenanceId;

    public MaintenanceEventType() {
        // Required for reflection.
    }

    public MaintenanceEventType(int maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    @Override
    public int getEventSourceId() {
        return EventType.EventSources.MAINTENANCE;
    }

    public int getMaintenanceId() {
        return maintenanceId;
    }

    @Override
    public String toString() {
        return "MaintenanceEventType(maintenanceId=" + maintenanceId + ")";
    }

    @Override
    public int getDuplicateHandling() {
        return EventType.DuplicateHandling.IGNORE;
    }

    @Override
    public int getReferenceId1() {
        return maintenanceId;
    }

    @Override
    public int getReferenceId2() {
        return 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + maintenanceId;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MaintenanceEventType other = (MaintenanceEventType) obj;
        if (maintenanceId != other.maintenanceId)
            return false;
        return true;
    }

    //
    //
    // Serialization
    //
    @Override
    public void jsonSerialize(Map<String, Object> map) {
        super.jsonSerialize(map);
        map.put("XID", new MaintenanceEventDao().getMaintenanceEvent(maintenanceId).getXid());
    }

    @Override
    public void jsonDeserialize(JsonReader reader, JsonObject json) throws JsonException {
        super.jsonDeserialize(reader, json);
        maintenanceId = getMaintenanceEventId(json, "XID");
    }
}
