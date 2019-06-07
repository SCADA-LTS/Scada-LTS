/*
 * (c) 2015 Abil'I.T. http://abilit.eu/
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
package org.scada_lts.utils;

import java.sql.SQLException;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.rt.event.type.AuditEventType;
import com.serotonin.mango.rt.event.type.CompoundDetectorEventType;
import com.serotonin.mango.rt.event.type.DataPointEventType;
import com.serotonin.mango.rt.event.type.DataSourceEventType;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.rt.event.type.MaintenanceEventType;
import com.serotonin.mango.rt.event.type.PublisherEventType;
import com.serotonin.mango.rt.event.type.ScheduledEventType;
import com.serotonin.mango.rt.event.type.SystemEventType;

/** 
 * Deliver type corresponding with mango.
 * 
 * @author grzegorz bylica Abil'I.T. development team, sdt@abilit.eu
 * person supporting and coreecting translation Jerzy Piejko
 */
public class EventTypeUtil {
	
	public static EventType createEventType(int typeId, int typeRef1, int typeRef2)
			throws SQLException {
		
		EventType type;
		if (typeId == EventType.EventSources.DATA_POINT) {
			type = new DataPointEventType(typeRef1,typeRef2);
		} else if (typeId == EventType.EventSources.DATA_SOURCE) {
			type = new DataSourceEventType(typeRef1,typeRef2);
		} else if (typeId == EventType.EventSources.SYSTEM) {
			type = new SystemEventType(typeRef1,typeRef2);
		} else if (typeId == EventType.EventSources.COMPOUND) {
			type = new CompoundDetectorEventType(typeRef1);
		} else if (typeId == EventType.EventSources.SCHEDULED) {
			type = new ScheduledEventType(typeRef1);
		} else if (typeId == EventType.EventSources.PUBLISHER) {
			type = new PublisherEventType(typeRef1,typeRef2);
		} else if (typeId == EventType.EventSources.AUDIT) {
			type = new AuditEventType(typeRef1,typeRef2);
		} else if (typeId == EventType.EventSources.MAINTENANCE) {
			type = new MaintenanceEventType(typeRef1);
		} else {
			throw new ShouldNeverHappenException("Unknown event type: "	+ typeId);
		}
		return type;
	}

}
