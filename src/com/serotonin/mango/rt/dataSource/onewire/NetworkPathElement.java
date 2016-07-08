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
package com.serotonin.mango.rt.dataSource.onewire;

import com.dalsemi.onewire.container.SwitchContainer;

/**
 * @author Matthew Lohbihler
 */
public class NetworkPathElement {
    private final SwitchContainer switchContainer;
    private final Long address;
    private final int channel;

    public NetworkPathElement(SwitchContainer switchContainer, Long address, int channelNumber) {
        this.switchContainer = switchContainer;
        this.address = address;
        channel = channelNumber;
    }

    public SwitchContainer getContainer() {
        return switchContainer;
    }

    public int getChannel() {
        return channel;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + channel;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final NetworkPathElement other = (NetworkPathElement) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        }
        else if (!address.equals(other.address))
            return false;
        if (channel != other.channel)
            return false;
        return true;
    }
}
