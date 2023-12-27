package org.scada_lts.web.mvc.api.datasources.snmp;

import com.serotonin.mango.vo.dataSource.snmp.SnmpDataSourceVO;
import org.scada_lts.web.mvc.api.datasources.DataSourceJson;

public class SnmpDataSourceJson extends DataSourceJson {

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

    public SnmpDataSourceJson(){}

    public SnmpDataSourceJson(SnmpDataSourceVO dataSourceVO) {
        super(dataSourceVO);
        this.host = dataSourceVO.getHost();
        this.port = dataSourceVO.getPort();
        this.snmpVersion = dataSourceVO.getSnmpVersion();
        this.community = dataSourceVO.getCommunity();
        this.contextName = dataSourceVO.getContextName();
        this.securityName = dataSourceVO.getSecurityName();
        this.authProtocol = dataSourceVO.getAuthProtocol();
        this.authPassphrase = dataSourceVO.getAuthPassphrase();
        this.privProtocol = dataSourceVO.getPrivProtocol();
        this.privPassphrase = dataSourceVO.getPrivPassphrase();
        this.securityLevel = dataSourceVO.getSecurityLevel();
        this.retries = dataSourceVO.getRetries();
        this.timeout = dataSourceVO.getTimeout();
        this.updatePeriodType = dataSourceVO.getUpdatePeriodType();
        this.updatePeriods = dataSourceVO.getUpdatePeriods();
        this.trapEnabled = dataSourceVO.isTrapEnabled();
        this.trapPort = dataSourceVO.getTrapPort();
        this.localAddress = dataSourceVO.getLocalAddress();
    }

    @Override
    public SnmpDataSourceVO createDataSourceVO() {
        SnmpDataSourceVO vo = new SnmpDataSourceVO();
        vo.setId(this.getId());
        vo.setName(this.getName());
        vo.setXid(this.getXid());
        vo.setEnabled(this.isEnabled());
        vo.setHost(this.getHost());
        vo.setPort(this.getPort());
        vo.setSnmpVersion(this.getSnmpVersion());
        vo.setCommunity(this.getCommunity());
        vo.setContextName(this.getContextName());
        vo.setSecurityName(this.getSecurityName());
        vo.setAuthProtocol(this.getAuthProtocol());
        vo.setAuthPassphrase(this.getAuthPassphrase());
        vo.setPrivProtocol(this.getPrivProtocol());
        vo.setPrivPassphrase(this.getPrivPassphrase());
        vo.setSecurityLevel(this.getSecurityLevel());
        vo.setRetries(this.getRetries());
        vo.setTimeout(this.getTimeout());
        vo.setUpdatePeriodType(this.getUpdatePeriodType());
        vo.setUpdatePeriods(this.getUpdatePeriods());
        vo.setTrapEnabled(this.isTrapEnabled());
        vo.setTrapPort(this.getTrapPort());
        vo.setLocalAddress(this.getLocalAddress());
        return vo;
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
