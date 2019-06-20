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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.UdpAddress;

/**
 * @author Matthew Lohbihler
 * 
 */
abstract public class Version {
    public static Version getVersion(int version, String community, String securityName, String authProtocol,
            String authPassphrase, String privProtocol, String privPassphrase, String engineId, String contextEngineId,
            String contextName) {
        if (version == SnmpConstants.version1)
            return new Version1(community);
        else if (version == SnmpConstants.version2c)
            return new Version2c(community);
        else if (version == SnmpConstants.version3)
            return new Version3(securityName, authProtocol, authPassphrase, privProtocol, privPassphrase, engineId,
                    contextEngineId, contextName);
        else
            throw new IllegalArgumentException("Invalid version value: " + version);
    }

    abstract public int getVersionId();

    abstract public void addUser(Snmp snmp);

    abstract public PDU createPDU();

    abstract protected Target getTarget();

    public Target getTarget(String host, int port, int retries, int timeout) throws UnknownHostException {
        Target target = getTarget();

        Address address = new UdpAddress(InetAddress.getByName(host), port);
        target.setAddress(address);
        target.setRetries(retries);
        target.setTimeout(timeout);

        return target;
    }
}
