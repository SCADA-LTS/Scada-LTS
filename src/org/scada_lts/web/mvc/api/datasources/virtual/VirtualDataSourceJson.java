package org.scada_lts.web.mvc.api.datasources.virtual;

import com.serotonin.mango.vo.dataSource.virtual.VirtualDataSourceVO;
import org.scada_lts.web.mvc.api.datasources.DataSourceJson;

public class VirtualDataSourceJson extends DataSourceJson {

    private int updatePeriodType;
    private int updatePeriods;

    public VirtualDataSourceJson() {}

    public VirtualDataSourceJson(VirtualDataSourceVO dataSourceVO) {
        super(dataSourceVO);
        this.updatePeriodType = dataSourceVO.getUpdatePeriodType();
        this.updatePeriods = dataSourceVO.getUpdatePeriods();
    }

    @Override
    public VirtualDataSourceVO createDataSourceVO() {
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
