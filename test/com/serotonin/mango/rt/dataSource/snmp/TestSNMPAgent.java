package com.serotonin.mango.rt.dataSource.snmp;

/*
import java.io.IOException;

import org.snmp4j.smi.OID;


public class TestSNMPAgent {

    static final OID sysDescr = new OID(".1.3.6.1.2.1.1.1.0");

    public static void main(String[] args) throws IOException {
        TestSNMPAgent client = new TestSNMPAgent("udp:127.0.0.1/161");
        client.init();
    }

    SNMPAgent agent = null;
  //  SNMPManager client = null;

    String address = null;

    public TestSNMPAgent(String add) {
        address = add;
    }

    private void init() throws IOException {
        agent = new SNMPAgent("0.0.0.0/2001");
        agent.start();
        agent.unregisterManagedObject(agent.getSnmpv2MIB());

        agent.registerManagedObject(MOCreator.createReadOnly(sysDescr,
                "This Description is set By ShivaSoft"));

       // client = new SNMPManager("udp:127.0.0.1/2001");
        //client.start();
    }

}*/
