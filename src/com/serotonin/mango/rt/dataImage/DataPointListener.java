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

public interface DataPointListener {
    /**
     * This method is called for all existing listeners when the point is initialized. Note that it is not called for
     * listeners that have begun listening to an already-initialized point.
     */
    void pointInitialized();

    /**
     * This method is called every time the point gets a value from somewhere (except for its initialization). This
     * includes when the point changes or is set.
     * 
     * @param newValue
     */
    void pointUpdated(PointValueTime newValue);

    /**
     * This method is called every time the point gets a value that is different from its previous value (except for its
     * initialization). This includes when the point is set, if the set changes the point's value.
     * 
     * @param oldValue
     * @param newValue
     */
    void pointChanged(PointValueTime oldValue, PointValueTime newValue);

    /**
     * This method is called every time the point gets set, whether the value changes or not.
     * 
     * @param oldValue
     * @param newValue
     */
    void pointSet(PointValueTime oldValue, PointValueTime newValue);

    /**
     * This method is called when a backdated value is received.
     * 
     * @param value
     */
    void pointBackdated(PointValueTime value);

    /**
     * This method is called when the point has been terminated, allowing listeners to react as necessary.
     */
    void pointTerminated();
}
