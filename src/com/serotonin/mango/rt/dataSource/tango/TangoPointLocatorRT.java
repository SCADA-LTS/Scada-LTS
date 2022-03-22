package com.serotonin.mango.rt.dataSource.tango;

import com.serotonin.mango.rt.dataSource.PointLocatorRT;
import com.serotonin.mango.vo.dataSource.tango.TangoPointLocatorVO;

/**
 * @author GP Orcullo
 */

public class TangoPointLocatorRT extends PointLocatorRT {
    private final TangoPointLocatorVO vo;

    public TangoPointLocatorRT(TangoPointLocatorVO vo) {
        this.vo = vo;
    }

    @Override
    public boolean isSettable() {
        return vo.isSettable();
    }

    public TangoPointLocatorVO getVO() {
        return vo;
    }
}