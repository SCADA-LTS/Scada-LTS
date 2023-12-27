package com.serotonin.mango.util;

import com.serotonin.mango.vo.event.PointEventDetectorVO;

public final class PointEventDetectorUtils {

    private PointEventDetectorUtils(){}

    public static String getDescription(PointEventDetectorVO vo) {
        return (vo.njbGetDataPoint().getDescription() == null || vo.njbGetDataPoint().getDescription().equals("")) ? "" : " (" + vo.njbGetDataPoint().getDescription() + ")";
    }
}
