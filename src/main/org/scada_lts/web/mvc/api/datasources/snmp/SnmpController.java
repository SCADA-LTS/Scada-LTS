package org.scada_lts.web.mvc.api.datasources.snmp;

import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataSource.snmp.Version;
import com.serotonin.mango.vo.User;
import com.serotonin.mango.web.dwr.beans.SnmpOidGet;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/api/datasources/snmp")
public class SnmpController {

    @PostMapping(value = "/testSnmp")
    public ResponseEntity<Map<Object, Object>> testSnmp(
            @RequestBody SnmpDataSourceTestingJson data,
            HttpServletRequest request
    ) {
        User user = Common.getUser(request);
        if (user != null && user.isAdmin()) {
            Map<Object, Object> response = new HashMap<>();
            try {
                Version v = Version.getVersion(data.getSnmpVersion(), data.getCommunity(),
                        data.getSecurityName(), data.getAuthProtocol(), data.getAuthPassphrase(),
                        data.getPrivProtocol(), data.getPrivPassphrase(), data.getSecurityLevel(),
                        data.getContextName());
                Target t = v.getTarget(data.getHost(), data.getPort(), data.getRetries(), data.getTimeout());
                response.put("response", getSnmpOid(v, t, data.getOid()));
                return new ResponseEntity<>(response, HttpStatus.OK);
            } catch (UnknownHostException e) {
                response.put("response", "snmp.tester.unknownHost");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

    }


    private String getSnmpOid(Version v, Target target, String oid) {
        String result = "snmp.tester.noResponse";
        Snmp snmp = initSnmpListener(v);
        if (snmp != null) {
            PDU pdu = v.createPDU();
            pdu.add(new VariableBinding(new OID(oid)));
            pdu.setType(PDU.GET);

            try {
                ResponseEvent event = snmp.send(pdu, target);
                if (event != null && event.getResponse() != null && pdu.getErrorStatus() == PDU.noError) {
                    result = event.getResponse().get(0).getVariable().toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (snmp != null) {
                    try {
                        snmp.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return result;
        }
        return "snmp.tester.noConnection";
    }

    private Snmp initSnmpListener(Version v) {
        Snmp snmp;
        try {
            snmp = new Snmp(new DefaultUdpTransportMapping());
            OctetString localEngineId = new OctetString(MPv3.createLocalEngineID()).substring(0, 9);
            USM usm = new USM(SecurityProtocols.getInstance(), localEngineId, 0);
            SecurityModels.getInstance().addSecurityModel(usm);

            v.addUser(snmp);

            snmp.listen();
            return snmp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
