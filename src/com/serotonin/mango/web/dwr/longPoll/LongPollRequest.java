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
package com.serotonin.mango.web.dwr.longPoll;

import java.io.Serializable;

/**
 * @author Matthew Lohbihler
 */
public class LongPollRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean maxAlarm = true;
    private boolean terminated;
    private boolean watchList;
    private boolean view;
    private boolean viewEdit;
    private boolean pointDetails;
    private boolean pendingAlarms;
    private boolean customView;

    private int anonViewId;

    public boolean isTerminated() {
        return terminated;
    }

    public void setTerminated(boolean terminated) {
        this.terminated = terminated;
    }

    public boolean isMaxAlarm() {
        return maxAlarm;
    }

    public void setMaxAlarm(boolean maxAlarm) {
        this.maxAlarm = maxAlarm;
    }

    public boolean isWatchList() {
        return watchList;
    }

    public void setWatchList(boolean watchList) {
        this.watchList = watchList;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }

    public boolean isViewEdit() {
        return viewEdit;
    }

    public void setViewEdit(boolean viewEdit) {
        this.viewEdit = viewEdit;
    }

    public boolean isPointDetails() {
        return pointDetails;
    }

    public void setPointDetails(boolean pointDetails) {
        this.pointDetails = pointDetails;
    }

    public boolean isPendingAlarms() {
        return pendingAlarms;
    }

    public void setPendingAlarms(boolean pendingAlarms) {
        this.pendingAlarms = pendingAlarms;
    }

    public int getAnonViewId() {
        return anonViewId;
    }

    public void setAnonViewId(int anonViewId) {
        this.anonViewId = anonViewId;
    }

    public boolean isCustomView() {
        return customView;
    }

    public void setCustomView(boolean customView) {
        this.customView = customView;
    }
}
