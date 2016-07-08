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

import net.sf.openv4j.Devices;
import net.sf.openv4j.Protocol;

/**
 *
 * @author aploese
 */
public class OpenV4JProtocolBean {

    public static OpenV4JProtocolBean[] fromDevice(Devices device) {
        OpenV4JProtocolBean[] result = new OpenV4JProtocolBean[device.getProtocols().length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new OpenV4JProtocolBean(device.getProtocols()[i]);
        }
        return result;
    }
    final Protocol p;

    public OpenV4JProtocolBean(Protocol p) {
        this.p = p;
    }

    public String getName() {
        return p.getName();
    }

    public String getLabel() {
        return p.getLabel();
    }
}
