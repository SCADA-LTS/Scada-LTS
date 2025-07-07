package org.scada_lts.dao.event;

import com.serotonin.mango.vo.event.CompoundEventDetectorVO;
import org.scada_lts.dao.GenericDAO;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

public interface ICompoundEventDetectorDAO extends GenericDAO<CompoundEventDetectorVO> {
    @Override
    List<CompoundEventDetectorVO> findAll();

    @Override
    CompoundEventDetectorVO findById(Object[] pk);

    CompoundEventDetectorVO findByXId(Object[] pk);

    @Override
    List<CompoundEventDetectorVO> filtered(String filter, Object[] argsFilter, long limit);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    @Override
    Object[] create(CompoundEventDetectorVO entity);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    @Override
    void update(CompoundEventDetectorVO entity);

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED, rollbackFor = SQLException.class)
    @Override
    void delete(CompoundEventDetectorVO entity);
}
