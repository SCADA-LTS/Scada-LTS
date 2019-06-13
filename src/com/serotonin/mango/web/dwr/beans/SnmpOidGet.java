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
package com.serotonin.mango.web.dwr.beans;

import java.io.IOException;
import java.util.ResourceBundle;

import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.serotonin.mango.rt.dataSource.snmp.Version;
import com.serotonin.web.i18n.I18NUtils;

/**
 * @author Matthew Lohbihler
 * 
 */
public class SnmpOidGet extends Thread implements TestingUtility {
    private final ResourceBundle bundle;
    private final String host;
    private final int port;
    private final Version version;
    private final String oid;
    private final int retries;
    private final int timeout;
    private String result;

    public SnmpOidGet(ResourceBundle bundle, String host, int port, Version version, String oid, int retries,
            int timeout) {
        this.bundle = bundle;
        this.host = host;
        this.port = port;
        this.version = version;
        this.oid = oid;
        this.retries = retries;
        this.timeout = timeout;
        start();
    }

    @Override
    public void run() {
        Snmp snmp = null;
        try {
            snmp = new Snmp(new DefaultUdpTransportMapping());
            version.addUser(snmp);
            snmp.listen();

            PDU pdu = version.createPDU();
            pdu.setType(PDU.GET);
            pdu.add(new VariableBinding(new OID(oid)));

            PDU response = snmp.send(pdu, version.getTarget(host, port, retries, timeout)).getResponse();
            if (response == null)
                result = I18NUtils.getMessage(bundle, "dsEdit.snmp.tester.noResponse");
            else
                result = response.get(0).getVariable().toString();
        }
        catch (IOException e) {
            result = e.getMessage();
        }
        finally {
            try {
                if (snmp != null)
                    snmp.close();
            }
            catch (IOException e) {
                // no op
            }
        }
    }

    public String getResult() {
        return result;
    }

    public void cancel() {
        // no op
    }
}
