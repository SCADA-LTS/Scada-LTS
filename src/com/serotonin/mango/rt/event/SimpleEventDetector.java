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
package com.serotonin.mango.rt.event;

import java.util.concurrent.CopyOnWriteArraySet;

import com.serotonin.util.ILifecycle;

/**
 * @author Matthew Lohbihler
 */
abstract public class SimpleEventDetector implements EventDetector, ILifecycle {
    private final CopyOnWriteArraySet<EventDetectorListener> listeners = new CopyOnWriteArraySet<EventDetectorListener>();

    public void addListener(EventDetectorListener l) {
        listeners.add(l);
    }

    public void removeListener(EventDetectorListener l) {
        listeners.remove(l);
    }

    protected void fireEventDetectorStateChanged(long time) {
        for (EventDetectorListener l : listeners)
            l.eventDetectorStateChanged(time);
    }

    protected void fireEventDetectorTerminated() {
        for (EventDetectorListener l : listeners)
            l.eventDetectorTerminated(this);
    }

    public boolean hasListeners() {
        return listeners.size() > 0;
    }

    public void terminate() {
        // no op
    }
}
