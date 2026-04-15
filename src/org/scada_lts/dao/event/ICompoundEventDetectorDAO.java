package org.scada_lts.dao.event;

import com.serotonin.mango.vo.event.CompoundEventDetectorVO;
import org.scada_lts.dao.GenericDAO;

public interface ICompoundEventDetectorDAO extends GenericDAO<CompoundEventDetectorVO> {

    CompoundEventDetectorVO findByXId(Object[] pk);
}
