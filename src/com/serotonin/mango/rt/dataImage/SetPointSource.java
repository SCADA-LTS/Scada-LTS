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

/**
 * A set point source is anything that can cause a set point to occur. For
 * example, a user can use the interface to explicitly set a point, in which
 * case the user is the ser point source. A program could reset a value to 0
 * every date, making that program the set point source. This information is
 * stored in the database as a point value annotation.
 * 
 * @author Matthew Lohbihler
 */
public interface SetPointSource {
	public interface Types {
		int USER = 1;
		int EVENT_HANDLER = 2;
		int ANONYMOUS = 3;
		int POINT_LINK = 4;
		int UNKNOWN = 5;
		int API = 6;
	}

	public int getSetPointSourceType();

	public int getSetPointSourceId();

	public void raiseRecursionFailureEvent();

	void pointSetComplete();
}
