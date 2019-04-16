package com.serotonin.mango.rt.dataSource.snmp;
import java.io.File;
import java.io.IOException;

import org.snmp4j.agent.BaseAgent;
import org.snmp4j.agent.CommandProcessor;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.agent.mo.MOScalar;
import org.snmp4j.agent.mo.snmp.RowStatus;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB;
import org.snmp4j.agent.mo.snmp.SnmpTargetMIB;
import org.snmp4j.agent.mo.snmp.StorageType;
import org.snmp4j.agent.mo.snmp.VacmMIB;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.USM;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;

public class SnmpAgent extends BaseAgent {

    public SnmpAgent() {
        super(new File("bootCounterFile.txt"), new File("configFile.txt"),
                new CommandProcessor(new OctetString(MPv3.createLocalEngineID())));
    }

    @Override
    protected void initTransportMappings() throws IOException {
       // transportMappings = new TransportMapping<?>[1];
        Address addr = GenericAddress.parse("0.0.0.0/161");
       // TransportMapping<? extends Address> tm = TransportMappings.getInstance().createTransportMapping(addr);
       // transportMappings[0] = tm;
    }

    public void start() throws IOException {
        init();
        addShutdownHook();
        getServer().addContext(new OctetString("public"));
        finishInit();
        run();
        sendColdStartNotification();
    }

    @Override
    protected void registerManagedObjects() {
        getSnmpv2MIB().unregisterMOs(server, getContext(getSnmpv2MIB()));

        MOScalar mo = new MOScalar(new OID(".1.3.6.1.2.1.1.1.0"),
                MOAccessImpl.ACCESS_READ_ONLY, new OctetString("13"));
        try {
            server.register(mo, null);
        } catch (DuplicateRegistrationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void unregisterManagedObjects() {
        // do nothing
    }

    @Override
    protected void addUsmUser(USM usm) {
        // do nothing
    }

    @Override
    protected void addNotificationTargets(SnmpTargetMIB targetMIB, SnmpNotificationMIB notificationMIB) {
        // do nothing
    }

    @Override
    protected void addViews(VacmMIB vacmMIB) {
        vacmMIB.addGroup(SecurityModel.SECURITY_MODEL_SNMPv2c, new OctetString("cpublic"), new OctetString("v1v2group"),
                StorageType.nonVolatile);

        vacmMIB.addAccess(new OctetString("v1v2group"), new OctetString("public"), SecurityModel.SECURITY_MODEL_ANY,
                SecurityLevel.NOAUTH_NOPRIV, MutableVACM.VACM_MATCH_EXACT, new OctetString("fullReadView"),
                new OctetString("fullWriteView"), new OctetString("fullNotifyView"), StorageType.nonVolatile);

        vacmMIB.addViewTreeFamily(new OctetString("fullReadView"), new OID(".1.3"), new OctetString(),
                VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
    }

    @Override
    protected void addCommunities(SnmpCommunityMIB communityMIB) {
        Variable[] com2sec = new Variable[] {
                new OctetString("public"),              // community name
                new OctetString("cpublic"),             // security name
                getAgent().getContextEngineID(),        // local engine ID
                new OctetString("public"),              // default context name
                new OctetString(),                      // transport tag
                new Integer32(StorageType.nonVolatile), // storage type
                new Integer32(RowStatus.active)         // row status
        };
        SnmpCommunityMIB.SnmpCommunityEntryRow row = communityMIB.getSnmpCommunityEntry().createRow(
                new OctetString("public2public").toSubIndex(true), com2sec);
        communityMIB.getSnmpCommunityEntry().addRow(row);
    }
}
