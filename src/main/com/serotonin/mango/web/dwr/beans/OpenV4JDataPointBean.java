/*
 *   Mango - Open Source M2M - http://mango.serotoninsoftware.com
 *   Copyright (C) 2010 Arne Pl\u00f6se
 *   @author Arne Pl\u00f6se
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.serotonin.mango.web.dwr.beans;

import net.sf.openv4j.DataPoint;

/**
 *
 * @author aploese
 */
public class OpenV4JDataPointBean {

    private final DataPoint p;
    private final String value;

    public OpenV4JDataPointBean(DataPoint p, String value) {
        this.p = p;
        this.value = value;
    }

    public OpenV4JDataPointBean(DataPoint p) {
        this.p = p;
        value = null;
    }

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return p.getGroup().getName();
    }

    /**
     * @return the groupLabel
     */
    public String getGroupLabel() {
        return p.getGroup().getLabel();
    }

    /**
     * @return the name
     */
    public String getName() {
        return p.getName();
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return p.getLabel();
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
}
