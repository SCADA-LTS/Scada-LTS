package org.scada_lts.web.mvc.api.datasources.snmp;

import com.serotonin.mango.vo.dataSource.snmp.SnmpPointLocatorVO;
import org.scada_lts.web.beans.validation.xss.XssProtect;
import org.scada_lts.web.mvc.api.datasources.DataPointLocatorJson;

public class SnmpPointLocatorJson extends DataPointLocatorJson {

    @XssProtect
    private String oid;
    @XssProtect
    private String binary0Value;
    private int setType;
    private boolean trapOnly;
    private boolean relinquishable;

    public SnmpPointLocatorJson() {}

    public SnmpPointLocatorJson(SnmpPointLocatorVO pointLocatorVO) {
        super(pointLocatorVO);
        this.oid = pointLocatorVO.getOid();
        this.binary0Value = pointLocatorVO.getBinary0Value();
        this.setType = pointLocatorVO.getSetType();
        this.trapOnly = pointLocatorVO.isTrapOnly();
        this.relinquishable = pointLocatorVO.isRelinquishable();
    }

    @Override
    public SnmpPointLocatorVO parsePointLocatorData() {
        SnmpPointLocatorVO plVO = new SnmpPointLocatorVO();
        plVO.setDataTypeId(this.getDataTypeId());
        plVO.setSetType(this.getSetType());
        plVO.setOid(this.getOid());
        plVO.setBinary0Value(this.getBinary0Value());
        plVO.setTrapOnly(this.isTrapOnly());
        return plVO;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getBinary0Value() {
        return binary0Value;
    }

    public void setBinary0Value(String binary0Value) {
        this.binary0Value = binary0Value;
    }

    public int getSetType() {
        return setType;
    }

    public void setSetType(int setType) {
        this.setType = setType;
    }

    public boolean isTrapOnly() {
        return trapOnly;
    }

    public void setTrapOnly(boolean trapOnly) {
        this.trapOnly = trapOnly;
    }

    public boolean isRelinquishable() {
        return relinquishable;
    }

    public void setRelinquishable(boolean relinquishable) {
        this.relinquishable = relinquishable;
    }
}
