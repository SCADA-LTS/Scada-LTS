package org.scada_lts.web.mvc.api.datasources;

import com.serotonin.mango.vo.dataSource.DataSourceVO;
import com.serotonin.mango.vo.dataSource.snmp.SnmpDataSourceVO;
import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;

public class VirtualDataSourceJson extends GenericJsonDataSource implements DataSourceJson<VirtualDataSourceVO> {

    private int updatePeriodType;
    private int updatePeriods;

    public VirtualDataSourceJson() {}

    public VirtualDataSourceJson(VirtualDataSourceVO dataSourceVO) {
        super(dataSourceVO);
        this.updatePeriodType = dataSourceVO.getUpdatePeriodType();
        this.updatePeriods = dataSourceVO.getUpdatePeriods();
    }

    @Override
    public VirtualDataSourceVO convertDataSourceToVO() {
        VirtualDataSourceVO vo = new VirtualDataSourceVO();
        vo.setId(this.getId());
        vo.setName(this.getName());
        vo.setXid(this.getXid());
        vo.setEnabled(this.isEnabled());
        vo.setUpdatePeriodType(this.getUpdatePeriodType());
        vo.setUpdatePeriods(this.getUpdatePeriods());
        return vo;
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
}
