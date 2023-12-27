package org.scada_lts.dao.model;

import com.serotonin.mango.vo.dataSource.DataSourceVO;

public class DataSourceIdentifier extends ScadaObjectIdentifier {

    private DataSourceVO.Type type;
    private boolean enabled;

    public DataSourceIdentifier(int id, String xid, String name, DataSourceVO.Type type, boolean enabled) {
        super(id, xid, name);
        this.type = type;
        this.enabled = enabled;
    }

    public DataSourceVO.Type getType() {
        return type;
    }

    public void setType(DataSourceVO.Type type) {
        this.type = type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
