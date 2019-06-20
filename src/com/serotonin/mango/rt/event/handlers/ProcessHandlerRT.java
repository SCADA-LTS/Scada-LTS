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

import com.serotonin.mango.rt.event.EventInstance;
import com.serotonin.mango.rt.maint.work.ProcessWorkItem;
import com.serotonin.mango.vo.event.EventHandlerVO;
import com.serotonin.util.StringUtils;

/**
 * @author Matthew Lohbihler
 */
public class ProcessHandlerRT extends EventHandlerRT {
    public ProcessHandlerRT(EventHandlerVO vo) {
        this.vo = vo;
    }

    @Override
    public void eventRaised(EventInstance evt) {
        executeCommand(vo.getActiveProcessCommand());
    }

    @Override
    public void eventInactive(EventInstance evt) {
        executeCommand(vo.getInactiveProcessCommand());
    }

    private void executeCommand(String command) {
        if (StringUtils.isEmpty(command))
            return;
        ProcessWorkItem.queueProcess(command);
    }
}
