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
package com.serotonin.mango.rt.dataImage;

public class DataPointEventMulticaster implements DataPointListener {
    protected final DataPointListener a, b;

    protected DataPointEventMulticaster(DataPointListener a, DataPointListener b) {
        this.a = a;
        this.b = b;
    }

    protected DataPointListener remove(DataPointListener oldl) {
        if (oldl == a)
            return b;
        if (oldl == b)
            return a;
        DataPointListener a2 = remove(a, oldl);
        DataPointListener b2 = remove(b, oldl);
        if (a2 == a && b2 == b) {
            return this;
        }
        return add(a2, b2);
    }

    public static DataPointListener add(DataPointListener a, DataPointListener b) {
        if (a == null)
            return b;
        if (b == null)
            return a;
        return new DataPointEventMulticaster(a, b);
    }

    public static DataPointListener remove(DataPointListener l, DataPointListener oldl) {
        if (l == oldl || l == null)
            return null;

        if (l instanceof DataPointEventMulticaster)
            return ((DataPointEventMulticaster) l).remove(oldl);

        return l;
    }

    private static int getListenerCount(DataPointListener l) {
        if (l instanceof DataPointEventMulticaster) {
            DataPointEventMulticaster mc = (DataPointEventMulticaster) l;
            return getListenerCount(mc.a) + getListenerCount(mc.b);
        }

        return 1;
    }

    private static int populateListenerArray(DataPointListener[] a, DataPointListener l, int index) {
        if (l instanceof DataPointEventMulticaster) {
            DataPointEventMulticaster mc = (DataPointEventMulticaster) l;
            int lhs = populateListenerArray(a, mc.a, index);
            return populateListenerArray(a, mc.b, lhs);
        }

        if (a.getClass().getComponentType().isInstance(l)) {
            a[index] = l;
            return index + 1;
        }

        return index;
    }

    public static DataPointListener[] getListeners(DataPointListener l) {
        int n = getListenerCount(l);
        DataPointListener[] result = new DataPointListener[n];
        populateListenerArray(result, l, 0);
        return result;
    }

    //
    // /
    // / DataPointListener interface
    // /
    //
    public void pointChanged(PointValueTime oldValue, PointValueTime newValue) {
        a.pointChanged(oldValue, newValue);
        b.pointChanged(oldValue, newValue);
    }

    public void pointSet(PointValueTime oldValue, PointValueTime newValue) {
        a.pointSet(oldValue, newValue);
        b.pointSet(oldValue, newValue);
    }

    public void pointUpdated(PointValueTime newValue) {
        a.pointUpdated(newValue);
        b.pointUpdated(newValue);
    }

    public void pointBackdated(PointValueTime value) {
        a.pointBackdated(value);
        b.pointBackdated(value);
    }

    public void pointInitialized() {
        a.pointInitialized();
        b.pointInitialized();
    }

    public void pointTerminated() {
        a.pointTerminated();
        b.pointTerminated();
    }
}
