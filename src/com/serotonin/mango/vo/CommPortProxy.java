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
package com.serotonin.mango.vo;

import gnu.io.CommPortIdentifier;

public class CommPortProxy {
    private final String name;
    private String portType;
    private final boolean currentlyOwned;
    private final String currentOwner;

    public CommPortProxy(CommPortIdentifier cpid) {
        name = cpid.getName();
        switch (cpid.getPortType()) {
        case CommPortIdentifier.PORT_SERIAL:
            portType = "Serial";
            break;
        case CommPortIdentifier.PORT_PARALLEL:
            portType = "Parallel";
            break;
        default:
            portType = "Unknown (" + cpid.getPortType() + ")";
        }
        currentlyOwned = cpid.isCurrentlyOwned();
        currentOwner = cpid.getCurrentOwner();
    }

    public boolean isCurrentlyOwned() {
        return currentlyOwned;
    }

    public String getCurrentOwner() {
        return currentOwner;
    }

    public String getName() {
        return name;
    }

    public String getPortType() {
        return portType;
    }
}
