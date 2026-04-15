package org.scada_lts.dao;

import com.serotonin.mango.vo.DataPointVO;
import org.scada_lts.dao.model.ScadaObjectIdentifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface IDataPointDAO {
    DataPointVO getDataPoint(int id);

    DataPointVO getDataPoint(String xid);

    List<DataPointVO> getDataPoints();

    List<DataPointVO> filtered(String filter, Object[] argsFilter, long limit);

    List<DataPointVO> getDataPoints(int dataSourceId);

    List<DataPointVO> getDataPointByKeyword(String[] keywords);

    List<DataPointVO> getPlcDataPoints(int dataSourceId);

    List<Integer> getDataPointsIds(int dataSourceId);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    int insert(DataPointVO dataPoint);

    DataPointVO create(DataPointVO entity);

    DataPointVO getById(int id) throws EmptyResultDataAccessException;

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    int update(DataPointVO dataPoint);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    int delete(int id);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void deleteWithIn(String dataPointIdList);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    void deleteEventHandler(String dataPointIdList);

    List<DataPointVO> selectDataPointsWithAccess(int userId, int profileId);

    List<DataPointVO> selectDataPointsWithAccess(int userId);

    List<ScadaObjectIdentifier> selectDataPointIdentifiersWithAccess(int userId, int profileId);

    List<ScadaObjectIdentifier> selectDataPointIdentifiersWithAccess(int userId);

    List<ScadaObjectIdentifier> findIdentifiers();

    List<ScadaObjectIdentifier> findIdentifiers(int dataSourceId);

    List<DataPointVO> getDataPoints(String dataSourceXid);

    List<DataPointVO> selectDataPoints(Set<Integer> ids);

    List<DataPointVO> getDataPointsWithLimit(Set<Integer> excludeIds, int offset, int limit);

    List<DataPointVO> getDataPointByKeywords(Set<String> keywords, Set<Integer> excludeIds, boolean startsWith, int offset, int limit);

    int selectDataPointIdWithAccessPrev(int userId, int profileId, String name);

    int selectDataPointIdWithAccessNext(int userId, int profileId, String name);

    int selectDataPointIdWithAccessPrev(String name);

    int selectDataPointIdWithAccessNext(String name);
}
