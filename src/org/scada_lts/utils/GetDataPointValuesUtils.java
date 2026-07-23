package org.scada_lts.utils;

import com.serotonin.mango.rt.dataImage.PointValueFacade;
import com.serotonin.mango.rt.dataImage.PointValueTime;

import java.util.List;

public final class GetDataPointValuesUtils {

    private GetDataPointValuesUtils() {}

    public static List<PointValueTime> getDataPointValues(long from, long to, int dataPointId) {
        PointValueFacade pointValueFacade = new PointValueFacade(dataPointId);
        List<PointValueTime> pointData;
        if (from == -1 && to == -1)
            pointData = pointValueFacade.getPointValues(0);
        else if (from == -1)
            pointData = pointValueFacade.getPointValuesBetween(0, to);
        else if (to == -1)
            pointData = pointValueFacade.getPointValues(from);
        else
            pointData = pointValueFacade.getPointValuesBetween(from, to);
        return pointData;
    }
}