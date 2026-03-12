package org.scada_lts.dao.event;

import com.serotonin.mango.vo.event.CompoundEventDetectorVO;
import org.scada_lts.dao.GenericDAO;

import java.util.List;

public interface ICompoundEventDetectorDAO extends GenericDAO<CompoundEventDetectorVO> {

    List<CompoundEventDetectorVO> findAll();

    CompoundEventDetectorVO findById(Object[] pk);

    CompoundEventDetectorVO findByXId(Object[] pk);

    List<CompoundEventDetectorVO> filtered(String filter, Object[] argsFilter, long limit);

    Object[] create(CompoundEventDetectorVO entity);

    void update(CompoundEventDetectorVO entity);

    void delete(CompoundEventDetectorVO entity);
}
