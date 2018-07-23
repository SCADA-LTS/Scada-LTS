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
package com.serotonin.mango.rt.dataSource.snmp;

import org.snmp4j.smi.OctetString;

/**
 * @author Matthew Lohbihler
 * 
 */
public class SnmpUtils {
    public static OctetString createOctetString(String s) {
        OctetString octetString;

        if (s.startsWith("0x"))
            octetString = OctetString.fromHexString(s.substring(2), ':');
        else
            octetString = new OctetString(s);

        return octetString;
    }
}
