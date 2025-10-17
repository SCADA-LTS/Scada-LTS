package org.scada_lts.ds.polling;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.vo.dataSource.PointLocatorVO;

public class PollingPointLocatorRT extends PointLocatorRT {

    private final PointLocatorVO vo;

    public PollingPointLocatorRT(PointLocatorVO vo) {
        this.vo = vo;
    }

    @Override
    public boolean isSettable() {
        return vo.isSettable();
    }

    public PointLocatorVO getVo() {
        return vo;
    }
}
