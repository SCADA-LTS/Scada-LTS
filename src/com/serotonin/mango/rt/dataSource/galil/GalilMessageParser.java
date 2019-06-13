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

import com.serotonin.messaging.IncomingMessage;
import com.serotonin.messaging.MessageParser;
import com.serotonin.util.queue.ByteQueue;

/**
 * @author Matthew Lohbihler
 */
public class GalilMessageParser implements MessageParser {
    private static final byte[] MESSAGE_END = ":".getBytes(GalilDataSourceRT.CHARSET);
    private static final byte[] ERROR_RESPONSE = "?".getBytes(GalilDataSourceRT.CHARSET);

    public IncomingMessage parseMessage(ByteQueue queue) {
        int end = queue.indexOf(MESSAGE_END);

        if (end == -1) {
            end = queue.indexOf(ERROR_RESPONSE);
            if (end == -1)
                return null;

            queue.pop(end + ERROR_RESPONSE.length);
            return new GalilResponse();
        }

        // Pop off the message
        byte[] data = new byte[end];
        queue.pop(data);

        // Pop off the end indicator
        queue.pop(MESSAGE_END.length);

        return new GalilResponse(data);
    }
}
