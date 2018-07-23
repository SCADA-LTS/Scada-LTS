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
package com.serotonin.mango.rt.dataSource.nmea;

import com.serotonin.messaging.IncomingRequestMessage;

/**
 * @author Matthew Lohbihler
 */
public class NmeaMessage implements IncomingRequestMessage {
    private final String message;
    private String name;
    private String[] fields;

    public NmeaMessage(String message) {
        this.message = message;

        if (message != null) {
            String[] parts = message.split(",");

            if (parts.length > 0) {
                name = parts[0];

                fields = new String[parts.length - 1];
                System.arraycopy(parts, 1, fields, 0, fields.length);
            }
            else {
                fields = new String[0];
            }
        }
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public int getFieldCount() {
        return fields.length;
    }

    public String getField(int index1base) {
        return fields[index1base - 1];
    }
}
