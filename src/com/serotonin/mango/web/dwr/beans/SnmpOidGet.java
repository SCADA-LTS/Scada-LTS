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

import com.serotonin.mango.rt.dataSource.snmp.Version;
import com.serotonin.web.i18n.I18NUtils;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

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

//          result = testOid(oid, snmp);
            result = walkOid(oid, snmp);

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

    private String testOid(String oid, Snmp snmp) throws IOException {
        String responseString;
        PDU pdu = version.createPDU();
        pdu.setType(PDU.GET);
        pdu.add(new VariableBinding(new OID(oid)));

        PDU response = snmp.send(pdu, version.getTarget(host, port, retries, timeout)).getResponse();
        if (response == null)
            responseString = I18NUtils.getMessage(bundle, "dsEdit.snmp.tester.noResponse");
        else
            responseString = response.get(0).getVariable().toString();

        return responseString;
    }

    private String walkOid(String tableOid, Snmp snmp) throws IOException {
        Map<String, String> resultMap = new TreeMap<>();

        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        List<TreeEvent> events = treeUtils.getSubtree(version.getTarget(host,port,retries,timeout), new OID(tableOid));

        if (events == null || events.size() == 0) {
            return I18NUtils.getMessage(bundle, "dsEdit.snmp.tester.noResponse");
        }

        for (TreeEvent event : events) {
            if (event == null) {
                continue;
            }
            if (event.isError()) {
                continue;
            }
            VariableBinding[] varBindings = event.getVariableBindings();
            if (varBindings == null || varBindings.length == 0) {
                continue;
            }
            for (VariableBinding varBinding: varBindings) {
                if (varBinding == null) {
                    continue;
                }
                resultMap.put("." + varBinding.getOid().toString(), varBinding.getVariable().toString());
            }
        }
        snmp.close();
        StringBuilder resultString = new StringBuilder();
        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            resultString.append(entry.getKey()).append(" - ").append(entry.getValue()).append("\n");
        }
        return resultString.toString();
    }
}
