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
package com.serotonin.mango.rt.dataSource;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.serotonin.ShouldNeverHappenException;
import com.serotonin.mango.Common;
import com.serotonin.mango.db.dao.DataSourceDao;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataImage.PointValueTime;
import com.serotonin.mango.rt.dataImage.SetPointSource;
import com.serotonin.mango.rt.event.type.DataSourceEventType;
import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.event.EventTypeVO;
import com.serotonin.util.ILifecycle;
import com.serotonin.web.i18n.LocalizableMessage;

/**
 * Data sources are things that produce data for consumption of this system. Anything that houses, creates, manages, or
 * otherwise can get data to Mango can be considered a data source. As such, this interface can more precisely be
 * considered a proxy of the real thing.
 * 
 * Mango contains multiple objects that carry the name data source. This interface represents those types of objects
 * that execute and perform the actual task of getting information one way or another from the external data source and
 * into the system, and is known as the "run-time" (RT) data source. (Another type is the data source VO, which
 * represents the configuration of a data source RT, a subtle but useful distinction. In particular, a VO is
 * serializable, while an RT is not.)
 * 
 * @author Matthew Lohbihler
 */
abstract public class DataSourceRT implements ILifecycle {
    public static final String ATTR_UNRELIABLE_KEY = "UNRELIABLE";

    private final DataSourceVO<?> vo;

    /**
     * Under the expectation that most data sources will run in their own threads, the addedPoints field is used as a
     * cache for points that have been added to the data source, so that at a convenient time for the data source they
     * can be included in the polling.
     * 
     * Note that updated versions of data points that could already be running may be added here, so implementations
     * should always check for existing instances.
     */
    protected List<DataPointRT> addedChangedPoints = new ArrayList<DataPointRT>();

    /**
     * Under the expectation that most data sources will run in their own threads, the removedPoints field is used as a
     * cache for points that have been removed from the data source, so that at a convenient time for the data source
     * they can be removed from the polling.
     */
    protected List<DataPointRT> removedPoints = new ArrayList<DataPointRT>();

    /**
     * Access to either the addedPoints or removedPoints lists should be synchronized with this object's monitor.
     */
    protected Boolean pointListChangeLock = new Boolean(false);

    private final List<DataSourceEventType> eventTypes;

    public DataSourceRT(DataSourceVO<?> vo) {
        this.vo = vo;

        eventTypes = new ArrayList<DataSourceEventType>();
        for (EventTypeVO etvo : vo.getEventTypes())
            eventTypes.add((DataSourceEventType) etvo.createEventType());
    }

    public int getId() {
        return vo.getId();
    }

    public String getName() {
        return vo.getName();
    }

    /**
     * This method is usable by subclasses to retrieve serializable data stored using the setPersistentData method.
     */
    public Object getPersistentData() {
        return new DataSourceDao().getPersistentData(vo.getId());
    }

    /**
     * This method is usable by subclasses to store any type of serializable data. This intention is to provide a
     * mechanism for data source RTs to be able to persist data between runs. Normally this method would at least be
     * called in the terminate method, but may also be called regularly for failover purposes.
     */
    protected void setPersistentData(Object persistentData) {
        new DataSourceDao().savePersistentData(vo.getId(), persistentData);
    }

    public void addDataPoint(DataPointRT dataPoint) {
        synchronized (pointListChangeLock) {
            addedChangedPoints.remove(dataPoint);
            addedChangedPoints.add(dataPoint);
            removedPoints.remove(dataPoint);
        }
    }

    public void removeDataPoint(DataPointRT dataPoint) {
        synchronized (pointListChangeLock) {
            addedChangedPoints.remove(dataPoint);
            removedPoints.add(dataPoint);
        }
    }

    abstract public void setPointValue(DataPointRT dataPoint, PointValueTime valueTime, SetPointSource source);

    public void relinquish(@SuppressWarnings("unused") DataPointRT dataPoint) {
        throw new ShouldNeverHappenException("not implemented in " + getClass());
    }

    public void forcePointRead(@SuppressWarnings("unused") DataPointRT dataPoint) {
        // No op by default. Override as required.
    }

    protected void raiseEvent(int eventId, long time, boolean rtn, LocalizableMessage message) {
        message = new LocalizableMessage("event.ds", vo.getName(), message);
        DataSourceEventType type = getEventType(eventId);

        Map<String, Object> context = new HashMap<String, Object>();
        context.put("dataSource", vo);

        Common.ctx.getEventManager().raiseEvent(type, time, rtn, type.getAlarmLevel(), message, context);
    }

    protected void returnToNormal(int eventId, long time) {
        DataSourceEventType type = getEventType(eventId);
        Common.ctx.getEventManager().returnToNormal(type, time);
    }

    private DataSourceEventType getEventType(int eventId) {
        for (DataSourceEventType et : eventTypes) {
            if (et.getDataSourceEventTypeId() == eventId)
                return et;
        }
        return null;
    }

    protected LocalizableMessage getSerialExceptionMessage(Exception e, String portId) {
        if (e instanceof NoSuchPortException)
            return new LocalizableMessage("event.serial.portOpenError", portId);
        if (e instanceof PortInUseException)
            return new LocalizableMessage("event.serial.portInUse", portId);
        return getExceptionMessage(e);
    }

    protected static LocalizableMessage getExceptionMessage(Exception e) {
        return new LocalizableMessage("event.exception2", e.getClass().getName(), e.getMessage());
    }

    //
    // /
    // / Lifecycle
    // /
    //
    public void initialize() {
        // no op
    }

    public void terminate() {
        // Remove any outstanding events.
        Common.ctx.getEventManager().cancelEventsForDataSource(vo.getId());
    }

    public void joinTermination() {
        // no op
    }

    //
    // Additional lifecycle.
    public void beginPolling() {
        // no op
    }
}
