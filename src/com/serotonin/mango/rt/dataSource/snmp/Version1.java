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

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OctetString;

/**
 * @author Matthew Lohbihler
 * 
 */
public class Version1 extends Version {
    private final OctetString community;

    public Version1(String community) {
        this.community = SnmpUtils.createOctetString(community);
    }

    @Override
    public int getVersionId() {
        return SnmpConstants.version1;
    }

    @Override
    public void addUser(Snmp snmp) {
        // no op
    }

    @Override
    public PDU createPDU() {
        return new PDU();
    }

    @Override
    public Target getTarget() {
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(community);
        return target;
    }
}
