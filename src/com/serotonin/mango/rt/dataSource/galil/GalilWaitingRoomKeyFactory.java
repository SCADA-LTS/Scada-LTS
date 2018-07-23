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

import com.serotonin.messaging.IncomingResponseMessage;
import com.serotonin.messaging.OutgoingRequestMessage;
import com.serotonin.messaging.WaitingRoomKey;
import com.serotonin.messaging.WaitingRoomKeyFactory;

/**
 * @author Matthew Lohbihler
 */
public class GalilWaitingRoomKeyFactory implements WaitingRoomKeyFactory {
    @Override
    public WaitingRoomKey createWaitingRoomKey(OutgoingRequestMessage request) {
        return new GalilWaitingRoomKey();
    }

    @Override
    public WaitingRoomKey createWaitingRoomKey(IncomingResponseMessage response) {
        return new GalilWaitingRoomKey();
    }

    static class GalilWaitingRoomKey implements WaitingRoomKey {
        @Override
        public int hashCode() {
            return 31;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            return true;
        }
    }
}
