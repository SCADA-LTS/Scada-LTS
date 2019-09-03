package org.scada_lts.ds.snmp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.net.InetAddress;

/**
 * @autor grzegorz.bylica@gmail.com on 02.09.2019
 */
public class SNMPSet {

    public static enum Version {
        vNone(-1),
        v1(1),
        v2c(2);
        private final int value;

        private Version(int value) {
            this.value = value;
        }
    }


    private static final Log log = LogFactory.getLog(SNMPSet.class);

    public PDU set(String host, int port, int retries, int timeout, String writeCommunity, VariableBinding vb, Version v) {
        Snmp snmp = null;
        try {

            PDU pdu = new PDU();
            pdu.add(vb);
            pdu.setType(PDU.GETNEXT); // pdu.setType(PDU.GET) ?

            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString(writeCommunity));
            InetAddress inetAddress = InetAddress.getByName(host);
            Address address = new UdpAddress(inetAddress, port);
            target.setAddress(address);
            target.setRetries(retries);
            target.setTimeout(timeout);

            if (v == Version.v1) {
                target.setVersion(SnmpConstants.version1);
            } else if (v == Version.v2c) {
                target.setVersion(SnmpConstants.version2c);
            } else {
                new RuntimeException("Unknown version");
            }

            snmp = new Snmp(new DefaultUdpTransportMapping());

            snmp.listen();

            ResponseEvent response = snmp.set(pdu, target);

            if (response.getResponse() == null) {
                throw new RuntimeException("timeout snmp:" + snmp.toString());
            } else {
                log.trace("Received response from: " +
                        response.getPeerAddress());
                //response PDU
                return response.getResponse();
            }

        } catch (Exception e) {
            log.error(e);
        } finally {
            if (snmp != null) {
                try {
                    snmp.close();
                } catch (IOException e) {
                    log.error(e);
                }
            }
        }
        return null;

    }
}