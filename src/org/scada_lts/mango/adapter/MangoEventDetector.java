package org.scada_lts.mango.adapter;

import com.serotonin.mango.vo.DataPointVO;
import com.serotonin.mango.vo.event.PointEventDetectorVO;

import java.util.List;

public interface MangoEventDetector {

    List<PointEventDetectorVO> getP(DataPointVO dataPointId);
}
