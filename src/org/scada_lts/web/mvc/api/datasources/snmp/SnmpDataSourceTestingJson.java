package org.scada_lts.web.mvc.api.datasources.snmp;

public class SnmpDataSourceTestingJson {

    private String oid;

    private int id;
    private String xid;
    private String name;
    private boolean enabled;



    private String host;
    private int port;
    private int snmpVersion;
    private String community;
    private String engineId;
    private String contextEngineId;
    private String contextName;
    private String securityName;
    private String authProtocol;
    private String authPassphrase;
    private String privProtocol;
    private String privPassphrase;
    private int securityLevel;
    private int retries;
    private int timeout;
    private int updatePeriodType;
    private int updatePeriods;
    private boolean trapEnabled;
    private int trapPort;
    private String localAddress;

    public SnmpDataSourceTestingJson() {
    }

    public SnmpDataSourceTestingJson(String oid, int id, String xid, String name, boolean enabled, String host, int port, int snmpVersion, String community, String engineId, String contextEngineId, String contextName, String securityName, String authProtocol, String authPassphrase, String privProtocol, String privPassphrase, int securityLevel, int retries, int timeout, int updatePeriodType, int updatePeriods, boolean trapEnabled, int trapPort, String localAddress) {
        this.oid = oid;
        this.id = id;
        this.xid = xid;
        this.name = name;
        this.enabled = enabled;
        this.host = host;
        this.port = port;
        this.snmpVersion = snmpVersion;
        this.community = community;
        this.engineId = engineId;
        this.contextEngineId = contextEngineId;
        this.contextName = contextName;
        this.securityName = securityName;
        this.authProtocol = authProtocol;
        this.authPassphrase = authPassphrase;
        this.privProtocol = privProtocol;
        this.privPassphrase = privPassphrase;
        this.securityLevel = securityLevel;
        this.retries = retries;
        this.timeout = timeout;
        this.updatePeriodType = updatePeriodType;
        this.updatePeriods = updatePeriods;
        this.trapEnabled = trapEnabled;
        this.trapPort = trapPort;
        this.localAddress = localAddress;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getXid() {
        return xid;
    }

    public void setXid(String xid) {
        this.xid = xid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getSnmpVersion() {
        return snmpVersion;
    }

    public void setSnmpVersion(int snmpVersion) {
        this.snmpVersion = snmpVersion;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getEngineId() {
        return engineId;
    }

    public void setEngineId(String engineId) {
        this.engineId = engineId;
    }

    public String getContextEngineId() {
        return contextEngineId;
    }

    public void setContextEngineId(String contextEngineId) {
        this.contextEngineId = contextEngineId;
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getSecurityName() {
        return securityName;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public String getAuthProtocol() {
        return authProtocol;
    }

    public void setAuthProtocol(String authProtocol) {
        this.authProtocol = authProtocol;
    }

    public String getAuthPassphrase() {
        return authPassphrase;
    }

    public void setAuthPassphrase(String authPassphrase) {
        this.authPassphrase = authPassphrase;
    }

    public String getPrivProtocol() {
        return privProtocol;
    }

    public void setPrivProtocol(String privProtocol) {
        this.privProtocol = privProtocol;
    }

    public String getPrivPassphrase() {
        return privPassphrase;
    }

    public void setPrivPassphrase(String privPassphrase) {
        this.privPassphrase = privPassphrase;
    }

    public int getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(int securityLevel) {
        this.securityLevel = securityLevel;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getUpdatePeriodType() {
        return updatePeriodType;
    }

    public void setUpdatePeriodType(int updatePeriodType) {
        this.updatePeriodType = updatePeriodType;
    }

    public int getUpdatePeriods() {
        return updatePeriods;
    }

    public void setUpdatePeriods(int updatePeriods) {
        this.updatePeriods = updatePeriods;
    }

    public boolean isTrapEnabled() {
        return trapEnabled;
    }

    public void setTrapEnabled(boolean trapEnabled) {
        this.trapEnabled = trapEnabled;
    }

    public int getTrapPort() {
        return trapPort;
    }

    public void setTrapPort(int trapPort) {
        this.trapPort = trapPort;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }
}
