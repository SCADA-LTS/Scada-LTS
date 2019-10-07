package org.scada_lts.dao;

import com.serotonin.mango.vo.DataPointVO;
import org.scada_lts.dao.batch.Limit;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

public interface IDataPointDAO {
    DataPointVO getDataPoint(int id);

    DataPointVO getDataPoint(String xid);

    @Deprecated
    List<DataPointVO> getDataPoints();

    List<DataPointVO> filtered(String filter, Object[] argsFilter, long limit);

    List<DataPointVO> getDataPoints(int dataSourceId);

    List<Integer> getDataPointsIds(int dataSourceId);

    @Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
    int insert(DataPointVO dataPoint);

    @Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
    void update(DataPointVO dataPoint);

    @Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
    void delete(int id);

    @Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
    void deleteWithIn(String dataPointIdList);

    @Transactional(readOnly = false,propagation= Propagation.REQUIRES_NEW,isolation= Isolation.READ_COMMITTED,rollbackFor=SQLException.class)
    void deleteEventHandler(String dataPointIdList);

    List<DataPointVO> getDataPoints(long offset, Limit<Integer> limit);

    long getCounts();
}
