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
package com.serotonin.mango.rt.dataSource.galil;

import com.serotonin.messaging.OutgoingRequestMessage;

/**
 * @author Matthew Lohbihler
 */
public class GalilRequest implements OutgoingRequestMessage {
    private final String data;

    public GalilRequest(String data) {
        this.data = data + "\r\n";
    }

    @Override
    public boolean expectsResponse() {
        return true;
    }

    public byte[] getMessageData() {
        return data.getBytes(GalilDataSourceRT.CHARSET);
    }
}
