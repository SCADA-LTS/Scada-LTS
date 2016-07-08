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

import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;

import com.serotonin.util.StringUtils;

/**
 * @author Matthew Lohbihler
 * 
 */
public class Version3 extends Version {
    private final OctetString securityName;
    private OID authProtocol;
    private final OctetString authPassphrase;
    private OID privProtocol;
    private final OctetString privPassphrase;
    private final OctetString engineId;
    private final OctetString contextEngineId;
    private final OctetString contextName;

    public Version3(String securityName, String authProtocol, String authPassphrase, String privProtocol,
            String privPassphrase, String engineId, String contextEngineId, String contextName) {
        this.securityName = SnmpUtils.createOctetString(securityName);

        if (!StringUtils.isEmpty(authProtocol)) {
            if (authProtocol.equals("MD5"))
                this.authProtocol = AuthMD5.ID;
            else if (authProtocol.equals("SHA"))
                this.authProtocol = AuthSHA.ID;
            else
                throw new IllegalArgumentException("Authentication protocol unsupported: " + authProtocol);
        }

        this.authPassphrase = SnmpUtils.createOctetString(authPassphrase);

        if (!StringUtils.isEmpty(privProtocol)) {
            if (privProtocol.equals("DES"))
                this.privProtocol = PrivDES.ID;
            else if ((privProtocol.equals("AES128")) || (privProtocol.equals("AES")))
                this.privProtocol = PrivAES128.ID;
            else if (privProtocol.equals("AES192"))
                this.privProtocol = PrivAES192.ID;
            else if (privProtocol.equals("AES256"))
                this.privProtocol = PrivAES256.ID;
            else
                throw new IllegalArgumentException("Privacy protocol " + privProtocol + " not supported");
        }

        this.privPassphrase = SnmpUtils.createOctetString(privPassphrase);
        this.engineId = SnmpUtils.createOctetString(engineId);
        this.contextEngineId = SnmpUtils.createOctetString(contextEngineId);
        this.contextName = SnmpUtils.createOctetString(contextName);
    }

    @Override
    public int getVersionId() {
        return SnmpConstants.version3;
    }

    @Override
    public void addUser(Snmp snmp) {
        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
        SecurityModels.getInstance().addSecurityModel(usm);
        if (engineId != null)
            snmp.setLocalEngine(engineId.getValue(), 0, 0);
        snmp.getUSM().addUser(securityName,
                new UsmUser(securityName, authProtocol, authPassphrase, privProtocol, privPassphrase));
    }

    @Override
    public Target getTarget() {
        UserTarget target = new UserTarget();
        if (authPassphrase != null) {
            if (privPassphrase != null)
                target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
            else
                target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
        }
        else
            target.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);

        target.setSecurityName(securityName);
        return target;
    }

    @Override
    public PDU createPDU() {
        ScopedPDU scopedPDU = new ScopedPDU();
        if (contextEngineId != null)
            scopedPDU.setContextEngineID(contextEngineId);
        if (contextName != null)
            scopedPDU.setContextName(contextName);
        return scopedPDU;
    }
}
