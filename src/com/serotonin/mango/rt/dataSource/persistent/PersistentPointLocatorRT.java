package com.serotonin.mango.rt.dataSource.persistent;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.vo.dataSource.persistent.PersistentPointLocatorVO;

public class PersistentPointLocatorRT extends PointLocatorRT {
    private final PersistentPointLocatorVO vo;

    public PersistentPointLocatorRT(PersistentPointLocatorVO vo) {
        this.vo = vo;
    }

    @Override
    public boolean isSettable() {
        return vo.isSettable();
    }

    public PersistentPointLocatorVO getPointLocatorVO() {
        return vo;
    }
}
