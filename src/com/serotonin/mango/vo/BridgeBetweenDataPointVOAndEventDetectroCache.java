package com.serotonin.mango.vo;

import com.serotonin.mango.vo.event.PointEventDetectorVO;
import org.quartz.SchedulerException;
import org.scada_lts.cache.EventDetectorsCache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class BridgeBetweenDataPointVOAndEventDetectroCache {

    public  List<PointEventDetectorVO> getEventDetectorsFromCache(DataPointVO dataPointVO) {
        try {
            return EventDetectorsCache.getInstance().getEventDetectorsForDataPointVO(dataPointVO);
        } catch (SchedulerException e) {
            return new ArrayList<PointEventDetectorVO>();
        } catch (IOException e) {
            return new ArrayList<PointEventDetectorVO>();
        }
    }
}
