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
package com.serotonin.mango.view.custom;

import javax.servlet.http.HttpServletRequest;

import com.serotonin.mango.rt.RuntimeManager;
import com.serotonin.mango.web.dwr.beans.CustomComponentState;

/**
 * @author Matthew Lohbihler
 */
abstract public class CustomViewComponent {
    private final int id;

    public CustomViewComponent(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public CustomComponentState createState(RuntimeManager rtm, HttpServletRequest request) {
        CustomComponentState state = new CustomComponentState();
        state.setId(id);
        createStateImpl(rtm, request, state);
        return state;
    }

    abstract protected void createStateImpl(RuntimeManager rtm, HttpServletRequest request, CustomComponentState state);
}
