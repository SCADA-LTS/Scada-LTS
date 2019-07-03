package com.serotonin.mango.web.dwr.beans;

import com.serotonin.mango.rt.dataSource.snmp.Version;
import com.serotonin.mango.rt.dataSource.snmp.Version3;
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
 * @author Radek Jajko
 *
 */
public class SnmpOidWalk extends Thread implements TestingUtility {

    private final ResourceBundle bundle;
    private final String host;
    private final int port;
    private final Version version;
    private final String oid;
    private final int retries;
    private final int timeout;
    private String result;

    public SnmpOidWalk(ResourceBundle bundle, String host, int port, Version version, String oid, int retries,
    int timeout){
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


            Map<String, String> resultMap = new TreeMap<>();

            PDU pdu = version.createPDU();
            pdu.setType(PDU.GET);

            TreeUtils treeUtils;
            if (version instanceof Version3) {
                treeUtils = new TreeUtils(snmp, new DefaultPDUFactory(PDU.GET, ((Version3) version).getContextEngineId(), ((Version3) version).getContextName()));
            } else {
                treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
            }

            List<TreeEvent> events = treeUtils.getSubtree(version.getTarget(host,port,retries,timeout), new OID(oid));

            if (events == null || events.size() == 0) {
                result = I18NUtils.getMessage(bundle, "dsEdit.snmp.tester.noResponse");
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
                resultString.append(entry.getKey()).append(" - ").append(entry.getValue()).append("<br>");
            }
            result = resultString.toString();

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

    @Override
    public void cancel() {

    }
}
