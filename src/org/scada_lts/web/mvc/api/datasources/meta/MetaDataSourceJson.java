package org.scada_lts.web.mvc.api.datasources.meta;

import com.serotonin.mango.vo.dataSource.meta.MetaDataSourceVO;
import org.scada_lts.web.mvc.api.datasources.DataSourceJson;

public class MetaDataSourceJson extends DataSourceJson {

    public MetaDataSourceJson() {}

    public MetaDataSourceJson(MetaDataSourceVO dataSourceVO) {
        super(dataSourceVO);
    }

    @Override
    public MetaDataSourceVO createDataSourceVO() {
        MetaDataSourceVO vo = new MetaDataSourceVO();
        vo.setId(this.getId());
        vo.setName(this.getName());
        vo.setXid(this.getXid());
        vo.setEnabled(this.isEnabled());
        return vo;
    }
}
