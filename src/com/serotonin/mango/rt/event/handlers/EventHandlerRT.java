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
package com.serotonin.mango.rt.event.handlers;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.EventManager;
import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.event.type.EventType;
import com.serotonin.mango.vo.event.EventHandlerVO;

public abstract class EventHandlerRT {
    protected EventHandlerVO vo;

    private final EventType eventType;

    protected EventHandlerRT(EventHandlerVO vo, EventType eventType) {
        this.vo = vo;
        this.eventType = eventType;
    }

    protected EventHandlerVO getVo() {
        return vo;
    }

    protected EventType getEventType() {
        return eventType;
    }

    /**
     * Not all events that are raised are made active. It depends on the event's alarm level and duplicate handling.
     * 
     * @see EventManager.raiseEvent for details.
     * @param evt
     */
    public abstract void eventRaised(EventInstance evt);

    /**
     * Called when the event is considered inactive.
     * 
     * @see EventManager.raiseEvent for details.
     * @param evt
     */
    public abstract void eventInactive(EventInstance evt);

    public int getId() {
        return getVo() == null ? Common.NEW_ID : getVo().getId();
    }
}
