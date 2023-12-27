package com.serotonin.mango.rt.dataSource.snmp;

import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.security.nonstandard.PrivAES192With3DESKeyExtension;
import org.snmp4j.security.nonstandard.PrivAES256With3DESKeyExtension;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;

import com.serotonin.util.StringUtils;

/**
 * @author Matthew Lohbihler
 *
 */
public class Version3 extends Version {

    private final int securityLevel;
    private final OctetString contextName;
    private final OctetString securityName;
    private OID authProtocol;
    private final OctetString authPassphrase;
    private OID privProtocol;
    private final OctetString privPassphrase;

    public Version3(String securityName, String authProtocol, String authPassphrase, String privProtocol,
                    String privPassphrase, int securityLevel, String contextName) {

        this.securityName = new OctetString(securityName);
        this.securityLevel = securityLevel;

        if (!StringUtils.isEmpty(authProtocol)) {
            if (authProtocol.equals("MD5"))
                this.authProtocol = AuthMD5.ID;
            else if (authProtocol.equals("SHA"))
                this.authProtocol = AuthSHA.ID;
            else if (authProtocol.equals("HMAC128SHA224"))
                this.authProtocol = AuthHMAC128SHA224.ID;
            else if (authProtocol.equals("HMAC192SHA256"))
                this.authProtocol = AuthHMAC192SHA256.ID;
            else if (authProtocol.equals("HMAC256SHA384"))
                this.authProtocol = AuthHMAC256SHA384.ID;
            else if (authProtocol.equals("HMAC384SHA512"))
                this.authProtocol = AuthHMAC384SHA512.ID;
            else
                throw new IllegalArgumentException("Authentication protocol unsupported: " + authProtocol);
        }

        this.authPassphrase = new OctetString(authPassphrase);

        if (!StringUtils.isEmpty(privProtocol)) {
            if (privProtocol.equals("DES"))
                this.privProtocol = PrivDES.ID;
            else if ((privProtocol.equals("AES128")) || (privProtocol.equals("AES")))
                this.privProtocol = PrivAES128.ID;
            else if (privProtocol.equals("AES192"))
                this.privProtocol = PrivAES192.ID;
            else if (privProtocol.equals("AES256"))
                this.privProtocol = PrivAES256.ID;
            else if (privProtocol.equals("3DES"))
                this.privProtocol = Priv3DES.ID;
            else if (privProtocol.equals("AES192With3DES"))
                this.privProtocol = PrivAES192With3DESKeyExtension.ID;
            else if (privProtocol.equals("AES256With3DES"))
                this.privProtocol = PrivAES256With3DESKeyExtension.ID;
            else
                throw new IllegalArgumentException("Privacy protocol " + privProtocol + " not supported");
        }

        this.privPassphrase = new OctetString(privPassphrase);
        this.contextName = new OctetString(contextName);
    }

    @Override
    public int getVersionId() {
        return SnmpConstants.version3;
    }

    @Override
    public void addUser(Snmp snmp) {
        snmp.getUSM().addUser(securityName,
                new UsmUser(securityName, authProtocol, authPassphrase, privProtocol, privPassphrase));
    }

    @Override
    public Target getTarget() {
        UserTarget target = new UserTarget();
        target.setSecurityLevel(securityLevel);
        target.setSecurityName(securityName);
        return target;
    }

    @Override
    public PDU createPDU() {
        ScopedPDU scopedPDU = new ScopedPDU();
        if (contextName != null)
            scopedPDU.setContextName(contextName);
        return scopedPDU;
    }

}