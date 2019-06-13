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
package com.serotonin.mango.rt.dataSource.virtual;

import com.serotonin.mango.rt.dataImage.types.MangoValue;
import com.serotonin.mango.rt.dataSource.PointLocatorRT;

public class VirtualPointLocatorRT extends PointLocatorRT {
    private final ChangeTypeRT changeType;
    private MangoValue currentValue;
    private final boolean settable;

    public VirtualPointLocatorRT(ChangeTypeRT changeType, MangoValue startValue, boolean settable) {
        this.changeType = changeType;
        currentValue = startValue;
        this.settable = settable;
    }

    public ChangeTypeRT getChangeType() {
        return changeType;
    }

    public MangoValue getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(MangoValue currentValue) {
        this.currentValue = currentValue;
    }

    public void change() {
        currentValue = changeType.change(currentValue);
    }

    @Override
    public boolean isSettable() {
        return settable;
    }
}
