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

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.serotonin.mango.web.dwr.beans.BasePointState;
import com.serotonin.mango.web.dwr.beans.CustomComponentState;
import com.serotonin.mango.web.dwr.beans.ViewComponentState;
import com.serotonin.mango.web.dwr.beans.WatchListState;

/**
 * @author Matthew Lohbihler
 */
public class LongPollState implements Serializable {
    private static final long serialVersionUID = 1L;

    private int maxAlarmLevel = -1;
    private long lastAlarmLevelChange = 0;
    private List<WatchListState> watchListStates = new ArrayList<WatchListState>();
    private WatchListState pointDetailsState = null;
    private List<ViewComponentState> viewComponentStates = new ArrayList<ViewComponentState>();
    private String pendingAlarmsContent;
    private List<CustomComponentState> customViewStates = new ArrayList<CustomComponentState>();

    public WatchListState getWatchListState(String id) {
        return (WatchListState) getBasePointState(id, watchListStates);
    }

    public ViewComponentState getViewComponentState(String id) {
        return (ViewComponentState) getBasePointState(id, viewComponentStates);
    }

    public CustomComponentState getCustomViewState(int id) {
        for (CustomComponentState state : customViewStates) {
            if (state.getId() == id)
                return state;
        }
        return null;
    }

    private BasePointState getBasePointState(String id, List<? extends BasePointState> list) {
        for (BasePointState state : list) {
            if (state.getId().equals(id))
                return state;
        }
        return null;
    }

    public int getMaxAlarmLevel() {
        return maxAlarmLevel;
    }

    public void setMaxAlarmLevel(int maxAlarmLevel) {
        this.maxAlarmLevel = maxAlarmLevel;
    }

    public long getLastAlarmLevelChange() {
        return lastAlarmLevelChange;
    }

    public void setLastAlarmLevelChange(long lastAlarmLevelChange) {
        this.lastAlarmLevelChange = lastAlarmLevelChange;
    }

    public List<WatchListState> getWatchListStates() {
        return watchListStates;
    }

    public void setWatchListStates(List<WatchListState> watchListStates) {
        this.watchListStates = watchListStates;
    }

    public WatchListState getPointDetailsState() {
        return pointDetailsState;
    }

    public void setPointDetailsState(WatchListState pointDetailsState) {
        this.pointDetailsState = pointDetailsState;
    }

    public List<ViewComponentState> getViewComponentStates() {
        return viewComponentStates;
    }

    public void setViewComponentStates(List<ViewComponentState> viewComponentStates) {
        this.viewComponentStates = viewComponentStates;
    }

    public String getPendingAlarmsContent() {
        return pendingAlarmsContent;
    }

    public void setPendingAlarmsContent(String pendingAlarmsContent) {
        this.pendingAlarmsContent = pendingAlarmsContent;
    }

    public List<CustomComponentState> getCustomViewStates() {
        return customViewStates;
    }

    public void setCustomViewStates(List<CustomComponentState> customViewStates) {
        this.customViewStates = customViewStates;
    }

    /**
     * @param out
     *            required by the serialization API.
     */
    private void writeObject(ObjectOutputStream out) {
        // no op
    }

    /**
     * @param in
     *            required by the serialization API.
     */
    private void readObject(ObjectInputStream in) {
        maxAlarmLevel = -1;
        lastAlarmLevelChange = 0;
        watchListStates = new ArrayList<WatchListState>();
        pointDetailsState = null;
        viewComponentStates = new ArrayList<ViewComponentState>();
        pendingAlarmsContent = null;
        customViewStates = new ArrayList<CustomComponentState>();
    }
}
