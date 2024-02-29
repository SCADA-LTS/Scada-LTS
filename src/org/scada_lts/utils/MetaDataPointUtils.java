package org.scada_lts.utils;

import com.serotonin.db.IntValuePair;
import com.serotonin.mango.rt.dataImage.DataPointRT;
import com.serotonin.mango.rt.dataSource.meta.MetaPointLocatorRT;
import com.serotonin.mango.vo.dataSource.meta.MetaPointLocatorVO;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public final class MetaDataPointUtils {

    private MetaDataPointUtils() {}

    public static boolean isDataPointInContext(DataPointRT dataPoint, int dataPointInContextId) {
        if (isMetaDataPointRT(dataPoint)) {
            MetaPointLocatorRT metaPointLocatorRT = dataPoint.getPointLocator();
            if(metaPointLocatorRT != null) {
                MetaPointLocatorVO metaPointLocatorVO = metaPointLocatorRT.getPointLocatorVO();
                if(metaPointLocatorVO != null && metaPointLocatorVO.getContext() != null) {
                    Set<Integer> keys = metaPointLocatorVO.getContext().stream()
                            .map(IntValuePair::getKey)
                            .collect(Collectors.toSet());
                    return keys.contains(dataPointInContextId);
                }
            }
        }
        return false;
    }

    public static boolean isMetaDataPointRT(DataPointRT dataPoint) {
        return Objects.nonNull(dataPoint) && dataPoint.getPointLocator() instanceof MetaPointLocatorRT;
    }
}
