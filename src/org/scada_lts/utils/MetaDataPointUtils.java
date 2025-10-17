package org.scada_lts.utils;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.Common;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataSource.meta.MetaPointLocatorRT;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;

import java.util.List;
import java.util.Objects;


public final class MetaDataPointUtils {

    private MetaDataPointUtils() {}

    public static boolean isDataPointInContext(DataPointRT dataPoint, int dataPointInContextId) {
        if (isMetaDataPointRT(dataPoint)) {
            MetaPointLocatorRT metaPointLocatorRT = dataPoint.getPointLocator();
            if(metaPointLocatorRT != null) {
                MetaPointLocatorVO metaPointLocatorVO = metaPointLocatorRT.getPointLocatorVO();
                if(metaPointLocatorVO != null && metaPointLocatorVO.getContext() != null) {
                    for(IntValuePair intValuePair: metaPointLocatorVO.getContext()) {
                        if(intValuePair.getKey() == dataPointInContextId)
                            return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean doResetUnreliableDataPoint(DataPointRT dataPoint, int dataPointInContextId) {
        DataPointRT dataPointRT = Common.ctx.getRuntimeManager().getDataPoint(dataPointInContextId);
        if(dataPointRT == null) {
            return false;
        }
        if (isMetaDataPointRT(dataPoint)) {
            MetaPointLocatorRT metaPointLocatorRT = dataPoint.getPointLocator();
            if(metaPointLocatorRT != null) {
                MetaPointLocatorVO metaPointLocatorVO = metaPointLocatorRT.getPointLocatorVO();
                if(metaPointLocatorVO != null && metaPointLocatorVO.getContext() != null) {
                    List<IntValuePair> context = metaPointLocatorVO.getContext();
                    for(IntValuePair intValuePair: context) {
                        if(intValuePair.getKey() != dataPointInContextId) {
                            dataPointRT = Common.ctx.getRuntimeManager().getDataPoint(intValuePair.getKey());
                            if (dataPointRT == null || dataPointRT.isUnreliable()) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public static boolean isMetaDataPointRT(DataPointRT dataPoint) {
        return Objects.nonNull(dataPoint) && dataPoint.getPointLocator() instanceof MetaPointLocatorRT;
    }
}
